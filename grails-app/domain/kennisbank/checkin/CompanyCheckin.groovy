package kennisbank.checkin

import kennisbank.equipment.*

class CompanyCheckin extends Checkin implements Serializable {

	String 	companyName,
			contactPerson,
			email,
			numberOfWorkers,
			projectDescription

	Object[] getInfo() {
		return [companyName, contactPerson, email, numberOfWorkers, projectDescription, dateCreated.toString()]
	}

	//static hasMany = [checkouts: Checkout]

	static mapping = {
		projectDescription type: "text"
		checkouts lazy: false
	}

	static constraints = {
		contactPerson blank: false
		email blank: false, email: true
		numberOfWorkers blank: false, matches: "[0-9]+"
		projectDescription blank: false
	}
}
