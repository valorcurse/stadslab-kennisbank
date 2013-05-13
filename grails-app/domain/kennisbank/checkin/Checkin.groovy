package kennisbank.checkin

import kennisbank.equipment.Equipment

class Checkin {

	Date dateCreated

	static hasMany = [equipment: Equipment]

	static constraints = {
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
