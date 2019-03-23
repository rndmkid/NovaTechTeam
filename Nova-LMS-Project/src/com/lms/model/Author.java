package com.lms.model;

public final class Author extends POJO{
	private String authorName;

	public Author(int id, String authorName) {
		super(id);
		this.authorName = authorName;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

}
