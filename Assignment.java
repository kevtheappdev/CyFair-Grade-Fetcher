package com.kevinturner.jv;

import java.io.Serializable;


public class Assignment implements Comparable<Assignment>,  Serializable{
	private String course_id;
	private String name;
	private String dueDate;
	private String assignedDate;
	private String type;
	private double score;
	private double weight;
	private double totalPoints;
	 private static final long serialVersionUID = 4L;
	
	  public Assignment(String courseId, String name, String dueDate, String assignedDate, String type, double score, double weight, double totalPoints){
		   this.course_id = courseId;
		   this.name = name;
		   this.dueDate = dueDate;
		   this.assignedDate = assignedDate;
		   this.type = type;
		   this.score = score;
		   this.weight = weight;
		   this.totalPoints = totalPoints;
	  }
	  
	  
	  @Override public String toString(){
		  return name + " - " + score;
	  }
	  
	  public String getName(){
		  return name;
	  }
	  
	  public String getDueDate(){
		  return dueDate;
	  }
	  
	  public String getAssignedDate(){
		  return assignedDate;
	  }
	  
	  public String getType(){
		  return type;
	  }
	  
	  public double getScore(){
		  return score;
	  }
	  
	  public double getTotalPoints(){
		  return totalPoints;
	  }
	  
	  public double getWeight(){
		  return weight;
	  }


	public String getCourse_id() {
		return course_id;
	}


	@Override
	public int compareTo(Assignment o) {
		return o.getName().compareTo(this.name);
	}
	
	
	@Override public boolean equals(Object o){
		if(o instanceof Assignment){
			Assignment a = (Assignment)o;
			if(a.getName().equals(this.name) && a.getScore() == this.score)
				  return true;
		}
		return false;
	}



	
}