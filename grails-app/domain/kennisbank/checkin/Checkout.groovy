package kennisbank.checkin

import kennisbank.project.Project
import kennisbank.File
import org.apache.commons.lang.RandomStringUtils

class Checkout {

	Date dateCreated
	String uniqueID, title
	Boolean idGenerated = false, published = false
	String picturePath

	static hasMany = [files: File]

	static constraints = {
		picturePath nullable: true
	}

	static mapping = {
	}

	def beforeValidate() {
		if (!idGenerated) {
			generateUniqueID()
			title = uniqueID
			idGenerated = true
			picturePath = "emptyImage.gif"
			println "Picture path was set on domain: " + picturePath
		}
	}

	void setPicturePath(String path) {
		picturePath = path
	}

	String generateUniqueID() {
		String id = RandomStringUtils.random(5, true, true)
		if (Checkout.findByUniqueID(id) != null) generateUniqueID()
		else uniqueID = id
	}

}
