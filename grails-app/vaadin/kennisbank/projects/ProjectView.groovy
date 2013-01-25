package kennisbank.projects

import com.vaadin.ui.*
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.MenuBar.Command
import com.vaadin.ui.MenuBar.MenuItem
import kennisbank.*
import kennisbank.projects.Member
import com.vaadin.ui.TabSheet.Tab
import com.vaadin.ui.Upload.Receiver
import com.vaadin.ui.Upload.StartedEvent
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.themes.Reindeer
import kennisbank.*
import com.vaadin.event.ShortcutAction.KeyCode;


class ProjectView extends CssLayout {

	String uriFragment
	ProjectService projectService
	Project currentProject

	String tabName() {
		return uriFragment
	}

	public ProjectView(Project project) {

		projectService = new ProjectService(project)
		currentProject = project

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


		RichTextArea editor = new RichTextArea()
		editor.setWidth("100%")
		Label summaryText = new Label()
		summaryText.setWidth("100%")
		summaryText.setContentMode(Label.CONTENT_XHTML)
		summaryText.setValue(project.getSummary())
		summaryLayout.addComponent(summaryText)
		Button editButton = new Button("Edit")
		editButton.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						if (editButton.getCaption() == "Apply") {
							Project.withTransaction {
								projectService.setSummary(editor.getValue())
							}
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
		summaryText.setContentMode(Label.CONTENT_XHTML)
		summaryPanel.setContent(summaryLayout)

		if(UI.getCurrent().getLogged()) {
			def currentUser = UI.getCurrent().getLoggedInUser().getUsername()
			if (checkIfMember(currentUser)) {
				summaryLayout.addComponent(editButton)
				summaryLayout.setComponentAlignment(editButton, Alignment.TOP_RIGHT)
			}
		}

		Panel membersPanel = new Panel("Members")
		membersPanel.setPrimaryStyleName("island-panel")
		membersPanel.setStyleName(Runo.PANEL_LIGHT)
		membersPanel.setWidth("450px")

		VerticalLayout membersLayout = new VerticalLayout()
		membersLayout.setWidth("450px")
		membersPanel.setContent(membersLayout)

		Table membersTable = new Table()
		//projectsTable.addStyleName(Reindeer.TABLE_BORDERLESS)
		membersTable.setHeight("150px")
		membersTable.setWidth("100%")

		membersTable.addContainerProperty("Name", String.class, null)
		membersTable.addContainerProperty("Email", String.class, null)
		membersTable.addContainerProperty("Birth Date", String.class, null)

		List<ProjectMember> members = project.projectMembers

		for (ProjectMember member : members) {
			membersTable.addItem(	[member.getUsername(), "",
				""] as Object[],
			new Integer(membersTable.size()+1));
		}
		membersLayout.addComponent(membersTable)

		Button createNewMemberButton = new Button("Add Member", new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						Window window = new Window("Add a new member")
						window.setModal(true)
						VerticalLayout windowLayout = new VerticalLayout()
						windowLayout.setSpacing(true)
						windowLayout.setMargin(true)
						TextField memberNameTextField = new TextField("Name")
						windowLayout.addComponent(memberNameTextField)
						Button okButton = new Button("Add", new Button.ClickListener() {
									public void buttonClick(ClickEvent event2) {
										def projectMemberService = new ProjectMemberService()
										ProjectMember.withTransaction {
											//ProjectMember newMember = projectMemberService.createMember(User.findByUsername(memberNameTextField.getValue()).getUsername())
											ProjectMember newMember = new ProjectMember(username: User.findByUsername(memberNameTextField.getValue()).getUsername())//.save(flush: true, ErrorOnFail: true)
											Project.withTransaction {
												project.addToProjectMembers(newMember)
												project.save()
											}
											membersTable.addItem(	[newMember.getUsername(), "",
												""] as Object[],
											new Integer(membersTable.size()+1));
										}

										window.close()
									}
								})

						okButton.setClickShortcut(KeyCode.ENTER);

						windowLayout.addComponent(okButton)
						windowLayout.setComponentAlignment(okButton, Alignment.MIDDLE_CENTER)
						windowLayout.setComponentAlignment(memberNameTextField, Alignment.MIDDLE_CENTER)
						window.setContent(windowLayout)
						UI.getCurrent().addWindow(window)
					}
				})

		membersLayout.setMargin(true)
		membersLayout.setSpacing(true)

		if(UI.getCurrent().getLogged()){
			membersLayout.addComponent(createNewMemberButton)
		}


		Panel updatesPanel = new Panel("Updates")
		updatesPanel.setPrimaryStyleName("island-panel")
		updatesPanel.setStyleName(Runo.PANEL_LIGHT)
		updatesPanel.setHeight("400px")

		VerticalLayout updatesLayout = new VerticalLayout()
		updatesLayout.setSizeFull()
		updatesLayout.setMargin(true)
		updatesLayout.setSpacing(true)
		updatesPanel.setContent(updatesLayout)

		Panel updateMessagePanel = new Panel()
		updatesLayout.addComponent(updateMessagePanel)
		updateMessagePanel.setHeight("310px")

		Update updates = new Update()
		updateMessagePanel.setContent(updates)

		HorizontalLayout messageUpdatesLayout = new HorizontalLayout()
		messageUpdatesLayout.setSpacing(true)
		messageUpdatesLayout.setWidth("100%")
		TextField messageField = new TextField()
		messageField.setWidth("100%")
		messageUpdatesLayout.addComponent(messageField)
		Button messageButton = new Button("Post", new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						updates.addMessage(messageField.getValue())
					}
				})
		messageUpdatesLayout.addComponent(messageButton)
		
		if(UI.getCurrent().getLogged()) {
			def currentUser = UI.getCurrent().getLoggedInUser().getUsername()
			if (checkIfMember(currentUser)) {
				updatesLayout.addComponent(messageUpdatesLayout)
			}
		}

		VerticalLayout filesLayout = new VerticalLayout()
		filesLayout.setWidth("450px")
		Panel filesPanel =  new Panel("Files")
		filesPanel.setPrimaryStyleName("island-panel")
		filesPanel.setStyleName(Runo.PANEL_LIGHT)
		filesPanel.setHeight("290px")
		filesPanel.setWidth("100%")
		VerticalLayout filesPanelLayout = new VerticalLayout()
		filesPanelLayout.setMargin(true)
		filesPanelLayout.setSpacing(true)
		Table fileTable = new Table()
		fileTable.setHeight("150px")
		fileTable.setWidth("100%")

		fileTable.addContainerProperty("File Name", String.class, null)

		fileTable.addContainerProperty("Date Created", String.class, null)

		List<Document> documents = Document.list()

		for (Document document : documents) {
			fileTable.addItem(	[document.getTitle(),
				document.getDateCreated().toString()] as Object[],
			new Integer(fileTable.size()+1));
		}
		Label status = new Label("Please select a file to upload");

		UploadReceiver receiver = new UploadReceiver()
		Upload upload = new Upload(null, receiver)
		upload.setImmediate(true)

		upload.addStartedListener(new Upload.StartedListener() {
					public void uploadStarted(StartedEvent event) {
						// This method gets called immediatedly after upload is started
						upload.setVisible(true);
						//progressLayout.setVisible(true);
						//pi.setValue(0f);
						//pi.setPollingInterval(500);
						status.setValue("Uploading file \"" + event.getFilename()
								+ "\"");
						def documentService = new DocumentService()
						Document.withTransaction {
							documentService.createDocument(event.getFilename())
						}
					}
				});

		filesPanelLayout.addComponent(fileTable)

		filesLayout.addComponent(filesPanel)
		if(UI.getCurrent().getLogged()){
			filesPanelLayout.addComponent(upload)
			filesPanelLayout.addComponent(status);
			filesPanelLayout.setComponentAlignment(upload, Alignment.MIDDLE_LEFT)

		}

		filesPanel.setContent(filesPanelLayout)



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

	private boolean checkIfMember(String username) {
		if(UI.getCurrent().getLogged()) {
			def currentUser = UI.getCurrent().getLoggedInUser().getUsername()
			if (currentProject.projectMembers.any { it.getUsername() == currentUser }) {
				return true
			}
		}
		return false
	}

	public class UploadReceiver implements Receiver {
		private static final long serialVersionUID = 2215337036540966711L;
		OutputStream outputFile = null;
		@Override
		public OutputStream receiveUpload(String strFilename, String strMIMEType) {
			File file=null;
			try {
				file = new File("C:\" "+strFilename);
				if(!file.exists()) {
					file.createNewFile();
				}
				outputFile =  new FileOutputStream(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return outputFile;
		}

		protected void finalize() {
			try {
				super.finalize();
				if(outputFile!=null) {
					outputFile.close();
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
		}
	}
}
