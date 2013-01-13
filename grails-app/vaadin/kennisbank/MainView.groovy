package kennisbank

import com.vaadin.server.ThemeResource
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TabSheet.Tab
import com.vaadin.navigator.Navigator
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import kennisbank.projects.*
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.themes.Reindeer

class MainView extends Panel implements View {

	TabSheet topTabs

	public MainView() {

		setSizeFull()

		//Main layout
		VerticalLayout view = new VerticalLayout()
		view.setSizeFull() // Set layout to cover the whole screen

		//view.setComponentAlignment(view, Alignment.TOP_CENTER)

		// Tabs on the right panel
		topTabs = new TabSheet()
		topTabs.setSizeFull()
		topTabs.addStyleName(Reindeer.TABSHEET_BORDERLESS)
		topTabs.addStyleName(Reindeer.TABSHEET_HOVER_CLOSABLE)

		//Home tab
		VerticalLayout homeVerticalLayout = new VerticalLayout()
		homeVerticalLayout.setSizeFull()
		topTabs.addTab(homeVerticalLayout, "Home")

		// Layout for the left panel
		VerticalLayout left = new VerticalLayout()
		//left.setPrimaryStyleName("island-layout")
		left.setSpacing(true)
		left.setMargin(true)
		left.setWidth("100%")

		// Split panel dividing the left panel from the right
		HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel()
		horizontalSplitPanel.setSplitPosition(12)
		horizontalSplitPanel.setLocked(false)
		horizontalSplitPanel.setHeight("100%")
		horizontalSplitPanel.setWidth("100%")
		horizontalSplitPanel.addComponent(left)
		horizontalSplitPanel.addComponent(topTabs)
		horizontalSplitPanel.setStyleName(Runo.SPLITPANEL_SMALL)
		view.addComponent(horizontalSplitPanel)

		// Logo on the top-left
		Panel logoPanel = new Panel()
		logoPanel.setPrimaryStyleName("island-panel")
		logoPanel.setStyleName(Runo.PANEL_LIGHT)
		
		HorizontalLayout logoLayout = new HorizontalLayout()
		logoLayout.setSpacing(true)
		logoPanel.setContent(logoLayout)
		
		Embedded logo = new Embedded(null, new ThemeResource("hr.gif"))
		//logo.setWidth("95%")
		logo.setHeight("32px")
		logoLayout.addComponent(logo)
		logoLayout.setComponentAlignment(logo, Alignment.MIDDLE_CENTER)
		
		Label logoLabel = new Label ("<b>Kennisbank</b>", Label.CONTENT_XHTML)
		logoLayout.addComponent(logoLabel)
		logoLayout.setComponentAlignment(logoLabel, Alignment.MIDDLE_CENTER)
		logoLayout.setMargin(true)

		// Search field on the left
		Panel searchPanel = new Panel("Search")

		searchPanel.setHeight("70px")
		searchPanel.setWidth("100%")

		searchPanel.setStyleName(Runo.PANEL_LIGHT)
		searchPanel.setPrimaryStyleName("island-panel")

		TextField searchField = new TextField()
		searchField.addStyleName("search")
		searchField.setInputPrompt("Search...")
		VerticalLayout searchLayout = new VerticalLayout()
		searchPanel.setContent(searchLayout)
		searchLayout.setSizeFull()
		searchLayout.addComponent(searchField)
		searchLayout.setComponentAlignment(searchField, Alignment.MIDDLE_CENTER)

		// Menu on the left
		Panel leftMenuPanel = new Panel("Menu")

		leftMenuPanel.setStyleName(Runo.PANEL_LIGHT)
		leftMenuPanel.setPrimaryStyleName("island-panel")

		leftMenuPanel.setWidth("100%")
		leftMenuPanel.setHeight("70px")
		VerticalLayout leftMenuLayout = new VerticalLayout()
		leftMenuLayout.setStyleName("sidebar-menu")
		leftMenuLayout.setSizeFull()
		leftMenuLayout.setSpacing(true)


		Button projectButton = new Button("Projects")
		projectButton.setWidth("90%")
		projectButton.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						Tab tab = topTabs.addTab(new ProjectsOverview(), "Projects")
						tab.setClosable(true)
						topTabs.setSelectedTab(tab)
					}
				})

		leftMenuPanel.setContent(leftMenuLayout)
		leftMenuLayout.addComponent(projectButton)
		leftMenuLayout.setComponentAlignment(projectButton, Alignment.MIDDLE_CENTER)

		// Login Panel
		Panel loginPanel = new Panel("Login")
		loginPanel.setPrimaryStyleName("island-panel")
		loginPanel.setHeight("130px")
		loginPanel.setStyleName(Runo.PANEL_LIGHT)
		VerticalLayout loginPanelLayout = new VerticalLayout()
		loginPanelLayout.setSizeFull()
		loginPanel.setContent(loginPanelLayout)
		loginPanel.setWidth("100%")
		TextField usernameField = new TextField()
		PasswordField passwordField = new PasswordField()
		usernameField.setInputPrompt("Username")
		passwordField.setInputPrompt("Password")
		loginPanelLayout.addComponent(usernameField)
		loginPanelLayout.addComponent(passwordField)
		
		Button loginButton = new Button("Login")
		
		//Loggedin Panel
		
		Panel loggedinPanel = new Panel ("Welcome")
		loggedinPanel.setPrimaryStyleName("island-panel")
		loggedinPanel.setHeight("130px")
		loggedinPanel.setStyleName(Runo.PANEL_LIGHT)
		VerticalLayout loggedinPanelLayout = new VerticalLayout()
		loggedinPanel.setSizeFull()
		loggedinPanel.setContent(loggedinPanelLayout)
		loggedinPanel.setWidth("100%")
		Label welcome = new Label()
		loggedinPanelLayout.addComponent(welcome)
		loggedinPanel.setVisible(false);
		
		Button logoutButton = new Button("Log out")
		
		loginButton.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						
						println(usernameField.getValue());
						println(passwordField.getValue());
						if(usernameField.getValue() == "admin" && passwordField.getValue() == "password"){
							UI.getCurrent().setLogged(true);
							println(UI.getCurrent().getLogged());
							Notification.show("Login succesful!");
							welcome.setValue("You're now logged in, "+usernameField.getValue()+".")
							left.replaceComponent(loginPanel, loggedinPanel)
							loggedinPanel.setVisible(true);
							loginPanel.setVisible(false);
							
						}
						else{
							Notification.show("Nope, chucktesta!")
						}
					}
				})
		logoutButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().setLogged(false);
				left.replaceComponent(loggedinPanel, loginPanel)
				loggedinPanel.setVisible(false);
				loginPanel.setVisible(true);
				Notification.show("You're now logged out")
			}
			
		})
		loginPanelLayout.addComponent(loginButton)
		loggedinPanelLayout.addComponent(logoutButton)
		loginPanelLayout.setComponentAlignment(usernameField, Alignment.MIDDLE_CENTER)
		loginPanelLayout.setComponentAlignment(passwordField, Alignment.TOP_CENTER)
		loginPanelLayout.setComponentAlignment(loginButton, Alignment.TOP_CENTER)
		loggedinPanelLayout.setComponentAlignment(logoutButton, Alignment.TOP_CENTER)
		
		//Add components to the left panel
		left.addComponent(logoPanel)
		left.addComponent(loginPanel)
		left.addComponent(searchPanel)
		left.addComponent(leftMenuPanel)
		left.addComponent(loggedinPanel)

		//Align components in the left panel
		left.setComponentAlignment(loginPanel, Alignment.TOP_CENTER)
		left.setComponentAlignment(leftMenuPanel, Alignment.TOP_CENTER)
		left.setComponentAlignment(searchPanel, Alignment.TOP_CENTER)
		left.setComponentAlignment(logoPanel, Alignment.TOP_CENTER)
		left.setComponentAlignment(loggedinPanel, Alignment.TOP_CENTER)

		setContent(view)
	}

	public void enter(ViewChangeEvent event) {
		if(event.getParameters() != null){
			String[] msgs = event.getParameters().split("/")

			if (msgs[0] == "project") {
				Project currentProject = Project.findByTitle(msgs[1])

				if (currentProject != null) {
					ProjectView projectTab = new ProjectView(currentProject)
					Tab tab = topTabs.addTab(projectTab, "Project: "+ currentProject.getTitle())
					tab.setClosable(true)
					topTabs.setSelectedTab(tab)
				}
			}
		}
	}

}
