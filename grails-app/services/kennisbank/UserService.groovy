package kennisbank

class UserService {

	static transactional = true

	void createProject(String u, String password) {
		new User(username: u, password: password).save(flush: true, ErrorOnFail: true)
	}

	def serviceMethod() {

	}
}
