package kennisbank.equipment

class Equipment {

	String name
	// Boolean hasSettings = false, hasMaterials = false
	Set materialTypes, settingTypes

	// void addToMaterials(Material material) {
	// 	materials.add(material)
	// 	hasMaterials = true
	// }

	// void addToSettings(Setting setting) {
	// 	settings.add(setting)
	// 	hasSettings = true
	// }

	static hasMany = [settingTypes: SettingType, materialTypes: MaterialType]

	static constraints = {
	}

	static mapping = {
		materialTypes lazy: false
		settingTypes lazy: false
	}

}