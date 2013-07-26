package kennisbank.equipment

class Equipment implements Serializable {

	String name
	Boolean hidden = true

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