FabTool
===================

##What is this?
This is a web application written as a student project to be used at the 
[Stadslab Rotterdam](http://stadslabrotterdam.nl/). The Stadslab is a hackerspace created from the joint efforts of 
the city of Rotterdam and the Rotterdam University of Applied Sciences to give everyone access to different types of machinery
otherwise unavailable to most people. The price for using of the facilities is releasing the knowledge accquired while
using them.  
This web app facilitates the gathering of that knowledge by giving users a simple and easy way to share it. This happens
by requiring users to *Check in* before entering the lab, this consisting of entering the purpose of their visit and 
what equipment they will be using, and *Check out* before leaving, this consisting of uploading the source files used in
their project, along with the settings used with the equipment, a short description of the project and a photo.

This application is a first prototype, not thoroughly tested or used yet, and will be further developed by other students.
It's as of now completly translated in dutch.

###Database:
  
	- Type: MySQL
    		- Name: Kennisbank, Kennisbank_test
			- Username: root
			- Password: 123456
			- Port: 3307

Check configuration on DataSource.groovy to match your system's.

###Checkouts:

    Checkouts are saved in /var/stadslab/checkouts.
    Make sure the directory exists and has the right permissions.


###Demo:
A demo website is running [here](http://145.24.222.154:8080/).  
The checkins/checkouts can be found [here](http://145.24.222.154:8080/checkinout).
	
  
#####Admin account:

    Username: admin
    Password: 12345
