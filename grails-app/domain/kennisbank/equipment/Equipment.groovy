package kennisbank.equipment

class Equipment implements Serializable {

	String name
	Set materialTypes, settingTypes

	static hasMany = [settingTypes: SettingType, materialTypes: MaterialType]

	static constraints = {
	}

	static mapping = {
		materialTypes lazy: false
		settingTypes lazy: false
	}

}