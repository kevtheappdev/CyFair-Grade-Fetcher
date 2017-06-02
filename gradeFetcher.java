package com.kevinturner.jv;

import java.util.*;
import java.io.*;

import org.jsoup.nodes.Document;




public class gradeFetcher extends Fetcher<Course>{
	private String username;
	private String password;
	private Student s;
	
	
	public gradeFetcher(Student s){
		super();
		this.username = s.getUsername();
		this.password = s.getPassword();
		this.s = s;
	}
	
	
	
	@Override public Course [] fetch(){
		System.out.println("Currently fetching grades");
		if(username != null && password != null){
			Crawler c = new Crawler("https://home-access.cfisd.net/HomeAccess/Content/Student/Assignments.aspx");
			if(c.logOn(username, password)){
				try {
					System.out.println("currently trying to fetch the grades");
				   Document doc = c.crawl();
				   System.out.println(doc);
				   Parser p = new Parser(doc);
				   Course [] cs = p.courses(s);
				   System.out.println("courses mixbeta:" + cs);
				   
				   
				   return cs;
		           
				} catch (IOException e){
					System.out.println(e);
				    
				}
				
				
			
			} 
			
		}
	  return null;
	}
	
	public gradeFetcher(String username, String password){
		 this.username = username;
		 this.password = password;
	}
	
	public static void printArray(Object [] arr){
		 for(Object o : arr){
			 System.out.println(o);
		 }
	}
	
	public static void main(String [] args){
		gradeFetcher gf = new gradeFetcher("s650665", "kai66wen");
		Course [] c = gf.fetch();
		printArray(c);
	}
}