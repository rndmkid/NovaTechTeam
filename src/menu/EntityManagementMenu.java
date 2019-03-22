package menu;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
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

	private String getInputWord(final String prompt) throws IOException {
		outStream.append(prompt);
		if (!prompt.isEmpty()
				&& !Character.isWhitespace(prompt.charAt(prompt.length() - 1))) {
			outStream.append(' ');
		}
		return inStream.next();
	}

	private void retrieveBook() throws IOException {
		try { // TODO: allow searching by title, e.g.
			final String input = getInputWord("ID of book to retrieve (-1 for all):")
					.trim();
			final int id = Integer.parseInt(input);
			if (id < 0) {
				for (final Book book : service.getAllBooks()) {
					outStream.append(book.toString()); // TODO: Don't just rely on
														// toString()
					outStream.append(System.lineSeparator());
				}
			} else {
				outStream.append(service.getBookByID(id).toString());
				outStream.append(System.lineSeparator());
			}
		} catch (final NumberFormatException except) {
			outStream.append("ID must be an integer");
			outStream.append(System.lineSeparator());
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	private void retrieveAuthor() throws IOException {
		try {
			final String input = getInputWord("ID of author to retrieve (-1 for all):")
					.trim();
			final int id = Integer.parseInt(input);
			if (id < 0) {
				for (final Author author : service.getAllAuthors()) {
					outStream.append(author.toString()); // TODO: Don't just rely on
															// toString()
					outStream.append(System.lineSeparator());
				}
			} else {
				outStream.append(service.getAuthorByID(id).toString());
				outStream.append(System.lineSeparator());
			}
		} catch (final NumberFormatException except) {
			outStream.append("ID must be an integer");
			outStream.append(System.lineSeparator());
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	private void retrievePublisher() throws IOException {
		try {
			final String input = getInputWord("ID of publisher to retrieve (-1 for all):")
					.trim();
			final int id = Integer.parseInt(input);
			if (id < 0) {
				for (final Publisher publisher : service.getAllPublishers()) {
					outStream.append(publisher.toString()); // TODO: Don't just rely on
															// toString()
					outStream.append(System.lineSeparator());
				}
			} else {
				outStream.append(service.getPublisherByID(id).toString());
				outStream.append(System.lineSeparator());
			}
		} catch (final NumberFormatException except) {
			outStream.append("ID must be an integer");
			outStream.append(System.lineSeparator());
			LOGGER.log(Level.FINER, "Failed to parse integer from input", except);
		}
	}

	public void retrieve() throws IOException {
		final String kind = getInputWord("Kind of entity to retrieve:").trim()
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

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		inStream.close();
		if (outStream instanceof Closeable) {
			((Closeable) outStream).close();
		}
	}
}
