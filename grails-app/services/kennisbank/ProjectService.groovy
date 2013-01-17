package kennisbank

class ProjectService {

	static transactional = true

	void createProject(String t) {
		new Project(title: t, course: "", summary: "").save(flush: true, ErrorOnFail: true)
	}
	
	def serviceMethod() {

	}
}
