package com.kevinturner.jv;



import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.util.UUID;
import com.google.gson.*;

/**
 * Servlet implementation class main
 */

public class main extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public main() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 //fetch grades 
		System.out.println("get request sent to server");
	   //response.getWriter().println("get request received");
		
		
		
		
	  	
		  JVDBManager db = new JVDBManager();
       int i = Integer.parseInt(request.getParameter("type"));
       
    	String username = request.getParameter("username");
	    String password = request.getParameter("password");
       
		switch(i){
		case 0:
			User u = db.authenticateStudent(username, password);
			 if(u != null){
		    	   Gson g = new Gson();
		    	String json = g.toJson(u);
		    	   response.getWriter().println(json);
		    } else {
		    	System.out.println("not authenticated");
		    	String newUsername = request.getParameter("username");
				   String newPassword = request.getParameter("password");
				   UUID new_id = UUID.randomUUID();		
				   Student new_s = new Student(new_id.toString(), newUsername, newPassword, null, null, null);
				    
				  
				    if(db.registerStudent(new_s)){
				    	db.incrementUserCount();
				    	System.out.println("new user registered");
				       User newUser = new User(newUsername, newPassword, new_id.toString());
				       Gson g = new Gson();
				       String json = g.toJson(newUser);
				       response.getWriter().println(json);
				    } 
		    }
			
		 break;
		 
		case 1:
			System.out.println("case 1");
		   
		    String student_id = request.getParameter("s_id");
		    User student_usr = db.authenticateStudent(username, password);
		    System.out.println(student_usr);
		    if(student_usr != null){
		    	 Course [] cs = db.getStudentsCourses(student_usr.getUserID());
			     System.out.println(cs[0]);
			     System.out.println("user is authenticated " + cs.length);
			    	 printArray(cs, response);
		    } 
		    
		    
		 break;
		case 2:
		   
		   Teacher [] t = db.getTeachers();
		   printArray(t, response);
		   break; 
		case 3:
			   Announcement [] a = db.getAnnouncements();
				printArray(a, response);
			break;
		case 4:
			Fetcher<Teacher> fetch = new teacherFetcher();
			Teacher [] teachers = fetch.fetch();
			System.out.println("fetching teachers");
			response.getWriter().print("Getting teachers")
			;
			for(Teacher teach : teachers){
			
				if(teach != null){
					System.out.println(teach.getName().length() + " : teacher name");
				
					if(teach.getName() != null && teach.getName().length() > 0)
						db.insertTeacher(teach);
				 }
				}
			break;
		case 5:
			db.deleteTeachers();
			
			break;
		case 6:
			Fetcher <Announcement> af = new announcementsFetcher();
			Announcement [] announcements = af.fetch();
			for(Announcement announce : announcements){
				db.insertAnnouncements(announce.getDate(), announce.getAnnouncement());
			}
		case 7:
			db.emptyAssignments();
			break;
		case 8:
			String deviceType = request.getParameter("device_type");
			String deviceToken = request.getParameter("device_token");
		    String username1 = request.getParameter("username");
		    String password1 = request.getParameter("password");
		    db.registerForPushNotifications(deviceType, deviceToken, username1, password1);
     	break;
		case 9:
			response.getWriter().println("This has been a test. Thank you very much ladies and gentlemen");
			break;
		
		   
		} 
		
		/*
		 Fetcher<Course> grades = new gradeFetcher("s650665", "kai66wen");
		 response.setContentType("text/plain");
        response.getWriter().println("Grades: \n");
		Course [] cs = grades.fetch();
		for(Course c : cs){
			response.getWriter().println(c);
		}
		
		UUID id = UUID.randomUUID();			
		
		JVDBManager db = new JVDBManager();
		//db.courseInsert(id, "Algebra II", 92);
		ResultSet rs = db.getCoursesForStudent();
		try {
		while(rs.next()){
			System.out.println("course_id: " + rs.getString("id"));
			System.out.println("courseName: " + rs.getString("courseName"));
			System.out.println("courseAverage: " + rs.getString("average"));
		}
		} catch (Exception e){
			System.out.println("Exception found");
		}
		//fetch teachers
		//Fetcher<Teacher> teachers = new teacherFetcher();
		//response.getWriter().println("Teachers: \n");
		//response.getWriter().println(teachers.fetch());
	*/
		
	}
	

	
	private void printArray(Object [] arr, HttpServletResponse response){
		try {
			
			response.getWriter().print("{\"results:\":[");
			int x = 0;
			for(Object o : arr){
			if(o == null)break;
				Gson g = new Gson();
				String s = g.toJson(o);
				
				if(arr[x+1] != null){
				response.getWriter().println(s+",");
				} else  {
				 response.getWriter().println(s);
				}
		     x++;
			}
		
			response.getWriter().print("]}");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void printArrayT(Teacher [] arr, HttpServletResponse response){
		try {
			response.getWriter().print("{\"results:\":[");
			int x = 0;
		for(Teacher o : arr){
		
				if(o == null)break;
				Gson g = new Gson();
				String s = g.toJson(o);
				if(x != arr.length -1){
				response.getWriter().print(s+",\n");
				} else {
					response.getWriter().print(s);
				}
			x++;
		}
	
			response.getWriter().print("]}\n");
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
     
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("post request sent to server");
	  	
		  JVDBManager db = new JVDBManager();
     int i = Integer.parseInt(request.getParameter("type"));
		switch(i){
		case 0:
		   String username = request.getParameter("username");
		   String password = request.getParameter("password");
		   UUID id = UUID.randomUUID();		
		   Student s = new Student(id.toString(), username, password, null, null, null);
		    
		  
		    if(db.registerStudent(s)){
		    	   response.getWriter().println("success");
		    }
		    
		break;
		
		case 1:
		    	username = request.getParameter("username");
		    password = request.getParameter("password");
		    String student_id = request.getParameter("s_id");
		    if(db.authenticateStudent(username, password) != null){
		    	 Course [] cs = db.getStudentsCourses(student_id);
		     System.out.println(cs[0]);
		    	 printArray(cs, response);
		    }
		    
		 break;
		case 2:
		   
		   Teacher [] t = db.getTeachers();
		   printArrayT(t, response);
		   break; 
		case 3:
			   Announcement [] a = db.getAnnouncements();
				printArray(a, response);
			break;
		}
	}

}
