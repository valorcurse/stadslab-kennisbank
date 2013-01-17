package kennisbank

class SummaryService {

	void createSummary(String s) {
		new Summary(summary: s).save(flush: true, ErrorOnFail: true)
	}
	
    def serviceMethod() {

    }
}