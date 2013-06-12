package kennisbank.project

class Project {

	String title, summary, course
	Date dateCreated
	List projectMembers, documents

	static hasMany = [documents:Document, tags:Tag, projectMembers:ProjectMember, updates:ProjectUpdate]

	static constraints = {
		summary nullable: true
		course nullable: true
	}

	static mapping = {
		summary type: "text"
		projectMembers lazy: false
		documents lazy: false
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
