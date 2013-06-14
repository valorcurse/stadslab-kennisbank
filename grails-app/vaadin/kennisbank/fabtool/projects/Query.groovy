package kennisbank.fabtool.projecs

class Query {

	enum QueryType {
		EQUIPMENT, MATERIAL, SETTING
	}

	QueryType queryType
	List checkouts
	String value

	Query(QueryType queryType, List checkouts, String value) {
		this.queryType = queryType
		this.checkouts = checkouts
		this.value = value
	}

	List executeQuery() {
		def results = []

		switch (queryType) {
			case QueryType.EQUIPMENT:
				break

			case QueryType.MATERIAL:
				results = Checkout.createCriteria().listDistinct {
					settings {
						or {
							materialType {
								eq("name", value)
							}
							materialType {
								material {
									eq("name", value)
								}
							}
						}
					}
				}
				break

			case QueryType.SETTING:
				break
		}

		return results
	}
}