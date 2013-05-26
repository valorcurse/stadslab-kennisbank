package kennisbank.fabtool

import com.vaadin.server.ThemeResource
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent
import com.vaadin.ui.TabSheet.Tab
import com.vaadin.navigator.Navigator
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent

import kennisbank.User;
import kennisbank.UserService;
import kennisbank.checkin.Administration
import kennisbank.fabtool.home.*
import kennisbank.project.*;
import kennisbank.fabtool.projects.*
import kennisbank.checkin.Checkout
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.themes.Reindeer
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Sizeable.Unit


class MainView extends Panel implements View {

	TabSheet topTabs

	public MainView() {

		setSizeFull()

		//Main layout
		VerticalLayout view = new VerticalLayout()
		view.setSizeFull() // Set layout to cover the whole screen

		// Tabs on the right panel
		topTabs = new TabSheet()
		topTabs.setSizeFull()
		topTabs.addStyleName(Reindeer.TABSHEET_BORDERLESS)
		topTabs.addStyleName(Reindeer.TABSHEET_HOVER_CLOSABLE)
		topTabs.setId("top-tabs")

		//Home tab
		topTabs.addTab(new HomeView(), "Home")
		topTabs.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			public void selectedTabChange(SelectedTabChangeEvent event) {
				Tab tab = topTabs.getTab(event.getTabSheet().getSelectedTab())
				UI.getCurrent().getPage().getCurrent().setLocation(tab.getComponent().tabName())
			}
			});

		// Layout for the left panel
		VerticalLayout left = new VerticalLayout()
		left.setSpacing(true)
		left.setWidth("100%")

		// Split panel dividing the left panel from the right
		HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel()
		horizontalSplitPanel.setSplitPosition(160, Unit.PIXELS)
		horizontalSplitPanel.setLocked(true)
		horizontalSplitPanel.addStyleName("invisible")
		horizontalSplitPanel.setHeight("100%")
		horizontalSplitPanel.setWidth("100%")
		horizontalSplitPanel.addComponent(left)
		horizontalSplitPanel.addComponent(topTabs)
		horizontalSplitPanel.setStyleName(Runo.SPLITPANEL_SMALL)
		view.addComponent(horizontalSplitPanel)

		// Logo on the top-left
		Panel logoPanel = new Panel()
		logoPanel.setPrimaryStyleName("sidebar-panel")
		logoPanel.setStyleName(Runo.PANEL_LIGHT)

		HorizontalLayout logoLayout = new HorizontalLayout()
		logoLayout.setSpacing(true)
		logoPanel.setContent(logoLayout)

		Embedded logo = new Embedded(null, new ThemeResource("hr.gif"))
		logo.setHeight("32px")
		logoLayout.addComponent(logo)
		logoLayout.setComponentAlignment(logo, Alignment.MIDDLE_CENTER)

		Label logoLabel = new Label ("<b>Kennisbank</b>", Label.CONTENT_XHTML)
		logoLabel.addStyleName("logo-label")
		logoLayout.addComponent(logoLabel)
		logoLayout.setComponentAlignment(logoLabel, Alignment.MIDDLE_CENTER)
		logoLayout.setMargin(true)

		// Search field on the left
		Panel searchPanel = new Panel("Search")

		searchPanel.setHeight("70px")
		searchPanel.setWidth("100%")

		searchPanel.setStyleName(Runo.PANEL_LIGHT)
		searchPanel.setPrimaryStyleName("sidebar-panel")

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
		leftMenuPanel.setPrimaryStyleName("sidebar-panel")

		leftMenuPanel.setWidth("100%")
		VerticalLayout leftMenuLayout = new VerticalLayout()
		leftMenuLayout.setSizeFull()
		leftMenuLayout.setSpacing(true)


		NativeButton projectButton = new NativeButton("Projects")
		projectButton.setWidth("90%")
		projectButton.addClickListener(new Button.ClickListener() {


			public void buttonClick(ClickEvent event) {
				Tab tab = topTabs.addTab(new ProjectsOverview(), "Projects")
				tab.setClosable(true)
				topTabs.setSelectedTab(tab)
			}
			})
		
		NativeButton adminButton = new NativeButton("Administration")
		adminButton.setWidth("90%")
		//adminButton.setHeight("50px")
		adminButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				Tab tab = topTabs.addTab(new Administration(), "Administrati")
				tab.setClosable(true)
				topTabs.setSelectedTab(tab)
			}
			})
		leftMenuPanel.setContent(leftMenuLayout)
		leftMenuLayout.addComponent(projectButton)
		leftMenuLayout.addComponent(adminButton)
		leftMenuLayout.setComponentAlignment(projectButton, Alignment.MIDDLE_CENTER)
		leftMenuLayout.setComponentAlignment(adminButton, Alignment.BOTTOM_CENTER)

		// ------------------------------------------------------- Login -------------------------------------------------------
		
		// Login Panel
		Panel loginPanel = new Panel("Login")
		loginPanel.setPrimaryStyleName("sidebar-panel")
		loginPanel.setStyleName(Runo.PANEL_LIGHT)
		loginPanel.setHeight("100%")
		VerticalLayout loginPanelLayout = new VerticalLayout()
		loginPanelLayout.setSpacing(true)
		loginPanel.setContent(loginPanelLayout)
		TextField usernameField = new TextField()
		PasswordField passwordField = new PasswordField()
		usernameField.setInputPrompt("Username")
		passwordField.setInputPrompt("Password")
		loginPanelLayout.addComponent(usernameField)
		loginPanelLayout.addComponent(passwordField)

		NativeButton loginButton = new NativeButton("Login")
		loginButton.setClickShortcut(KeyCode.ENTER);

		//Loggedin Panel
		Panel loggedinPanel = new Panel ("Welcome")
		loggedinPanel.setPrimaryStyleName("sidebar-panel")
		loggedinPanel.setStyleName(Runo.PANEL_LIGHT)
		loggedinPanel.setHeight("150px")
		VerticalLayout loggedinPanelLayout = new VerticalLayout()
		loggedinPanelLayout.setSizeFull()
		loggedinPanelLayout.setSpacing(true)
		loggedinPanel.setSizeFull()
		loggedinPanel.setContent(loggedinPanelLayout)
		loggedinPanel.setWidth("100%")
		Label welcome = new Label()
		welcome.addStyleName("welcome-label")
		loggedinPanelLayout.addComponent(welcome)
		loggedinPanel.setVisible(false);

		NativeButton logoutButton = new NativeButton("Log out")

		loginButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {

				User user = User.findByUsername(usernameField.getValue())
				if(user != null && usernameField.getValue() == user.getUsername() && passwordField.getValue() == user.getPassword()) {
					UI.getCurrent().loggedIn = true;
					UI.getCurrent().loggedInUser = user

					Notification.show("Login succesful!");
					welcome.setValue("You're now logged in, "+usernameField.getValue()+".")
					left.replaceComponent(loginPanel, loggedinPanel)
					loggedinPanel.setVisible(true)
					loginPanel.setVisible(false)


					for (int t = 1; t < topTabs.getComponentCount(); t++) {
						topTabs.getTab(t).getComponent().revealHiddenComponents()
					}
				}
				else{
					Notification.show("Username and/or password incorrect.")
				}
			}
			})
		
		logoutButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().loggedIn = false
				left.replaceComponent(loggedinPanel, loginPanel)
				loggedinPanel.setVisible(false);
				loginPanel.setVisible(true);
				Notification.show("You're now logged out")

				for (int t = 1; t < topTabs.getComponentCount(); t++) {
					topTabs.getTab(t).getComponent().hideRevealedComponents()
				}
			}

			})

		Button registerButton = new Button("Register", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				Window window = new Window("Register")
				window.setModal(true)
				VerticalLayout windowLayout = new VerticalLayout()
				windowLayout.setSpacing(true)
				windowLayout.setMargin(true)
				TextField userNameTextField = new TextField("Username")
				PasswordField passwordTextField = new PasswordField("Password")
				windowLayout.addComponent(userNameTextField)
				windowLayout.addComponent(passwordTextField)
				Button okButton = new Button("Apply", new Button.ClickListener() {
					public void buttonClick(ClickEvent event2) {
						def userService = new UserService()
						User.withTransaction {
							userService.createProject(userNameTextField.getValue(), passwordTextField.getValue())
						}
						window.close()
					}
					})
				okButton.setClickShortcut(KeyCode.ENTER);
				windowLayout.addComponent(okButton)
				windowLayout.setComponentAlignment(userNameTextField, Alignment.MIDDLE_CENTER)
				windowLayout.setComponentAlignment(passwordTextField, Alignment.MIDDLE_CENTER)
				windowLayout.setComponentAlignment(okButton, Alignment.MIDDLE_CENTER)
				window.setContent(windowLayout)
				UI.getCurrent().addWindow(window)
			}
			})
registerButton.setStyleName(Reindeer.BUTTON_LINK)



loginPanelLayout.addComponent(loginButton)
loggedinPanelLayout.addComponent(logoutButton)
loginPanelLayout.setComponentAlignment(usernameField, Alignment.MIDDLE_CENTER)
loginPanelLayout.setComponentAlignment(passwordField, Alignment.TOP_CENTER)
loginPanelLayout.setComponentAlignment(loginButton, Alignment.TOP_CENTER)
loggedinPanelLayout.setComponentAlignment(logoutButton, Alignment.TOP_CENTER)
loggedinPanelLayout.setComponentAlignment(welcome, Alignment.MIDDLE_CENTER)
loginPanelLayout.addComponent(registerButton)

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

			for (int t = 1; t < topTabs.getComponentCount(); t++) {
				if (topTabs.getTab(t).getComponent().tabName().replace("#!/", "").equals(event.getParameters())) {
					return
				}
			}

			String[] urlParameters = event.getParameters().split("/")
			if (urlParameters[0] == "project") {
				if (urlParameters.size() == 2) {
					Project currentProject = Project.findByTitle(urlParameters[1])

					if (currentProject != null) {
						ProjectView projectTab = new ProjectView(currentProject)
						Tab tab = topTabs.addTab(projectTab, "Project: "+ currentProject.getTitle())
						tab.setClosable(true)
						topTabs.setSelectedTab(tab)
					}
					//else {
					//	Notification.show("No such project was found.")
					//}

					print Checkout.list().toString()

					Checkout currentCheckout = Checkout.findByUniqueID(urlParameters[1])

					if (currentCheckout != null) {
						print "Unique ID " + currentCheckout.uniqueID + " exists!"
						Project.withTransaction {
							
							//currentCheckout.project = new Project(title: currentCheckout.uniqueID)
							//if (currentCheckout.project.save(flush: true)) {
								ProjectView projectTab = new ProjectView(currentCheckout)
								Tab tab = topTabs.addTab(projectTab, "Project: "+ currentCheckout.uniqueID)
								tab.setClosable(true)
								topTabs.setSelectedTab(tab)
							//}
							//else { println "Oh-oh..."}
						}
					}
				}
				else {
					ProjectsOverview projectTab = new ProjectsOverview()
					Tab tab = topTabs.addTab(projectTab, "Projects")
					tab.setClosable(true)
					topTabs.setSelectedTab(tab)
				}
			}

		}
	}
}
