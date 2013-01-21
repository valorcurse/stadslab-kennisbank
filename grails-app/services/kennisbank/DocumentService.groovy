package kennisbank

class DocumentService {
	
	static transactional = true
	
		void createDocument(String t, String s, Date d) {
			new Document(title: t, size: s, dateAdded: d).save(flush: true, ErrorOnFail: true)
		}

    def serviceMethod() {

    }
}
