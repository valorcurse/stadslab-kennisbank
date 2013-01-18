package kennisbank

class Project {

	String title, summary, course
	Date dateCreated

	static hasMany = [document:Document, tags:Tag, projectmember:ProjectMember, information:Information]

	static constraints = {}

	static mapping = {
		summary type: "text"
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
