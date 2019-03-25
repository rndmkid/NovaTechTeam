package service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import dao.DataAccessObject;
import model.Author;
import model.Book;
import model.Publisher;

/**
 * An implementation of the service-class interface, to stand between the DAO
 * layer and the user interface.
 *
 * @author Jonathan Lovelace
 *
 */
public final class LibraryServiceImpl implements LibraryService {

	private final DataAccessObject<Book> bookDAO;
	private final DataAccessObject<Author> authorDAO;
	private final DataAccessObject<Publisher> publisherDAO;

	public LibraryServiceImpl(final DataAccessObject<Book> bookDAO,
			final DataAccessObject<Author> authorDAO,
			final DataAccessObject<Publisher> publisherDAO) {
		this.bookDAO = bookDAO;
		this.publisherDAO = publisherDAO;
		this.authorDAO = authorDAO;
	}

	// TODO: Add maxID field to DAO interface?
	@Override
	public Author createAuthor(final String authorName) throws IOException {
		long maxID;
		try {
			maxID = authorDAO.findAll().stream().mapToLong(Author::getId).max()
					.orElse(0L);
		} catch (final FileNotFoundException except) {
			maxID = 0L;
		}
		final Author author = new Author(maxID + 1, authorName);
		authorDAO.save(author);
		return author;
	}

	@Override
	public Publisher createPublisher(final String publisherName,
			final String publisherAddress, final String publisherPhone)
			throws IOException {
		long maxID;
		try {
			maxID = publisherDAO.findAll().stream().mapToLong(Publisher::getId).max()
					.orElse(0L);
		} catch (final FileNotFoundException except) {
			maxID = 0L;
		}
		final Publisher publisher = new Publisher(maxID + 1, publisherName,
				publisherAddress, publisherPhone);
		publisherDAO.save(publisher);
		return publisher;
	}

	@Override
	public Book createBook(final String title, final String isbn, final Author author,
			final Publisher publisher) throws IOException {
		if (!authorDAO.find(author.getId()).isPresent()) {
			authorDAO.save(author);
		}
		if (!publisherDAO.find(publisher.getId()).isPresent()) {
			publisherDAO.save(publisher);
		}
		long maxID;
		try {
			maxID = bookDAO.findAll().stream().mapToLong(Book::getId).max().orElse(0L);
		} catch (final FileNotFoundException except) {
			maxID = 0L;
		}
		final Book book = new Book(maxID + 1, author, publisher, title, isbn);
		bookDAO.save(book);
		return book;
	}

	@Override
	public Book createBook(final String title, final String isbn, final String authorName,
			final String publisherName) throws IOException {
		final List<Author> matchingAuthors = getAuthorsNamed(authorName);
		final List<Publisher> matchingPublishers = getPublishersNamed(publisherName);
		Author author;
		if (matchingAuthors.isEmpty()) {
			author = createAuthor(authorName);
		} else {
			author = matchingAuthors.get(0);
		}
		Publisher publisher;
		if (matchingPublishers.isEmpty()) {
			publisher = createPublisher(publisherName);
		} else {
			publisher = matchingPublishers.get(0);
		}
		final Book book = new Book(
				bookDAO.findAll().stream().mapToLong(Book::getId).max().orElse(0L) + 1,
				author, publisher, title, isbn);
		bookDAO.save(book);
		return book;
	}

	@Override
	public List<Author> getAllAuthors() throws IOException {
		return authorDAO.findAll();
	}

	@Override
	public List<Author> getAuthorsNamed(final String name) throws IOException {
		return authorDAO.findAll().stream()
				.filter(author -> name.equals(author.getName()))
				.collect(Collectors.toList());
	}

	@Override
	public List<Author> getAuthorsMatching(final String pattern) throws IOException {
		return authorDAO.findAll().stream().filter(
				author -> author.getName().toLowerCase().contains(pattern.toLowerCase()))
				.collect(Collectors.toList());
	}

	@Override
	public List<Publisher> getAllPublishers() throws IOException {
		return publisherDAO.findAll();
	}

	@Override
	public List<Publisher> getPublishersNamed(final String name) throws IOException {
		return publisherDAO.findAll().stream()
				.filter(publisher -> name.equals(publisher.getName()))
				.collect(Collectors.toList());
	}

	/**
	 * A helper method to simplify {@link getPublishersMatching}.
	 *
	 * @param publisher a publisher
	 * @param pattern   a pattern, in which all letters are lowercase
	 * @return whether the publisher's name, address, or phone number match
	 */
	private static boolean publisherMatches(final Publisher publisher,
			final String pattern) {
		return publisher.getName().toLowerCase().contains(pattern)
				|| publisher.getAddress().toLowerCase().contains(pattern)
				|| publisher.getPhone().toLowerCase().contains(pattern);
	}

	@Override
	public List<Publisher> getPublishersMatching(final String pattern,
			final boolean global) throws IOException {
		if (global) {
			return publisherDAO.findAll().stream().filter(
					publisher -> publisherMatches(publisher, pattern.toLowerCase()))
					.collect(Collectors.toList());
		} else {
			return publisherDAO.findAll().stream()
					.filter(publisher -> publisher.getName().toLowerCase()
							.contains(pattern.toLowerCase()))
					.collect(Collectors.toList());
		}
	}

	@Override
	public List<Book> getAllBooks() throws IOException {
		return bookDAO.findAll();
	}

	@Override
	public List<Book> getBooksWithTitle(final String title) throws IOException {
		return bookDAO.findAll().stream().filter(book -> title.equals(book.getTitle()))
				.collect(Collectors.toList());
	}

	@Override
	public List<Book> getBooksByAuthor(final Author author) throws IOException {
		return bookDAO.findAll().stream().filter(book -> author.equals(book.getAuthor()))
				.collect(Collectors.toList());
	}

	@Override
	public List<Book> getBooksPublishedBy(final Publisher publisher) throws IOException {
		return bookDAO.findAll().stream()
				.filter(book -> publisher.equals(book.getPublisher()))
				.collect(Collectors.toList());
	}

	/**
	 * A helper method to simplify {@link getBooksMatching}.
	 *
	 * @param book    a book
	 * @param pattern a pattern, in which all letters are lowercase
	 * @return whether the book's title or ISBN contain the pattern,
	 *         case-insensitively
	 */
	private static boolean bookMatchesSimple(final Book book, final String pattern) {
		return book.getTitle().toLowerCase().contains(pattern)
				|| book.getIsbn().toLowerCase().contains(pattern);
	}

	/**
	 * A helper method to simplify {@link getBooksMatching}.
	 *
	 * @param book    a book
	 * @param pattern a pattern, in which all letters are lowercase
	 * @return whether the book's title, its isbn, the name of its author, or the
	 *         name of its publisher contain the pattern, case-insensitively.
	 */
	private static boolean bookMatches(final Book book, final String pattern) {
		return bookMatchesSimple(book, pattern)
				|| book.getAuthor().getName().toLowerCase().contains(pattern)
				|| book.getPublisher().getName().toLowerCase().contains(pattern);
	}

	@Override
	public List<Book> getBooksMatching(final String pattern, final boolean global)
			throws IOException {
		if (global) {
			return bookDAO.findAll().stream()
					.filter(book -> bookMatches(book, pattern.toLowerCase()))
					.collect(Collectors.toList());
		} else {
			return bookDAO.findAll().stream()
					.filter(book -> bookMatchesSimple(book, pattern.toLowerCase()))
					.collect(Collectors.toList());
		}
	}

	@Override
	public void updateAuthor(final Author author) throws IOException {
		authorDAO.save(author);
	}

	@Override
	public void updatePublisher(final Publisher publisher) throws IOException {
		publisherDAO.save(publisher);
	}

	@Override
	public void updateBook(final Book book) throws IOException {
		if (!getAllAuthors().contains(book.getAuthor())) {
			authorDAO.save(book.getAuthor());
		}
		if (!getAllPublishers().contains(book.getPublisher())) {
			publisherDAO.save(book.getPublisher());
		}
		bookDAO.save(book);
	}

	@Override
	public void deleteAuthor(final Author author) throws IOException {
		final List<Book> writtenBooks = getAllBooks().stream()
				.filter(book -> author.equals(book.getAuthor()))
				.collect(Collectors.toList());
		// Can't use Stream.forEach() because delete() may throw
		for (final Book book : writtenBooks) {
			bookDAO.delete(book);
		}
		authorDAO.delete(author);
	}

	@Override
	public void removePublisher(final Publisher publisher) throws IOException {
		final List<Book> publishedBooks = getAllBooks().stream()
				.filter(book -> publisher.equals(book.getPublisher()))
				.collect(Collectors.toList());
		// Can't use Stream.forEach() because delete() may throw
		for (final Book book : publishedBooks) {
			bookDAO.delete(book);
		}
		publisherDAO.delete(publisher);
	}

	@Override
	public void removeBook(final Book book) throws IOException {
		bookDAO.delete(book); // TODO: check it matches beyond ID?
	}

	@Override
	public Optional<Author> getAuthorByID(final long id) throws IOException {
		return authorDAO.find(id);
	}

	@Override
	public Optional<Publisher> getPublisherByID(final long id) throws IOException {
		return publisherDAO.find(id);
	}

	@Override
	public Optional<Book> getBookByID(final long id) throws IOException {
		return bookDAO.find(id);
	}
}
