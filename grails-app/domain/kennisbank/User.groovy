package kennisbank

import java.util.Date;

class User {
	
	String username, password
	
	static constraints = {
    }

	String getUsername() {
		return username
	}

	String getPassword() {
		return password
	}
	
}