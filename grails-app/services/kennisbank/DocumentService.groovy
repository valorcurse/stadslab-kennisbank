package kennisbank

class DocumentService {
	
	static transactional = true
	
		void createDocument(String t) {
			new Document(title: t).save(flush: true, ErrorOnFail: true)
		}

    def serviceMethod() {

    }
}
