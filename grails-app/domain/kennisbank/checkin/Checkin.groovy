package kennisbank.checkin

import kennisbank.equipment.Equipment

class Checkin {

	Date dateCreated
	Set equipment

	static hasMany = [equipment: Equipment]

	static constraints = {
	}

	static mapping = {
		equipment lazy: false
	}
}
