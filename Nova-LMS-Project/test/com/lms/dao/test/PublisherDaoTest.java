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

import com.lms.dao.PublisherDaoImpl;
import com.lms.model.Publisher;


public class PublisherDaoTest {

	private Publisher publisher;
	private PublisherDaoImpl publisherDaoIml;
	private int newPublisherId;
	
	@BeforeEach
	void init() throws IOException {
		publisherDaoIml = new PublisherDaoImpl();
		publisher = new Publisher(789, "publisher name", "publisher address", "1234567890");
		newPublisherId = publisherDaoIml.save(publisher);
	}
	
	@AfterEach
	void tearThis() throws FileNotFoundException, IOException {
		// WARNING maybe something that doesn't call the method we are trying to test
		publisherDaoIml.delete(newPublisherId);
	}

	@Test
	public void saveTest() throws FileNotFoundException, IOException {
		publisherDaoIml.delete(newPublisherId);
		
		int previousCount;
		previousCount = publisherDaoIml.findAll().size();
		newPublisherId = publisherDaoIml.save(publisher);
		int currentCount = publisherDaoIml.findAll().size();
		assertTrue(previousCount < currentCount);
	}
	
	@Test
	public void deleteTest() throws FileNotFoundException, IOException {
		int previousCount = publisherDaoIml.findAll().size();
		publisherDaoIml.delete(newPublisherId);
		int currentCount = publisherDaoIml.findAll().size();
		assertTrue(previousCount > currentCount);
	}
	
	@Test
	public void findPublisherTest() throws FileNotFoundException, IOException {
		assertEquals(newPublisherId, publisherDaoIml.find(newPublisherId).getId());
	}
	
	@DisplayName("return null for publisher not found")
	@Test
	public void notFindPublisherTest() throws IOException {
		publisherDaoIml.delete(newPublisherId);
		
		assertNull(publisherDaoIml.find(newPublisherId));
	}
	
	@DisplayName("Will update correct")
	@Test
	public void updatePublisherTest() throws FileNotFoundException, IOException {
		Publisher newUpdate = new Publisher(newPublisherId, "publisher name2", "publisher address2", "0987654321");
		publisherDaoIml.update(newUpdate);
		Publisher newPublisher = publisherDaoIml.find(newPublisherId);
		assertEquals(newPublisher.getId(), newUpdate.getId());
		assertTrue(newPublisher.getPublisherName().equals(newUpdate.getPublisherName()));
		assertTrue(newPublisher.getPublisherAddress().equals(newUpdate.getPublisherAddress()));
		assertTrue(newPublisher.getPublisherPhone().equals(newUpdate.getPublisherPhone()));
	}
}
