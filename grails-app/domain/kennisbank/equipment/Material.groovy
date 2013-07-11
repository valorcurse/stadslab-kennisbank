package kennisbank.equipment

class Material implements Serializable {

	String name

	static hasMany = [materialTypes: MaterialType]

	static constraints = {
	}

	static mapping = {
		materialTypes lazy: false
	}

}
