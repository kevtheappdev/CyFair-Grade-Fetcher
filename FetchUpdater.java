package com.kevinturner.jv;

import apns.PushNotification;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;


public class FetchUpdater implements DeferredTask {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    public boolean last = false;

	@Override
	public void run() 
	{   JVDBManager db = new JVDBManager();
		 Student [] allStudents = db.getAllStudents();
		 if(last){
		    db.emptyCursor(); 
		 }
       for(Student student : allStudents){
    	     if(student != null){
    	    	    System.out.println("fetching grade for student named: " + student.getUsername());
    	      Fetcher<Course> gf = new gradeFetcher(student);
    	      Course [] courses = gf.fetch();
    	      Course [] course = db.getStudentsCourses(student.getId());
    	       System.out.println("alphamix: " + courses);
   	       db.deleteCourses(student.getUser());
   	       
    	     if(student.getDeviceToken() != null && student.getDeviceType().equals("ios")){
    	      System.out.println("Sending push notif!!");
    	      
    	       
    	      for(int i = 0; i < courses.length; i++){
    	    	   if(courses[i] == null)break;
    	    	   if(course[i] == null)break;
    	    	   String notification = courses[i].getCourseName() + " -";
    	    	   boolean added = false;
    	    	      Assignment[] old = course[i].getAssignments();
    	    	      Assignment[] newer = courses[i].getAssignments();
    	    	     for(Assignment a: newer){
    	    	    	    if(a == null)break;
    	    	    	    if(!exists(a, old)){
    	    	    	    	   notification = notification + " \n " + a.toString();
    	    	    	       added = true;
    	    	    	    }
    	    	     }
    	    	     if(added){
	    	    		 PushNotification pn = new PushNotification()
	    	   	    	  .setAlert(notification)
	    	   	    	  .setBadge(1).setDeviceTokens(student.getDeviceToken());
	    	   	    	 SendPushNotification dt = new SendPushNotification(pn);
	    	   	    	
	    	   	    	 Queue sq = QueueFactory.getQueue("saver");
	    	   	    	 sq.add(TaskOptions.Builder.withPayload(dt));
    	    	     }
    	    	     } 
    	        
    	       }
   	       
   	       
    	       db.insertCourses(courses);
    	     }
       }
	}
	
	private boolean exists(Assignment a, Assignment[] array){
		System.out.println("exists.." + array.length);
		for(Assignment assign : array){
			if(assign == null)break;
			System.out.println("The assign thetamix: " + assign);
			if(assign.equals(a))
				return true;
		}
		
		return false;
	}

}
