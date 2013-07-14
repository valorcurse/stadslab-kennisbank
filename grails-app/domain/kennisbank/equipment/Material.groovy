package kennisbank.equipment

class Material implements Serializable {

	String name

	static hasMany = [materialTypes: MaterialType]

	static constraints = {
	}

	static mapping = {
		materialTypes lazy: false
	}

	def beforeDelete() {
    	MaterialType.withNewSession { materialTypes*.delete() } 
	}

}
