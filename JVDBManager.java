package com.kevinturner.jv;


import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.QueryResultList;

import java.util.Arrays;
import java.util.Iterator;

public class JVDBManager extends DatabaseManager {
  private DatastoreService conn;
	
	public JVDBManager(){
		super();
		conn = getConnection();
	}
	
	
	public void updateUserCount(long userCount){
		Query q = new Query("UserCount");
		PreparedQuery pq = conn.prepare(q);
		Iterator<Entity> it = pq.asIterator();
		if(it.hasNext()){
		  Entity currentCount = it.next();
		  currentCount.setProperty("count", userCount);
		  conn.put(currentCount);
		} else {
			Entity count = new Entity("UserCount");
			count.setProperty("count", userCount);
			conn.put(count);
		}
	}
	
	public long getUserCount(){
		Query query = new Query("UserCount");
		PreparedQuery pq = conn.prepare(query);
        Iterator<Entity> it = pq.asIterator();
        if(it.hasNext()){
        	   return (long) it.next().getProperty("count");
        }
        return 0;
	}
	
	public void incrementUserCount(){
		Query q = new Query("UserCount");
		PreparedQuery pq = conn.prepare(q);
		Iterator<Entity> it = pq.asIterator();
		if(it.hasNext()){
		  Entity currentCount = it.next();
		  long current = (long) currentCount.getProperty("count");
		  currentCount.setProperty("count", ++current);
		  conn.put(currentCount);
		} else {
			updateUserCount(0);
		}
	}
	
	public void courseInsert(Course c){
		
		  
		  Entity course = new Entity("Course");
		  System.out.println(course);
		  course.setProperty("courseName", c.getCourseName());
		  course.setProperty("courseAverage", c.getAverage());
		  course.setProperty("studentID", c.getStudent_id());
		  course.setProperty("id", c.getId());
		  course.setProperty("archived", false);
		  course.setProperty("totalAssignments", c.getAssignments().length);
		 
		  conn.put(course);
	} 
	
	
	public void assignmentInsert(String id, String name, String dueDate, String assignedDate, String type, double grade, double totalPoints, double weight){
	
		Entity assignment = new Entity("Assignment");
		assignment.setProperty("id", id);
		assignment.setProperty("name", name);
	    assignment.setProperty("dueDate", dueDate);
	    assignment.setProperty("assignedDate", assignedDate);
	    assignment.setProperty("grade", grade);
	    assignment.setProperty("totalPoints", totalPoints);
	    assignment.setProperty("weight", weight);
	    assignment.setProperty("type", type);
	    assignment.setProperty("archived", false);
	    
	    conn.put(assignment);
	}
	
	
	private Cursor getCursor(){
 		Query q = new Query("Cursor");
		PreparedQuery pq = conn.prepare(q);
		Iterator<Entity> it = pq.asIterator();
		if(it.hasNext()){
			Entity e = it.next();
			String cursor = (String)e.getProperty("cursor");
			if(cursor.equals("")){
				System.out.println("cursor is empty");
				return null;
			}		
			 return Cursor.fromWebSafeString(cursor);
		}
		
		return null;
	}
	
	
	public void emptyCursor(){
		Query q = new Query("Cursor");
		PreparedQuery pq = conn.prepare(	q);
		Iterator<Entity> it = pq.asIterator();
		if(it.hasNext()){
			Entity e = it.next();
			e.setProperty("cursor", "");
			conn.put(e);
		}
	}
	
	private void saveCursor(String cursor){
		Query q = new Query("Cursor");
		PreparedQuery pq = conn.prepare(q);
		Iterator<Entity> it = pq.asIterator();
		if(it.hasNext()){
			Entity e = it.next();			
			e.setProperty("cursor", cursor);
			conn.put(e);
		}  else {
			Entity newCursor = new Entity("Cursor");
			newCursor.setProperty("cursor", cursor);
			conn.put(newCursor);
		}
	}
	
	
	public void registerForPushNotifications(String deviceType, String deviceToken, String username, String password){
		Filter usernameFilter = new FilterPredicate("username", FilterOperator.EQUAL, username);
		Filter passwordFilter = new FilterPredicate("password", FilterOperator.EQUAL, password);
		Filter composite = CompositeFilterOperator.and(usernameFilter, passwordFilter);
		Query q = new Query("Student").setFilter(composite);
		PreparedQuery pq = conn.prepare(q);
		
		Iterator<Entity> it = pq.asIterator();
		if(it.hasNext()){
			Entity studentEntity = it.next();
			studentEntity.setProperty("deviceType", deviceType);
			studentEntity.setProperty("deviceToken", deviceToken);
		   conn.put(studentEntity);
		}
	}
	
	
	public Student [] getAllStudents(){
		Query sq = new Query("Student");
		PreparedQuery pq = conn.prepare(sq);
		Cursor c = getCursor();
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(2);
		if(c != null){
			System.out.println("Cursor is not null");
			fetchOptions.startCursor(c);
		}
		
		QueryResultList<Entity> list = pq.asQueryResultList(fetchOptions);
		  System.out.println("Fetched students now");
		  String cursor = list.getCursor().toWebSafeString();
		  saveCursor(cursor);
		  Student [] students = new Student[2];
		  int i = 0;
		 for(Entity es : list){
			
			 System.out.println("populating...");
			  String studentID = (String)es.getProperty("studentID");
			  String username = (String)es.getProperty("username");
			  String password = (String)es.getProperty("password");
		      String deviceType = (String)es.getProperty("deviceType");
		      String deviceToken = (String)es.getProperty("deviceToken");
			  Course [] courses = this.getStudentsCourses(studentID);
		      Student s = new Student(studentID, username, password, courses, deviceType, deviceToken);
		      students[i++] = s;
		  }
		  
		  
		  
		  return students;
	}

	
	public void deleteCourses(User u){
		Filter userFilter = new FilterPredicate("studentID", FilterOperator.EQUAL, u.getUserID());
		Filter archivedFilter = new FilterPredicate("archived", FilterOperator.EQUAL, false);
		Filter composite = CompositeFilterOperator.and(userFilter, archivedFilter);
		Query q = new Query("Course").setFilter(composite);
		PreparedQuery pq = conn.prepare(q);
		Iterator <Entity> it = pq.asIterator();
		while(it.hasNext()){
			Entity item = it.next();
			conn.delete(item.getKey());
		}
	}
	
	public void deleteAnnouncements(){
		Query q = new Query("Announcement");
		PreparedQuery pq = conn.prepare(q);
		Iterator<Entity> it = pq.asIterator();
		while(it.hasNext()){
			conn.delete(it.next().getKey());
		}
	}
	
	public void deleteAssignments(Course c){
		System.out.println(c + "course");
		Filter courseFilter = new FilterPredicate("id", FilterOperator.EQUAL, c.getId());
	    Filter archivedFilter = new FilterPredicate("archived", FilterOperator.EQUAL, false);
	    Filter composite = CompositeFilterOperator.and(courseFilter, archivedFilter);
	    Query q = new Query("Course").setFilter(composite);
	    PreparedQuery pq = conn.prepare(q);
	    Iterator <Entity> it = pq.asIterator();
	    while(it.hasNext()){
	    	  Entity item = it.next();
	    	  item.setProperty("archived", true);
	    	  conn.put(item);
	    }
	}

	
	public void emptyAssignments(){
		Query q = new Query("Assignment");
		PreparedQuery pq = conn.prepare(q);
		Iterator <Entity> it = pq.asIterator();
		while(it.hasNext()){
			conn.delete(it.next().getKey());
		}
	}
	
	
	public void insertAnnouncements(String date, String announcement){
		
		
		Entity announcements = new Entity("Announcement");
		announcements.setProperty("date", date);
		announcements.setProperty("announcement", announcement);
		
		conn.put(announcements);
	}
	
	public Announcement [] getAnnouncements(){
	   Query q = new Query("Announcement");
	    PreparedQuery pq = conn.prepare(q);
	  Announcement [] announcements = new Announcement[pq.countEntities()+5];
	  int i = 0;
	  for(Entity e : pq.asIterable()){
		 String date = (String)e.getProperty("date");
		 String announcement = (String)e.getProperty("announcement");
		 announcements[i++] = new Announcement(announcement, date);
	  }
	  return announcements;
	}
	
	
	
	public boolean registerStudent(Student s){
	   if(this.authenticateStudent(s.getUsername(), s.getPassword()) == null){	
			//System.out.println((new Crawler()).logOn(s.getUsername(), s.getPassword()) + "logged on");
			if((new Crawler()).logOn(s.getUsername(), s.getPassword())){
		
			Entity student = new Entity("Student");
			student.setProperty("username", s.getUsername());
			student.setProperty("password", s.getPassword());
			student.setIndexedProperty("studentID", s.getId().toString());
			conn.put(student);
	
			
			Fetcher<Course> fetchGrades = new gradeFetcher(s);
		    Course [] courses = fetchGrades.fetch();
		    for(Course c : courses){
		    	 System.out.println(c);
		    }
		    s.setCourses(courses);
		    insertCourses(courses);
			return true;
			} else {
				return false;
			}
	
	   } 
		return false;
	}
	
	public void insertCourses(Course [] courses){
		if(courses == null)
			return;
		for(Course c : courses){
			if(c != null){
			courseInsert(c);
			
		  Assignment [] assign = c.getAssignments();
		  if(assign.length > 0){
		  System.out.println("last: " + assign[assign.length-1]);
		   for(int i = 0; i < assign.length; i++){
			   Assignment a = assign[i];
			  if(a == null)break; 
			   System.out.println(a);
			  assignmentInsert(a.getCourse_id(), a.getName(), a.getDueDate(), a.getAssignedDate(), a.getType(), a.getScore(), a.getTotalPoints(), a.getWeight());
		   }
		  }
		}
		}
	}
	
	public User authenticateStudent(String username, String password){
	
		Filter usernameFilter = new FilterPredicate("username", FilterOperator.EQUAL, username);
		Filter passwordFilter = new FilterPredicate("password", FilterOperator.EQUAL, password);
		Filter composite = CompositeFilterOperator.and(usernameFilter, passwordFilter);
		Query q = new Query("Student").setFilter(composite);
		PreparedQuery pq = conn.prepare(q);
		
	   Iterator<Entity> it = pq.asIterator();
	   if(it.hasNext()){
		   Entity user = it.next();
		 String id =   (String)user.getProperty("studentID");
		   return new User(username, password, id);
	   }
		    
		return null;
	}
	
	
	public Course [] getStudentsCourses(String id){

			System.out.println(id + ": id");
		
			Filter studentFilter = new FilterPredicate("studentID", FilterOperator.EQUAL, id);
			Filter currentFilter = new FilterPredicate("archived", FilterOperator.EQUAL, false);
			Filter bothFilters =
					  CompositeFilterOperator.and(studentFilter, currentFilter);

		    Query q = new Query("Course").setFilter(bothFilters);
		    PreparedQuery pq = conn.prepare(q);
		    
			
			Course [] courses = new Course[12];
		    Assignment [] assignments = null;
		    FetchOptions fa = FetchOptions.Builder.withChunkSize(200);
		    int x = 0;
		    Iterator<Entity> iterator = pq.asIterator(fa);
			while(iterator.hasNext()){
		    	    if(x == 7)break;
		    	    
		    	   Entity course = iterator.next();
		    	  String c_id = (String) course.getProperty("id");
		    	  
		    	  String courseName = (String)course.getProperty("courseName");
		    	  System.out.println("retreived course name");
		    	  double average = (Double)course.getProperty("courseAverage");
		    	  Long long_numberOfAssignments = (Long)course.getProperty("totalAssignments");
		    	  int numberOfAssignments = long_numberOfAssignments.intValue();
		    	  System.out.println("Number of assignments " + numberOfAssignments);
		    	  assignments = new Assignment[numberOfAssignments];
		    	  
		    	  if(numberOfAssignments != 0){
		    	  Filter assignFilter = new FilterPredicate("id", FilterOperator.EQUAL, c_id);
		    	   Query aq  = new Query("Assignment").setFilter(assignFilter);
		    	  PreparedQuery paq = conn.prepare(aq);
		    	  FetchOptions fo = FetchOptions.Builder.withChunkSize(200);
		    	  Iterator<Entity> it = paq.asIterator(fo);
		    	  
		    	  int i = 0;
		    	  while(it.hasNext()){
		    		  Entity assignment = it.next();
		    		  
		    		  String name = (String)assignment.getProperty("name");
		    		  String dueDate = (String)assignment.getProperty("dueDate");
		    		  String assignedDate = (String)assignment.getProperty("assignedDate");
		    		  String type = (String)assignment.getProperty("type");
		    		  double grade = (Double)assignment.getProperty("grade");
		    		  double totalPoints = (Double)assignment.getProperty("totalPoints");
		    	      double weight = (Double)assignment.getProperty("weight");
		    	      Assignment a =  new Assignment(c_id, name, dueDate, assignedDate,type, grade, weight, totalPoints);
		    	   
		    	     assignments[i++]  = a;
		    	      
		    	  }
		    	     printArray(assignments);
		    	     Arrays.sort(assignments, 0, i);
		    	  }
		    	                 System.out.println(courseName + "coursename");
		    	  courses[x++] = new Course(c_id, courseName, average, assignments, id);
		    	    
		    }
		    Arrays.sort(courses, 0, x);
			
		    return courses;
	
	
	}
	
	void printArray(Object [] arr){
		System.out.println("Assignments");
		for(Object o : arr){
			System.out.println(o);
		}
	}
	
	public Teacher[] getTeachers(){
    
		Query q = new Query("Teacher");
		PreparedQuery pq = conn.prepare(q);
		
		    Teacher [] teachers = new Teacher[1000];
		    
		    Iterator<Entity> it = pq.asIterator();
			 int i = 0;
		    while(it.hasNext()){
		    	 Entity teacher = it.next();
		    	 String name = (String)teacher.getProperty("name");
	         String url = (String)teacher.getProperty("url");
			  teachers[i++] = new Teacher(url, name);
		   
			 
		    }
		    
		    Arrays.sort(teachers, 0, i);
		    return teachers;
	
	
	}
	
	
	public void insertTeacher(Teacher t){
		
		Entity teacher = new Entity("Teacher");
		teacher.setProperty("url", t.getPageUrl());
		teacher.setProperty("name", t.getName());
		
		conn.put(teacher);
		
	}
	
	public void deleteTeachers(){
		Query q = new Query("Teacher");
		PreparedQuery pq = conn.prepare(q);
		Iterator<Entity> it = pq.asIterator();
				while(it.hasNext()){
				   conn.delete(it.next().getKey());
				}
	}
	
    
	
	
}