package kennisbank.equipment

class MaterialType {

	String name
	Setting setting
	Material material

	// static hasOne = [material: Material]

	static constraints = {
		setting nullable: true
	}

	static mapping = {
	}

}
