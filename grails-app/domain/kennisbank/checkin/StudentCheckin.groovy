package kennisbank.checkin

class StudentCheckin {

	Date dateCreated
	
	
	String 	studentNumber, 
			firstName, 
			lastName, 
			email, 
			institute, 
			study, 
			course, 
			teacher

	String[] equipment

	static constraints = {
		studentNumber size: 7..7, matches: "[0-9]+", blank: false
		email email: true, blank: false
		equipment minSize: 1
		firstName blank: false
		lastName blank: false
		institute blank: false
		study blank: false
		course blank: false
		teacher blank: false
	}

	static mapping = {
	}
	
	
}
