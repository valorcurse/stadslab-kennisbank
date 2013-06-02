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
	// byte[] picture
	List settings

	static hasMany = [files: AttachedFile, settings: Setting]
	static hasOne = [checkin: Checkin]


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

	def afterInsert() {
		checkin.closed = true

		def rootDir = new File("/var/stadslab/checkouts/" + title)							
		rootDir.mkdirs()

		def pictureSource = new File (picturePath)
		def pictureDestination = new File (rootDir.absolutePath + "/" + 
			(pictureSource.name =~ /\d+\.tmp/).replaceAll(""))

		pictureDestination.createNewFile()

		pictureSource.withInputStream { is -> 
			pictureDestination << is 
		}

		picturePath = pictureDestination.absolutePath

		for (file in files) {
			def fileSource = new File (file.path)
			def fileDestination = new File (rootDir.absolutePath + "/" + 
				(fileSource.name =~ /\d+\.tmp/).replaceAll(""))

			fileDestination.createNewFile()

			fileSource.withInputStream { is -> 
				fileDestination << is 
			}

			file.path = fileDestination.absolutePath
		}
		save()
	}

}
