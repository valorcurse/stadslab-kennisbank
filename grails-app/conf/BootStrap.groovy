import kennisbank.*

class BootStrap {

    def init = { servletContext ->
    
		new Project(title: "Kennisbank", course: "Technische Informatica", summary:"").save(flush: true, failOnError: true)
		new User(username: "admin", password: "admin").save(flush: true, failOnError: true)
		
		}
    def destroy = {
    }
}
