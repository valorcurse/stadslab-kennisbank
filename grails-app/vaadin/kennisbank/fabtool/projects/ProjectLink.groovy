package kennisbank.fabtool.projects

import com.vaadin.server.ExternalResource
import com.vaadin.ui.Button
import com.vaadin.ui.Link
import com.vaadin.ui.TabSheet
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.TabSheet.Tab
import groovy.transform.InheritConstructors
import com.vaadin.ui.themes.Reindeer
import kennisbank.*
import kennisbank.checkin.Checkout

//@InheritConstructors
class ProjectLink extends Button implements Button.ClickListener {

	String projectTitle

	ProjectLink(String projectTitle) {
		addClickListener(this)
		setStyleName(Reindeer.BUTTON_LINK)
		setCaption(projectTitle)
		this.projectTitle = projectTitle
	}

	public void buttonClick(ClickEvent event) {
		TabSheet tabs = UI.getCurrent().mainView.topTabs
		Tab tab = tabs.addTab(
				new ProjectView(Checkout.findByUniqueID(projectTitle)), "Project: " + projectTitle)
		tab.setClosable(true)
		tabs.setSelectedTab(tab)
	}

}
