package kennisbank.checkin

import kennisbank.project.Project
import kennisbank.File
import kennisbank.equipment.Material
import kennisbank.equipment.Equipment
import kennisbank.equipment.Setting
import org.apache.commons.lang.RandomStringUtils

class Checkout {

	Date dateCreated
	String title
	Boolean published = false
	String picturePath = "emptyImage.gif"
	List settings

	static hasMany = [files: File, settings: Setting]

	static constraints = {
		picturePath nullable: true
		title blank: false, nullable: true
	}

	static mapping = {
		materials lazy: false
		settings lazy: false
	}

}
