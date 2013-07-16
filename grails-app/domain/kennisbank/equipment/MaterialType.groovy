package kennisbank.equipment

class MaterialType implements Serializable {

	String name

	static belongsTo = [material: Material]

	static constraints = {
	}

	static mapping = {
		material lazy: false
	}

	def beforeDelete() {
		Equipment.withTransaction {
			for (equipment in Equipment.list()) {
				def type = MaterialType.findByName(this.name)
				equipment.removeFromMaterialTypes(type)
			}
		}
	}

}
