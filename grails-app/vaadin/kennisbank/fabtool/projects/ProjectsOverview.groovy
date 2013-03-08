package kennisbank.fabtool.projects

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
import kennisbank.project.Project;
import kennisbank.project.ProjectMember;


class ProjectsOverview extends VerticalLayout {

	String uriFragment

	def hiddenComponents

	String tabName() {
		return uriFragment
	}

	ProjectsOverview() {

		setMargin(true)
		setSizeFull()

		hiddenComponents = []

		uriFragment = "#!/project"
		UI.getCurrent().getPage().getCurrent().setLocation(uriFragment)

		Panel panel = new Panel()
		panel.setPrimaryStyleName("island-panel")

		VerticalLayout layout = new VerticalLayout()
		layout.setSpacing(true)
		layout.setMargin(true)
		layout.setSizeFull()

		panel.setContent(layout)

		Label titleLabel = new Label("<h1><b>Projects</b></h1>", Label.CONTENT_XHTML)
		titleLabel.setWidth("100%")

		// ------------------------------------------------------- New Project -------------------------------------------------------
		
		Panel createNewProjectPanel = new Panel("New project")
		hiddenComponents.add(createNewProjectPanel)
		createNewProjectPanel.setPrimaryStyleName("embedded-panel")
		createNewProjectPanel.addStyleName(Runo.PANEL_LIGHT)

		HorizontalLayout createNewProjectLayout = new HorizontalLayout()
		createNewProjectLayout.setSpacing(true)
		createNewProjectLayout.setMargin(true)
		createNewProjectPanel.setContent(createNewProjectLayout)

		NativeButton createNewProjectButton = new NativeButton("Create a new project", new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						Window window = new Window("Create a new project")
						window.setModal(true)
						VerticalLayout windowLayout = new VerticalLayout()
						windowLayout.setSpacing(true)
						windowLayout.setMargin(true)
						TextField projectNameTextField = new TextField("Project name")
						windowLayout.addComponent(projectNameTextField)
						NativeButton okButton = new NativeButton("Ok", new Button.ClickListener() {
									public void buttonClick(ClickEvent event2) {
										def projectService = new ProjectService()
										Project.withTransaction {
											Project project = new Project(title: projectNameTextField.getValue()).save(flush: true, ErrorOnFail: true)
											project.addToProjectMembers(new ProjectMember(username: UI.getCurrent().loggedInUser.getUsername()))

										}
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

		// ------------------------------------------------------- Existing Project -------------------------------------------------------
		
		Panel existingProjectsPanel = new Panel("Existing project")
		existingProjectsPanel.setPrimaryStyleName("embedded-panel")
		existingProjectsPanel.addStyleName(Runo.PANEL_LIGHT)

		VerticalLayout existingProjectsLayout = new VerticalLayout()
		existingProjectsPanel.setContent(existingProjectsLayout)
		existingProjectsLayout.setMargin(true)
		existingProjectsLayout.setSpacing(true)

		Table projectsTable = new Table()
		//projectsTable.addStyleName(Reindeer.TABLE_BORDERLESS)
		projectsTable.setHeight("350px")
		projectsTable.setWidth("100%")

		projectsTable.addContainerProperty("Project name", ProjectLink.class, null)
		projectsTable.addContainerProperty("Course", String.class, null)
		projectsTable.addContainerProperty("Date created", String.class, null)

		projectsTable.setColumnWidth("Project name", 200)

		List<Project> projects = Project.list()

		for (Project project : projects) {
			projectsTable.addItem(	[new ProjectLink(project.getTitle()),
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

		existingProjectsLayout.addComponent(searchProjectsTextField)
		existingProjectsLayout.addComponent(projectsTable)

		layout.addComponent(titleLabel)
		layout.addComponent(createNewProjectPanel)
		layout.addComponent(existingProjectsPanel)

		layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
		layout.setComponentAlignment(existingProjectsPanel, Alignment.TOP_CENTER)

		addComponent(panel)

		if(UI.getCurrent().loggedIn) {
			revealHiddenComponents()
		}
		else {
			hideRevealedComponents()
		}
	}

	void revealHiddenComponents() {
		for (c in hiddenComponents) {
			c.setVisible(true)
		}
	}

	void hideRevealedComponents() {
		for (c in hiddenComponents) {
			c.setVisible(false)
		}
	}
}
