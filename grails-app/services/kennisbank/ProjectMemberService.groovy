package kennisbank

class ProjectMemberService {

	static transactional = true

	ProjectMember createMember(String username) {
		ProjectMember projectMember = new ProjectMember(username: username).save(flush: true, ErrorOnFail: true)
		return projectMember
	}

	def serviceMethod() {

	}
}