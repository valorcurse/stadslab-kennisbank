package kennisbank.equipment

import kennisbank.checkin.Checkout

class Setting {

	String value
	SettingType settingType
	MaterialType materialType
	Equipment equipment

	static belongsTo = [checkout: Checkout]

	static constraints = {
		value nullable: true
	}

	static mapping = {
	}

}

