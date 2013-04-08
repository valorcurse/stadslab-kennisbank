package kennisbank.equipment

import kennisbank.equipment.Setting

class Equipment {

	String name
	Boolean hasSettings = false

	static hasMany = [settings: Setting, materials: Material]

	static constraints = {
	}

	static mapping = {
	}

}