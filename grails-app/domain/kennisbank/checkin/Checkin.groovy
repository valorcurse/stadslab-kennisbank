package kennisbank.checkin

import kennisbank.equipment.Equipment

class Checkin implements Serializable {

	Date dateCreated
	Boolean closed = false

	static hasMany = [equipment: Equipment]

	static constraints = {
	}

	static mapping = {
		equipment lazy: false
	}
}
