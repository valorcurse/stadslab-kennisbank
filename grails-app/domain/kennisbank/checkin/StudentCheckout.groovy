package kennisbank.checkin

import kennisbank.project.Project

class StudentCheckout extends Checkout {

	// StudentCheckin checkin

	static hasOne = [checkin: StudentCheckin]

	static constraints = {
		checkin nullable: true
	}

	static mapping = {
		checkin lazy: false
	}

	def beforeInsert() {
	}
}
