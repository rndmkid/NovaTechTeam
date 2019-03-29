package menu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.DataAccessObject;
import dao.InMemoryAuthorDAO;
import dao.InMemoryBookDAO;
import dao.InMemoryPublisherDAO;
import model.Author;
import model.Book;
import model.Publisher;
import service.LibraryServiceImpl;

public final class MenuTest {

	private DataAccessObject<Book> bookDAO;
	private DataAccessObject<Author> authorDAO;
	private DataAccessObject<Publisher> publisherDAO;

	@BeforeEach
	public void init() {
		bookDAO = new InMemoryBookDAO();
		authorDAO = new InMemoryAuthorDAO();
		publisherDAO = new InMemoryPublisherDAO();
	}

	@Test
	void testAuthors() throws IOException {
		final StringBuilder out = new StringBuilder();
		final StringBuilder inputBuilder = new StringBuilder();
		inputBuilder.append("a\n2\nJane Austen\na\n3\nJules Verne\n");
		inputBuilder.append("a\n2\n");
		final EntityManagementMenu menu = new EntityManagementMenu(
				new StringReader(inputBuilder.toString()), out,
				new LibraryServiceImpl(bookDAO, authorDAO, publisherDAO));
		menu.add(); // adding Austen
		menu.add(); // adding Verne
		assertEquals(2, authorDAO.findAll().size(), "Menu allows adding authors");
		out.delete(0, out.length());
		menu.retrieve();
		assertEquals(
				"Kind of entity to retrieve: ID of author to retrieve (-1 for all): Author Jane Austen (2)\n",
				out.toString(), "Retrieval gives expected output");
	}

}
