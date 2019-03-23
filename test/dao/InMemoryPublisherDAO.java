package dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.Publisher;

public class InMemoryPublisherDAO implements DataAccessObject<Publisher> {

	private final List<Publisher> cache = new ArrayList<>();

	@Override
	public void save(final Publisher book) {
		cache.add(book);
	}

	@Override
	public void delete(final Publisher publisher) {
		cache.remove(publisher);
	}

	@Override
	public void update(final Publisher t) {
		// No-op
	}

	@Override
	public List<Publisher> findAll() {
		return new ArrayList<>(cache);
	}

	@Override
	public Optional<Publisher> find(final long id) throws IOException {
		return cache.parallelStream().filter(publisher -> publisher.getId() == id)
				.findAny();
	}

}
