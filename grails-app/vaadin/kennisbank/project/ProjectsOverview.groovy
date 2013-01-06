package kennisbank.project

import java.rmi.server.UID;

import com.vaadin.server.ExternalResource
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.*
import kennisbank.*

class ProjectsOverview extends CssLayout {

	ProjectsOverview() {

		setSizeFull()

		VerticalLayout layout = new VerticalLayout()
		layout.setSpacing(true)
		layout.setMargin(true)

		Label titleLabel = new Label("<h1><b>Projects</b></h1>", Label.CONTENT_XHTML)
		titleLabel.setWidth("100%")

		CustomLayout createNewProjectLayout = new CustomLayout("newprojectsoverview")

		Button createNewProjectButton = new Button("new project", new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						Window window = new Window("Create a new project")
						window.setModal(true)
						VerticalLayout windowLayout = new VerticalLayout()
						windowLayout.setSpacing(true)
						windowLayout.setMargin(true)
						TextField projectNameTextField = new TextField("Project name")
						windowLayout.addComponent(projectNameTextField)
						Button okButton = new Button("Ok", new Button.ClickListener() {
									public void buttonClick(ClickEvent event2) {
										//def project = new Project(title: "Kennisbank", course: "Technische Informatica", summary:"")
										//project.save(flush: true, failOnError: true)
										//new Project(title: projectNameTextField.getValue(), course: "", summary: "").save(failOnError: true)
										
										def projectService = new ProjectService()
										Project.withTransaction {
											//new Project(title: projectNameTextField, course: "", summary:"").save(flush: true, ErrorOnFail: true)
											projectService.createProject(projectNameTextField.getValue())
										}
										UI.getCurrent().getPage().getCurrent().setLocation("http://localhost:8080/kennisbank/#!/project/" + projectNameTextField)
										window.close()
									}
								})
						windowLayout.addComponent(okButton)
						windowLayout.setComponentAlignment(okButton, Alignment.MIDDLE_CENTER)
						window.setContent(windowLayout)
						UI.getCurrent().addWindow(window)
					}
				})

		createNewProjectLayout.addComponent(createNewProjectButton, "newProjectButton")

		CustomLayout existingProjectsLayout = new CustomLayout(
				"existingprojectsoverview");

		Table projectsTable = new Table()
		projectsTable.addStyleName(Reindeer.TABLE_BORDERLESS)
		projectsTable.setHeight("350px")
		projectsTable.setWidth("100%")

		projectsTable.addContainerProperty("Project name", Link.class, null)
		projectsTable.addContainerProperty("Course", String.class, null)
		projectsTable.addContainerProperty("Date created", String.class, null)

		projectsTable.setColumnWidth("Project name", 200)

		List<Project> projects = Project.list()

		Notification.show(projects.size().toString())
		
		for (Project project : projects) {
			projectsTable.addItem(	[new Link(project.getTitle(), new ExternalResource("http://localhost:8080/kennisbank/#!/project/" + project.getTitle())),
				project.getCourse(), project.getDateCreated().toString()] as Object[],
			new Integer(projectsTable.size()+1));
		}

		TextField searchProjects = new TextField()
		searchProjects.setInputPrompt("Search")

		existingProjectsLayout.addComponent(searchProjects, "searchField")
		existingProjectsLayout.addComponent(projectsTable, "projectsTable")

		layout.addComponent(titleLabel)
		layout.addComponent(createNewProjectLayout)
		layout.addComponent(existingProjectsLayout)

		layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
		layout.setComponentAlignment(existingProjectsLayout, Alignment.TOP_CENTER)

		addComponent(layout)
	}

}
