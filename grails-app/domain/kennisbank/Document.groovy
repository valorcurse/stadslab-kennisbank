package kennisbank

import java.util.Date;

class Document {
	
	String title
	String size
	Date dateCreated
	
	static belongsTo = [project:Project]

    static constraints = {
    }
	
	String getTitle() {
		return title
	}

	String getSize() {
		return size
	}
	
	Date getDateAdded() {
		return dateAdded
	}
}
