package kennisbank.checkin

import kennisbank.equipment.Equipment

class Checkin {

	Date dateCreated

	static hasMany = [equipment: Equipment]

	static constraints = {
	}

	static mapping = {
		equipment lazy: false
	}
}
