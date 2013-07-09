package kennisbank.equipment

import kennisbank.checkin.Checkout

class Setting {

	String value
	SettingType settingType
	MaterialType materialType
	Equipment equipment

	static belongsTo = [checkout: Checkout]

	static constraints = {
		value nullable: true, blank: true
		settingType nullable: true
		materialType nullable: true
		equipment nullable: true
	}

	static mapping = {
		equipment lazy: false
	}

}

