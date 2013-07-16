import kennisbank.*
import kennisbank.checkin.*
import kennisbank.equipment.*
import kennisbank.auth.*
import org.codehaus.groovy.grails.commons.ApplicationHolder
import grails.util.Environment


class BootStrap {

	def init = { servletContext ->

		// environments {
  //           development {
				String rootPath = ApplicationHolder.application.parentContext.getResource("").file.absolutePath

				new User(username: "admin", password: "12345", enabled: true).save()

				Material glas = new Material(name: "Glas").save(failOnError: true)
				Material leer = new Material(name: "Leer").save(failOnError: true)
				Material hout = new Material(name: "Hout").save(failOnError: true)
				Material karton = new Material(name: "Karton").save(failOnError: true)
				Material systeemBord = new Material(name: "Systeem boord").save(failOnError: true)

				// Hout
				MaterialType duplex = new MaterialType(name: "Duplex", material: hout).save(failOnError: true)
				MaterialType triplex = new MaterialType(name: "Triplex", material: hout).save(failOnError: true)
				// Karton
				MaterialType massiefKarton = new MaterialType(name: "Massief Karton", material: karton).save(failOnError: true)
				MaterialType golfKarton = new MaterialType(name: "Golfkarton", material: karton).save(failOnError: true)
				// Systeem boord
				MaterialType arduino = new MaterialType(name: "Arduino", material: systeemBord).save(failOnError: true)


				hout.addToMaterialTypes(duplex)
				.addToMaterialTypes(triplex)

				karton.addToMaterialTypes(massiefKarton)
				.addToMaterialTypes(golfKarton)

				SettingType passes = new SettingType(name: "Passes")
				SettingType power = new SettingType(name: "Power")
				SettingType dikte = new SettingType(name: "Dikte")

				Equipment elektronica = new Equipment(name: "Elektronica").save(failOnError: true)
				.addToMaterialTypes(arduino)

				Equipment printer = new Equipment(name: "3D printer").save(failOnError: true)
				Equipment laserSnijder = new Equipment(name: "Laser snijder").save(failOnError: true)
				.addToSettingTypes(passes)
				.addToSettingTypes(power)
				.addToSettingTypes(dikte)
				.addToMaterialTypes(duplex)
				.addToMaterialTypes(triplex)
				.addToMaterialTypes(massiefKarton)
				.addToMaterialTypes(golfKarton)


				String description = "Lorem ipsum Minim eu sunt reprehenderit nisi voluptate Excepteur commodo cillum esse" + 
				" dolore quis exercitation aliquip esse dolore culpa sit laboris dolor sed consequat dolor labore ea voluptate" + 
				" in dolor in cupidatat eu quis sint."


				StudentCheckin checkin = new StudentCheckin(
					studentNumber: "0840416", firstName: "Marcelo", 
					lastName: "Dias Avelino", email: "valorcurse@gmail.com", 
					institute: "CMI", study: "Technische Informatica", 
					course: "ICT-Lab", teacher: "Abd el Ghany")
				.addToEquipment(laserSnijder)
				.save(failOnError: true)

				Checkout checkout1 = new Checkout(title: "The new and improved iPad", 
					picturePath: rootPath + "/samples/ipad.jpg", description: description, checkin: checkin)

				Checkout checkout2 = new Checkout(title: "Ubuntu laptop", 
					picturePath: rootPath + "/samples/ubuntu.jpg", description: description, checkin: checkin)
				

				def checkout1Settings = [
				new Setting(value: "4", settingType: passes, materialType: duplex, equipment: printer, checkout: checkout1),	
				new Setting(value: "100", settingType: power, materialType: duplex, equipment: printer, checkout: checkout1),
				new Setting(value: "3", settingType: dikte, materialType: duplex, equipment: printer, checkout: checkout1)]

				def checkout2Settings = [
				new Setting(value: "4", settingType: passes, materialType: triplex, equipment: laserSnijder, checkout: checkout2),	
				new Setting(value: "100", settingType: power, materialType: triplex, equipment: laserSnijder, checkout: checkout2),
				new Setting(value: "3", settingType: dikte, materialType: triplex, equipment: laserSnijder, checkout: checkout2)]

				AttachedFile file = new AttachedFile(name: "someFile.txt", path: rootPath + "/samples/someFile.txt")

				checkout1.addToSettings(checkout1Settings[0])
				.addToSettings(checkout1Settings[1])
				.addToSettings(checkout1Settings[2])
				.addToFiles(file)
				.save()

				checkout2.addToSettings(checkout2Settings[0])
				.addToSettings(checkout2Settings[1])
				.addToSettings(checkout2Settings[2])
				.addToFiles(file)
				.save()

				checkin.addToCheckouts(checkout1)
				checkin.addToCheckouts(checkout2)
		// 	}
		// }

	}

	def destroy = {
	}
}
