package kennisbank

import kennisbank.checkin.StudentCheckin
import kennisbank.project.Document;

class DocumentService {
	
	static transactional = true
	
		void createDocument(String t) {
			new StudentCheckin(studentNumber: "5464874", firstName: "Jack", lastName: "Taylor", email: "JT@hotmail.com", institute: "CMI", study: "TI", course: "ComputerTalen", teacher: "Bropj").save(flush: true, failOnError: true)
		}

    def serviceMethod() {

    }
}
