package menu;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import dao.AuthorDataAccessObject;
import dao.BookDataAccessObject;
import dao.DataAccessObject;
import dao.PublisherDataAccessObject;
import model.Author;
import model.Book;
import model.Publisher;
import service.LibraryService;
import service.LibraryServiceImpl;

/**
 * A command-line application to let the user manage the database.
 *
 * @author Jonathan Lovelace
 *
 */
public final class EntityManagementMenu {
	private static final Logger LOGGER = Logger
			.getLogger(EntityManagementMenu.class.getName());
	private final LibraryService service;
	private final Scanner inStream;
	private final Appendable outStream;
	private static final Predicate<String> numericPattern = Pattern.compile("-?\\d+")
			.asPredicate();

	public EntityManagementMenu(final Reader in, final Appendable out,
			final LibraryService service) {
		this.service = service;
		inStream = new Scanner(in);
		outStream = out;
	}

	private String getInputLine(final String prompt) throws IOException {
		outStream.append(prompt);
		if (!prompt.isEmpty()
				&& !Character.isWhitespace(prompt.charAt(prompt.length() - 1))) {
			outStream.append(' ');
		}
		return Optional.ofNullable(inStream.nextLine()).orElse("");
	}

	private void println(final String line) throws IOException {
		outStream.append(line);
		outStream.append(System.lineSeparator());
	}

	private void printBook(final Book book, final boolean includeID) throws IOException {
		outStream.append("Title:\t");
		println(book.getTitle());
		outStream.append("Author:\t)");
		println(book.getAuthor().getName());
		outStream.append("Publisher:\t");
		println(book.getPublisher().getName());
		outStream.append("ISBN:\t");
		println(book.getIsbn());
		if (includeID) {
			outStream.append("Record ID:\t");
			println(Long.toString(book.getId()));
		}
		println("");
	}

	private void retrieveBook() throws IOException {
		final String input = getInputLine(
				"ID of or search term for book to retrieve (-1 for all):").trim();
		if (numericPattern.test(input)) {
			final int id = Integer.parseInt(input);
			if (id < 0) {
				for (final Book book : service.getAllBooks()) {
					printBook(book, true);
				}
			} else {
				final Optional<Book> book = service.getBookByID(id);
				if (book.isPresent()) {
					printBook(book.get(), true);
				} else {
					println("No record for that ID");
				}
			}
		} else {
			final List<Book> matchingBooks = service.getBooksMatching(input, true);
			if (matchingBooks.isEmpty()) {
				println("No books matched your search");
			} else {
				println("Books matching your search:");
				for (final Book book : matchingBooks) {
					printBook(book, true);
				}
			}
		}
	}

	private void printAuthor(final Author author, final boolean printID) throws IOException {
		outStream.append("Name:\t");
		println(author.getName());
		if (printID) {
			outStream.append("Record ID:\t");
			println(Long.toBinaryString(author.getId()));
		}
		println("");
	}

	private void retrieveAuthor() throws IOException {
		final String input = getInputLine(
				"ID of or search term for author to retrieve (-1 for all):").trim();
		if (numericPattern.test(input)) {
			final int id = Integer.parseInt(input);
			if (id < 0) {
				for (final Author author : service.getAllAuthors()) {
					printAuthor(author, true);
				}
			} else {
				final Optional<Author> author = service.getAuthorByID(id);
				if (author.isPresent()) {
					printAuthor(author.get(), true);
				} else {
					println("No record for that ID");
				}
			}
		} else {
			final List<Author> matchingAuthors = service.getAuthorsMatching(input);
			if (matchingAuthors.isEmpty()) {
				println("No authors matched your search");
			} else {
				println("Authors matching your search:");
				for (final Author author : matchingAuthors) {
					printAuthor(author, true);
				}
			}
		}
	}

	private void printPublisher(final Publisher publisher, final boolean printID) throws IOException {
		outStream.append("Name:\t");
		println(publisher.getName());
		outStream.append("Phone:\t");
		println(publisher.getPhone());
		outStream.append("Address:\t");
		println(publisher.getAddress());
		if (printID) {
			outStream.append("Record ID:\t");
			println(Long.toString(publisher.getId()));
		}
		println("");
	}

	private void retrievePublisher() throws IOException {
		final String input = getInputLine(
				"ID of or search term for publisher to retrieve (-1 for all):").trim();
		if (numericPattern.test(input)) {
			final int id = Integer.parseInt(input);
			if (id < 0) {
				for (final Publisher publisher : service.getAllPublishers()) {
					printPublisher(publisher, true);
				}
			} else {
				final Optional<Publisher> publisher = service.getPublisherByID(id);
				if (publisher.isPresent()) {
					printPublisher(publisher.get(), true);
				} else {
					println("No record for that ID");
				}
			}
		} else {
			final List<Publisher> matchingPublishers = service.getPublishersMatching(input, true);
			if (matchingPublishers.isEmpty()) {
				println("No publishers matched your search");
			} else {
				println("Publishers matching your search:");
				for (final Publisher publisher : matchingPublishers) {
					printPublisher(publisher, true);
				}
			}
		}
	}

	public void retrieve() throws IOException {
		final String kind = getInputLine("Kind of entity to retrieve:").trim()
				.toLowerCase();
		switch (kind) {
		case "book": case "b":
			retrieveBook();
			break;
		case "author": case "a":
			retrieveAuthor();
			break;
		case "publisher": case "p":
			retrievePublisher();
			break;
		default:
			println("Entity must be a book, author, or publisher.");
			break;
		}
	}

	private void removeBook() throws IOException {
		final String input = getInputLine(
				"ID of or search term for book to remove (-1 for all):").trim();
		if (numericPattern.test(input)) {
			final int id = Integer.parseInt(input);
			if (id < 0) {
				for (final Book book : service.getAllBooks()) {
					service.removeBook(book);
				}
			} else {
				final Optional<Book> book = service.getBookByID(id);
				if (book.isPresent()) {
					service.removeBook(book.get());
				} else {
					println("No book with that ID");
				}
			}
		} else {
			final List<Book> matchingBooks = service.getBooksMatching(input, true);
			if (matchingBooks.isEmpty()) {
				println("No books matched your search");
			} else {
				for (final Book book : matchingBooks) {
					service.removeBook(book);
				}
				outStream.append("Removed ");
				outStream.append(Integer.toString(matchingBooks.size()));
				println(" books matching your search");
			}
		}
	}

	private void removeAuthor() throws IOException {
		final String input = getInputLine(
				"ID of or search term for author to remove (-1 for all):").trim();
		if (numericPattern.test(input)) {
			final int id = Integer.parseInt(input);
			if (id < 0) {
				for (final Author author : service.getAllAuthors()) {
					service.deleteAuthor(author);
				}
			} else {
				final Optional<Author> author = service.getAuthorByID(id);
				if (author.isPresent()) {
					service.deleteAuthor(author.get());
				} else {
					println("No author with that ID");
				}
			}
		} else {
			final List<Author> matchingAuthors = service.getAuthorsMatching(input);
			if (matchingAuthors.isEmpty()) {
				println("No authors matched your search");
			} else {
				int bookCount = 0;
				for (final Author author : matchingAuthors) {
					bookCount += service.getBooksByAuthor(author).size();
					service.deleteAuthor(author);
				}
				outStream.append("Removed ");
				outStream.append(Integer.toString(matchingAuthors.size()));
				outStream.append(" authors and their ");
				outStream.append(Integer.toString(bookCount));
				println(" books.");
			}
		}
	}

	private void removePublisher() throws IOException {
		final String input = getInputLine(
				"ID of or search term for publisher to remove (-1 for all):").trim();
		if (numericPattern.test(input)) {
			final int id = Integer.parseInt(input);
			if (id < 0) {
				for (final Publisher publisher : service.getAllPublishers()) {
					service.removePublisher(publisher);
				}
			} else {
				final Optional<Publisher> publisher = service.getPublisherByID(id);
				if (publisher.isPresent()) {
					service.removePublisher(publisher.get());
				}
			}
		} else {
			final List<Publisher> matchingPublishers = service.getPublishersMatching(input, true);
			if (matchingPublishers.isEmpty()) {
				println("No publishers matched your search");
			} else {
				int bookCount = 0;
				for (final Publisher publisher : matchingPublishers) {
					bookCount += service.getBooksPublishedBy(publisher).size();
					service.removePublisher(publisher);
				}
				outStream.append("Removed ");
				outStream.append(Integer.toString(matchingPublishers.size()));
				outStream.append(" publishers and their ");
				outStream.append(Integer.toString(bookCount));
				println(" books.");
			}
		}
	}

	public void remove() throws IOException {
		final String kind = getInputLine("Kind of entity to remove:").trim()
				.toLowerCase();
		switch (kind) {
		case "book": case "b":
			removeBook();
			break;
		case "author": case "a":
			removeAuthor();
			break;
		case "publisher": case "p":
			removePublisher();
			break;
		default:
			println("Entity must be a book, author, or publisher.");
			break;
		}
	}

	private Author createAuthor() throws IOException {
		return service.createAuthor(getInputLine("Name of new author:"));
	}

	private Publisher createPublisher() throws IOException {
		return service.createPublisher(getInputLine("Name of new publisher:"),
				getInputLine("Publisher address:"), getInputLine("Publisher phone:"));
	}

	private void addBook() throws IOException {
		try {
			final String authorString = getInputLine("ID or name of author of book")
					.trim();
			Author author;
			if (numericPattern.test(authorString)) {
				final Optional<Author> temp = service
						.getAuthorByID(Long.parseLong(authorString));
				if (temp.isPresent()) {
					author = temp.get();
				} else {
					println("No author with that ID");
					return;
				}
			} else {
				final List<Author> matching = service.getAuthorsNamed(authorString);
				if (matching.isEmpty()) {
					author = service.createAuthor(authorString);
				} else {
					author = matching.get(0);
				}
			}
			final String publisherString = getInputLine(
					"ID or name of publisher of book (-1 to add new):").trim();
			Publisher publisher;
			if (numericPattern.test(publisherString)) {
				final Optional<Publisher> temp = service
						.getPublisherByID(Long.parseLong(publisherString));
				if (temp.isPresent()) {
					publisher = temp.get();
				} else {
					println("No publisher with that ID");
					return;
				}
			} else {
				final List<Publisher> matching = service.getPublishersNamed(publisherString);
				if (matching.isEmpty()) {
					publisher = service.createPublisher(publisherString);
				} else {
					publisher = matching.get(0);
				}
			}
			service.createBook(getInputLine("Title of new book:"),
					getInputLine("ISBN of new book:").trim(), author, publisher);
		} catch (final NumberFormatException except) {
			println("ID must be an integer");
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	private void addAuthor() throws IOException {
		createAuthor();
	}

	private void addPublisher() throws IOException {
		createPublisher();
	}

	public void add() throws IOException {
		final String kind = getInputLine("Kind of entity to add").trim().toLowerCase();
		switch (kind) {
		case "book": case "b":
			addBook();
			break;
		case "author": case "a":
			addAuthor();
			break;
		case "publisher": case "p":
			addPublisher();
			break;
		default:
			println("Entity must be a book, author, or publisher.");
			break;
		}
	}

	private void updateIndividualBook(final Book book) throws IOException {
		println("Current contents of the record:");
		printBook(book, false);
		println("New values (blank to leave existing values):");
		final String title = getInputLine("Title:\t").trim();
		if (!title.isEmpty()) {
			book.setTitle(title);
		}
		final String authorName = getInputLine("Author:\t)").trim();
		if (!authorName.isEmpty()) {
			final List<Author> authors = service.getAuthorsNamed(authorName);
			if (authors.isEmpty()) {
				book.setAuthor(service.createAuthor(authorName));
			} else {
				book.setAuthor(authors.get(0));
			}
		}
		final String publisherName = getInputLine("Publisher:\t").trim();
		if (!publisherName.isEmpty()) {
			final List<Publisher> publishers = service.getPublishersNamed(publisherName);
			if (publishers.isEmpty()) {
				book.setPublisher(service.createPublisher(publisherName));
			} else {
				book.setPublisher(publishers.get(0));
			}
		}
		final String isbn = getInputLine("ISBN:\t");
		if (!isbn.isEmpty()) {
			book.setIsbn(isbn);
		}
		service.updateBook(book);
		println("");
	}

	private void updateBook() throws IOException {
		final String input = getInputLine(
				"ID of or search term for book to update (-1 for all):").trim();
		if (numericPattern.test(input)) {
			final int id = Integer.parseInt(input);
			if (id < 0) {
				final List<Book> books = service.getAllBooks();
				if (books.isEmpty()) {
					println("No books in the database");
				} else {
					for (final Book book : books) {
						updateIndividualBook(book);
					}
				}
			} else {
				final Optional<Book> book = service.getBookByID(id);
				if (book.isPresent()) {
					updateIndividualBook(book.get());
				} else {
					println("No record for that ID");
				}
			}
		} else {
			final List<Book> matchingBooks = service.getBooksMatching(input, true);
			if (matchingBooks.isEmpty()) {
				println("No books matched your search");
			} else {
				for (final Book book : matchingBooks) {
					updateIndividualBook(book);
				}
			}
		}
	}

	private void updateIndividualAuthor(final Author author) throws IOException {
		outStream.append("Author's current name:\t");
		println(author.getName());
		final String name = getInputLine("New name (blank to leave unchanged):\t").trim();
		if (!name.isEmpty()) {
			author.setName(name);
		}
		service.updateAuthor(author);
		println("");
	}

	private void updateAuthor() throws IOException {
		final String input = getInputLine(
				"ID of or search term for author to update (-1 for all):").trim();
		if (numericPattern.test(input)) {
			final int id = Integer.parseInt(input);
			if (id < 0) {
				final List<Author> authors = service.getAllAuthors();
				if (authors.isEmpty()) {
					println("No authors in the database");
				} else {
					for (final Author author : authors) {
						updateIndividualAuthor(author);
					}
				}
			} else {
				final Optional<Author> author = service.getAuthorByID(id);
				if (author.isPresent()) {
					updateIndividualAuthor(author.get());
				} else {
					println("No record for that ID");
				}
			}
		} else {
			final List<Author> matchingAuthors = service.getAuthorsMatching(input);
			if (matchingAuthors.isEmpty()) {
				println("No authors matched your search");
			} else {
				for (final Author author : matchingAuthors) {
					updateIndividualAuthor(author);
				}
			}
		}
	}

	private void updateIndividualPublisher(final Publisher publisher) throws IOException {
		println("Current contents of the record:");
		printPublisher(publisher, false);
		println("New values (blank to leave unchanged):");
		final String name = getInputLine("Name:\t").trim();
		if (!name.isEmpty()) {
			publisher.setName(name);
		}
		final String phone = getInputLine("Phone:\t");
		if (!phone.isEmpty()) {
			publisher.setPhone(phone);
		}
		final String address = getInputLine("Address:\t").trim();
		if (!address.isEmpty()) {
			publisher.setAddress(address);
		}
		service.updatePublisher(publisher);
		println("");
	}

	private void updatePublisher() throws IOException {
		final String input = getInputLine(
				"ID of or search term for publisher to remove (-1 for all):").trim();
		if (numericPattern.test(input)) {
			final int id = Integer.parseInt(input);
			if (id < 0) {
				final List<Publisher> publishers = service.getAllPublishers();
				if (publishers.isEmpty()) {
					println("No publishers in the database");
				} else {
					for (final Publisher publisher : publishers) {
						updateIndividualPublisher(publisher);
					}
				}
			} else {
				final Optional<Publisher> publisher = service.getPublisherByID(id);
				if (publisher.isPresent()) {
					updateIndividualPublisher(publisher.get());
				} else {
					println("No record for that ID");
				}
			}
		} else {
			final List<Publisher> matchingPublishers = service.getPublishersMatching(input, true);
			if (matchingPublishers.isEmpty()) {
				println("No publishers matched your search");
			} else {
				for (final Publisher publisher : matchingPublishers) {
					updateIndividualPublisher(publisher);
				}
			}
		}
	}

	public void update() throws IOException {
		final String kind = getInputLine("Kind of entity to add").trim().toLowerCase();
		switch (kind) {
		case "book": case "b":
			updateBook();
			break;
		case "author": case "a":
			updateAuthor();
			break;
		case "publisher": case "p":
			updatePublisher();
			break;
		default:
			println("Entity must be a book, author, or publisher.");
			break;
		}
	}

	/**
	 * Ask for and handle a command; return false on I/O error or if the user says
	 * "Quit."
	 */
	public boolean mainMenu() {
		try {
			final String command = getInputLine(
					"Add, Update, Delete, or Retrieve an entity, or Quit?").trim()
							.toLowerCase();
			switch (command) {
			case "add": case "a":
				add();
				return true;
			case "update": case "u":
				update();
				return true;
			case "delete": case "d": case "remove":
				remove();
				return true;
			case "retrieve": case "r": case "get":
				retrieve();
				return true;
			case "quit": case "q": case "exit":
				return false;
			default:
				println("Unsupported command");
				return true;
			}
		} catch (final IOException except) {
			LOGGER.log(Level.SEVERE, "I/O error", except);
			return false;
		}
	}

	public static void main(final String... args) {
		String basePath;
		if (args.length == 0) {
			basePath = ".";
		} else {
			basePath = args[0];
		}
		final DataAccessObject<Author> authorDAO = new AuthorDataAccessObject(basePath + "/authors.csv");
		final DataAccessObject<Publisher> publisherDAO = new PublisherDataAccessObject(basePath + "/publishers.csv");
		final DataAccessObject<Book> bookDAO = new BookDataAccessObject(basePath + "/books.csv", authorDAO, publisherDAO);
		try (InputStreamReader in = new InputStreamReader(System.in)) {
			final EntityManagementMenu menu = new EntityManagementMenu(in, System.out,
					new LibraryServiceImpl(bookDAO, authorDAO, publisherDAO));
			while (menu.mainMenu()) {}
		} catch (final IOException except) {
			System.err.println("I/O error dealing with standard input stream");
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		inStream.close();
		if (outStream instanceof Closeable) {
			((Closeable) outStream).close();
		}
	}
}
