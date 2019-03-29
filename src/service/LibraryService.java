package service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import model.Author;
import model.Book;
import model.Publisher;

/**
 * An interface for "service classes" that use data-access objects to interact
 * with the on-disk (or other master) database while maintaining data
 * consistency.
 *
 * @author Jonathan Lovelace
 *
 */
public interface LibraryService {
	/**
	 * Create a new Author with the given name, add it to the database, and return
	 * it. Its ID is assigned to not collide with any existing Author in the
	 * database.
	 *
	 * @param authorName the name of the author
	 * @return the newly created author
	 * @throws IOException on I/O error while interacting with the database
	 */
	Author createAuthor(String authorName) throws IOException;

	/**
	 * Create a new Publisher with the given name, add it to the database, and
	 * return it. Its ID is assigned to not collide with any existing Publisher in
	 * the database.
	 *
	 * @param publisherName the name of the publisher
	 * @return the newly created publisher
	 * @throws IOException on I/O error while interacting with the database
	 */
	default Publisher createPublisher(final String publisherName) throws IOException {
		return createPublisher(publisherName, "", "");
	}

	/**
	 * Create a new Publisher with the given fields, add it to the database, and
	 * return it. Its ID is assigned to not collide with any existing Publisher in
	 * the database.
	 *
	 * @param publisherName    the name of the publisher
	 * @param publisherAddress the address of the publisher's office
	 * @param publisherPhone   the publisher's phone number
	 * @return the newly created publisher
	 * @throws IOException on I/O error while interacting with the database
	 */
	Publisher createPublisher(String publisherName, String publisherAddress,
			String publisherPhone) throws IOException;

	/**
	 * Create a new Book with the given fields, add it to the database, and return
	 * it. Its ID is assigned to not collide with any existing Book in the database.
	 * If its author or publisher is not in the database (which shouldn't be able to
	 * happen if callers use the service class properly, but can happen if they call
	 * constructors directly), add records for the author or publisher as needed.
	 * However, this will not *update* author or publisher records if they *exist*
	 * in the database but those records' state (other than IDs) differs from the
	 * provided object(s).
	 *
	 * @param title     the title of the book
	 * @param isbn      the book's ISBN catalog number
	 * @param author    the author of the book
	 * @param publisher the publisher of the book
	 * @return the newly created book
	 * @throws IOException on I/O error while interacting with the database
	 */
	Book createBook(String title, String isbn, Author author, Publisher publisher)
			throws IOException;

	/**
	 * Create a new Book with the given title, ISBN, author, and publisher,
	 * searching the author and publisher tables to find existing author and
	 * publisher objects matching the provided names and creating new ones if not
	 * found; IDs are assigned to not collide with any existing records in the
	 * database. If multiple authors or publishers have the given names, which one
	 * is chosen is implementation-defined.
	 *
	 * @param title         the title of the book
	 * @param authorName    the name of the author of the book
	 * @param publisherName the name of the publisher of the book
	 * @throws IOException on I/O error while interacting with the database
	 */
	Book createBook(String title, String isbn, String authorName, String publisherName)
			throws IOException;

	/**
	 * Get all authors in the database.
	 *
	 * @return the list of authors in the database
	 * @throws IOException on I/O error while interacting with the database
	 */
	List<Author> getAllAuthors() throws IOException;

	/**
	 * Get the author with the given ID.
	 *
	 * @param id an ID number
	 * @return the author, if any, with that ID
	 * @throws IOException on I/O error while interacting with the database
	 */
	Optional<Author> getAuthorByID(long id) throws IOException;

	/**
	 * Get the list of authors with the given name (most likely a list with a single
	 * element, but may be empty if none, or have more elements in the case of
	 * duplicate data).
	 *
	 * @param name the name of the author(s) to find.
	 * @return the list of author(s) with that name.
	 * @throws IOException on I/O error while interacting with the database
	 */
	List<Author> getAuthorsNamed(String name) throws IOException;

	/**
	 * Get the list of authors whose name contains the given substring
	 * (case-insensitively).
	 *
	 * @param pattern the search string TODO: allow regex
	 * @return the list of matching authors
	 * @throws IOException on I/O error while interacting with the database
	 */
	List<Author> getAuthorsMatching(String pattern) throws IOException;

	/**
	 * Get all publishers in the database.
	 *
	 * @return the list of all publishers in the database.
	 * @throws IOException on I/O error while interacting with the database
	 */
	List<Publisher> getAllPublishers() throws IOException;

	/**
	 * Get the publisher with the given ID.
	 *
	 * @param id an ID number
	 * @return the publisher, if any, with that ID.
	 * @throws IOException on I/O error while interacting with the database
	 */
	Optional<Publisher> getPublisherByID(long id) throws IOException;

	/**
	 * Get the list of publishers with the given name (most likely a list with a
	 * single element, but may be empty if none, or have more elements in the case
	 * of duplicate data).
	 *
	 * @param name the name of the publisher(s) to find.
	 * @return the list of publisher(s) with that name.
	 * @throws IOException on I/O error while interacting with the database
	 */
	List<Publisher> getPublishersNamed(String name) throws IOException;

	/**
	 * Get the list of publishers matching the given pattern (as a case-insensitive
	 * substring).
	 *
	 * @param pattern the search string TODO: allow regex
	 * @param global  if true, search address and phone as well; if false, only
	 *                search the name field
	 * @return the list of matching publishers
	 * @throws IOException on I/O error while interacting with the database
	 */
	List<Publisher> getPublishersMatching(String pattern, boolean global)
			throws IOException;

	/**
	 * Get all books in the database.
	 *
	 * @return the list of all books in the database
	 * @throws IOException on I/O error while interacting with the database
	 */
	List<Book> getAllBooks() throws IOException;

	/**
	 * Get the book with the given ID.
	 *
	 * @param id an ID number
	 * @return the book, if any, with that ID
	 * @throws IOException on I/O error while interacting with the database
	 */
	Optional<Book> getBookByID(long id) throws IOException;

	/**
	 * Get the list of books with the given title (most likely a list with a single
	 * element, but may be empty if none match, or have more elements in the case of
	 * duplicate or overlapping data).
	 *
	 * @param title the title of the book(s) to find
	 * @return the list of book(s) with that title
	 * @throws IOException on I/O error while interacting with the database
	 */
	List<Book> getBooksWithTitle(String title) throws IOException;

	/**
	 * Get the list of books by the given author.
	 *
	 * @param author the author in question
	 * @return the list of books by that author
	 * @throws IOException on I/O error while interacting with the database
	 */
	List<Book> getBooksByAuthor(Author author) throws IOException;

	/**
	 * Get the list of books published by the given publisher.
	 *
	 * @param publisher the publisher in question
	 * @return the list of books published by that publisher
	 * @throws IOException on I/O error while interacting with the database
	 */
	List<Book> getBooksPublishedBy(Publisher publisher) throws IOException;

	/**
	 * Get the list of books matching the given pattern (as a case-insensitive
	 * substring)
	 *
	 * @param pattern the search string TODO: allow regex
	 * @param global  if true, search author name and publisher name as well; if
	 *                false, only search book title and ISBN.
	 * @return the list of matching books
	 * @throws IOException on I/O error while interacting with the database
	 */
	List<Book> getBooksMatching(String pattern, boolean global) throws IOException;

	/**
	 * Change the given author's record in the database to match its current state.
	 *
	 * @param author the author to update in the database
	 * @throws IOException on I/O error while interacting with the database
	 */
	void updateAuthor(Author author) throws IOException;

	/**
	 * Change the given publisher's record in the database to match its current
	 * state.
	 *
	 * @param publisher the publisher to update in the database
	 * @throws IOException on I/O error while interacting with the database
	 */
	void updatePublisher(Publisher publisher) throws IOException;

	/**
	 * Change the give book's record in the database to match its current state. If
	 * its author or publisher has changed to one not in the database (which
	 * shouldn't be able to happen if callers use the service class properly, but
	 * can happen if they call constructors directly), add records for the author or
	 * publisher as needed.
	 *
	 * @param book the book to update in the database
	 * @throws IOException on I/O error while interacting with the database
	 */
	void updateBook(Book book) throws IOException;

	/**
	 * Remove the given author from the database, along with all books written by
	 * that author.
	 *
	 * @param author the author to remove
	 * @throws IOException on I/O error while interacting with the database
	 */
	void deleteAuthor(Author author) throws IOException;

	/**
	 * Remove the given publisher from the database, along with all books published
	 * by that publisher.
	 *
	 * @param publisher the publisher to remove
	 * @throws IOException on I/O error while interacting with the database
	 */
	void removePublisher(Publisher publisher) throws IOException;

	/**
	 * Remove the given book from the database.
	 *
	 * @param book the book to remove
	 * @throws IOException on I/O error while interacting with the database
	 */
	void removeBook(Book book) throws IOException;
}
