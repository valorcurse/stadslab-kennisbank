package kennisbank.fabtool.projects

import com.vaadin.data.Property
import com.vaadin.event.FieldEvents.TextChangeEvent
import com.vaadin.event.FieldEvents.TextChangeListener
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.server.ExternalResource
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Field.ValueChangeEvent
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.*
import kennisbank.*
import kennisbank.project.Project
import kennisbank.project.ProjectMember
import kennisbank.checkin.Checkout


class ProjectsOverview extends VerticalLayout {

	String uriFragment

	def hiddenComponents

	String tabName() {
		return uriFragment
	}

	ProjectsOverview() {

		setMargin(true)
		setSizeFull()

		hiddenComponents = []

		uriFragment = "#!/project"
		UI.getCurrent().getPage().getCurrent().setLocation(uriFragment)

		Panel panel = new Panel()
		panel.setPrimaryStyleName("island-panel")

		VerticalLayout layout = new VerticalLayout()
		layout.setSpacing(true)
		layout.setMargin(true)
		layout.setSizeFull()

		panel.setContent(layout)

		Label titleLabel = new Label("<h1><b>Projects</b></h1>", ContentMode.HTML)
		titleLabel.setWidth("100%")

		// ------------------------------------------------------- Existing Project -------------------------------------------------------
		
		Panel existingProjectsPanel = new Panel("Existing project")
		existingProjectsPanel.setPrimaryStyleName("embedded-panel")
		existingProjectsPanel.addStyleName(Runo.PANEL_LIGHT)

		VerticalLayout existingProjectsLayout = new VerticalLayout()
		existingProjectsPanel.setContent(existingProjectsLayout)
		existingProjectsLayout.setMargin(true)
		existingProjectsLayout.setSpacing(true)
		existingProjectsLayout.setSizeUndefined()

		for (def checkout : Checkout.list()) {
			if (checkout.published) existingProjectsLayout.addComponent(new ProjectLink(checkout))
		}

		layout.addComponent(titleLabel)
		layout.addComponent(existingProjectsPanel)

		layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
		layout.setComponentAlignment(existingProjectsPanel, Alignment.TOP_CENTER)

		addComponent(panel)

		if(UI.getCurrent().loggedIn) {
			revealHiddenComponents()
		}
		else {
			hideRevealedComponents()
		}
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
}
