package kennisbank

import com.vaadin.ui.*
import com.vaadin.ui.MenuBar.Command
import com.vaadin.ui.MenuBar.MenuItem
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent
import com.vaadin.ui.TabSheet.Tab
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.ChameleonTheme;
import com.vaadin.ui.themes.Runo;
import com.vaadin.server.Resource
import com.vaadin.server.VaadinRequest;
import com.vaadin.annotations.Theme;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import kennisbank.project.Member;

/**
 *
 *
 * @author
 */

//Theme being used, default is Reindeer
//@Theme("runo")

class MyUI extends UI {

	Member members = new Member()
	
	@Override
	public void init(VaadinRequest request) {
		
		//Main layout
		VerticalLayout view = new VerticalLayout()
		view.setSizeFull() // Set layout to cover the whole screen

		VerticalLayout centerView = new VerticalLayout()
		centerView.setWidth("70%")
		centerView.setHeight("100%")
		view.addComponent(centerView)
		view.setComponentAlignment(centerView, Alignment.TOP_CENTER)

		// Tabs on the right panel
		TabSheet topTabs = new TabSheet()
		topTabs.setSizeFull()

		//Home tab
		VerticalLayout homeVerticalLayout = new VerticalLayout();
		homeVerticalLayout.setSizeFull()
		topTabs.addTab(homeVerticalLayout, "Home")

		// Layout for the left panel
		VerticalLayout left = new VerticalLayout()
		left.setSpacing(true)

		// Split panel dividing the left panel from the right
		HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
		horizontalSplitPanel.setSplitPosition(18);
		horizontalSplitPanel.setLocked(true);
		horizontalSplitPanel.setHeight("100%");
		horizontalSplitPanel.setWidth("100%");
		horizontalSplitPanel.addComponent(left)
		horizontalSplitPanel.addComponent(topTabs)
		horizontalSplitPanel.setStyleName(Runo.SPLITPANEL_SMALL);
		centerView.addComponent(horizontalSplitPanel);

		// Logo on the top-left
		Embedded logo = new Embedded("", new ThemeResource("hr.gif"));
		logo.setWidth("95%")
		//logo

		// Search field on the left
		Panel searchPanel = new Panel("Search")
		searchPanel.setHeight("80px")
		searchPanel.setStyleName(Runo.PANEL_LIGHT)
		TextField searchField = new TextField()
		searchPanel.setWidth("95%")
		searchField.addStyleName("search")
		searchField.setInputPrompt("Search...")
		VerticalLayout searchLayout = new VerticalLayout()
		searchPanel.setContent(searchLayout)
		searchLayout.setSizeFull()
		searchLayout.addComponent(searchField)
		searchLayout.setComponentAlignment(searchField, Alignment.MIDDLE_CENTER)

		// Menu on the left
		Accordion leftMenu = new Accordion()
		VerticalLayout leftMenuButtonsLayout = new VerticalLayout()
		leftMenuButtonsLayout.setStyleName("sidebar-menu")

		Button projectButton = new Button("Projects")
		projectButton.setWidth("100%")
		projectButton.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						Tab tab = topTabs.addTab(createProjectPage(), "Projects")
						tab.setClosable(true)
						topTabs.setSelectedTab(tab)
					}
				});

		leftMenuButtonsLayout.addComponent(projectButton)
		leftMenu.setStyleName(Runo.ACCORDION_LIGHT)
		leftMenu.setWidth("100%")
		leftMenu.addTab(leftMenuButtonsLayout, "Menu")
		leftMenu.setWidth("95%")

		// Login Panel
		Panel loginPanel = new Panel("Login")
		loginPanel.setHeight("150px")
		loginPanel.setStyleName(Runo.PANEL_LIGHT);
		VerticalLayout loginPanelLayout = new VerticalLayout()
		loginPanelLayout.setSizeFull()
		loginPanel.setContent(loginPanelLayout)
		loginPanel.setWidth("95%")
		TextField usernameField = new TextField()
		PasswordField passwordField = new PasswordField()
		usernameField.setInputPrompt("Username")
		passwordField.setInputPrompt("Password")
		loginPanelLayout.addComponent(usernameField)
		loginPanelLayout.addComponent(passwordField)
		Button loginButton = new Button("Login")
		loginButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				//authenticate...
			}
		});
		loginPanelLayout.addComponent(loginButton)
		loginPanelLayout.setComponentAlignment(usernameField, Alignment.MIDDLE_CENTER)
		loginPanelLayout.setComponentAlignment(passwordField, Alignment.TOP_CENTER)
		loginPanelLayout.setComponentAlignment(loginButton, Alignment.TOP_CENTER)

		//Add components to the left panel
		left.addComponent(logo)
		left.addComponent(loginPanel)
		left.addComponent(searchPanel)
		left.addComponent(leftMenu)

		//Align components in the left panel
		left.setComponentAlignment(loginPanel, Alignment.TOP_CENTER)
		left.setComponentAlignment(leftMenu, Alignment.TOP_CENTER)
		left.setComponentAlignment(searchPanel, Alignment.TOP_CENTER)
		left.setComponentAlignment(logo, Alignment.TOP_CENTER)

		setContent(view)
	}

	private Layout createProjectPage() {
		VerticalLayout mainLayout = new VerticalLayout()
		mainLayout.setWidth("100%")
		GridLayout layout = new GridLayout(2, 4)
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
		membersPanelLayout.addComponent(members.addMember("Marcelo"))
		membersPanelLayout.addComponent(members.addMember("Marouane"))
		membersPanelLayout.addComponent(members.addMember("Nilson"))
		Label membersLabel = new Label("<b>Members</b>", Label.CONTENT_XHTML)
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

		layout.addComponent(menu, 0, 0, 1, 0)
		layout.addComponent(summaryLayout, 0, 1, 1, 1)
		layout.addComponent(membersLayout, 0, 2)
		layout.addComponent(updatesLayout, 1, 2, 1, 3)
		layout.addComponent(filesLayout, 0, 3)

		mainLayout.addComponent(menu)
		mainLayout.addComponent(layout)

		layout.setColumnExpandRatio(1, 0.1)

		return mainLayout
	}
}