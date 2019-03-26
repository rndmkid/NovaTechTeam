package dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.Author;

public final class AuthorDataAccessObject implements DataAccessObject<Author> {

	private final Path filename;

	public AuthorDataAccessObject(final String filename) {
		this.filename = Paths.get(filename);
	}

	// TODO: keep a cache of IDs that have been used, to make save() idempotent on
	// repeated calls
	@Override
	public void save(final Author entity) throws IOException {
		try (PrintWriter out = new PrintWriter(
				Files.newBufferedWriter(filename, StandardOpenOption.APPEND,
						StandardOpenOption.CREATE))) {
			out.println(Stream.of(Long.toString(entity.getId()), entity.getName())
					.map(CSVHelper::quoteCSV).collect(Collectors.joining(",")));
		}
	}

	@Override
	public void delete(final Author entity) throws IOException {
		final List<List<String>> table = new ArrayList<>();
		try (PushbackReader in = new PushbackReader(Files.newBufferedReader(filename))) {
			while (true) {
				final List<String> record = CSVHelper.readCSVRecord(in);
				if (record.isEmpty()) {
					break;
				} else if (!Long.toString(entity.getId()).contentEquals(record.get(0))) {
					table.add(record);
				}
			}
		}
		try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(filename))) {
			for (final List<String> record : table) {
				out.println(record.stream().map(CSVHelper::quoteCSV)
						.collect(Collectors.joining(",")));
			}
		}
	}

	@Override
	public void update(final Author entity) throws IOException {
		final List<List<String>> table = new ArrayList<>();
		try (PushbackReader in = new PushbackReader(Files.newBufferedReader(filename))) {
			while (true) {
				final List<String> record = CSVHelper.readCSVRecord(in);
				if (record.isEmpty()) {
					break;
				} else if (Long.toString(entity.getId()).contentEquals(record.get(0))) {
					table.add(Arrays.asList(Long.toString(entity.getId()),
							entity.getName()));
				} else {
					table.add(record);
				}
			}
		}
		try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(filename))) {
			for (final List<String> record : table) {
				out.println(record.stream().map(CSVHelper::quoteCSV)
						.collect(Collectors.joining(",")));
			}
		}
	}

	@Override
	public Optional<Author> find(final long id) throws IOException {
		try (PushbackReader in = new PushbackReader(Files.newBufferedReader(filename))) {
			while (true) {
				final List<String> record = CSVHelper.readCSVRecord(in);
				if (record.isEmpty()) {
					return Optional.empty();
				} else if (Long.toString(id).contentEquals(record.get(0))) {
					return Optional.of(new Author(Long.parseLong(record.get(0)),
							record.get(1)));
				}
			}
		}
	}

	@Override
	public List<Author> findAll() throws IOException {
		final List<Author> retval = new ArrayList<>();
		try (PushbackReader in = new PushbackReader(Files.newBufferedReader(filename))) {
			while (true) {
				final List<String> record = CSVHelper.readCSVRecord(in);
				if (record.isEmpty()) {
					return retval;
				} else {
					retval.add(new Author(Long.parseLong(record.get(0)),
							record.get(1)));
				}
			}
		}
	}

}
