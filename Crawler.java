package com.kevinturner.jv;

import java.io.*;
import java.util.Map.Entry;
import java.util.Map;

import org.jsoup.*;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

public class Crawler {
	private final static String loginUrl = "https://home-access.cfisd.net/HomeAccess/Account/LogOn?ReturnUrl=%2fhomeaccess%2f"; 

	private String url;
	private Map<String, String> cookies;
	
    public boolean logOn(String username, String password){
    	   try {
    		   Response r = Jsoup.connect(loginUrl)
    				    .data("LogOnDetails.UserName", username)
    				    .data("LogOnDetails.Password", password).data("Database", "20")
    				    .method(Method.POST).timeout(10*1000).ignoreHttpErrors(true).execute();
    		cookies = r.cookies();
    		System.out.println(cookies);
    		
    		int t = 0;
    		 for(Map.Entry<String, String>cookie : cookies.entrySet()){
    			if(cookie.getValue() != null){
    				t++;
    			}
    		 }
    		 System.out.println("t: " + t);
    	     return t == 2;  
    	   } catch(IOException e){
    		   System.out.println(e);
    		
    	   }
    	   return false;
    }
    
    public Map<String, String> getCookies(){
    	   return cookies;
    }
    
    public void setCookies(Map<String, String>cookies){
    	 this.cookies = cookies;
    }
    
    public Document crawl() throws IOException{
       Connection connection = Jsoup.connect(url).data("ctl00$plnMain$ddlReportCardRuns", "3").timeout(1000*1000).method(Method.POST);
       if(this.cookies != null){
       for(Entry<String, String>cookie : cookies.entrySet()){
    	       connection.cookie(cookie.getKey(), cookie.getValue());
        }
       }
       
       return connection.ignoreHttpErrors(false).followRedirects(true).userAgent("Mozilla").referrer("http://www.cfisd.net").get();
       
    
    }
    
         
    public Crawler(String url){
        this.url = url;
    }
    
    public Crawler(){}
    
    public String getUrl(){
      return this.url;
    }
    
    public void setUrl(String url){
      this.url = url;
    }
    
}
