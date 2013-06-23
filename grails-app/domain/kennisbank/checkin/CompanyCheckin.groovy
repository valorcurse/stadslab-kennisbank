package kennisbank.checkin

import kennisbank.equipment.*

class CompanyCheckin extends Checkin {

	String 	companyName,
			contactPerson,
			email,
			numberOfWorkers,
			projectDescription

	static hasMany = [checkouts: Checkout]

	static mapping = {
		projectDescription type: "text"
	}

	static constraints = {
		contactPerson blank: false
		email blank: false, email: true
		numberOfWorkers blank: false, matches: "[0-9]+"
		projectDescription blank: false
		checkouts lazy: false
	}
}
