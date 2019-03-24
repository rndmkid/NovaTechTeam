package dao;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * An interface for data-access objects for the library-management system
 * project, hiding the details of how the database(s) is/are stored from the
 * rest of the code.
 *
 * @author Salem Ozaki (original author)
 * @author Jonathan Lovelace (further improvements)
 *
 * @param <T> the type of object stored in the table this object allows the code
 *            to access
 */
public interface DataAccessObject<T> {
	/**
	 * Add a new object to the database. If it (or another with the same ID) is
	 * already in the database, behavior is undefined.
	 *
	 * @param entity the object to add to the database.
	 * @throws IOException on I/O error while writing
	 */
	void save(T entity) throws IOException;

	/**
	 * Remove an object from the database, ignoring relational constraints.
	 *
	 * @param entity the object to remove from the database.
	 * @throws IOException if file not found or on other I/O error while reading or
	 *                     writing
	 */
	void delete(T entity) throws IOException;

	/**
	 * Update the database record for the given object (ignoring any relational
	 * constraints that might be affected by this).
	 *
	 * @param entity the object to update
	 * @throws IOException if file not found or on other I/O error while reading or
	 *                     writing
	 */
	void update(T entity) throws IOException;

	/**
	 * Find an object in the database by its ID number. (Any other type of search is
	 * expected to be performed in another layer of the architecture.) The ID field
	 * is assumed to be a unique key; if this precondition constraint is not met,
	 * behavior is undefined.
	 *
	 * @param id the ID number to look for
	 * @return the object, if any, with that ID.
	 * @throws IOException if file not found or on other I/O error while reading
	 */
	Optional<T> find(long id) throws IOException;

	/**
	 * Produce a list of all objects in (this table of) the database.
	 *
	 * @return the list of objects in the database.
	 * @throws IOException if file not found or on other I/O error while reading
	 */
	List<T> findAll() throws IOException;
}
