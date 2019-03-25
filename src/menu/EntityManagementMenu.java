package menu;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	private void retrieveBook() throws IOException {
		try { // TODO: allow searching by title, e.g.
			final String input = getInputLine("ID of book to retrieve (-1 for all):")
					.trim();
			final int id = Integer.parseInt(input);
			if (id < 0) {
				for (final Book book : service.getAllBooks()) {
					outStream.append(book.toString()); // TODO: Don't just rely on
														// toString()
					outStream.append(System.lineSeparator());
				}
			} else {
				final Optional<Book> book = service.getBookByID(id);
				if (book.isPresent()) {
					outStream.append(book.toString());
					outStream.append(System.lineSeparator());
				} else {
					outStream.append("No record for that ID");
					outStream.append(System.lineSeparator());
				}
			}
		} catch (final NumberFormatException except) {
			outStream.append("ID must be an integer");
			outStream.append(System.lineSeparator());
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	private void retrieveAuthor() throws IOException {
		try {
			final String input = getInputLine("ID of author to retrieve (-1 for all):")
					.trim();
			final int id = Integer.parseInt(input);
			if (id < 0) {
				for (final Author author : service.getAllAuthors()) {
					outStream.append(author.toString()); // TODO: Don't just rely on
															// toString()
					outStream.append(System.lineSeparator());
				}
			} else {
				final Optional<Author> author = service.getAuthorByID(id);
				if (author.isPresent()) {
					outStream.append(author.get().toString());
					outStream.append(System.lineSeparator());
				} else {
					outStream.append("No record for that ID");
					outStream.append(System.lineSeparator());
				}
			}
		} catch (final NumberFormatException except) {
			outStream.append("ID must be an integer");
			outStream.append(System.lineSeparator());
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	private void retrievePublisher() throws IOException {
		try {
			final String input = getInputLine("ID of publisher to retrieve (-1 for all):")
					.trim();
			final int id = Integer.parseInt(input);
			if (id < 0) {
				for (final Publisher publisher : service.getAllPublishers()) {
					outStream.append(publisher.toString()); // TODO: Don't just rely on
															// toString()
					outStream.append(System.lineSeparator());
				}
			} else {
				final Optional<Publisher> publisher = service.getPublisherByID(id);
				if (publisher.isPresent()) {
					outStream.append(publisher.toString());
					outStream.append(System.lineSeparator());
				} else {
					outStream.append("No record for that ID");
					outStream.append(System.lineSeparator());
				}
			}
		} catch (final NumberFormatException except) {
			outStream.append("ID must be an integer");
			outStream.append(System.lineSeparator());
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
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
			outStream.append("Entity must be a book, author, or publisher.");
			outStream.append(System.lineSeparator());
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
					outStream.append("No book with that ID");
					outStream.append(System.lineSeparator());
				}
			}
		} catch (final NumberFormatException except) {
			outStream.append("ID must be an integer");
			outStream.append(System.lineSeparator());
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
					outStream.append("No author with that ID");
					outStream.append(System.lineSeparator());
				}
			}
		} catch (final NumberFormatException except) {
			outStream.append("ID must be an integer");
			outStream.append(System.lineSeparator());
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
			outStream.append("ID must be an integer");
			outStream.append(System.lineSeparator());
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
			outStream.append("Entity must be a book, author, or publisher.");
			outStream.append(System.lineSeparator());
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
			outStream.append("ID must be an integer");
			outStream.append(System.lineSeparator());
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	private void addAuthor() throws IOException {
		try {
			createAuthor();
		} catch (final NumberFormatException except) {
			outStream.append("ID must be an integer");
			outStream.append(System.lineSeparator());
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	private void addPublisher() throws IOException {
		try {
			createPublisher();
		} catch (final NumberFormatException except) {
			outStream.append("ID must be an integer");
			outStream.append(System.lineSeparator());
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
			outStream.append("Entity must be a book, author, or publisher.");
			outStream.append(System.lineSeparator());
			break;
		}
	}

	private void updateBook() throws IOException {
		try { // TODO: allow searching by title, e.g.
			final String input = getInputLine("ID of book to update:").trim();
			final int id = Integer.parseInt(input);
			if (id < 0) {
				outStream.append("ID must not be negative");
				outStream.append(System.lineSeparator());
			} else {
				final Optional<Book> book = service.getBookByID(id);
				if (book.isPresent()) {
					// TODO: ask user for changes to its data
					service.updateBook(book.get());
				} else {
					outStream.append("No record for that ID");
					outStream.append(System.lineSeparator());
				}
			}
		} catch (final NumberFormatException except) {
			outStream.append("ID must be an integer");
			outStream.append(System.lineSeparator());
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	private void updateAuthor() throws IOException {
		try { // TODO: allow searching by other fields
			final String input = getInputLine("ID of author to update:").trim();
			final int id = Integer.parseInt(input);
			if (id < 0) {
				outStream.append("ID must not be negative");
				outStream.append(System.lineSeparator());
			} else {
				final Optional<Author> author = service.getAuthorByID(id);
				if (author.isPresent()) {
					// TODO: ask user for changes to its data
					service.updateAuthor(author.get());
				} else {
					outStream.append("No record for that ID");
					outStream.append(System.lineSeparator());
				}
			}
		} catch (final NumberFormatException except) {
			outStream.append("ID must be an integer");
			outStream.append(System.lineSeparator());
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	private void updatePublisher() throws IOException {
		try { // TODO: allow searching by other fields
			final String input = getInputLine("ID of publisher to update:").trim();
			final int id = Integer.parseInt(input);
			if (id < 0) {
				outStream.append("ID must not be negative");
				outStream.append(System.lineSeparator());
			} else {
				final Optional<Publisher> publisher = service.getPublisherByID(id);
				if (publisher.isPresent()) {
					// TODO: ask user for changes to its data
					service.updatePublisher(publisher.get());
				} else {
					outStream.append("No record for that ID");
					outStream.append(System.lineSeparator());
				}
			}
		} catch (final NumberFormatException except) {
			outStream.append("ID must be an integer");
			outStream.append(System.lineSeparator());
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
			outStream.append("Entity must be a book, author, or publisher.");
			outStream.append(System.lineSeparator());
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
				outStream.append("Unsupported command");
				outStream.append(System.lineSeparator());
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
