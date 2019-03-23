package model;

import java.util.Objects;

/**
 * Data class representing an author of a book. At this point we only keep track
 * of authors' names.
 *
 * @author Jonathan Lovelace
 */
public final class Author {
	private final long id;
	private String name;

	public Author(final long id, final String name) {
		this.id = id;
		this.name = name;
	}

	public Author(final long id) { // TODO: Do we really want to allow authors with no names?
		this(id, "");
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof Author) {
			return ((Author) obj).getId() == id
					&& Objects.equals(name, ((Author) obj).getName());
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
		return String.format("Author %s (%d)", name, id);
	}
}
