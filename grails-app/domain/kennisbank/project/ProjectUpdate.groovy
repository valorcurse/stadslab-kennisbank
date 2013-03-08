package kennisbank.project

import kennisbank.User

class ProjectUpdate {

	User author
	String message
	Date dateCreated
	
    static constraints = {
	}
	
	static belongsTo = [project:Project]
	
	void setAuthor(User author) {
		this.author = author
	}
	
	User getAuthor() {
		return author
	}
	
	void setMessage(String message) {
		this.message = message
	}
	
	String getMessage() {
		return message
	}
	
	
}
