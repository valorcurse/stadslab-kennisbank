package kennisbank.equipment

class Equipment implements Serializable {

	String name
	// Set materialTypes, settingTypes

	static hasMany = [settingTypes: SettingType, materialTypes: MaterialType]

	static constraints = {
		materialTypes nullable: true
		name unique: true
	}

	static mapping = {
		materialTypes lazy: false
		settingTypes lazy: false
	}

}