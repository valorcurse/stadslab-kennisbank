package kennisbank.fabtool.projects

import kennisbank.checkin.Checkout

/**
 * Provides {@link kennisbank.projects.ProjectOverview} the ability to filter {@link kennisbank.checkin.Checkout Checkouts}.
 *
 * @author Marcelo Dias Avelino
 */
class Query {

	/**
	 * All classes the query can apply to.
	 */
	static enum QueryType {
		EQUIPMENT("Apparaat"), 
		MATERIAL("Materiaal"), 
		SETTING("Instelling"),
		TEXT("Tekst")
		
		/**
		 * Name of the QueryType which is displayed.
		 */
		String caption
		
		/**
		 * Constructor of the QueryType enum.
		 */
		QueryType(String caption) {
			this.caption = caption
		}
	}

	/**
	 * The type of the query, chosen from {@link #QueryType}.
	 */
	private QueryType queryType
	
	/**
	 * Value for the query to filter with.
	 */
	private String value

	/**
	 * Extra value for the query to filter with, i.e. with {@link #SETTING}
	 */
	private String extraValue

	/**
	 * All classes the query can apply to.
	 *
	 * @param queryType The type of the query
	 * @param value Value for the query to filter by
	 * @param extraValue Optional, in case a type of query requires it
	 */
	Query(QueryType queryType, String value, String extraValue = null) {
		this.queryType = queryType
		this.value = value
		this.extraValue = extraValue
	}

	/**
	 * Receives a list of {@link kennisbank.checkin.Checkout checkouts}, executes the query and returns a list of {@link kennisbank.checkin.Checkout checkouts}.
	 * 
	 * @param checkouts {@link kennisbank.checkin.Checkout Checkouts} to filter from. 
	 * @return A list of filtered {@link kennisbank.checkin.Checkout checkouts}.
	 */
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
								settingType {
									eq("name", value)
								}
							}
							else {
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