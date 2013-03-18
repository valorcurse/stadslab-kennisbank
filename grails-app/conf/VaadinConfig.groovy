vaadin {

	// Your Vaadin UI classes that extends com.vaadin.ui.UI.
	// The application will be available at e.g. http://localhost:8080/grails-vaadin7-demo/
	mapping = [
				"/*": "kennisbank.fabtool.MyUI",
				"/checkin/*": "kennisbank.checkin.CheckIn"
			]

	// This is optional because the servlet is provided by default.
	// servletClass = "com.mycompany.MyGrailsAwareApplicationServlet"
	applicationClass = "kennisbank.MyUI"
	productionMode = false

	//widgetset = "MyWidgetset"
}

environments {
	production {
		vaadin {
			productionMode = true
		}
	}
}
