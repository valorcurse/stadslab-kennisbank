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
		
		VerticalLayout membersLayout = new VerticalLayout()
		membersLayout.setWidth("150px")
		membersPanel.setContent(membersLayout)
		
		/*VerticalLayout popupLayout = new VerticalLayout()
		Button addMemberButton = new Button("Add")
		TextField usernameField = new TextField()
		popupLayout.addComponent(usernameField)
		popupLayout.addComponent(addMemberButton)
		addMemberButton.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
					}
				})

		PopupView popup = new PopupView("Add Member", popupLayout)
		membersLayout.addComponent(popup)
*/
		Panel updatesPanel = new Panel("Panel")
		VerticalLayout updatesLayout = new VerticalLayout()
		updatesLayout.setWidth("100%")
		updatesPanel.setContent(updatesLayout)
		
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
