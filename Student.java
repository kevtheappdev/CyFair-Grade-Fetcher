package com.kevinturner.jv;

import java.io.Serializable;


public class Student implements Serializable{
   private String username;
   private String password;
   private String id;
   private String deviceType;
   private String deviceToken;
   private Course [] courses;
   private static final long serialVersionUID = 2L;
   
    public Student(String id2, String username, String password, Course [] courses, String deviceType, String deviceToken){
    	   this.id = id2;
    	   this.username = username;
    	   this.password = password;
    	   this.courses = courses;
    	   this.deviceToken = deviceToken;
    	   this.deviceType = deviceType;
    }
    
    public String getDeviceType(){
    	  return deviceType;
    }
    
    public String getDeviceToken(){
      return deviceToken;     
    }
    
    public String getId(){
    	
    	  return id;
    }
    
    public User getUser(){
    	   return new User(username, password, id);
    }
    
    public void setCourses(Course [] courses){
    	  this.courses = courses;
    }
    
    public String getUsername(){
    	  return username;
    }
    
    public String getPassword(){
    	 return password;
    }
    
    public Course [] getCourses(){
    	 return courses;
    }
    
    @Override public String toString(){
    	   String student = username + "\n";
    	    for(int i = 0; i < courses.length; i++){
    	    	if(courses[i] != null)
    	    	   student = student + courses[i].toString() + "\n\n";
    	    }
    	    return student;
    }
}
