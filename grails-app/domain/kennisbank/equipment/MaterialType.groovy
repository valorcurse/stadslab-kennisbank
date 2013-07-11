package kennisbank.equipment

class MaterialType implements Serializable {

	String name
	Setting setting
	// Material material

	static hasOne = [material: Material]

	static constraints = {
		setting nullable: true
	}

	static mapping = {
		material lazy: false
	}

}
