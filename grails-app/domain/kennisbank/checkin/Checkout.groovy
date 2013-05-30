package kennisbank.checkin

import kennisbank.project.Project
import kennisbank.AttachedFile
import kennisbank.equipment.Material
import kennisbank.equipment.Equipment
import kennisbank.equipment.Setting
import org.apache.commons.lang.RandomStringUtils

class Checkout {

	// Date dateCreated
	String title
	Boolean published = false
	String description
	String picturePath
	List settings

	static hasMany = [files: AttachedFile, settings: Setting]

	static constraints = {
		picturePath nullable: false
		title blank: false, nullable: false
		settings nullable: false
		files nullable: false
		description nullable: false, blank: false
	}

	static mapping = {
		settings lazy: false
		description type: "text"
	}

}
