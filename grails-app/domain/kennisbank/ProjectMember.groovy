package kennisbank

class ProjectMember {

	String name
	Date dateOfBirth
	String gender
	String address
	String city
	String zipcode
	String email
	
    static constraints = {
    }
	
	String getName() {
		return name
	}
	
	Date getDateOfBirth() {
		return dateOfBirth
	}
	
	String getGender() {
		return gender
	}
	
	String getAddress() {
		return address
	}
	
	String getCity() {
		return city
	}
	
	String getZipCode() {
		return zipcode
	}
	
	String getEmail() {
		return email
	}
	
}
