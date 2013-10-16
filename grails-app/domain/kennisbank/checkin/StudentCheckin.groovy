package kennisbank.checkin

import kennisbank.equipment.Equipment

class StudentCheckin extends Checkin implements Serializable {

	String 	studentNumber, 
			firstName, 
			lastName, 
			email, 
			institute, 
			study, 
			course, 
			teacher

	Object[] getInfo() {
		return [studentNumber, firstName, lastName, email,	institute, study, 
					course, teacher,  equipment*.name.toString(), dateCreated.toString()]
	}

	static hasMany = [equipment: Equipment]

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
		equipment lazy: false
		checkouts lazy: false
	}
	
	
}
