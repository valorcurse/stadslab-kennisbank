package kennisbank.project

import com.vaadin.server.ExternalResource
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.*

class ProjectsOverview extends CssLayout {

	ProjectsOverview() {

		setSizeFull()

		VerticalLayout layout = new VerticalLayout()
		layout.setSpacing(true)
		layout.setMargin(true)

		Label titleLabel = new Label("<h1><b>Projects</b></h1>", Label.CONTENT_XHTML)
		titleLabel.setWidth("100%")

		CustomLayout existingProjectsLayout = new CustomLayout(
			"projectsoverview");
		
		Table projectsTable = new Table()
		projectsTable.addStyleName(Reindeer.TABLE_BORDERLESS)
		projectsTable.setHeight("350px")
		projectsTable.setWidth("100%")
		
		projectsTable.addContainerProperty("Project name", Link.class, null)
		projectsTable.addContainerProperty("Course", String.class, null)
		projectsTable.addContainerProperty("Date created", String.class, null)

		projectsTable.setColumnWidth("Project name", 200)
		
		for (i in 1..10) {
			projectsTable.addItem(	[new Link("Kennisbank", new ExternalResource("http://localhost:8080/kennisbank/#!/project/kennisbank")), 
									"Technische Informatica", "15/9/2012"] as Object[], 
									new Integer(i));
		}
		
		TextField searchProjects = new TextField()
		searchProjects.setInputPrompt("Search for projects...")
		
		layout.addComponent(titleLabel)
		existingProjectsLayout.addComponent(searchProjects, "searchField")
		existingProjectsLayout.addComponent(projectsTable, "projectsTable")
		layout.addComponent(existingProjectsLayout)

		layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
		layout.setComponentAlignment(existingProjectsLayout, Alignment.TOP_CENTER)

		addComponent(layout)
	}

}
