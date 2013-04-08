package kennisbank.checkin

import kennisbank.equipment.Equipment

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

	static hasOne = [checkout: StudentCheckout]

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
		checkout nullable: true
	}

	def beforeInsert() {
		println "Creating checkout in StudentCheckin"
		checkout = new StudentCheckout()
		if (checkout.save()) {
			checkout.checkin = this
			println "Saved from domain: " + checkout.uniqueID
		}
		else println "Nope, Chuck Testa"
	}

	static mapping = {
		equipment lazy: false
	}
}
