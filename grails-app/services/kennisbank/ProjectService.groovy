package kennisbank

class ProjectService {

	Project project
	
	static transactional = true

	ProjectService(Project p) {
		project = p
	}
	
	void createProject(String t) {
		new Project(title: t, course: "", summary: "").save(flush: true, ErrorOnFail: true)
	}
	
	void setSummary(String s) {
		project.setSummary(s)
		project.save()
	}
	
	void addMember(ProjectMember member) {
		project.addToProjectmembers(member)
		project.save()
	}
	
	ProjectMember[] getMembers() {
		return project.members
	}
	
	def serviceMethod() {

	}
}
