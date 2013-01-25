package kennisbank

class Project {

	String title, summary, course
	Date dateCreated
	List projectMembers
	
	static hasMany = [document:Document, tags:Tag, projectMembers:ProjectMember, information:Information]

	static constraints = {}

	static mapping = {
		summary type: "text"
		projectMembers lazy: false
	}
	
	String getTitle() {
		return title
	}
	
	String getSummary() {
		return summary
	}

	String getCourse() {
		return course
	}
	
	Date getDateCreated() {
		return dateCreated
	}
	
	void setSummary(String s) {
		summary = s
	}
}
