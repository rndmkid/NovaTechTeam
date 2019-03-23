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

import com.lms.dao.AuthorDaoImpl;

import com.lms.model.Author;

public class AuthorDaoTest {
	
	private Author author;
	private AuthorDaoImpl authorDao;
	private int newAuthorId;
	
	@BeforeEach
	void init() throws FileNotFoundException, IOException {
		authorDao = new AuthorDaoImpl();
		author = new Author(789, "test 1");
		newAuthorId = authorDao.save(author);
		
	}
	
	@AfterEach
	void tearThis() throws FileNotFoundException, IOException {
		// WARNING maybe something that doesn't call the method we are trying to test
		authorDao.delete(newAuthorId);
	}

	@Test
	public void saveTest() throws FileNotFoundException, IOException {
		authorDao.delete(newAuthorId);
		
		int previousCount;
		previousCount = authorDao.findAll().size();
		newAuthorId = authorDao.save(author);
		int currentCount = authorDao.findAll().size();
		assertTrue(previousCount < currentCount);
	}
	
	@Test
	public void deleteTest() throws FileNotFoundException, IOException {
		int previousCount = authorDao.findAll().size();
		authorDao.delete(newAuthorId);
		int currentCount = authorDao.findAll().size();
		assertTrue(previousCount > currentCount);
	}
	
	@Test
	public void findAuthorTest() throws FileNotFoundException, IOException {
		assertEquals(newAuthorId, authorDao.find(newAuthorId).getId());
	}
	
	@DisplayName("return null for author not found")
	@Test
	public void notFindAuthorTest() throws IOException {
		authorDao.delete(newAuthorId);
		
		assertNull(authorDao.find(newAuthorId));
	}
	
	@DisplayName("Will update correct")
	@Test
	public void updateAuthorTest() throws FileNotFoundException, IOException {
		Author newUpdate = new Author(newAuthorId, "test 2");
		authorDao.update(newUpdate);
		Author newauthor = authorDao.find(newAuthorId);
		assertEquals(newauthor.getId(), newUpdate.getId());
		assertTrue(newauthor.getAuthorName().equals(newUpdate.getAuthorName()));
	}
}
