package com.kevinturner.jv;

public class User {
   private String username;
   private String password;
   private String user_id;
   
   
   public User(String username, String password, String user_id){
	   this.username = username;
	   this.password = password;
	   this.user_id = user_id;
   }
   
   
   public String getUsername(){
	   return username;
   }
   
   
   public String getPassword(){
	   return password;
   }
   
   public String getUserID() {
	   return this.user_id;
   }
   
   @Override public String toString(){
	   return "user_id: "+ this.user_id + " username: " + this.username + " password: " + this.password;
   }
}
