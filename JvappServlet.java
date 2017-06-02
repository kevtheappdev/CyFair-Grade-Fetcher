package com.kevinturner.jv;

import java.io.IOException;

import apns.PushNotification;




import javax.servlet.http.*;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;


@SuppressWarnings("serial")
public class JvappServlet extends HttpServlet {
	JVDBManager db = new JVDBManager();
	
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	   int type = Integer.parseInt(req.getParameter("type"));
	  
	   switch(type){
	     case 0:
		   updateGrades();
		   System.out.println("Updating grades");
		   break;
	     case 1:
	    	    updateAnnouncements();
		   break;
	     case 2:
	    	    db.updateUserCount(10);
	    	    break;
	     case 3: 
	    	    long count = db.getUserCount();
	    	    resp.getWriter().println(count);
	    	    break;
	     case 4:
	    	 
	    	 PushNotification pn = new PushNotification()
	    	  .setAlert("New Grade in: ClassName")
	    	  .setBadge(0).setDeviceTokens("6951517b463ad5b8058e8594a22f8cfd1f34183e80cb053f533c0387a7e1e943");
	    	 SendPushNotification dt = new SendPushNotification(pn);
	    	
	    	 Queue sq = QueueFactory.getQueue("saver");
	    	 sq.add(TaskOptions.Builder.withPayload(dt));
		
	    	 break;
	    	    
	   }
	}
	
	
	public void updateAnnouncements(){
		db.deleteAnnouncements();
		Fetcher<Announcement> af = new announcementsFetcher();
		Announcement [] announcements = af.fetch();
		if(announcements.length > 0){
			for(Announcement a : announcements){
				db.insertAnnouncements(a.getDate(), a.getAnnouncement());
			}
		}
	}
	
	/*
	public void updateGrades(){
		Student [] allStudents  = db.getAllStudents();
		for(Student s : allStudents){
			Course [] courses = s.getCourses();
			Fetcher <Course> gradeFetcher = new gradeFetcher(s);
			Course [] newCourses = gradeFetcher.fetch();
			for(int i = 0; i < courses.length; i++){
				if(courses[i] == null)break;
				System.out.println(courses[i] + " course1415");
				Assignment [] assignments = courses[i].getAssignments();
				Assignment [] newAssignments = newCourses[i].getAssignments();
				for(Assignment as : newAssignments){
					if(!exists(as, assignments)){
						System.out.println(as + " : new assignment found!!!!");
					    db.assignmentInsert(as.getCourse_id(), as.getName(), as.getDueDate(), as.getAssignedDate(), as.getType(), as.getScore(), as.getTotalPoints(), as.getWeight());
					}
				}
			}
		}
	}
	*/
	
	

	
	
	private boolean exists(Assignment a, Assignment[] array){
		for(Assignment assign : array){
			if(assign.equals(a))
				return true;
		}
		
		return false;
	}
	
  /*
	public void updateGrades(){
	   Student [] allStudents = db.getAllStudents();
	   System.out.println(allStudents.length + " : all students count");
	   for(Student s : allStudents){
		   if(s == null)break;
		   System.out.println("The user " + s);
		   User u = s.getUser();
		   db.deleteCourses(u);
		   Fetcher <Course> gf = new gradeFetcher(s);
		   Course [] newCourses = gf.fetch();
		   db.insertCourses(newCourses);
	   }
	   
	
	}
	
	

	** uses task queue **
	public void updateGrades()
	{
		
	   Queue queue = QueueFactory.getDefaultQueue();
	   FetchUpdater fu = new FetchUpdater();
	   queue.add(TaskOptions.Builder.withPayload(fu));
	   
	}
	
	*/
	

	public void updateGrades()
	{
	   int userCount = (int)db.getUserCount();
	   int x = (userCount/2);
	    if(userCount % 2 != 0){
	    	   x++;
	    }
	    System.out.println(userCount % 2 != 0);
	   System.out.println("The user count: " + x);
	   long delay = 100;
	   for(int i = 0; i < x; i++){
		   delay = delay + 10000;
		 Queue queue = QueueFactory.getDefaultQueue();
		 FetchUpdater fu = new FetchUpdater();
		 if(i == (x-1)){
			 fu.last = true;
		 }
		 queue.add(TaskOptions.Builder.withPayload(fu).etaMillis(System.currentTimeMillis() + delay));
	   }
	   
	   JVDBManager db = new JVDBManager();
	   db.emptyCursor();
	}
	
	

	
}
