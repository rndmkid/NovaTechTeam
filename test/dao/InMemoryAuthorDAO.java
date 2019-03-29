package dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.Author;

/**
 * A data-access-object implementation using a purely in-memory "table" for
 * use in test code.
 *
 * @author Jonathan Lovelace
 */
public class InMemoryAuthorDAO implements DataAccessObject<Author> {

	/**
	 * The table of authors.
	 */
	private final List<Author> cache = new ArrayList<>();

	@Override
	public void save(final Author book) {
		cache.add(book);
	}

	@Override
	public void delete(final Author author) {
		cache.remove(author);
	}

	@Override
	public void update(final Author t) {
		// No-op
	}

	@Override
	public List<Author> findAll() {
		return new ArrayList<>(cache);
	}

	@Override
	public Optional<Author> find(final long id) throws IOException {
		return cache.stream().filter(author -> author.getId() == id).findAny();
	}

}
