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

This application is licensed under the GPLv2 license.

##Configuration

###Frameworks
This project has been developed using the following frameworks:

####Grails
    Grails is an Open Source, full stack, web application framework for the JVM. It takes advantage of the Groovy 
    programming language and convention over configuration to provide a productive and stream-lined development 
    experience.


The Grails version used is __2.2.3__. I recommend using [GVM](http://gvmtool.net/) to install grails if developing on
Linux/Mac OSX or following [this](http://www.grailsexample.net/installing-a-grails-development-environment-on-windows/)
tutorial for Windows.

####Vaadin
    <cite>Vaadin is a Java web application framework. It is designed for creating rich and interactive applications 
    that run in the browser, without any plugins. A server-driven architecture together with reusable component 
    model is used to simplify programming of applications and for better web application security. No HTML, XML or 
    JavaScript necessary and all Java libraries and tools are at your disposal.</cite>

Although it's mainly developed for Java, using the [Vaadin 7 Plugin](http://grails.org/plugin/vaadin) for Grails it's
possible to develop using Groovy.

###Database:
  
	- Type: MySQL
    		- Name: Kennisbank (production), Kennisbank_test (development/test)
			- Username: root
			- Password: 123456
			- Port: 3307
The configuration above is the default now being used. You can change them at conf/DataSource.groovy.

###Fabtool account:
    Default: 
        Username: admin
        Password: 12345

Currently the account used to login to the FabTool is created in conf/BootStrap.groovy.
######Change the following values to choose your own credentials:
    new User(username: "admin", password: "12345", enabled: true).save()

###Projects (checkouts):

Checkouts are saved in /var/stadslab/checkouts.
Make sure the directory exists and has the right permissions.
######Change the following line at domain/checkin/Checkout.groovy to change the directory where checkouts are saved:
    def rootDir = new File("/var/stadslab/checkouts/" + title)

##Documentation
To generate JavaDoc-style documentation of the project run the following command:
#
    grails doc
    
To generate a UML diagram of all the domain class run the following command:
#
    grails create-domain-uml

I tried documenting, commenting and writing as much clean code as possible, but due to lack of time this was not possible everywhere.

##Demo:
A demo website is running [here](http://145.24.222.154:8080/).  
The checkins/checkouts can be found [here](http://145.24.222.154:8080/checkinout).
