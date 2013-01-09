package kennisbank

class Project {

	String title, summary, course
	Date dateCreated

	static searchable = true
	
	static hasMany = [document:Document, tags:Tag, projectmember:ProjectMember, information:Information]

	static constraints = {}

	String getTitle() {
		return title
	}

	String getCourse() {
		return course
	}
	
	Date getDateCreated() {
		return dateCreated
	}
}
