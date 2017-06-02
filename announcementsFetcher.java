
package com.kevinturner.jv;

import java.util.*;
import java.io.*;

import org.jsoup.*;
import org.jsoup.nodes.Document;


public class announcementsFetcher extends Fetcher<Announcement>{
	
	@Override public Announcement[] fetch(){
		Crawler c = new Crawler("http://jerseyvillage.cfisd.net/en/parents-students/student-life/daily-announcements?announce=13");
		try {
		  Document announce = c.crawl();
		  Parser p = new Parser(announce);
		  Announcement [] a = p.getAnnouncements();
		 return a;
		} catch (IOException e){
			System.out.println(e);
			System.exit(0);
		}
		
	return null;
	}
	
	
	public static void main(String [] args){
		announcementsFetcher af = new announcementsFetcher();
		Announcement [] as = af.fetch();
	    printArray(as);
	    System.out.println("array of announcements");
	      JVDBManager db = new JVDBManager();
	    for(Announcement a : as){
		   db.insertAnnouncements(a.getDate(), a.getAnnouncement());
	      System.out.println(a);
	    }
	}
	
	private static void printArray(Object [] arr){
		for(Object o : arr){
			System.out.println(o);
		}
	}
}
