package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public final class ModelTest {

	@Test
	public void testAuthorEquality() {
		assertEquals(new Author(1L, "Jane Austen"), new Author(1L, "Jane Austen"),
				"Two identical authors are equal");
		assertNotEquals(new Author(1L, "Jane Austen"), new Author(1L, "Dorothy Sayers"),
				"Two authors with the same ID but different names are not equal");
		assertNotEquals(new Author(1L, "Jane Austen"), new Author(2L, "Jane Austen"),
				"Two authors with same name but different ID are not equal");
	}

	@Test
	public void testPublisherEquality() {
		assertEquals(new Publisher(2L, "Bantam"), new Publisher(2L, "Bantam"),
				"Two identical publishers are equal");
		assertNotEquals(new Publisher(2L, "Bantam"), new Publisher(2L, "Baen"),
				"Two publishers with same ID but different names are not equal");
		assertNotEquals(new Publisher(2L, "Baen"), new Publisher(3L, "Baen"),
				"Two publishers with same name but different ID are not equal");
	}

	@Test
	public void testTrivialBookEquality() {
		final Author author = new Author(1L, "Lois McMaster Bujold");
		final Publisher publisher = new Publisher(2L, "Baen Books");
		assertEquals(new Book(3L, author, publisher, "A Civil Campaign", "0671578855"),
				new Book(3L, author, publisher, "A Civil Campaign", "0671578855"),
				"Two identical books are equal");
		assertNotEquals(new Book(3L, author, publisher, "A Civil Campaign", "0671578855"),
				new Book(4L, new Author(5L, "Robin McKinley"),
						new Publisher(6L, "ACE Books"), "The Blue Sword",
						"9780441068807"),
				"Two very different books are not equal");
		assertEquals(new Book(3L, author, publisher, "Civil Campaign", "0671578855"),
				new Book(3L, author, publisher, "A Civil Campaign", "0671578855"),
				"Different-title books from same author and publisher are not equal");
		assertEquals(new Book(3L, author, publisher, "A Civil Campaign", "0671578855"),
				new Book(3L, author, publisher, "A Civil Campaign", "second-isbn"),
				"Different-ISBN books from same author and publisher are not equal");
	}

	@Test
	public void testDeepBookEquality() {
		assertEquals(new Book(4L, new Author(5L, "Robin McKinley"),
				new Publisher(6L, "ACE Books"), "The Blue Sword", "9780441068807"),
				new Book(4L, new Author(5L, "Robin McKinley"),
						new Publisher(6L, "ACE Books"), "The Blue Sword",
						"9780441068807"),
				"Identical books with equal but not same-instance author/publisher are equal");
	}
}
