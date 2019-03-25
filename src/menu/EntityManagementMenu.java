package menu;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import model.Author;
import model.Book;
import model.Publisher;
import service.LibraryService;

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
		return inStream.nextLine();
	}

	private void println(final String line) throws IOException {
		outStream.append(line);
		outStream.append(System.lineSeparator());
	}

	private void printBook(final Book book) throws IOException {
		outStream.append("Title:\t");
		println(book.getTitle());
		outStream.append("Author:\t)");
		println(book.getAuthor().getName());
		outStream.append("Publisher:\t");
		println(book.getPublisher().getName());
		outStream.append("ISBN:\t");
		println(book.getIsbn());
		outStream.append("Record ID:\t");
		println(Long.toString(book.getId()));
		println("");
	}

	private void retrieveBook() throws IOException {
		final String input = getInputLine(
				"ID of or search term for book to retrieve (-1 for all):").trim();
		if (numericPattern.test(input)) {
			final int id = Integer.parseInt(input);
			if (id < 0) {
				for (final Book book : service.getAllBooks()) {
					printBook(book);
				}
			} else {
				final Optional<Book> book = service.getBookByID(id);
				if (book.isPresent()) {
					printBook(book.get());
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
					printBook(book);
				}
			}
		}
	}

	private void printAuthor(final Author author) throws IOException {
		outStream.append("Name:\t");
		println(author.getName());
		outStream.append("Record ID:\t");
		println(Long.toBinaryString(author.getId()));
		println("");
	}

	private void retrieveAuthor() throws IOException {
		final String input = getInputLine(
				"ID of or search term for author to retrieve (-1 for all):").trim();
		if (numericPattern.test(input)) {
			final int id = Integer.parseInt(input);
			if (id < 0) {
				for (final Author author : service.getAllAuthors()) {
					printAuthor(author);
				}
			} else {
				final Optional<Author> author = service.getAuthorByID(id);
				if (author.isPresent()) {
					printAuthor(author.get());
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
					printAuthor(author);
				}
			}
		}
	}

	private void printPublisher(final Publisher publisher) throws IOException {
		outStream.append("Name:\t");
		println(publisher.getName());
		outStream.append("Phone:\t");
		println(publisher.getPhone());
		outStream.append("Address:\t");
		println(publisher.getAddress());
		outStream.append("Record ID:\t");
		println(Long.toString(publisher.getId()));
		println("");
	}

	private void retrievePublisher() throws IOException {
		final String input = getInputLine("ID of publisher to retrieve (-1 for all):")
				.trim();
		if (numericPattern.test(input)) {
			final int id = Integer.parseInt(input);
			if (id < 0) {
				for (final Publisher publisher : service.getAllPublishers()) {
					printPublisher(publisher);
				}
			} else {
				final Optional<Publisher> publisher = service.getPublisherByID(id);
				if (publisher.isPresent()) {
					printPublisher(publisher.get());
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
					printPublisher(publisher);
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
		try { // TODO: allow searching by title, e.g.
			final String input = getInputLine("ID of book to remove (-1 for all):")
					.trim();
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
		} catch (final NumberFormatException except) {
			println("ID must be an integer");
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	private void removeAuthor() throws IOException {
		try { // TODO: allow searching by other fields
			final String input = getInputLine("ID of author to remove (-1 for all):")
					.trim();
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
		} catch (final NumberFormatException except) {
			println("ID must be an integer");
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	private void removePublisher() throws IOException {
		try { // TODO: allow searching by other fields
			final String input = getInputLine("ID of publisher to remove (-1 for all):")
					.trim();
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
		} catch (final NumberFormatException except) {
			println("ID must be an integer");
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
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

	private Author createAuthor() throws IOException, NumberFormatException {
		return service.createAuthor(getInputLine("Name of new author:"));
	}

	private Publisher createPublisher() throws IOException, NumberFormatException {
		return service.createPublisher(getInputLine("Name of new publisher:"),
				getInputLine("Publisher address:"), getInputLine("Publisher phone:"));
	}

	private void addBook() throws IOException {
		try {
			final long authorID = Long.parseLong(
					getInputLine("ID of author of book (-1 to add new):").trim());
			final Author author;
			if (authorID < 0) {
				author = createAuthor();
			} else {
				author = service.getAuthorByID(authorID).get();
			}
			final long publisherID = Long.parseLong(
					getInputLine("ID of publisher of book (-1 to add new):").trim());
			final Publisher publisher;
			if (publisherID < 0) {
				publisher = createPublisher();
			} else {
				publisher = service.getPublisherByID(publisherID).get();
			}
			service.createBook(getInputLine("Title of new book:"),
					getInputLine("ISBN of new book:").trim(), author, publisher);
		} catch (final NumberFormatException except) {
			println("ID must be an integer");
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	private void addAuthor() throws IOException {
		try {
			createAuthor();
		} catch (final NumberFormatException except) {
			println("ID must be an integer");
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	private void addPublisher() throws IOException {
		try {
			createPublisher();
		} catch (final NumberFormatException except) {
			println("ID must be an integer");
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
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

	private void updateBook() throws IOException {
		try { // TODO: allow searching by title, e.g.
			final String input = getInputLine("ID of book to update:").trim();
			final int id = Integer.parseInt(input);
			if (id < 0) {
				println("ID must not be negative");
			} else {
				final Optional<Book> book = service.getBookByID(id);
				if (book.isPresent()) {
					// TODO: ask user for changes to its data
					service.updateBook(book.get());
				} else {
					println("No record for that ID");
				}
			}
		} catch (final NumberFormatException except) {
			println("ID must be an integer");
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	private void updateAuthor() throws IOException {
		try { // TODO: allow searching by other fields
			final String input = getInputLine("ID of author to update:").trim();
			final int id = Integer.parseInt(input);
			if (id < 0) {
				println("ID must not be negative");
			} else {
				final Optional<Author> author = service.getAuthorByID(id);
				if (author.isPresent()) {
					// TODO: ask user for changes to its data
					service.updateAuthor(author.get());
				} else {
					println("No record for that ID");
				}
			}
		} catch (final NumberFormatException except) {
			println("ID must be an integer");
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	private void updatePublisher() throws IOException {
		try { // TODO: allow searching by other fields
			final String input = getInputLine("ID of publisher to update:").trim();
			final int id = Integer.parseInt(input);
			if (id < 0) {
				println("ID must not be negative");
			} else {
				final Optional<Publisher> publisher = service.getPublisherByID(id);
				if (publisher.isPresent()) {
					// TODO: ask user for changes to its data
					service.updatePublisher(publisher.get());
				} else {
					println("No record for that ID");
				}
			}
		} catch (final NumberFormatException except) {
			println("ID must be an integer");
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
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

	public void mainMenu() {
		try {
			final String command = getInputLine(
					"Add, Update, Delete, or Retrieve an entity?").trim().toLowerCase();
			switch (command) {
			case "add": case "a":
				add();
				break;
			case "update": case "u":
				update();
				break;
			case "delete": case "d": case "remove":
				remove();
				break;
			case "retrieve": case "r": case "get":
				retrieve();
				break;
			default:
				println("Unsupported command");
				break;
			}
		} catch (final IOException except) {
			LOGGER.log(Level.SEVERE, "I/O error", except);
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
