package com.hiveBackend.entity;

import java.util.Date;

public class Application {
	private int id;
	private String name;
	private int env;
	private Date createDate;
	private int author;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getEnv() {
		return env;
	}
	public void setEnv(int env) {
		this.env = env;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public int getAuthor() {
		return author;
	}
	public void setAuthor(int author) {
		this.author = author;
	}

	
}
