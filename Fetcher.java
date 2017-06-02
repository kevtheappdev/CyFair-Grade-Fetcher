package com.kevinturner.jv;

public abstract class Fetcher<a>{
	public abstract a[] fetch();
	
	public boolean validateLink(String link){
		//basic link validator, only checks for http prefix. Override for more complex validation
	 String [] split = link.split("://");
	 
	 
	 return (split[0].equals("http") || split[0].equals("https"));
		
	}
}