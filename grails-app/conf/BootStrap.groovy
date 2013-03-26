import kennisbank.*
import kennisbank.checkin.StudentCheckin

class BootStrap {

    def init = { servletContext ->
    
		//new Project(title: "Kennisbank", course: "Technische Informatica", summary:"").save(flush: true, failOnError: true)
		//new User(username: "admin", password: "admin").save(flush: true, failOnError: true)
		new StudentCheckin(studentNumber: "0840416", firstName: "Marcelo", lastName: "Dias Avelino", email: "valorcurse@gmail.com", 
							institute: "CMI", study: "Technische Informatica", course: "ICT-Lab", teacher: "Abd el Ghany").save(flush: true)

		}
    def destroy = {
    }
}
