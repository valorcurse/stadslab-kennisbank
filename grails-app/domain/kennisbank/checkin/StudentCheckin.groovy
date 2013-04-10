package kennisbank.checkin

import kennisbank.equipment.Equipment

class StudentCheckin extends Checkin {

	String 	studentNumber, 
			firstName, 
			lastName, 
			email, 
			institute, 
			study, 
			course, 
			teacher

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

	// def beforeInsert() {
	// 	println "Creating checkout in StudentCheckin"
	// 	checkout = new StudentCheckout()
	// 	if (checkout.save()) {
	// 		checkout.checkin = this
	// 		println "Saved from domain: " + checkout.uniqueID
	// 	}
	// }

	static mapping = {
		equipment lazy: false
	}
}
