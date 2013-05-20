package kennisbank.equipment

class SettingType {

	String name
	// Equipment equipment
	Date dateCreated

	static belongsTo = [equipment: Equipment]

	static constraints = {
	}

	static mapping = {
	}

}

