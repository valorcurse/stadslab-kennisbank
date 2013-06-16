package kennisbank.fabtool.projects

import kennisbank.checkin.Checkout

class Query {

	static enum QueryType {
		EQUIPMENT("Apparaat"), 
		MATERIAL("Materiaal"), 
		SETTING("Instelling"),
		TEXT("Tekst")

		String caption

		QueryType(String caption) {
			this.caption = caption
		}
	}

	QueryType queryType
	String value, extraValue

	Query(QueryType queryType, String value, String extraValue = null) {
		this.queryType = queryType
		this.value = value
		this.extraValue = extraValue
	}

	List executeQuery(List checkouts) {
		def results = []

		switch (queryType) {
			case QueryType.EQUIPMENT:
				results = Checkout.createCriteria().listDistinct {
					and {
						'in'("id", checkouts*.id)
						settings {
							equipment {
								eq("name", value)
							}
						}
					}
				}
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
				results = Checkout.createCriteria().listDistinct {
					and {
						'in'("id", checkouts*.id)
						settings {
							if (extraValue == null) {
								print "extraValue is null"
								settingType {
									eq("name", value)
								}
							}
							else {
								print "extraValue is not null"

								and {
									settingType {
										eq("name", value)
									}	
									eq("value", extraValue)
								}
							}
						}
					}
				}
				break

			case QueryType.TEXT:
				results = Checkout.createCriteria().listDistinct {
					and {
						'in'("id", checkouts*.id)
						or {
							ilike("title", "%" + value + "%")
							ilike("description", "%" + value + "%")
						}
					}
				}
				break
		}

		return results
	}
}