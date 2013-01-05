package kennisbank

class Project {
	
	static hasMany = [document:Document,tags:Tag,projectmember:ProjectMember,information:Information]
	
	String name
	Date startDate
	String city
	String state
	BigDecimal distance
	BigDecimal cost
	Integer maxRunners = 100000
	
	static constraints = {} 
}
