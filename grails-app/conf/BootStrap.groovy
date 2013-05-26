import kennisbank.*
import kennisbank.checkin.StudentCheckin
import kennisbank.equipment.*

class BootStrap {

	def init = { servletContext ->

		Material glas = new Material(name: "Glas").save(flush: true, failOnError: true)
		Material leer = new Material(name: "Leer").save(flush: true, failOnError: true)
		Material hout = new Material(name: "Hout").save(flush: true, failOnError: true)

		MaterialType duplex = new MaterialType(name: "Duplex", material: Material.findByName("Hout")).save(flush: true, failOnError: true)
		MaterialType triplex = new MaterialType(name: "Triplex", material: Material.findByName("Hout")).save(flush: true, failOnError: true)

		hout.addToMaterialTypes(duplex)
			.addToMaterialTypes(triplex)

		// new Equipment(name: "Folie snijder").save(flush: true, failOnError: true)
		// new Equipment(name: "3D printer").save(flush: true, failOnError: true)
		new Equipment(name: "Laser snijder").addToSettingTypes(new SettingType(name: "Passes"))
											.addToSettingTypes(new SettingType(name: "Power"))
											.addToSettingTypes(new SettingType(name: "Dikte"))
											.addToMaterialTypes(duplex)
											.addToMaterialTypes(triplex)
											.save(flush: true, failOnError: true)

		new StudentCheckin(
			studentNumber: "0840416", firstName: "Marcelo", 
			lastName: "Dias Avelino", email: "valorcurse@gmail.com", 
			institute: "CMI", study: "Technische Informatica", 
			course: "ICT-Lab", teacher: "Abd el Ghany")
			.addToEquipment(Equipment.findByName("Laser snijder"))
			.save(flush: true, failOnError: true)

		}

		def destroy = {
		}
	}
