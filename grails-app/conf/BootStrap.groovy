import kennisbank.*
import kennisbank.checkin.StudentCheckin

class BootStrap {

    def init = { servletContext ->
    
		//new Project(title: "Kennisbank", course: "Technische Informatica", summary:"").save(flush: true, failOnError: true)
		//new User(username: "admin", password: "admin").save(flush: true, failOnError: true)
		//new StudentCheckin(studentNumber: "5464874", firstName: "Jack", lastName: "Taylor", email: "JT@hotmail.com", institute: "CMI", study: "TI", course: "ComputerTalen", teacher: "Bropj").save(flush: true, failOnError: true)
		}
    def destroy = {
    }
}
