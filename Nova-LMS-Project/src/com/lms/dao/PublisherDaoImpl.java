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

import com.lms.model.Publisher;

public class PublisherDaoImpl implements PublisherDao{
	private static String REGEX = "`";
	private static String FILELOCATION = "./resources/publishers.csv";

	// creates new entry using DataObject into file
	@Override
	public int save(Publisher publisher) throws IOException {
		List<Publisher> listPublisher = new ArrayList<Publisher>();
		listPublisher = this.findAll();
		
		// WARNING have not made a test to test if there are not entries
		int primaryId = 0;
		if(listPublisher.size() > 0) {
			primaryId = listPublisher.get(listPublisher.size() - 1).getId() + 1;
		}
		
		String newEntry = primaryId + REGEX + publisher.getPublisherName() + 
				REGEX + publisher.getPublisherAddress() + REGEX + publisher.getPublisherPhone();
		FileWriter fw = new FileWriter(FILELOCATION, true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
		pw.println(newEntry);
		pw.close();
		return primaryId;
	}
	
	// deletes associated book entry using the bookId and returns the entry in an dataObject
	// returns null if it cannot find the publisher
	@Override
	public Publisher delete(int id) throws FileNotFoundException, IOException {
		// WARNING This does not account for dependencies with Book
		FileReader fr = new FileReader(FILELOCATION);
		BufferedReader br = new BufferedReader(fr);
		String currentLine;
		List<String> newFileString = new ArrayList<String>();
		Publisher deletedPublisher = null;
		// save all non deleted entries into newFileString
		while((currentLine = br.readLine()) != null) {
			String[] splitEntry = currentLine.split(REGEX);
			int entryId = Integer.parseInt(splitEntry[0]);
			
			if(entryId != id) {
				newFileString.add(currentLine);
			} else {
				deletedPublisher = new Publisher(entryId, splitEntry[1], splitEntry[2], splitEntry[3]);
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
		return deletedPublisher;
	}
	
	// Not sure about this (maybe pass id and object and compare the object with the entry and if anything is different then update
	@Override
	public void update(Publisher publisher) throws FileNotFoundException, IOException {
		// WARNING This does not account for if the id does not exist
		FileReader fr = new FileReader(FILELOCATION);
		BufferedReader br = new BufferedReader(fr);
		
		List<String> newEntries = new ArrayList<String>();
		
		String currentLine;
		while((currentLine = br.readLine()) != null) {
			String[] splitArray = currentLine.split(REGEX);
			int entryId = Integer.parseInt(splitArray[0]);
			
			if(entryId == publisher.getId()) {
				String newEntry = publisher.getId() + REGEX + publisher.getPublisherName() + 
						REGEX + publisher.getPublisherAddress() + REGEX + publisher.getPublisherPhone();
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
	public Publisher find(int id) throws FileNotFoundException, IOException {
		// may want to overload to account for if they try to find using Author or whatever
		Publisher correctPublisher = null;
		FileReader fr = new FileReader(FILELOCATION);
		BufferedReader br = new BufferedReader(fr);
		String currentLine;
		while((currentLine = br.readLine()) != null) {
			String[] splitEntry = currentLine.split(REGEX);
			int entryId = Integer.parseInt(splitEntry[0]);
			if(entryId == id) {
				correctPublisher = new Publisher(entryId, splitEntry[1],  splitEntry[2], splitEntry[3]);
			}
		}
		br.close();
		return correctPublisher;
	}
	
	// return DataObject array with all entries in the database
	@Override
	public List<Publisher> findAll() throws FileNotFoundException, IOException {
		List<Publisher> publishers = new ArrayList<Publisher>();
		FileReader fr = new FileReader(FILELOCATION);
		BufferedReader br = new BufferedReader(fr);
		String currentLine;
		while((currentLine = br.readLine()) != null) {
			String[] splitEntry = currentLine.split(REGEX);
			Publisher publisher = new Publisher(Integer.parseInt(splitEntry[0]), splitEntry[1], splitEntry[2], splitEntry[3]);
			publishers.add(publisher);
		}
		
		br.close();
		
		return publishers;
	}

}
