package kennisbank

class ProjectMemberService {

	static transactional = true

	void createMember(String n, String e, Date b) {
		new ProjectMember(name: n, dateOfBirth: b, gender: "", address: "", city: "", zipcode: "", email: e).save(flush: true, ErrorOnFail: true)
	}

	def serviceMethod() {

	}
}