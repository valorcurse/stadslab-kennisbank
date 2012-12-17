vaadin {

    // Your Vaadin UI classes that extends com.vaadin.ui.UI.
    // The application will be available at e.g. http://localhost:8080/grails-vaadin7-demo/
    mapping = [
            "/*": "kennisbank.MyUI"
    ]

    // This is optional because the servlet is provided by default.
    // servletClass = "com.mycompany.MyGrailsAwareApplicationServlet"
	applicationClass = "kennisbank.MyUI"
    productionMode = false
}

environments {
    production {
        vaadin {
            productionMode = true
        }
    }
}
