package kennisbank.fabtool.projects

import com.vaadin.server.ExternalResource
import com.vaadin.event.LayoutEvents.LayoutClickListener
import com.vaadin.event.LayoutEvents.LayoutClickEvent
import com.vaadin.ui.*
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.TabSheet.Tab
import groovy.transform.InheritConstructors
import com.vaadin.ui.themes.Reindeer
import com.vaadin.server.FileResource
import kennisbank.*
import kennisbank.checkin.Checkout

class ProjectLink extends VerticalLayout implements LayoutClickListener {

	Checkout checkout

	ProjectLink(Checkout checkout) {
		addLayoutClickListener(this)
		setStyleName("projectButton")
		this.checkout = checkout

		setSizeFull()

		Image picture = new Image();
		addComponent(picture);
		picture.setId("picture");
		picture.setSource(new FileResource(new File(checkout.picturePath)))

		Label title = new Label(checkout.title)
		addComponent(title)
		setComponentAlignment(title, Alignment.TOP_CENTER)
		
	}

	void layoutClick(LayoutClickEvent event) {
		TabSheet tabs = UI.getCurrent().mainView.topTabs
		Tab tab = tabs.addTab(new ProjectView(checkout, "Project: " + checkout.title))
		tab.setClosable(true)
		tabs.setSelectedTab(tab)
	}

}
