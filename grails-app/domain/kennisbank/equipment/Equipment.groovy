package kennisbank.equipment

class Equipment {

	String name
	Boolean hasSettings = false, hasMaterials = false
	List materials, settings

	void addToMaterials(Material material) {
		materials.add(material)
		hasMaterials = true
	}

	void addToSettings(Setting setting) {
		settings.add(setting)
		hasSettings = true
	}

	static hasMany = [settings: Setting, materials: Material]

	static constraints = {
	}

	static mapping = {
		materials lazy: false
		settings lazy: false
	}

}