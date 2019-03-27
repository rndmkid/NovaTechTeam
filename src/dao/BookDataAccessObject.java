package dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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
import model.Book;
import model.Publisher;

/**
 * A class to read Books from and write them to the database (CSV file)
 * @author Jonathan Lovelace
 */
public final class BookDataAccessObject implements DataAccessObject<Book> {

	/**
	 * The file the table is stored in.
	 */
	private final Path filename;

	/**
	 * The DAO that manages the author table.
	 */
	private final DataAccessObject<Author> authorDAO;

	/**
	 * The DAO that manages the publisher table.
	 */
	private final DataAccessObject<Publisher> publisherDAO;

	/**
	 * Constructing the DAO for the book table requires the filename to look in and
	 * references to the author and publisher table DAOs.
	 *
	 * @param filename the name of the file the book table is stored in
	 * @param authorDAO the author-table DAO
	 * @param publisherDAO the publisher-table DAO
	 */
	public BookDataAccessObject(final String filename,
			final DataAccessObject<Author> authorDAO,
			final DataAccessObject<Publisher> publisherDAO) {
		this.filename = Paths.get(filename);
		this.authorDAO = authorDAO;
		this.publisherDAO = publisherDAO;
	}

	/**
	 * Store a book in the database.
	 *
	 * <p>TODO: keep a cache of IDs that have been used, to make this idempotent on
	 * repeated calls
	 *
	 * @param entity the book to save
	 */
	@Override
	public void save(final Book entity) throws IOException {
		try (PrintWriter out = new PrintWriter(
				Files.newBufferedWriter(filename, StandardOpenOption.APPEND,
						StandardOpenOption.CREATE))) {
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
		try (PushbackReader in = new PushbackReader(Files.newBufferedReader(filename))) {
			while (true) {
				final List<String> record = CSVHelper.readCSVRecord(in);
				if (record.isEmpty()) {
					break;
				} else if (!Long.toString(entity.getId()).equals(record.get(0))) {
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
	public void update(final Book entity) throws IOException {
		final List<List<String>> table = new ArrayList<>();
		try (PushbackReader in = new PushbackReader(Files.newBufferedReader(filename))) {
			while (true) {
				final List<String> record = CSVHelper.readCSVRecord(in);
				if (record.isEmpty()) {
					break;
				} else if (Long.toString(entity.getId()).equals(record.get(0))) {
					table.add(Arrays.asList(Long.toString(entity.getId()),
							Long.toString(entity.getAuthor().getId()),
							Long.toString(entity.getPublisher().getId()),
							entity.getTitle(), entity.getIsbn()));
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
	public Optional<Book> find(final long id) throws IOException {
		try (PushbackReader in = new PushbackReader(Files.newBufferedReader(filename))) {
			while (true) {
				final List<String> record = CSVHelper.readCSVRecord(in);
				if (record.isEmpty()) {
					return Optional.empty();
				} else if (Long.toString(id).equals(record.get(0))) {
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
		try (PushbackReader in = new PushbackReader(Files.newBufferedReader(filename))) {
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
		} catch (FileNotFoundException|NoSuchFileException except) {
			return new ArrayList<>();
		}
	}

}
