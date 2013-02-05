package kennisbank

import java.util.Date;

class Document {

	String title, path
	Date dateCreated

	static belongsTo = [project:Project]

	static constraints = {
	}

	String getTitle() {
		return title
	}

	Date getDateAdded() {
		return dateAdded
	}
}
