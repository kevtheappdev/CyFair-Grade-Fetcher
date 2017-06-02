# Cy Fair Grade Fetcher



My Junior year of High School, I published the JVHS (Jersey Village High School) app that allowed students to access and be notified of their grades. The cost of keeping such a service running on Google App Engine proved to be too difficult and I took down the service by the end of that school year. This repository contains the entirety of the back end code I used to run the app. I am no longer maintaining and have not intention to improve or add features to this code, I have published it here for the sole purpose for others who may find interest in developing their own app/service for checking grades in a more elegant way for Cy Fair School(s).
## Structure and features

The grade fetching occurred by crawling the Cy-Fair Home Access Center site periodically via a cron job. To crawl it, user login credentials were stored in a database (in not the most secure way but I'll get to that later) and these credentials were sent to the login forms return address and the cookies from the request are saved for the following request to the grades page itself. These grades were then cached in Google App Engine's Datastore for later retreival when the user queried the service from their device. There is also functionality to retreive Announcements from the Jersey Village High School website though this may be out of date considering its been over a year. I made extensive use of ADT's and Abstract to package data efficiently and all API calls return data in JSON format with a GET request parameter 'type' (an integer value) specifying what data to return (i.e. Announcements, Teacher Websites, Grades).

### 'Type' parameters for main.java endpoint
0 - Authenticates user with 'username' and 'password'
1 - Gets the user's grade, the user is given by 's_id', the students id, as returend from call with type '0'
2 - Gets listing of Teacher objects from the datastore and outputs the result
3 - Gets cached announcements from datastore and outputs
4 - Gets teachers from the Jersey Village High School web site's listing and saves them into the datastore
5 - Deletes all Teacher entries from the datastore (for refreshing the faculty listing yearly)
6 - Gets announcements from the Jersey Village High School web site and savs them into the datastore
7 - Clears the datastore of assignments (honestly don't remember why I made this function)
8 - Registers iOS device for grad push notifications

### 'Type' parameters for JvappServlett.java endpoint
0 - Updates grades for all students, called via cron job
1 - Updates announcements with the current for the day, called via cron job
2 - Don't remember exactly what this one is for, will update soon as I reinvestigate this code base. Something to do with keeping track of the number of users
3 - Returns the current user count
4 - Sends a test push notification to what at the time was my device




## Potential Issues and Downsides

There are a number of known issues with this code base stemming from my lack of expertise at the time in the area of building RESTful API's and backend code. One is that user credentials are not stored with any kind of encryption - a security feature that is definitely a must. Dictating the type of response from a single endpoint with simply an integer on a post request was also a poor design decision, separate endpoints should have been crafted for each function to be carried out. Another security issue lies in the fact that all data seems to be passed via GET request which is definitely not how passwords or authentication data should be sent over the internet. The final major issue comes from the fact that Datastore reads and writes can become very expensive, and this code base does little to mitigate that.

## Conclusion
If you are looking to make a grade checking app, I hope you may find some of my code useful. Otherwise, I hope you found this juvenile attempt at a RESTful backend amusing :)
