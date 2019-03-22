package model;

import java.util.Objects;

/**
 * Data class representing a book. We know its title, ISBN if any, author, and
 * publisher.
 *
 * @author Jonathan Lovelace
 */
public final class Book {
	private final long id;
	private String title;
	private String isbn;
	private Author author;
	private Publisher publisher;

	public Book(final long id, final Author author, final Publisher publisher) { // TODO: Do we really want
		// to allow books with no
		// titles?
		this(id, author, publisher, "", "");
	}

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
}
