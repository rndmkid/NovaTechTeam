package com.lms.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.lms.model.Book;

public interface BookDao {
	// creates new entry using DataObject into file
	public abstract int save(Book book) throws IOException;
	// deletes associated book entry using the bookId and returns the entry in an dataObject
	// returns null if it cannot find the publisher
	public abstract Book delete(int id) throws FileNotFoundException, IOException;
	// Not sure about this (maybe pass id and object and compare the object with the entry and if anything is different then update
	public abstract void update(Book book) throws FileNotFoundException, IOException;
	// returns DataObject of the requested Id (in the future it can be anything else like name)
	public abstract Book find(int id) throws FileNotFoundException, IOException;
	// return DataObject array with all entries in the database
	public abstract List<Book> findAll() throws FileNotFoundException, IOException;
}
