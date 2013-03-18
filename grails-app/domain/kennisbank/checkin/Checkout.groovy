package kennisbank.checkin

import kennisbank.project.Project
import kennisbank.File
import org.apache.commons.lang.RandomStringUtils

class Checkout {

	Date dateCreated	
	String uniqueID
	Boolean idGenerated = false
	File picture

	static hasMany = [files: File]

	static constraints = {
		picture nullable: true
	}

	static mapping = {
	}

	def beforeValidate() {
		if (!idGenerated) {
			generateUniqueID()
			idGenerated = true
		}
	}

	String generateUniqueID() {
		String id = RandomStringUtils.random(5, true, true)
		if (Checkout.findByUniqueID(id) != null) generateUniqueID()
		else uniqueID = id
	}

}
