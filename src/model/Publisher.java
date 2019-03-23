package model;

import java.util.Objects;

/**
 * Data class representing a publishing company. At this point all the
 * information about it that we keep track of is its name.
 *
 * @author Jonathan Lovelace
 */
public final class Publisher {
	private final long id;
	private String name;
	private String address;
	private String phone;

	public Publisher(final long id) { // TODO: Do we really want to allow publishers with no names?
		this(id, "");
	}

	public Publisher(final long id, final String name) {
		this(id, name, "", "");
	}

	public Publisher(final long id, final String name, final String address,
			final String phone) {
		this.id = id;
		this.name = name;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(final String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Publisher) {
			return ((Publisher) obj).getId() == id
					&& Objects.equals(name, ((Publisher) obj).getName())
					&& Objects.equals(address, ((Publisher) obj).getAddress())
					&& Objects.equals(phone, ((Publisher) obj).getPhone());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return String.format("Publisher %s (%d)", name, id);
	}
}
