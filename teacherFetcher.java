package com.kevinturner.jv;

import java.util.*;
import java.io.*;

import org.jsoup.nodes.Document;

public class teacherFetcher extends Fetcher<Teacher> {
	
	@Override public Teacher [] fetch(){
		Crawler c = new Crawler("https://app.cfisd.net/urlcap/campus_list_003.html");
       
		try {
			Document doc = c.crawl();
			Parser p = new Parser(doc);
			Teacher [] links = p.teacherLinks();
	   
		   return links;
		} catch (IOException e){
			System.out.println(e);
			System.exit(0);
		}
		
		return null;
	}
	
	
	@Override public boolean validateLink(String link){
		
		boolean valid = super.validateLink(link);
		if(valid){
			String [] splitUrl = link.split("/");
			String [] dotSplit = link.split("\\.");
			printArray(splitUrl);
			System.out.println(splitUrl.length + " length");
			if(splitUrl.length <= 4 && dotSplit.length > 1){
				return true;
			} else {
				return false;
			}
		} 
			return false;
		
	}
	
	public void printArray(String [] array){
		for(String s : array){
			System.out.println(s);
		}
		
	}
	
	public static void main(String [] args){
		Teacher [] teachers = (new teacherFetcher()).fetch();
		JVDBManager db = new JVDBManager();
		for(Teacher teacher : teachers){
			
			db.insertTeacher(teacher);
			System.out.println(teacher);
		}
	}
	
	
}