package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.InMemoryAuthorDAO;
import dao.InMemoryBookDAO;
import dao.InMemoryPublisherDAO;
import model.Author;
import model.Book;
import model.Publisher;

public final class ServiceTest {
	private LibraryService service;

	@BeforeEach
	public void setUp() {
		service = new LibraryServiceImpl(new InMemoryBookDAO(), new InMemoryAuthorDAO(),
				new InMemoryPublisherDAO());
	}

	@Test
	public void testSimpleAuthorAdding() throws IOException {
		assertTrue(service.getAllAuthors().isEmpty(), "Database starts with no authors");
		service.createAuthor("Lois McMaster Bujold");
		service.createAuthor("Patricia C. Wrede");
		service.createAuthor("Robin McKinley");
		assertEquals(3, service.getAllAuthors().size(),
				"Database includes three authors after adding three");
	}

	@Test
	public void testSimplePublisherAdding() throws IOException {
		assertTrue(service.getAllPublishers().isEmpty(),
				"Database starts with no publishers");
		service.createPublisher("Baen Books");
		service.createPublisher("Bantam Spectra");
		service.createPublisher("Avon");
		service.createPublisher("CreateSpace");
		assertEquals(4, service.getAllPublishers().size(),
				"Database includes four publishers after adding four");
	}

	@Test
	public void testBookAdding() throws IOException {
		assertTrue(service.getAllAuthors().isEmpty(), "Database starts with no authors");
		assertTrue(service.getAllBooks().isEmpty(), "Database starts with no books");
		assertTrue(service.getAllPublishers().isEmpty(),
				"Database starts with no publishers");
		final Author bujold = service.createAuthor("Lois McMaster Bujold");
		final Publisher baen = service.createPublisher("Baen Books");
		service.createBook("A Civil Campaign", "0671578855", bujold, baen);
		assertEquals(1, service.getAllBooks().size(),
				"Can create book with pre-saved author and publisher");
		final Book coc = service.createBook("The Curse of Chalion", "0380818604",
				"Lois McMaster Bujold", "HarperCollins");
		assertEquals(1, service.getAllAuthors().size(),
				"Service class reuses existing author");
		assertTrue(coc.getAuthor() == bujold, "Service class reuses existing author");
	}

	@Test
	public void testAuthorRemoval() throws IOException {
		final Author bujold = service.createAuthor("Lois McMaster Bujold");
		final Publisher baen = service.createPublisher("Baen Books");
		service.createBook("A Civil Campaign", "0671578855", bujold, baen);
		service.createBook("The Curse of Chalion", "0380818604", "Lois McMaster Bujold",
				"HarperCollins");
		service.createBook("Persuasion", "9781908533081", "Jane Austen",
				"TransAtlantic Press");
		assertEquals(3, service.getAllBooks().size(),
				"Three books before removing an author");
		service.deleteAuthor(bujold);
		assertEquals(1, service.getAllBooks().size(),
				"Removing an author removed all her books");
	}
}
