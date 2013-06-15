package kennisbank.fabtool.projects

import kennisbank.checkin.Checkout

class Query {

	static enum QueryType {
		EQUIPMENT("Apparaat"), 
		MATERIAL("Materiaal"), 
		SETTING("Instelling")

		String caption

		QueryType(String caption) {
			this.caption = caption
		}
	}

	QueryType queryType
	String value

	Query(QueryType queryType, String value) {
		this.queryType = queryType
		this.value = value
	}

	List executeQuery(List checkouts) {
		def results = []

		switch (queryType) {
			case QueryType.EQUIPMENT:
				break

			case QueryType.MATERIAL:
				results = Checkout.createCriteria().listDistinct {
					and {
						'in'("id", checkouts*.id)
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
				}
				break

			case QueryType.SETTING:
				break
		}

		return results
	}
}