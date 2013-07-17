package kennisbank.equipment

class SettingType implements Serializable {

	String name

	static belongsTo = [equipment: Equipment]

	static constraints = {
		name unique: true
	}

	static mapping = {
	}

}

