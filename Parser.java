package com.kevinturner.jv;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.*;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {
	private Document doc;
	 private static final Logger log = Logger.getLogger(Parser.class.getName());
	public Parser(Document doc){
		this.doc = doc;
	}
	
	
	/*
	public Teacher  teachers(Document doc){
		if(doc != null){
			
			Element name = doc.select("span#ctl00_cphMain_lblName").first();
			Element email = doc.select("div#ctl00_cphMain_divPrimaryEmail").first();
			Element phone = doc.select("span#ctl00_cphMain_lblOfficePhoneValue").first();
			Element photo = doc.select("img#ctl00_cphMain_imgUserPic").first();
			
			Element subject = doc.select("span#ctl00_cphMain_lblPrimaryTitle").first();
			  Element descriptionContainer = doc.select("table#ctl00_cphMain_tblWelcomeNote").first();
			  Element descriptionTD = descriptionContainer.select("td").get(1);
			Element description  = descriptionTD.select("span").first();
			
			print(name.text() + ": name");
			print(email.text() + ": email");
			print(phone.text() + ": phone");
			print(description.text() + ": description");
			print(subject.text() + ": subject");
		
			Teacher t = 
			   new Teacher(name.text(), email.text(), phone.text(), description.text(), subject.text());
			JVDBManager db = new JVDBManager();
			db.insertTeacher(t);
			
			return t;
			
			
		}
		
		return null;
	}
	*/
	
	public void print(Object o){
		System.out.println(o);
	}
	
	
	public Teacher [] teacherLinks(){
		if(doc != null){
			
		   Elements teachers = doc.select("span.arhev");
		  Teacher [] links = new Teacher[teachers.size()];
		   
		   int i = 0;
		   
		   for(Element teacher : teachers){
               Element teachersLink = teacher.select("a").first();
               System.out.println("teachersLink " + teachersLink);
               if(teachersLink != null){
               links[i++] = new Teacher(teachersLink.attr("href"), teachersLink.text());
               }
		   }
		   return links;
		}
		return null;
	}
	
	public Course [] courses(Student s){
		 Course [] c = new Course[15];
		 int count = 0;
		 Elements courses = doc.select("div.AssignmentClass");
		 int i = 0;
		 
		  for(Element course : courses){
			  //System.out.println(e + " Element");
			  //get course name and average
			  int f = 0;
			  System.out.println(f++ + " :f");
	
			   //System.out.println(course + " courses");
			   Element courseName =  course.select("a.sg-header-heading").first();
			   String name = courseName.text();
			   
			   Element average = course.select("span#plnMain_rptAssigmnetsByCourse_lblHdrAverage_" + i++).first();
			   String av = average.text();
			   float deciAverage = 0;
	            if(!av.isEmpty()){
	           String [] avs = av.split(" ");
	           char [] avChars = avs[avs.length - 1].toCharArray();
	             String courseAverage = "";
	             for(int x = 0; x < avChars.length -1; x++){
	            	    courseAverage = courseAverage + avChars[x];
	             }
	             System.out.println(courseAverage);
	             
	            deciAverage = Float.parseFloat(courseAverage);
	            } 
	            
	          
	            UUID id = UUID.randomUUID();
	              
	            c[count++] = new Course(id.toString(),
	            		name, deciAverage, getAssignments(course, id.toString()), s.getId().toString());
	            System.out.println(getAssignments(course, id.toString()));
	            
	            
		  }
		  Arrays.sort(c, 0, count);
		  return c;
	}
	
	private Assignment[] getAssignments(Element course, String id){
		  Assignment [] list = new Assignment[0];
        int listCount = 0;
        Elements sects = course.select("table.sg-asp-table");
        
        for(Element sect : sects){
         String sectID = sect.attr("id");
         String [] idArray = sectID.split("_");
           if(idArray[2].equals("dgCourseAssignments")){
        	System.out.println(idArray[3] + "/n");
       Elements assignments = sect.select("tr.sg-asp-table-data-row");
      list = new Assignment[assignments.size()];
       for(Element el : assignments){
       
       Elements gradeDetails = el.select("td");
        int f = 0;
        String assignDate = "";
        String dueDate = "";
        String paperName = "";
        String type = "";
        float grade = 0;
        float totalPoints = 0;
        float weight = 0;
       // System.out.println(Jsoup.parse(el.outerHtml()));
       for(Element paperGrade : gradeDetails){
          
       	 switch(f){
       	 case 0:
       	   dueDate = paperGrade.text();
       	   break;
       	 case 1:
       	    assignDate = paperGrade.text();
       	  break;
       	 case 2:{
       		 Element pg = paperGrade.getElementsByTag("a").first();
       		//System.out.println(pg + "Element");
       		 paperName = pg.text();
       	
       		break;
       	 }
       	 case 3:
       		 type = paperGrade.text();
       		// System.out.println("type: " + type);
       		 break;
       	 case 4:{
       		 log.info("Case 4, going in");
       		 if(!paperGrade.text().equals("\u00a0")){
       			 System.out.println("paper grade:" + paperGrade.text());
       	 
       
       	if(!paperGrade.text().equals("X") && !paperGrade.text().equals("Z") && !paperGrade.text().equals("I") && !paperGrade.text().equals("NG"))
       		  try {
       		   grade = Float.parseFloat(paperGrade.text());
       		  } catch(Exception e) {
       			  System.out.println("number format");
       		  }
       		 } else if(paperGrade.text().equals("X") || paperGrade.text().equals("I")){
       			 System.out.println("-1");
       			 grade = -1;
       		 }
       		 break;
       	  
       	 }
       	 case 5:
       		 try {
       		  totalPoints = Float.parseFloat(paperGrade.text());
       		 } catch(NumberFormatException e){
       			 
       		 }
       		 break;
       	 case 6:
       		 try {
       		 weight = Float.parseFloat(paperGrade.text());
             } catch(NumberFormatException e){
       			 
       		 }
       		 break;
       	 }
       	   f++;
        }

         System.out.println(paperName + " " + dueDate + " " + assignDate + " " + grade + " " + weight + " " + totalPoints);
          list[listCount++] = new Assignment(id, paperName, dueDate, assignDate, type, grade, weight, totalPoints);  
       }
      }
           
    }

    	   
       return list;
	}
	
	
	public void printArray(Object [] array){
	  for(Object o : array){
		  System.out.println(o);
	  }
	}
	
	public String[] getClasses(){
		
		String [] classes = new String[10];
	
		int count = 0;
		Elements courses = doc.select("a.sg-header-heading");
		for(Element e : courses){
			classes[count++] = e.text();
		}
		return classes;
	}
	
	public Announcement[] getAnnouncements(){
		 Element mainBlock = doc.select("div.row").first();
		 System.out.println("announcements: " + mainBlock);
		 String date = mainBlock.select("h4.foundation-mq-large").first().text();
		   print(mainBlock + " :mainBlock");
		 print(date  + ":date");
		Element announcementsList = mainBlock.getElementsByTag("ul").get(1);
		 Elements announcements = announcementsList.getElementsByTag("li");
		 print("announcements count: " + announcements.size());
		 Announcement [] a = new Announcement[announcements.size()];
		 int i = 0;
		 for(Element announcement : announcements){
			 System.out.println("announcement: " + announcement.text());
			 a[i++] = new Announcement(announcement.text(), date);
		 }
		printArray(a);
		return a;
	}
   
}
