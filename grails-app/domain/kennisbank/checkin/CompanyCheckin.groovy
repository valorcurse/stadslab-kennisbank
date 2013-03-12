package kennisbank.checkin

class CompanyCheckin {

	Date dateCreated
	
	String 	companyName,
			contactPerson,
			email,
			numberOfWorkers,
			projectDescription

	static mapping = {
		projectDescription type: "text"
	}

	static constraints = {
		contactPerson blank: false
		email blank: false, email: true
		numberOfWorkers blank: false, matches: "[0-9]+"
		projectDescription blank: false
	}
}
