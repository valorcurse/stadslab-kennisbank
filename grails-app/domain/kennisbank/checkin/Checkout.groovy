package kennisbank.checkin

import kennisbank.project.Project
import kennisbank.File
import kennisbank.equipment.Material
import kennisbank.equipment.Equipment
import org.apache.commons.lang.RandomStringUtils

class Checkout {

	Date dateCreated
	String title
	Boolean idGenerated = false, published = false
	String picturePath = "emptyImage.gif"

	static hasMany = [files: File, equipment: Equipment]

	static constraints = {
		picturePath nullable: true
	}

	static mapping = {
		materials lazy: false
	}

}
