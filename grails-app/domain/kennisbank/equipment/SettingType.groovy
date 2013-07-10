package kennisbank.equipment

class SettingType {

	String name
	Date dateCreated

	static belongsTo = [equipment: Equipment]

	static constraints = {
	}

	static mapping = {
	}

}

