package com.kevinturner.jv;

public class Announcement {
	private String announcement;
	private String date;
	
	public Announcement(String announcement, String date){
		this.announcement = announcement;
		this.date = date;
	}
	
	public String getDate(){
		return this.date;
	}
	
	public String getAnnouncement(){
		return this.announcement;
	}
	
	@Override public String toString(){
		return this.date + " - " + this.announcement;
	}
}