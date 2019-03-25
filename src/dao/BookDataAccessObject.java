package dao;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.Author;
import model.Book;
import model.Publisher;

public final class BookDataAccessObject implements DataAccessObject<Book> {

	private final String filename;

	private final DataAccessObject<Author> authorDAO;

	private final DataAccessObject<Publisher> publisherDAO;

	public BookDataAccessObject(final String filename,
			final DataAccessObject<Author> authorDAO,
			final DataAccessObject<Publisher> publisherDAO) {
		this.filename = filename;
		this.authorDAO = authorDAO;
		this.publisherDAO = publisherDAO;
	}

	// TODO: keep a cache of IDs that have been used, to make save() idempotent on
	// repeated calls
	@Override
	public void save(final Book entity) throws IOException {
		try (PrintWriter out = new PrintWriter(new FileWriter(filename, true))) {
			out.println(Stream
					.of(entity.getId(), entity.getAuthor().getId(),
							entity.getPublisher().getId(), entity.getTitle(),
							entity.getIsbn())
					.map(Object::toString).map(CSVHelper::quoteCSV)
					.collect(Collectors.joining(",")));
		}
	}

	@Override
	public void delete(final Book entity) throws IOException {
		final List<List<String>> table = new ArrayList<>();
		try (PushbackReader in = new PushbackReader(new FileReader(filename))) {
			while (true) {
				final List<String> record = CSVHelper.readCSVRecord(in);
				if (record.isEmpty()) {
					break;
				} else if (!Long.toString(entity.getId()).contentEquals(record.get(0))) {
					table.add(record);
				}
			}
		}
		try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
			for (final List<String> record : table) {
				out.println(record.stream().map(CSVHelper::quoteCSV)
						.collect(Collectors.joining(",")));
			}
		}
	}

	@Override
	public void update(final Book entity) throws IOException {
		final List<List<String>> table = new ArrayList<>();
		try (PushbackReader in = new PushbackReader(new FileReader(filename))) {
			while (true) {
				final List<String> record = CSVHelper.readCSVRecord(in);
				if (record.isEmpty()) {
					break;
				} else if (Long.toString(entity.getId()).contentEquals(record.get(0))) {
					table.add(Arrays.asList(Long.toString(entity.getId()),
							Long.toString(entity.getAuthor().getId()),
							Long.toString(entity.getPublisher().getId()),
							entity.getTitle(), entity.getIsbn()));
				} else {
					table.add(record);
				}
			}
		}
		try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
			for (final List<String> record : table) {
				out.println(record.stream().map(CSVHelper::quoteCSV)
						.collect(Collectors.joining(",")));
			}
		}
	}

	@Override
	public Optional<Book> find(final long id) throws IOException {
		try (PushbackReader in = new PushbackReader(new FileReader(filename))) {
			while (true) {
				final List<String> record = CSVHelper.readCSVRecord(in);
				if (record.isEmpty()) {
					return Optional.empty();
				} else if (Long.toString(id).contentEquals(record.get(0))) {
					return Optional.of(new Book(Long.parseLong(record.get(0)),
							authorDAO.find(Long.parseLong(record.get(1))).get(),
							publisherDAO.find(Long.parseLong(record.get(2))).get(),
							record.get(3), record.get(4)));
				}
			}
		}
	}

	@Override
	public List<Book> findAll() throws IOException {
		final List<Book> retval = new ArrayList<>();
		try (PushbackReader in = new PushbackReader(new FileReader(filename))) {
			while (true) {
				final List<String> record = CSVHelper.readCSVRecord(in);
				if (record.isEmpty()) {
					return retval;
				} else {
					retval.add(new Book(Long.parseLong(record.get(0)),
							authorDAO.find(Long.parseLong(record.get(1))).get(),
							publisherDAO.find(Long.parseLong(record.get(2))).get(),
							record.get(3), record.get(4)));
				}
			}
		}
	}

}
