package kennisbank.projects

import com.vaadin.data.Property
import com.vaadin.event.FieldEvents.TextChangeEvent
import com.vaadin.event.FieldEvents.TextChangeListener
import com.vaadin.server.ExternalResource
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Field.ValueChangeEvent
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.*
import kennisbank.*


class ProjectsOverview extends VerticalLayout {

	String tabName
	
	String tabName() {
		return tabName
	}	
	
	ProjectsOverview() {

		setMargin(true)
		
		tabName = "#!/project"

		Panel panel = new Panel()
		panel.setPrimaryStyleName("island-panel")
		panel.addStyleName(Runo.PANEL_LIGHT)

		VerticalLayout layout = new VerticalLayout()
		layout.setSpacing(true)
		layout.setMargin(true)
		layout.setSizeFull()

		panel.setContent(layout)

		Label titleLabel = new Label("<h1><b>Projects</b></h1>", Label.CONTENT_XHTML)
		titleLabel.setWidth("100%")

		Panel createNewProjectPanel = new Panel("New project")
		createNewProjectPanel.setPrimaryStyleName("embedded-panel")
		createNewProjectPanel.addStyleName(Runo.PANEL_LIGHT)
		
		HorizontalLayout createNewProjectLayout = new HorizontalLayout()
		createNewProjectLayout.setSpacing(true)
		createNewProjectLayout.setMargin(true)
		createNewProjectPanel.setContent(createNewProjectLayout)

		Button createNewProjectButton = new Button("Create a new project", new Button.ClickListener() {
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
										def projectService = new ProjectService()
										Project.withTransaction {
											projectService.createProject(projectNameTextField.getValue())
										}
										UI.getCurrent().getPage().getCurrent().setLocation("#!/project/" + projectNameTextField)
										window.close()
									}
								})
						windowLayout.addComponent(okButton)
						windowLayout.setComponentAlignment(okButton, Alignment.MIDDLE_CENTER)
						windowLayout.setComponentAlignment(projectNameTextField, Alignment.MIDDLE_CENTER)
						window.setContent(windowLayout)
						UI.getCurrent().addWindow(window)
					}
				})

		createNewProjectLayout.addComponent(createNewProjectButton)

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

		for (Project project : projects) {
			projectsTable.addItem(	[new Link(project.getTitle(), new ExternalResource("http://localhost:8080/kennisbank/#!/project/" + project.getTitle())),
				project.getCourse(), project.getDateCreated().toString()] as Object[],
			new Integer(projectsTable.size()+1));
		}

		TextField searchProjectsTextField = new TextField()
		searchProjectsTextField.setInputPrompt("Search")
		searchProjectsTextField.addTextChangeListener(new TextChangeListener() {
					public void textChange(TextChangeEvent event) {
						def filteredProjects = searchableService.search(event.getText(),
								[offset: 0, max: 20])
						projectsTable.removeAllItems()

						for (Project project : filteredProjects) {
							projectsTable.addItem(	[new Link(project.getTitle(), new ExternalResource("http://localhost:8080/kennisbank/#!/project/" + project.getTitle())),
								project.getCourse(), project.getDateCreated().toString()] as Object[],
							new Integer(projectsTable.size()+1));
						}
					}
				})

		existingProjectsLayout.addComponent(searchProjectsTextField, "searchField")
		existingProjectsLayout.addComponent(projectsTable, "projectsTable")

		layout.addComponent(titleLabel)
		layout.addComponent(createNewProjectPanel)
		layout.addComponent(existingProjectsLayout)

		layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
		layout.setComponentAlignment(existingProjectsLayout, Alignment.TOP_CENTER)

		addComponent(panel)
	}

}
