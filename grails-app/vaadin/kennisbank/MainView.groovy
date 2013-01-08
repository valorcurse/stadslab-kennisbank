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

		VerticalLayout centerView = new VerticalLayout()
		centerView.setWidth("70%")
		centerView.setHeight("100%")
		view.addComponent(centerView)
		view.setComponentAlignment(centerView, Alignment.TOP_CENTER)

		// Tabs on the right panel
		topTabs = new TabSheet()
		topTabs.setSizeFull()
		topTabs.addStyleName(Reindeer.TABSHEET_BORDERLESS)
		topTabs.addStyleName(Reindeer.TABSHEET_HOVER_CLOSABLE)

		//Home tab
		VerticalLayout homeVerticalLayout = new VerticalLayout()
		homeVerticalLayout.setSizeFull()
		homeVerticalLayout.addComponent(new Label("Home tab"))
		topTabs.addTab(homeVerticalLayout, "Home")

		// Layout for the left panel
		VerticalLayout left = new VerticalLayout()
		left.setSpacing(true)

		// Split panel dividing the left panel from the right
		HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel()
		horizontalSplitPanel.setSplitPosition(13)
		horizontalSplitPanel.setLocked(true)
		horizontalSplitPanel.setHeight("100%")
		horizontalSplitPanel.setWidth("100%")
		horizontalSplitPanel.addComponent(left)
		horizontalSplitPanel.addComponent(topTabs)
		horizontalSplitPanel.setStyleName(Runo.SPLITPANEL_SMALL)
		centerView.addComponent(horizontalSplitPanel)

		// Logo on the top-left
		Embedded logo = new Embedded("", new ThemeResource("hr.gif"))
		logo.setWidth("95%")

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
		Panel leftMenu = new Panel("Menu")
		leftMenu.setStyleName(Runo.PANEL_LIGHT)
		leftMenu.setWidth("95%")
		VerticalLayout leftMenuLayout = new VerticalLayout()
		leftMenuLayout.setStyleName("sidebar-menu")
		leftMenuLayout.setSizeFull()
		leftMenuLayout.setSpacing(true)


		Button projectButton = new Button("Projects")
		projectButton.setWidth("100%")
		projectButton.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {

						Tab tab = topTabs.addTab(new ProjectsOverview(), "Projects")
						tab.setClosable(true)
						topTabs.setSelectedTab(tab)
					}
				})

		leftMenu.setContent(leftMenuLayout)
		leftMenuLayout.addComponent(projectButton)
		leftMenuLayout.setComponentAlignment(projectButton, Alignment.MIDDLE_CENTER)

		// Login Panel
		Panel loginPanel = new Panel("Login")
		loginPanel.setHeight("150px")
		loginPanel.setStyleName(Runo.PANEL_LIGHT)
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
				})
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
