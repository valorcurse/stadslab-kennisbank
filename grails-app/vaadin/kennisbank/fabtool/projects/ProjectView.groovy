package kennisbank.fabtool.projects

import java.rmi.server.UID;

import com.vaadin.ui.*
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import kennisbank.project.*
import kennisbank.*
import kennisbank.checkin.Checkout
import com.vaadin.ui.TabSheet.Tab
import com.vaadin.ui.Upload.Receiver
import com.vaadin.ui.Upload.StartedEvent
import com.vaadin.ui.Upload.SucceededEvent
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.themes.Reindeer
import com.vaadin.server.ExternalResource
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.shared.ui.label.ContentMode

class ProjectView extends VerticalLayout {

	String uriFragment
	ProjectService projectService
	Checkout currentProject
	def hiddenComponents
	Update updates

	String tabName() {
		return uriFragment
	}

	public ProjectView(Checkout project) {

		print project.toString()

		//projectService = new ProjectService(project)
		currentProject = project
		hiddenComponents = []
		updates = new Update()
		
		updates.addSystemMessage("Project created")
		
		uriFragment = "#!/project/" + project.uniqueID
		UI.getCurrent().getPage().getCurrent().setLocation(uriFragment)

		setSizeFull()
		setMargin(true)

		Panel panel = new Panel()
		panel.setPrimaryStyleName("island-panel")

		TabSheet projectTabs = new TabSheet()
		projectTabs.setSizeFull()
		projectTabs.setStyleName(Reindeer.TABSHEET_MINIMAL)
		projectTabs.setId("project-tabs")

		GridLayout layout = new GridLayout(2, 4)
		layout.setSpacing(true)
		layout.setMargin(true)
		layout.setWidth("100%")
		layout.setHeight("100%")


		// ------------------------------------------------------- Main Project -------------------------------------------------------

		projectTabs.addTab(layout, "Main Project")

		// ------------------------------------------------------- Title -------------------------------------------------------

		Label titleLabel = new Label("<h1><b>"+project.uniqueID+"</b></h1>", ContentMode.HTML)
		// Column 0, Row 0 to Column 1, Row 0
		layout.addComponent(titleLabel, 0, 0, 1, 0)
		titleLabel.setWidth("100%")

		Label madeByLabel = new Label("<i>Gemaakt door " + project.checkin.firstName + " " + project.checkin.lastName + " (" + project.checkin.email + ")</i>", ContentMode.HTML)
		// Column 0, Row 1 to Column 1, Row 1
		layout.addComponent(madeByLabel, 0, 1, 1, 1)
		madeByLabel.setWidth("100%")

		// ------------------------------------------------------- Summary -------------------------------------------------------
		/*
		Panel summaryPanel = new Panel("Summary")
		summaryPanel.setPrimaryStyleName("embedded-panel")
		summaryPanel.setStyleName(Runo.PANEL_LIGHT)

		VerticalLayout summaryLayout = new VerticalLayout()
		summaryLayout.setSpacing(true)
		summaryLayout.setMargin(true)
		summaryLayout.setWidth("100%")

		
		RichTextArea editor = new RichTextArea()
		editor.setWidth("100%")
		Label summaryText = new Label()
		summaryText.setWidth("100%")
		summaryText.setContentMode(ContentMode.HTML)
		summaryText.setValue(project.getSummary())
		summaryLayout.addComponent(summaryText)
		NativeButton editButton = new NativeButton("Edit")
		editButton.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						if (editButton.getCaption() == "Apply") {
							Project.withTransaction {
								//projectService.setSummary(editor.getValue())
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
		summaryLayout.addComponent(editButton)
		hiddenComponents.add(editButton)
		editButton.setVisible(false)
		summaryLayout.setComponentAlignment(editButton, Alignment.TOP_RIGHT)
		summaryPanel.setContent(summaryLayout)

		// ------------------------------------------------------- Members -------------------------------------------------------

		Panel membersPanel = new Panel("Members")
		membersPanel.setPrimaryStyleName("embedded-panel")
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
			new Integer(membersTable.size()+1))
		}
		membersLayout.addComponent(membersTable)

		NativeButton createNewMemberButton = new NativeButton("Add Member", new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						Window window = new Window("Add a new member")
						window.setModal(true)

						VerticalLayout windowLayout = new VerticalLayout()
						windowLayout.setSpacing(true)
						windowLayout.setMargin(true)

						TextField memberNameTextField = new TextField("Name")
						windowLayout.addComponent(memberNameTextField)

						NativeButton okButton = new NativeButton("Add", new Button.ClickListener() {
									public void buttonClick(ClickEvent event2) {
										def projectMemberService = new ProjectMemberService()
										ProjectMember.withTransaction {
											ProjectMember newMember = new ProjectMember(username: User.findByUsername(memberNameTextField.getValue()).getUsername())
											project.addToProjectMembers(newMember)
											project.save()
										}
										membersTable.addItem(	[newMember.getUsername(), "", ""] as Object[],
										new Integer(membersTable.size()+1))

										window.close()
									}
								})

						okButton.setClickShortcut(KeyCode.ENTER)

						windowLayout.addComponent(okButton)
						windowLayout.setComponentAlignment(okButton, Alignment.MIDDLE_CENTER)
						windowLayout.setComponentAlignment(memberNameTextField, Alignment.MIDDLE_CENTER)
						window.setContent(windowLayout)
						UI.getCurrent().addWindow(window)
					}
				})

		hiddenComponents.add(createNewMemberButton)
		membersLayout.addComponent(createNewMemberButton)
		membersLayout.setMargin(true)
		membersLayout.setSpacing(true)

		// ------------------------------------------------------- Updates -------------------------------------------------------

		Panel updatesPanel = new Panel("Updates")
		updatesPanel.setPrimaryStyleName("embedded-panel")
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

		updateMessagePanel.setContent(updates)

		HorizontalLayout messageUpdatesLayout = new HorizontalLayout()
		messageUpdatesLayout.setSpacing(true)
		messageUpdatesLayout.setWidth("100%")
		TextField messageField = new TextField()
		messageField.setWidth("100%")
		messageUpdatesLayout.addComponent(messageField)
		NativeButton messageButton = new NativeButton("Post", new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						if (messageField.getValue() != "") {
							updates.addMessage(messageField.getValue())
							messageField.setValue("")
						}
					}
				})
		messageUpdatesLayout.addComponent(messageButton)
		updatesLayout.addComponent(messageUpdatesLayout)
		hiddenComponents.add(messageUpdatesLayout)

		// ------------------------------------------------------- Uploads -------------------------------------------------------

		VerticalLayout filesLayout = new VerticalLayout()
		filesLayout.setWidth("450px")

		Panel filesPanel =  new Panel("Files")
		filesPanel.setPrimaryStyleName("embedded-panel")
		filesPanel.setStyleName(Runo.PANEL_LIGHT)
		filesPanel.setHeight("290px")
		filesPanel.setWidth("100%")

		VerticalLayout filesPanelLayout = new VerticalLayout()
		filesPanelLayout.setMargin(true)
		filesPanelLayout.setSpacing(true)

		Table fileTable = new Table()
		fileTable.setHeight("150px")
		fileTable.setWidth("100%")
		fileTable.addContainerProperty("File Name", DownloadLink.class, null)
		fileTable.addContainerProperty("Date Created", String.class, null)

		// List all uploaded files in the table
		List<Document> documents = project.documents
		for (Document document : documents) {
			fileTable.addItem(	[new DownloadLink(document), document.getDateCreated().toString()] as Object[],
			new Integer(fileTable.size()+1))
		}

		ProgressIndicator progressBar = new ProgressIndicator() // Progress bar for uploads
		progressBar.setVisible(false) // Hide the progress bar from unauthorized users

		UploadReceiver receiver = new UploadReceiver(project) // Receiver that handles the data stream
		Upload upload = new Upload(null, receiver) // Upload button
		upload.setImmediate(true) // Starts to upload immediately after choosing file

		upload.addStartedListener(new Upload.StartedListener() {
					public void uploadStarted(StartedEvent event) {
						progressBar.setValue(0f)
						progressBar.setVisible(true)
					}
				})

		upload.addProgressListener(new Upload.ProgressListener() {
					public void updateProgress(long readBytes, long contentLength) {
						// this method gets called several times during the update
						progressBar.setValue(new Float(readBytes / (float) contentLength));
					}

				});

		upload.addSucceededListener(new Upload.SucceededListener() {
					public void uploadSucceeded(SucceededEvent event) {
						progressBar.setVisible(false)
					}
				});

		filesPanelLayout.addComponent(fileTable)
		filesPanelLayout.addComponent(upload)
		hiddenComponents.add(upload)
		filesPanelLayout.addComponent(progressBar)

		filesPanelLayout.setComponentAlignment(upload, Alignment.MIDDLE_LEFT)

		filesLayout.addComponent(filesPanel)

		filesPanel.setContent(filesPanelLayout)
*/
		// Add components to the grid
		// Column 0, Row 1 to Column 1, Row 1
		//layout.addComponent(summaryPanel, 0, 1, 1, 1)
		// Column 0, Row 2
		//layout.addComponent(membersPanel, 0, 2)
		// Column 1, Row 2 to Column 1, Row 3
		//layout.addComponent(updatesPanel, 1, 2, 1, 3)
		// Column 0, Row 3
		//layout.addComponent(filesLayout, 0, 3)

		layout.setColumnExpandRatio(1, 0.1)

		// Add the main layout to the main panel
		panel.setContent(layout)

		addComponent(panel)

		if(UI.getCurrent().loggedIn) {
			def currentUser = UI.getCurrent().getLoggedInUser().getUsername()
			if (checkIfMember(currentUser)) {
				revealHiddenComponents()
			}
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

	private boolean checkIfMember(String username) {
		if(UI.getCurrent().getLoggedIn()) {
			def currentUser = UI.getCurrent().getLoggedInUser().getUsername()
			if (currentProject.projectMembers.any { it.getUsername() == currentUser }) {
				return true
			}
		}
		return false
	}

	public class UploadReceiver implements Receiver {

		OutputStream outputFile = null
		Project project = null

		UploadReceiver(Project project) {
			this.project = project
		}

		@Override
		public OutputStream receiveUpload(String strFilename, String strMIMEType) {
			File file = null

			try {
				new File("uploads/"+project.getTitle()).mkdirs()
				file = new File("uploads/"+project.getTitle()+"/"+strFilename)


				if(!file.exists()) {
					file.createNewFile()
					Document.withTransaction {
						Document newDocument = new Document(title: file.name, path: file.absolutePath)
						project.addToDocuments(newDocument)
						project.save()
					}
				}
				outputFile =  new FileOutputStream(file)
			} catch (IOException e) {
				e.printStackTrace()
			}
			return outputFile
		}

		protected void finalize() {
			try {
				super.finalize()
				if(outputFile != null) {
					outputFile.close()
				}
			} catch (Throwable exception) {
				exception.printStackTrace()
			}
		}
	}
}
