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

import com.lms.model.Author;

public class AuthorDaoImpl implements AuthorDao{
	private static String REGEX = "`";
	private static String FILELOCATION = "./resources/authors.csv";

	// creates new entry using DataObject into file
	@Override
	public int save(Author author) throws IOException {
		List<Author> listAuthor = new ArrayList<Author>();
		listAuthor = this.findAll();
		
		// WARNING have not made a test to test if there are not entries
		int primaryId = 0;
		if(listAuthor.size() > 0) {
			primaryId = listAuthor.get(listAuthor.size() - 1).getId() + 1;
		}
		
		String entry = primaryId + REGEX + author.getAuthorName();
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
	public Author delete(int id) throws FileNotFoundException, IOException {
		FileReader fr = new FileReader(FILELOCATION);
		BufferedReader br = new BufferedReader(fr);
		String currentLine;
		List<String> newFileString = new ArrayList<String>();
		Author deletedAuthor = null;
		// save all non deleted entries into newFileString
		while((currentLine = br.readLine()) != null) {
			String[] splitEntry = currentLine.split(REGEX);
			int entryId = Integer.parseInt(splitEntry[0]);

			if(entryId != id) {
				newFileString.add(currentLine);
			} else {
				deletedAuthor = new Author(entryId, splitEntry[1]);
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
		return deletedAuthor;
	}

	// Not sure about this (maybe pass id and object and compare the object with the entry and if anything is different then update
	@Override
	public void update(Author author) throws FileNotFoundException, IOException {
		// WARNING This does not account for if the id does not exist
		FileReader fr = new FileReader(FILELOCATION);
		BufferedReader br = new BufferedReader(fr);
		
		List<String> newEntries = new ArrayList<String>();
		
		String currentLine;
		while((currentLine = br.readLine()) != null) {
			String[] splitArray = currentLine.split(REGEX);
			int entryId = Integer.parseInt(splitArray[0]);
			
			if(entryId == author.getId()) {
				String newEntry = author.getId() + REGEX + author.getAuthorName();
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
	public Author find(int id) throws FileNotFoundException, IOException {
		// may want to overload to account for if they try to find using Author or whatever
		Author correctAuthor = null;
		FileReader fr = new FileReader(FILELOCATION);
		BufferedReader br = new BufferedReader(fr);
		String currentLine;
		while((currentLine = br.readLine()) != null) {
			String[] splitEntry = currentLine.split(REGEX);
			int entryId = Integer.parseInt(splitEntry[0]);
			if(entryId == id) {
				correctAuthor = new Author(entryId, splitEntry[1]);
			}
		}
		br.close();
		return correctAuthor;
	}

	// return DataObject array with all entries in the database
	@Override
	public List<Author> findAll() throws FileNotFoundException, IOException {
		List<Author> authors = new ArrayList<Author>();
		FileReader fr = new FileReader(FILELOCATION);
		BufferedReader br = new BufferedReader(fr);
		String currentLine;
		while((currentLine = br.readLine()) != null) {
			String[] splitEntry = currentLine.split(REGEX);
			Author author = new Author(Integer.parseInt(splitEntry[0]), splitEntry[1]);
			authors.add(author);
		}
		
		br.close();
		
		return authors;
	}
}
