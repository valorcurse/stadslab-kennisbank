package kennisbank.projects

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import kennisbank.Project
import kennisbank.projects.Member
import com.vaadin.ui.TabSheet.Tab


class ProjectView extends CssLayout {


	public ProjectView(Project project) {

		VerticalLayout mainLayout = new VerticalLayout()
		mainLayout.setWidth("100%")
		GridLayout layout = new GridLayout(2, 5)
		layout.setSpacing(true)
		layout.setMargin(true)
		layout.setWidth("100%")

		MenuBar menu = new MenuBar()
		menu.setWidth("100%")
		final MenuBar.MenuItem projectItem = menu.addItem("Project", null);
		final MenuBar.MenuItem membersItem = menu.addItem("Members", null);
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
		
		
		VerticalLayout summaryLayout = new VerticalLayout()
		summaryLayout.setSpacing(true)
		summaryLayout.setWidth("100%")

		RichTextArea editor = new RichTextArea()
		editor.setWidth("100%")

		Panel summaryTextPanel = new Panel()
		summaryTextPanel.setHeight("150px")
		Label summaryText = new Label()
		summaryText.setWidth("100%")
		summaryText.setContentMode(Label.CONTENT_XHTML);
		summaryTextPanel.setContent(summaryText)

		Label summaryLabel = new Label("<b>Summary</b>", Label.CONTENT_XHTML)

		Button editButton = new Button("Edit")
		editButton.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						if (editButton.getCaption() == "Apply") {
							summaryText.setValue(editor.getValue());
							summaryLayout.replaceComponent(editor, summaryTextPanel);
							editButton.setCaption("Edit");
						}
						else if (editButton.getCaption() == "Edit") {
							editor.setValue(summaryText.getValue());
							summaryLayout.replaceComponent(summaryTextPanel, editor);
							editButton.setCaption("Apply");
						}
					}
				})

		summaryLayout.addComponent(summaryLabel)
		summaryLayout.addComponent(summaryTextPanel)
		summaryLayout.addComponent(editButton)
		summaryLayout.setComponentAlignment(editButton, Alignment.TOP_RIGHT)


		VerticalLayout membersLayout = new VerticalLayout()
		membersLayout.setWidth("150px")
		Panel membersPanel =  new Panel()
		membersPanel.setHeight("250px")
		VerticalLayout membersPanelLayout = new VerticalLayout()
		membersPanel.setContent(membersPanelLayout)
		//membersPanelLayout.addComponent(members.addMember("Marcelo"))
		//membersPanelLayout.addComponent(members.addMember("Marouane"))
		//membersPanelLayout.addComponent(members.addMember("Nilson"))
		Label membersLabel = new Label("<b>Members<\b>", Label.CONTENT_XHTML)
		VerticalLayout popupLayout = new VerticalLayout()
		Button addMemberButton = new Button("Add")
		TextField usernameField = new TextField()
		popupLayout.addComponent(usernameField)
		popupLayout.addComponent(addMemberButton)
		addMemberButton.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						membersPanelLayout.addComponent(members.addMember(usernameField.getValue()))
					}
				})

		PopupView popup = new PopupView("Add Member", popupLayout);

		membersLayout.addComponent(membersLabel)
		membersLayout.addComponent(membersPanel)
		membersLayout.addComponent(popup)

		VerticalLayout updatesLayout = new VerticalLayout()
		updatesLayout.setWidth("100%")
		Panel updatesPanel = new Panel()
		updatesPanel.setWidth("100%")
		updatesPanel.setHeight("540px")
		Label updatesLabel = new Label("<b>Updates</b>", Label.CONTENT_XHTML)
		updatesLayout.addComponent(updatesLabel)
		updatesLayout.addComponent(updatesPanel)
		updatesLayout.setComponentAlignment(updatesPanel, Alignment.TOP_LEFT)

		VerticalLayout filesLayout = new VerticalLayout()
		filesLayout.setWidth("150px")
		Panel filesPanel =  new Panel()
		filesPanel.setHeight("250px")
		filesPanel.setWidth("100%")
		VerticalLayout filesPanelLayout = new VerticalLayout()
		Label filesLabel = new Label("<b>Files</b>", Label.CONTENT_XHTML)

		Upload upload = new Upload(null, null);

		filesLayout.addComponent(filesLabel)
		filesLayout.addComponent(filesPanel)
		filesPanel.setContent(filesPanelLayout)
		filesPanelLayout.addComponent(upload)

		layout.addComponent(titleLabel, 0, 0, 1, 0)
		layout.addComponent(menu, 0, 1, 1, 1)
		layout.addComponent(summaryLayout, 0, 2, 1, 2)
		layout.addComponent(membersLayout, 0, 3)
		layout.addComponent(updatesLayout, 1, 3, 1, 4)
		layout.addComponent(filesLayout, 0, 4)

		mainLayout.addComponent(menu)
		mainLayout.addComponent(layout)

		layout.setColumnExpandRatio(1, 0.1)

		addComponent(mainLayout)
	}


}
