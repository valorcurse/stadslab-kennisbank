package kennisbank.equipment

class Material implements Serializable {

	String name

	static hasMany = [materialTypes: MaterialType]

	static constraints = {
		name unique: true
	}

	static mapping = {
		materialTypes lazy: false
	}

	def beforeDelete() {
		// print "Deleting all MaterialTypes"
    	// Material.withNewSession { materialTypes*.delete() }
	}

}
