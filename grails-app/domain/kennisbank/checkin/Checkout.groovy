package kennisbank.checkin

import kennisbank.AttachedFile
import kennisbank.equipment.Setting

class Checkout {

	String title
	String description
	String picturePath
	
	static hasMany = [files: AttachedFile, settings: Setting]
	static hasOne = [checkin: Checkin]


	static constraints = {
		picturePath nullable: false
		title blank: false, nullable: false, unique: true
		settings nullable: false
		files nullable: false
		description nullable: false, blank: false
	}

	static mapping = {
		settings lazy: false
		files lazy: false
		checkin lazy: false
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
	}

}
