package kennisbank.projects

import com.vaadin.ui.*
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.MenuBar.Command
import com.vaadin.ui.MenuBar.MenuItem
import kennisbank.Project
import kennisbank.projects.Member
import com.vaadin.ui.TabSheet.Tab
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.themes.Reindeer
import kennisbank.*


class ProjectView extends CssLayout {

	String uriFragment

	String tabName() {
		return uriFragment
	}

	public ProjectView(Project project) {

		uriFragment = "#!/project/" + project.getTitle()
		UI.getCurrent().getPage().getCurrent().setLocation(uriFragment)

		VerticalLayout mainLayout = new VerticalLayout()
		mainLayout.setWidth("100%")
		GridLayout layout = new GridLayout(2, 5)
		layout.setSpacing(true)
		layout.setMargin(true)
		layout.setWidth("100%")

		MenuBar menu = new MenuBar()
		menu.setWidth("100%")
		final MenuBar.MenuItem projectItem = menu.addItem("Project", null)
		final MenuBar.MenuItem membersItem = menu.addItem("Members", null)
		projectItem.addItem("New project", new Command() {
					public void menuSelected(MenuItem selectedItem) {
						showNotification("New project created!")
					}
				})
		projectItem.addItem("Edit project", new Command() {
					public void menuSelected(MenuItem selectedItem) {
						showNotification("Edit this project!")
					}
				})

		Label titleLabel = new Label("<h1><b>"+project.getTitle()+"</b></h1>", Label.CONTENT_XHTML)
		titleLabel.setWidth("100%")
		layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)


		Panel summaryPanel = new Panel("Summary")
		summaryPanel.setPrimaryStyleName("island-panel")
		summaryPanel.setStyleName(Runo.PANEL_LIGHT)

		VerticalLayout summaryLayout = new VerticalLayout()
		summaryLayout.setSpacing(true)
		summaryLayout.setMargin(true)
		summaryLayout.setWidth("100%")
		summaryPanel.setContent(summaryLayout)

		RichTextArea editor = new RichTextArea()
		editor.setWidth("100%")

		Label summaryText = new Label()
		summaryText.setWidth("100%")
		summaryText.setContentMode(Label.CONTENT_XHTML)
		summaryLayout.addComponent(summaryText)

		Button editButton = new Button("Edit")
		editButton.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						if (editButton.getCaption() == "Apply") {
							summaryText.setValue(editor.getValue())
							summaryLayout.replaceComponent(editor, summaryText)
							editButton.setCaption("Edit")
						}
						else if (editButton.getCaption() == "Edit") {
							editor.setValue(summaryText.getValue())
							summaryLayout.replaceComponent(summaryText, editor)
							editButton.setCaption("Apply")
						}
					}
				})

		summaryLayout.addComponent(summaryText)
		if(UI.getCurrent().getLogged()){
			summaryLayout.addComponent(editButton)
			summaryLayout.setComponentAlignment(editButton, Alignment.TOP_RIGHT)
		}
		Panel membersPanel = new Panel("Members")
		membersPanel.setPrimaryStyleName("island-panel")
		membersPanel.setStyleName(Runo.PANEL_LIGHT)
		membersPanel.setWidth("450px")

		VerticalLayout membersLayout = new VerticalLayout()
		membersLayout.setWidth("450px")
		membersPanel.setContent(membersLayout)

		Button createNewMemberButton = new Button("Add Member", new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						Window window = new Window("Add a new member")
						window.setModal(true)
						VerticalLayout windowLayout = new VerticalLayout()
						windowLayout.setSpacing(true)
						windowLayout.setMargin(true)
						TextField memberNameTextField = new TextField("Name")
						TextField memberEmailTextField = new TextField("Email")
						DateField memberBirthTextField = new DateField("Birth date")
						windowLayout.addComponent(memberNameTextField)
						windowLayout.addComponent(memberEmailTextField)
						windowLayout.addComponent(memberBirthTextField)
						Button okButton = new Button("Add", new Button.ClickListener() {
									public void buttonClick(ClickEvent event2) {
										def projectMemberService = new ProjectMemberService()
										ProjectMember.withTransaction {
											projectMemberService.createMember(memberNameTextField.getValue(),
													memberEmailTextField.getValue(), memberBirthTextField.getValue())
										}

										//UI.getCurrent().getPage().getCurrent().setLocation("#!/project/" + projectNameTextField)
										window.close()
									}
								})
						okButton.setClickShortcut(KeyCode.ENTER);
						windowLayout.addComponent(okButton)
						windowLayout.setComponentAlignment(okButton, Alignment.MIDDLE_CENTER)
						windowLayout.setComponentAlignment(memberNameTextField, Alignment.MIDDLE_CENTER)
						windowLayout.setComponentAlignment(memberEmailTextField, Alignment.MIDDLE_CENTER)
						windowLayout.setComponentAlignment(memberBirthTextField, Alignment.MIDDLE_CENTER)
						window.setContent(windowLayout)
						UI.getCurrent().addWindow(window)
					}
				})

		membersLayout.setMargin(true)
		membersLayout.setSpacing(true)

		Table membersTable = new Table()
		//projectsTable.addStyleName(Reindeer.TABLE_BORDERLESS)
		membersTable.setHeight("150px")
		membersTable.setWidth("100%")

		membersTable.addContainerProperty("Name", String.class, null)
		membersTable.addContainerProperty("Email", String.class, null)
		membersTable.addContainerProperty("Birth Date", String.class, null)

		List<ProjectMember> members = ProjectMember.list()

		for (ProjectMember member : members) {
			membersTable.addItem(	[member.getName(), member.getEmail(),
				member.getDateOfBirth().toString()] as Object[],
			new Integer(membersTable.size()+1));
		}
		membersLayout.addComponent(membersTable)

		if(UI.getCurrent().getLogged()){
			membersLayout.addComponent(createNewMemberButton)
		}
		Panel updatesPanel = new Panel("Updates")
		updatesPanel.setPrimaryStyleName("island-panel")
		updatesPanel.setStyleName(Runo.PANEL_LIGHT)
		updatesPanel.setHeight("400px")

		VerticalLayout updatesLayout = new VerticalLayout()
		updatesLayout.setSizeFull()
		updatesPanel.setContent(updatesLayout)

		VerticalLayout updateMessageLayout = new VerticalLayout()
		updatesLayout.addComponent(updateMessageLayout)

		/*HorizontalLayout messageUpdatesLayout = new HorizontalLayout()
		 messageUpdatesLayout.setSpacing(true)
		 messageUpdatesLayout.setMargin(true)
		 messageUpdatesLayout.setSizeFull()
		 TextField messageField = new TextField()
		 messageField.setWidth("100%")
		 messageUpdatesLayout.addComponent(messageField)
		 Button messageButton = new Button("Post", new Button.ClickListener() {
		 public void buttonClick(ClickEvent event) {
		 updateMessageLayout.addComponent(new Update(messageField.getValue()))
		 }
		 })
		 messageUpdatesLayout.addComponent(messageButton)
		 updatesLayout.addComponent(messageUpdatesLayout)
		 updatesLayout.setComponentAlignment(messageUpdatesLayout, Alignment.BOTTOM_LEFT)*/


		VerticalLayout filesLayout = new VerticalLayout()
		filesLayout.setWidth("150px")
		Panel filesPanel =  new Panel()
		filesPanel.setHeight("250px")
		filesPanel.setWidth("100%")
		VerticalLayout filesPanelLayout = new VerticalLayout()
		Label filesLabel = new Label("<b>Files</b>", Label.CONTENT_XHTML)

		Upload upload = new Upload(null, null)

		filesLayout.addComponent(filesLabel)
		filesLayout.addComponent(filesPanel)
		filesPanel.setContent(filesPanelLayout)
		if(UI.getCurrent().getLogged()){
			filesPanelLayout.addComponent(upload)
		}
		layout.addComponent(titleLabel, 0, 0, 1, 0)
		layout.addComponent(menu, 0, 1, 1, 1)
		layout.addComponent(summaryPanel, 0, 2, 1, 2)
		layout.addComponent(membersPanel, 0, 3)
		layout.addComponent(updatesPanel, 1, 3, 1, 4)
		layout.addComponent(filesLayout, 0, 4)

		mainLayout.addComponent(menu)
		mainLayout.addComponent(layout)

		layout.setColumnExpandRatio(1, 0.1)

		addComponent(mainLayout)
	}


}
