package kennisbank.fabtool.projects

import java.rmi.server.UID;
<<<<<<< HEAD
=======
import com.vaadin.shared.ui.label.ContentMode
>>>>>>> nilson
import com.vaadin.ui.*
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
<<<<<<< HEAD
import kennisbank.checkin.Checkout
=======
import kennisbank.ProjectMemberService
import kennisbank.ProjectService
import kennisbank.project.*
import kennisbank.fabtool.*
>>>>>>> nilson
import com.vaadin.ui.TabSheet.Tab
import com.vaadin.ui.Upload.Receiver
import com.vaadin.ui.Upload.StartedEvent
import com.vaadin.ui.Upload.SucceededEvent
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.themes.Reindeer
import com.vaadin.server.ExternalResource
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.server.ThemeResource
import com.vaadin.server.FileResource
import com.vaadin.server.Sizeable.Unit
import com.vaadin.data.util.HierarchicalContainer
import com.vaadin.data.Item

class ProjectView extends VerticalLayout {

	String uriFragment, oldPicturePath
	Checkout project
	def hiddenComponents
	Update updates

	String tabName() {
		return uriFragment
	}

	public ProjectView(Checkout project) {

		this.project = Checkout.findByUniqueID(project.uniqueID)
		hiddenComponents = []
		updates = new Update()
		
		updates.addSystemMessage("Project created")
		
		uriFragment = "#!/project/" + project.uniqueID
		UI.getCurrent().getPage().getCurrent().setLocation(uriFragment)

		setSizeFull()
		setMargin(true)

		Panel viewPanel = GenerateView()

		addComponent(viewPanel)
		setComponentAlignment(viewPanel, Alignment.TOP_CENTER)
	}

	private Panel GenerateView() {
		Panel panel = new Panel()
		panel.setPrimaryStyleName("island-panel")
		panel.setSizeUndefined()

		GridLayout layout = new GridLayout(2, 4)
		panel.setContent(layout)
		layout.setSpacing(true)
		layout.setMargin(true)

		// ------------------------------------------------------- Main Project -------------------------------------------------------


		// ------------------------------------------------------- Title -------------------------------------------------------

		VerticalLayout titleLayout = new VerticalLayout()
		layout.addComponent(titleLayout, 0, 0, 1, 0) // Column 0, Row 0 to Column 1, Row 0
		Label titleLabel = new Label("<h1><b>"+project.uniqueID+"</b></h1>", ContentMode.HTML)
		titleLayout.addComponent(titleLabel)
		titleLayout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
		titleLabel.setSizeUndefined()

		Label madeByLabel = new Label("Gemaakt door: <br><i>" + 
			project.checkin.firstName + " " + project.checkin.lastName + 
			"<br>(<A HREF=\"mailto:" + project.checkin.email + "\">"+ project.checkin.email +"</A>)" +
			"<br> op " + project.checkin.dateCreated.format('dd MMMM yyyy') + "</i>", ContentMode.HTML)
		
		// Column 1, Row 1
		layout.addComponent(madeByLabel, 1, 1)
		madeByLabel.setWidth("-1")

		// ------------------------------------------------------- Picture -------------------------------------------------------

		VerticalLayout uploadLayout = new VerticalLayout()
		layout.addComponent(uploadLayout, 0, 1) // Column 0, Row 1
		uploadLayout.setPrimaryStyleName("embedded-panel")
		uploadLayout.setSpacing(true)
		//uploadLayout.setMargin(true)

		Button pictureButton = new Button();
		uploadLayout.addComponent(pictureButton);
		pictureButton.setStyleName(Reindeer.BUTTON_LINK);
		pictureButton.addStyleName("picture-button");
		pictureButton.setIcon(new FileResource(new File(project.picturePath)));
		pictureButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Window window = new Window()
				window.setModal(true)
				window.setCaption(new File(project.picturePath).name)
				window.setStyleName(Reindeer.WINDOW_LIGHT)

				VerticalLayout windowLayout = new VerticalLayout()
				window.setContent(windowLayout)
				windowLayout.setSpacing(true)
				windowLayout.setMargin(true)

				Image image = new Image()
				windowLayout.addComponent(image)
				image.setSource(new FileResource(new File(project.picturePath)))
				image.setPrimaryStyleName("image-modal-window")				

				UI.getCurrent().addWindow(window)
			}
			})

		UploadReceiver receiver = new UploadReceiver(project) // Receiver that handles the data stream
		Upload upload = new Upload(null, receiver) // Upload button
		upload.addSucceededListener(new Upload.SucceededListener() {
			public void uploadSucceeded(SucceededEvent event) {
				Notification.show("Uploaden geslaagd!")	
			}
			})
		uploadLayout.setWidth("-1")
		uploadLayout.addComponent(upload)
		upload.setImmediate(true) // Starts to upload immediately after choosing file

		// ------------------------------------------------------- Material -------------------------------------------------------
		
		//VerticalLayout materialLayout = new VerticalLayout()
		//layout.addComponent(materialLayout, 0, 2, 1, 2) // Column 0, Row 2 to Column 1, Row 2
		//materialLayout.setMargin(true)

		TreeTable materialTreeTable = new TreeTable()
		layout.addComponent(materialTreeTable, 0, 2, 1, 2) // Column 0, Row 2 to Column 1, Row 2
		//materialLayout.addComponent(materialTreeTable)
		materialTreeTable.setWidth("100%")
		materialTreeTable.setPageLength(0)
		
		HierarchicalContainer container = new HierarchicalContainer()
		container.addContainerProperty("Apparatuur", Component.class, "")
		container.addContainerProperty("Instellingen", String.class, "")
		materialTreeTable.setContainerDataSource(container)

		for (def equipmentUsed : project.checkin.equipment) {
			Item item = container.addItem(equipmentUsed)
			item.getItemProperty("Apparatuur").setValue(new AddMaterialButton(equipmentUsed, container))
		}

		HorizontalLayout buttonsLayout = new HorizontalLayout()
		layout.addComponent(buttonsLayout, 0, 3, 1, 3)  // Column 0, Row 3 to Column 1, Row 3
		layout.setComponentAlignment(buttonsLayout, Alignment.TOP_CENTER)
		buttonsLayout.setSpacing(true)
		buttonsLayout.setWidth("100%")

		Button saveButton = new Button("Opslaan")
		buttonsLayout.addComponent(saveButton)
		buttonsLayout.setComponentAlignment(saveButton, Alignment.TOP_CENTER)		

		Button saveDraftButton = new Button("Tijdelijk opslaan")
		buttonsLayout.addComponent(saveDraftButton)
		buttonsLayout.setComponentAlignment(saveDraftButton, Alignment.TOP_LEFT)

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

		//layout.setColumnExpandRatio(1, 0.1)
		//layout.setRowExpandRatio(1, 0.1)

		// Add the main layout to the main panel
		panel.setContent(layout)

		if(UI.getCurrent().loggedIn) {
			def currentUser = UI.getCurrent().getLoggedInUser().getUsername()
			if (checkIfMember(currentUser)) {
				revealHiddenComponents()
			}
		}
		else {
			hideRevealedComponents()
		}

		return panel
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
			if (project.projectMembers.any { it.getUsername() == currentUser }) {
				return true
			}
		}
		return false
	}

	public class UploadReceiver implements Receiver {

		OutputStream outputFile = null
		Checkout project

		public UploadReceiver(Checkout project) {
			this.project = project
		}

		@Override
		public OutputStream receiveUpload(String strFilename, String strMIMEType) {
			File file

			try {

				new File('uploads/'+project.uniqueID).mkdirs()
				file = new File("uploads/"+project.uniqueID+"/"+strFilename)

				if(!file.exists()) {
					file.createNewFile()
					Checkout.withTransaction {
						String oldPicturePath = project.picturePath
						project.picturePath = file.absolutePath
						project = project.merge()
						project.save()
						new File(oldPicturePath).delete()
					}
				}
				else { 
					Notification.show("This file has already been uploaded!") 
					return
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
