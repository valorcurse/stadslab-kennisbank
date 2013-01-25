package kennisbank

class ProjectMember {

	String username
	
    static constraints = {
    }
	
	static belongsTo = [project:Project]
	
	String getUsername() {
		return username
	}
	
}
