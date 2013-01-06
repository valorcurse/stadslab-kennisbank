import kennisbank.*

class BootStrap {

    def init = { servletContext ->
    
		//new User(username:"valorcurse", password:"123456").save(failOnError: true)
		
		new Project(title: "Kennisbank", course: "Technische Informatica", summary:"").save(flush: true, failOnError: true)
		
		}
    def destroy = {
    }
}
