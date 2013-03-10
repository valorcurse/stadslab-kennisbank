package kennisbank.checkin

class CompanyCheckin {

	Date dateCreated
	
	String 	companyName,
			contactPerson,
			email,
			numberOfWorkers,
			projectDescription

	String[] equipment

	static mapping = {
		projectDescription type: "text"
	}
}
