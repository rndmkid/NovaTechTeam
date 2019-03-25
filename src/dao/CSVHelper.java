package dao;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A class containing helper code for working with CSV files.
 * TODO: Once we have a build system in place that does dependency management, switch to a library
 * @author Jonathan Lovelace
 */
public abstract class CSVHelper {
	private static final Logger LOGGER = Logger.getLogger(CSVHelper.class.getName());
	private CSVHelper() {
		// Do not instantiate
	}
	public static String quoteCSV(final String string) {
		if (string == null) {
			return "";
		}
		final String retval = string.replace("\"", "\"\"");
		if (retval.contains(",") || retval.contains("\n") || retval.contains("\r")
				|| retval.contains("\"")) {
			return String.format("\"%s\"", retval);
		} else {
			return retval;
		}
	}
	public static List<String> readCSVRecord(final PushbackReader in) throws IOException {
		final List<String> retval = new ArrayList<>();
		final StringBuilder buffer = new StringBuilder();
		boolean inQuotes = false;
		while (true) {
			final int ch = in.read();
			if (ch == -1) { // EOF
				if (!retval.isEmpty() || buffer.length() != 0) {
					retval.add(buffer.toString());
				}
				return retval;
			} else if (ch == '"') {
				final int next = in.read();
				if (next == -1) {
					if (!inQuotes) {
						LOGGER.warning("File ended with a non-closing non-doubled quote");
					}
					retval.add(buffer.toString());
					return retval;
				} else if (next == '"') {
					if (!inQuotes) {
						// TODO: Somehow avoid false-positive warning when properly-quoted
						// field begins with a quote character
						LOGGER.warning("Apparent doubled qoute character outside quoted region");
					}
					buffer.append('"');
				} else {
					in.unread(next);
					inQuotes = !inQuotes;
				}
			} else if (!inQuotes && ch == ',') {
				retval.add(buffer.toString());
				buffer.setLength(0);
			} else if (!inQuotes && (ch == '\n' || ch == '\r')) {
				retval.add(buffer.toString());
				return retval;
			} else {
				buffer.append(ch);
			}
		}
	}
}
