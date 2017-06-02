package com.kevinturner.jv;

public class Teacher implements Comparable<Teacher>{
private String pageUrl;
private String name;

	
	public Teacher(String pageUrl, String name){
		this.pageUrl = pageUrl;
		this.name = name;
	}

	
	public String getPageUrl(){
		return this.pageUrl;
	}
	
	
	public String getName(){
		return this.name;
	}


	@Override
	public int compareTo(Teacher o) {
		
		return this.name.compareTo(o.getName());
	}
	
}