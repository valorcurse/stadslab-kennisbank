package kennisbank.equipment

class SettingType implements Serializable {

	String name
	Date dateCreated

	static belongsTo = [equipment: Equipment]

	static constraints = {
	}

	static mapping = {
	}

}

