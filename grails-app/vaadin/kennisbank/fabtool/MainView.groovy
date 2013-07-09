package kennisbank.fabtool

import com.vaadin.server.ThemeResource
import com.vaadin.ui.*
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.MenuBar.Command
import com.vaadin.ui.MenuBar.MenuItem
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent
import com.vaadin.ui.TabSheet.Tab
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.themes.Reindeer
import com.vaadin.navigator.Navigator
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.shared.ui.label.ContentMode

import com.vaadin.grails.*
import java.lang.SecurityException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

import kennisbank.auth.*
import kennisbank.fabtool.administration.Administration
import kennisbank.fabtool.home.*
import kennisbank.fabtool.projects.*
import kennisbank.fabtool.adjustment.*
import kennisbank.checkin.Checkout

import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.server.Sizeable.Unit

/**
 * The main view for the FabTool section. All other views are displayed as a tab within this view.
 *
 * @author Marcelo Dias Avelino
 */
class MainView extends Panel implements View {

	/**
	 * TabSheet where the other views of the FabTool are displayed.
	 */
	TabSheet topTabs

	/**
	 * Service used to authenticate logins.
	 */
	private SecurityService security = (SecurityService) Grails.get(SecurityService)

	/**
	 * Components which are only displayed to authorized users.
	 */
	private def authComponents

	/**
	 * Panel where the controls for logging in are displayed.
	 */
	private Panel loginPanel

	/**
	 * Panel which replaces {@link #loginPanel} after logging in and displays the option to logout.
	 */
	private Panel loggedinPanel
	
	/**
	 * Function to handle the login and hide/reveal the necessary components.
	 * 
	 * @param username Username for the user who's trying to login.
	 * @param username Password for the user who's trying to login.
	 */
	private Boolean login(String username, String password) {
		try {
			security.signIn(username, password)
			revealHiddenComponents()
			loginPanel.setVisible(false)
			loggedinPanel.setVisible(true)
			
			return true

		} catch (SecurityException e) {
			return false
		}
	}

	/**
	 * Function to logout the current user and hide/reveal the necessary components.
	 */
	private void logout() {
		try {
			security.signOut()
			
			def tabsToRemove = []

			Iterator<Component> i = topTabs.getComponentIterator();
			while (i.hasNext()) {
				Component c = (Component) i.next();
				Tab tab = topTabs.getTab(c);
				
				if (!tab.getCaption().equals("Home")) {
					tabsToRemove.add(tab)
				}
			}

			tabsToRemove.each {
				topTabs.removeTab(it)
			}
		}
		catch (SecurityException e) {
			print "something happened: " + e
		}
	}

	/**
	 * Construct of the MainView class.
	 */
	public MainView() {
		authComponents = []

		setSizeFull()
		setContent(GenerateView())
	}

	/**
	 * Function which generates the graphical interface and corresponding logic
	 * 
	 * @return Panel with generated view
	 */
	private Panel GenerateView() {

		Panel mainPanel = new Panel()
		mainPanel.setSizeFull()

		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Main Layout >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

		VerticalLayout view = new VerticalLayout()
		mainPanel.setContent(view)
		view.setSizeFull() // Set layout to cover the whole screen

		HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel()
		view.addComponent(horizontalSplitPanel)
		horizontalSplitPanel.setSplitPosition(160, Unit.PIXELS)
		horizontalSplitPanel.setLocked(true)
		horizontalSplitPanel.addStyleName("invisible")
		horizontalSplitPanel.setHeight("100%")
		horizontalSplitPanel.setWidth("100%")
		horizontalSplitPanel.setStyleName(Runo.SPLITPANEL_SMALL)


		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Left Bar >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

		VerticalLayout left = new VerticalLayout()
		horizontalSplitPanel.addComponent(left)
		left.setSpacing(true)
		left.setWidth("100%")


		// ------------------------------------------------------- Logo -------------------------------------------------------

		Panel logoPanel = new Panel()
		left.addComponent(logoPanel)
		left.setComponentAlignment(logoPanel, Alignment.TOP_CENTER)
		logoPanel.setPrimaryStyleName("sidebar-panel")
		logoPanel.setStyleName(Runo.PANEL_LIGHT)

		HorizontalLayout logoLayout = new HorizontalLayout()
		logoPanel.setContent(logoLayout)
		logoLayout.setSpacing(true)
		logoLayout.setMargin(true)

		Embedded logo = new Embedded(null, new ThemeResource("hr.gif"))
		logoLayout.addComponent(logo)
		logoLayout.setComponentAlignment(logo, Alignment.MIDDLE_CENTER)
		logo.setHeight("32px")

		Label logoLabel = new Label ("<b>Kennisbank</b>", Label.CONTENT_XHTML)
		logoLayout.addComponent(logoLabel)
		logoLayout.setComponentAlignment(logoLabel, Alignment.MIDDLE_CENTER)
		logoLabel.addStyleName("logo-label")

		// ------------------------------------------------------- Login -------------------------------------------------------
		
		loginPanel = new Panel("Login")
		left.addComponent(loginPanel)
		left.setComponentAlignment(loginPanel, Alignment.TOP_CENTER)
		loginPanel.setPrimaryStyleName("sidebar-panel")
		loginPanel.setStyleName(Runo.PANEL_LIGHT)
		loginPanel.setHeight("100%")
		
		VerticalLayout loginPanelLayout = new VerticalLayout()
		loginPanelLayout.setSpacing(true)
		loginPanel.setContent(loginPanelLayout)

		TextField usernameField = new TextField()
		loginPanelLayout.addComponent(usernameField)
		loginPanelLayout.setComponentAlignment(usernameField, Alignment.MIDDLE_CENTER)
		usernameField.setWidth("90%")
		usernameField.setInputPrompt("Gebruikersnaam")

		PasswordField passwordField = new PasswordField()
		loginPanelLayout.addComponent(passwordField)
		loginPanelLayout.setComponentAlignment(passwordField, Alignment.TOP_CENTER)
		passwordField.setWidth("90%")
		passwordField.setInputPrompt("Wachtwoord")

		NativeButton loginButton = new NativeButton("Login")
		loginPanelLayout.addComponent(loginButton)
		loginPanelLayout.setComponentAlignment(loginButton, Alignment.TOP_CENTER)
		loginButton.setClickShortcut(KeyCode.ENTER);
		loginButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {

				if (!login(usernameField.getValue(), passwordField.getValue())) {
					Notification.show("Gebruikersnaam en/of wachtwoord is onjuist.")
				}

				usernameField.setValue("")
				passwordField.setValue("")
			}
		})

		// ------------------------------------------------------- Logged In -------------------------------------------------------

		loggedinPanel = new Panel("Welkom")
		left.addComponent(loggedinPanel)
		left.setComponentAlignment(loggedinPanel, Alignment.TOP_CENTER)
		loggedinPanel.setHeight("150px")
		loggedinPanel.setPrimaryStyleName("sidebar-panel")
		loggedinPanel.setStyleName(Runo.PANEL_LIGHT)
		loggedinPanel.setVisible(false);

		VerticalLayout loggedinPanelLayout = new VerticalLayout()
		loggedinPanelLayout.setSizeFull()
		loggedinPanelLayout.setSpacing(true)
		loggedinPanel.setSizeFull()
		loggedinPanel.setContent(loggedinPanelLayout)
		loggedinPanel.setWidth("100%")
		
		Label welcome = new Label("Welkom <b>" + security.getCurrentUsername() + "</b>, je bent ingelogd.", ContentMode.HTML)
		loggedinPanelLayout.addComponent(welcome)
		loggedinPanelLayout.setComponentAlignment(welcome, Alignment.MIDDLE_CENTER)
		welcome.addStyleName("welcome-label")

		NativeButton logoutButton = new NativeButton("Uitloggen")
		loggedinPanelLayout.addComponent(logoutButton)
		loggedinPanelLayout.setComponentAlignment(logoutButton, Alignment.TOP_CENTER)
		logoutButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				loggedinPanel.setVisible(false);
				loginPanel.setVisible(true);
				hideRevealedComponents()
				logout()
			}

		})


		// ------------------------------------------------------- Search -------------------------------------------------------

		// Panel searchPanel = new Panel("Search")
		// left.addComponent(searchPanel)
		// left.setComponentAlignment(searchPanel, Alignment.TOP_CENTER)
		// searchPanel.setHeight("70px")
		// searchPanel.setWidth("100%")
		// searchPanel.setStyleName(Runo.PANEL_LIGHT)
		// searchPanel.setPrimaryStyleName("sidebar-panel")

		// VerticalLayout searchLayout = new VerticalLayout()
		// searchPanel.setContent(searchLayout)
		// searchLayout.setSizeFull()
		
		// TextField searchField = new TextField()
		// searchLayout.addComponent(searchField)
		// searchLayout.setComponentAlignment(searchField, Alignment.MIDDLE_CENTER)
		// searchField.setWidth("90%")
		// searchField.addStyleName("search")
		// searchField.setInputPrompt("Search...")
		

		// ------------------------------------------------------- Navigation Menu -------------------------------------------------------

		Panel leftMenuPanel = new Panel("Menu")
		left.addComponent(leftMenuPanel)
		left.setComponentAlignment(leftMenuPanel, Alignment.TOP_CENTER)
		leftMenuPanel.setStyleName(Runo.PANEL_LIGHT)
		leftMenuPanel.setPrimaryStyleName("sidebar-panel")
		leftMenuPanel.setWidth("100%")
		
		VerticalLayout leftMenuLayout = new VerticalLayout()
		leftMenuPanel.setContent(leftMenuLayout)
		leftMenuLayout.setSizeFull()
		leftMenuLayout.setSpacing(true)


		NativeButton projectButton = new NativeButton("Projecten")
		leftMenuLayout.addComponent(projectButton)
		leftMenuLayout.setComponentAlignment(projectButton, Alignment.MIDDLE_CENTER)
		projectButton.setWidth("90%")
		projectButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				Tab tab = topTabs.addTab(new ProjectsOverview(), "Projecten")
				tab.setClosable(true)
				topTabs.setSelectedTab(tab)
			}
		})
		
		NativeButton adminButton = new NativeButton("Administratie")
		leftMenuLayout.addComponent(adminButton)
		leftMenuLayout.setComponentAlignment(adminButton, Alignment.BOTTOM_CENTER)
		authComponents.add(adminButton)
		adminButton.setWidth("90%")
		adminButton.setVisible(false)
		adminButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				Tab tab = topTabs.addTab(new Administration(), "Administratie")
				tab.setClosable(true)
				topTabs.setSelectedTab(tab)
			}
		})

		NativeButton adjustButton = new NativeButton("Aanpassingen")
		leftMenuLayout.addComponent(adjustButton)
		leftMenuLayout.setComponentAlignment(adjustButton, Alignment.BOTTOM_CENTER)
		authComponents.add(adjustButton)
		adjustButton.setWidth("90%")
		adjustButton.setVisible(false)
		adjustButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				Tab tab = topTabs.addTab(new AdjustmentView(), "Aanpassingen")
				tab.setClosable(true)
				topTabs.setSelectedTab(tab)
			}
		})


		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Right Area >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

		topTabs = new TabSheet()
		horizontalSplitPanel.addComponent(topTabs)
		topTabs.setSizeFull()
		topTabs.addStyleName(Reindeer.TABSHEET_BORDERLESS)
		topTabs.addStyleName(Reindeer.TABSHEET_HOVER_CLOSABLE)
		topTabs.setId("top-tabs")

		topTabs.addTab(new HomeView(), "Hoofdpagina")
		
		topTabs.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			public void selectedTabChange(SelectedTabChangeEvent event) {
				Tab tab = topTabs.getTab(event.getTabSheet().getSelectedTab())
				
				if (tab.getComponent().uriFragment) {
					UI.getCurrent().getPage().getCurrent().setLocation(tab.getComponent().uriFragment)
				}
			}
		});

		return mainPanel
	}

	public void enter(ViewChangeEvent event) {
		if(event.getParameters() != null) {

			// Check if a tab is already open
			for (int t = 1; t < topTabs.getComponentCount(); t++) {
				if (topTabs.getTab(t).getComponent().uriFragment.replace("#!/", "").equals(event.getParameters())) {
					return
				}
			}

			String[] urlParameters = event.getParameters().split("/")
			
			switch(urlParameters[0]) {
				case "project":
					if (urlParameters[0] == "project") {
						if (urlParameters.size() == 2) {
							Checkout currentCheckout = Checkout.findByTitle(urlParameters[1].replace("-", " "))

							if (currentCheckout != null) {
								Checkout.withTransaction {
									ProjectView projectTab = new ProjectView(currentCheckout)
									Tab tab = topTabs.addTab(projectTab, "Project: " + currentCheckout.title.replace(" ", "-"))
									tab.setClosable(true)
									topTabs.setSelectedTab(tab)
								}
							}
						}
						else {
							ProjectsOverview projectTab = new ProjectsOverview()
							Tab tab = topTabs.addTab(projectTab, "Projecten")
							tab.setClosable(true)
							topTabs.setSelectedTab(tab)
						}
					}		
				break
			
			}
			
		}
	}

	/**
	 * Reveals components the current user has access to.
	 */
	private void revealHiddenComponents() {
		for (c in authComponents) {
			c.setVisible(true)
		}
	}

	/**
	 * Hides components the current user doesn't have access to.
	 */
	private void hideRevealedComponents() {
		for (c in authComponents) {
			c.setVisible(false)
		}
	}
}
