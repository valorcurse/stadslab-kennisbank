import kennisbank.User;

class BootStrap {

    def init = { servletContext ->
    
		new User(username:"valorcurse", password:"123456").save(failOnError: true)
		
		}
    def destroy = {
    }
}
