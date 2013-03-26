package kennisbank.checkin

import kennisbank.project.Project
import kennisbank.File

class StudentCheckout extends Checkout {

	StudentCheckin checkin

	static constraints = {
		checkin nullable: true
	}

	static mapping = {
		checkin lazy: false
	}

	def beforeInsert() {
	}
}