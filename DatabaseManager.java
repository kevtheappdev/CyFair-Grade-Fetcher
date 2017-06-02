package com.kevinturner.jv;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public abstract class DatabaseManager {
	private DatastoreService  conn;
    
	
	   
	   public DatabaseManager(){
		   establishConnection();
	   }
	   
	   private void establishConnection(){
		  this.conn = DatastoreServiceFactory.getDatastoreService();
	   }
	   
	   
	   public DatastoreService getConnection(){
		   return conn;
	   }
	   
	   
	   
	   
	   
}