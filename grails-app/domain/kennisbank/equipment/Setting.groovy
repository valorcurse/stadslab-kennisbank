package kennisbank.equipment

import kennisbank.checkin.Checkout

class Setting implements Serializable {

	String value
	SettingType settingType
	MaterialType materialType
	Equipment equipment

	static belongsTo = [checkout: Checkout]

	static constraints = {
		value nullable: true, blank: true
		settingType nullable: true
		materialType nullable: false
		equipment nullable: false
	}

	static mapping = {
		equipment lazy: false
	}

}

