package kennisbank

class User {

	static belongsTo = [projectmember:ProjectMember]
	
	static constraints = {
    }
}