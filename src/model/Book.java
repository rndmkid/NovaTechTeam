package model;

import java.util.Objects;

/**
 * Data class representing a book. We know its title, ISBN if any, author, and
 * publisher.
 *
 * @author Jonathan Lovelace
 */
public final class Book {
	/**
	 * The book's ID number (database key).
	 */
	private final long id;
	/**
	 * The book's title.
	 */
	private String title;
	/**
	 * The book's ISBN. (Validity is not even minimally checked at this point.)
	 */
	private String isbn;
	/**
	 * The book's author. Must not be null.
	 */
	private Author author;
	/**
	 * The book's publisher. Must not be null.
	 */
	private Publisher publisher;

	/**
	 * Constructor taking only the parameters for which we can't provide defaults.
	 *
	 * <p>TODO: Do we really want to allow books with no titles?
	 * @param id the book's ID number
	 * @param author the author of the book
	 * @param publisher the publisher of the book
	 */
	public Book(final long id, final Author author, final Publisher publisher) {
		this(id, author, publisher, "", "");
	}

	/**
	 * Constructor initializing all fields.
	 * @param id the book's ID number
	 * @param author the author of the book
	 * @param publisher the publisher of the book
	 * @param title the title of the book
	 * @param isbn the book's ISBN
	 */
	public Book(final long id, final Author author, final Publisher publisher, final String title, final String isbn) {
		this.id = id;
		this.author = author;
		this.publisher = publisher;
		this.title = title;
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(final String isbn) {
		this.isbn = isbn;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(final Author author) {
		this.author = author;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(final Publisher publisher) {
		this.publisher = publisher;
	}

	public long getId() {
		return id;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof Book) {
			return id == ((Book) obj).getId()
					&& Objects.equals(author, ((Book) obj).getAuthor())
					&& Objects.equals(publisher, ((Book) obj).getPublisher())
					&& Objects.equals(title, ((Book) obj).getTitle());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}

	@Override
	public String toString() {
		return String.format("%s (%d), by %s, published by %s", title, id,
				author.getName(), publisher.getName());
	}
}
