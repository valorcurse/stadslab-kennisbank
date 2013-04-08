import kennisbank.*
import kennisbank.checkin.StudentCheckin
import kennisbank.equipment.Material
import kennisbank.equipment.Equipment

class BootStrap {

	def init = { servletContext ->

		//new Project(title: "Kennisbank", course: "Technische Informatica", summary:"").save(flush: true, failOnError: true)
		//new User(username: "admin", password: "admin").save(flush: true, failOnError: true)

		new Material(name: "Hout").save(flush: true, failOnError: true)
		new Material(name: "Glas").save(flush: true, failOnError: true)
		new Material(name: "Leer").save(flush: true, failOnError: true)

		new Equipment(name: "Folie snijder").save(flush: true, failOnError: true)
		new Equipment(name: "3D printer").save(flush: true, failOnError: true)
		new Equipment(name: "Laser snijder").addToSettings(name: "Passes").addToSettings(name: "Power").save(flush: true, failOnError: true)

		new StudentCheckin(studentNumber: "0840416", firstName: "Marcelo", 
			lastName: "Dias Avelino", email: "valorcurse@gmail.com", 
			institute: "CMI", study: "Technische Informatica", 
			course: "ICT-Lab", teacher: "Abd el Ghany").addToEquipment(
			Equipment.findByName("Folie snijder")).addToEquipment(
			Equipment.findByName("3D printer")).save(flush: true, failOnError: true)

		}

		def destroy = {
		}
	}
