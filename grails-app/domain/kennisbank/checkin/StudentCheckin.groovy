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

	// StudentCheckout checkout

	static hasMany = [equipment: Equipment, checkouts: StudentCheckout]

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
		// checkouts nullable: true
	}

	def beforeInsert() {
		// checkout = new StudentCheckout()
		// if (checkout.save()) {
		// 	checkout.checkin = this
		// }
	}

	static mapping = {
		equipment lazy: false
	}
	
	
}
