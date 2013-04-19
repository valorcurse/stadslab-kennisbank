package kennisbank.equipment

class Material {

	String name

	static hasMany = [materialTypes: MaterialType]

	static constraints = {
	}

	static mapping = {
		materialTypes lazy: false
	}

}
