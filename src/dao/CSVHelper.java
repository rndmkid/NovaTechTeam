package dao;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A class containing helper code for working with CSV files.
 *
 * <p>TODO: Once we have a build system in place that does dependency management,
 * switch to a library
 *
 * @author Jonathan Lovelace
 */
public final class CSVHelper {
	/**
	 * The logger to use, primarily to log errors.
	 */
	private static final Logger LOGGER = Logger.getLogger(CSVHelper.class.getName());
	/**
	 * The character used to quote fields in CSV.
	 */
	private static final char QUOTE_CHAR = '"';
	private CSVHelper() {
		// Do not instantiate
	}

	/**
	 * Quote the given string for writing to CSV.
	 *
	 * @param string the field to quote
	 * @return it, with all quote characters doubled, and surrounded by quotes if it
	 *         contains any quote characters, commas, or newlines.
	 */
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

	/**
	 * Read a record from a CSV file. We require a {@link PushbackReader} because we
	 * need to be able to "peek" to distinguish between the close-quote ending a
	 * field and a doubled quote indicating a quote character inside the field.
	 *
	 * <p>TODO: Refactor to reduce cyclomatic complexity.
	 *
	 * @param in the stream to read from
	 * @return the decoded fields in the first record in the stream
	 * @throws IOException on I/O error
	 */
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
			} else if (ch == QUOTE_CHAR) {
				final int next = in.read();
				if (next == -1) {
					if (!inQuotes) {
						LOGGER.warning("File ended with a non-closing non-doubled quote");
					}
					retval.add(buffer.toString());
					return retval;
				} else if (next == QUOTE_CHAR) {
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
				buffer.append((char) ch);
			}
		}
	}
}
