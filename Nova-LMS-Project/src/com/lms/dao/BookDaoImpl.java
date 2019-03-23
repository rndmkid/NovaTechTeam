package com.lms.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.lms.model.Book;


public class BookDaoImpl implements BookDao{
	private static String REGEX = "`";
	private static String FILELOCATION = "./resources/books.csv";

	// creates new entry using DataObject into file
	@Override
	public int save(Book book) throws IOException {
		List<Book> listBook = new ArrayList<Book>();
		listBook = this.findAll();
		
		// WARNING have not made a test to test if there are not entries
		int primaryId = 0;
		if(listBook.size() > 0) {
			primaryId = listBook.get(listBook.size() - 1).getId() + 1;
		}
		
		String entry = primaryId + REGEX + book.getTitle() + REGEX
				+ book.getAuthorId() + REGEX + book.getPublisherId();
		FileWriter fw = new FileWriter(FILELOCATION, true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
		pw.println(entry);
		pw.close();
		return primaryId;
	}
	
	// deletes associated book entry using the bookId and returns the entry in an dataObject
	// returns null if it cannot find the publisher
	@Override
	public Book delete(int id) throws FileNotFoundException, IOException {
		// WARNING This does not account for dependencies with Author and Publisher
		FileReader fr = new FileReader(FILELOCATION);
		BufferedReader br = new BufferedReader(fr);
		String currentLine;
		
		List<String> newFileString = new ArrayList<String>();
		
		Book deletedBook = null;
		
		// save all non deleted entries into newFileString
		while((currentLine = br.readLine()) != null) {
			String[] splitEntry = currentLine.split(REGEX);
			int entryId = Integer.parseInt(splitEntry[0]);
			int authorId = Integer.parseInt(splitEntry[2]);
			int publisherId = Integer.parseInt(splitEntry[3]);

			if(entryId != id) {
				newFileString.add(currentLine);
			} else {
				deletedBook = new Book(entryId, splitEntry[1], authorId, publisherId);
			}
		}
		br.close();
		
		
		FileWriter fw = new FileWriter(FILELOCATION, false);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
		
		// put the entries back without the deleted entry
		for(String entry : newFileString) {
			pw.println(entry);
			
		}
		pw.close();
		
		// return null if it did not find a match
		return deletedBook;
	
	}
	
	// Not sure about this (maybe pass id and object and compare the object with the entry and if anything is different then update
	@Override
	public void update(Book book) throws FileNotFoundException, IOException {
		// WARNING This does not account for if the id does not exist
		// WARNING also does not account for if the publisher and author get change and do not exist
		// WARNING also you dont know if it worked or not
		FileReader fr = new FileReader(FILELOCATION);
		BufferedReader br = new BufferedReader(fr);
		
		List<String> newEntries = new ArrayList<String>();
		
		String currentLine;
		while((currentLine = br.readLine()) != null) {
			String[] splitArray = currentLine.split(REGEX);
			int entryId = Integer.parseInt(splitArray[0]);
			
			if(entryId == book.getId()) {
				String newEntry = book.getId() + REGEX + book.getTitle() + 
						REGEX + book.getAuthorId() + REGEX + book.getPublisherId();
				newEntries.add(newEntry);
			} else {
				newEntries.add(currentLine);
			}
		}
		br.close();
		
		
		FileWriter fw = new FileWriter(FILELOCATION, false);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
		
		for(String entry : newEntries) {
			pw.println(entry);
		}
		pw.close();
	}

	// returns DataObject of the requested Id (in the future it can be anything else like name)
	@Override
	public Book find(int id) throws FileNotFoundException, IOException {
		// WARNING may want to overload to account for if they try to find using Author or whatever
		Book correctBook = null;
		
		FileReader fr = new FileReader(FILELOCATION);
		BufferedReader br = new BufferedReader(fr);
		String currentLine;
		while((currentLine = br.readLine()) != null) {
			String[] splitEntry = currentLine.split(REGEX);
			int entryId = Integer.parseInt(splitEntry[0]);
			int authorId = Integer.parseInt(splitEntry[2]);
			int publisherId = Integer.parseInt(splitEntry[3]);

			if(entryId == id) {
				correctBook = new Book(entryId, splitEntry[1],  authorId, publisherId);
			}
		}
		br.close();
		return correctBook;
	}
	
	// return DataObject array with all entries in the database
	@Override
	public List<Book> findAll() throws FileNotFoundException, IOException {
		
		List<Book> books = new ArrayList<Book>();
		
		FileReader fr = new FileReader(FILELOCATION);
		BufferedReader br = new BufferedReader(fr);
		String currentLine;
		while((currentLine = br.readLine()) != null) {
			String[] splitEntry = currentLine.split(REGEX);
			int entryId = Integer.parseInt(splitEntry[0]);
			int authorId = Integer.parseInt(splitEntry[2]);
			int publisherId = Integer.parseInt(splitEntry[3]);
			Book book = new Book(entryId, splitEntry[1], authorId, publisherId);
			books.add(book);
		}
		
		br.close();
		
		return books;
	}


}
