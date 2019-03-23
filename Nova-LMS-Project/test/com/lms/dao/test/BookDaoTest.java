package com.lms.dao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lms.dao.BookDaoImpl;
import com.lms.model.Book;

public class BookDaoTest {

	private Book book;
	private BookDaoImpl bookDaoIml;
	private int newBookId;
	private static int AUTHORnewBookId = 111111;
	private static int PUBLISHERnewBookId = 222222;
	
	@BeforeEach
	void init() throws IOException {
		bookDaoIml = new BookDaoImpl();
		book = new Book(789, "publisher name", AUTHORnewBookId, PUBLISHERnewBookId);
		newBookId = bookDaoIml.save(book);
	}
	
	@AfterEach
	void tearThis() throws FileNotFoundException, IOException {
		// WARNING maybe something that doesnt call the method we are trying to test
		bookDaoIml.delete(newBookId);
	}
	
	@Test
	public void saveBookTest() throws FileNotFoundException, IOException {
		bookDaoIml.delete(newBookId);
		
		int previousCount;
		previousCount = bookDaoIml.findAll().size();
		newBookId = bookDaoIml.save(book);
		int currentCount = bookDaoIml.findAll().size();
		assertTrue(previousCount < currentCount);
	}
	
	@Test
	public void deleteBookTest() throws FileNotFoundException, IOException {
		int previousCount = bookDaoIml.findAll().size();
		bookDaoIml.delete(newBookId);
		int currentCount = bookDaoIml.findAll().size();
		assertTrue(previousCount > currentCount);
	}
	
	@Test
	public void findBookTest() throws FileNotFoundException, IOException {
		assertEquals(newBookId, bookDaoIml.find(newBookId).getId());
	}
	
	@DisplayName("return null for book not found")
	@Test
	public void notFindBookTest() throws IOException {
		bookDaoIml.delete(newBookId);
		
		assertNull(bookDaoIml.find(newBookId));
	}
	
	@Test
	public void updateBookTest() throws FileNotFoundException, IOException {
		Book newUpdate = new Book(newBookId, "publisher name2", 123451, 1455123);
		bookDaoIml.update(newUpdate);
		Book newBook = bookDaoIml.find(newBookId);
		assertEquals(newBook.getId(), newUpdate.getId());
		assertTrue(newBook.getTitle().equals(newUpdate.getTitle()));
		assertEquals(newBook.getAuthorId(), (newUpdate.getAuthorId()));
		assertEquals(newBook.getPublisherId(), newBook.getPublisherId());
	}
}
