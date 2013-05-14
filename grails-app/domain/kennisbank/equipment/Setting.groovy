package kennisbank.equipment

class Setting {

	String value
	SettingType settingType
	MaterialType materialType

	// static hasOne = [settingType: SettingType, materialType: MaterialType]

	static constraints = {
		value nullable: true
	}

	static mapping = {
	}

}

