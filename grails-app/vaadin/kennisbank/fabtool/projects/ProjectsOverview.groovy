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
import kennisbank.project.*
import kennisbank.checkin.Checkout
import kennisbank.utils.*


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
		addComponent(panel)

		VerticalLayout layout = new VerticalLayout()
		panel.setContent(layout)
		layout.setSpacing(true)
		layout.setMargin(true)
		layout.setSizeFull()

		Label titleLabel = new Label("<h1><b>Projects</b></h1>", ContentMode.HTML)
		layout.addComponent(titleLabel) // Column 0, row 0 to column 1, row 0
		layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
		titleLabel.setWidth("100%")

		

		// ------------------------------------------------------- Projects -------------------------------------------------------
		
		HorizontalLayout mainLayout = new HorizontalLayout()
		layout.addComponent(mainLayout)

		// ------------------------------------------------------- Search -------------------------------------------------------

		VerticalLayout searchLayout = new VerticalLayout()
		mainLayout.addComponent(searchLayout)
		searchLayout.setMargin(true)
		searchLayout.setSpacing(true)

		TextField searchTextField = new TextField("Zoek")
		searchLayout.addComponent(searchTextField)

		ExtendedComboBox equipmentComboBox = new ExtendedComboBox("Apparaat", [], false, true)
		searchLayout.addComponent(equipmentComboBox)

		ExtendedComboBox materialComboBox = new ExtendedComboBox("Materiaal", [], false, false)
		searchLayout.addComponent(materialComboBox)

		ExtendedComboBox materialTypeComboBox = new ExtendedComboBox("Material type", [], false, true)
		searchLayout.addComponent(materialTypeComboBox)

		ExtendedComboBox 

		// ------------------------------------------------------- Content -------------------------------------------------------

		VerticalLayout contentLayout = new VerticalLayout()
		mainLayout.addComponent(contentLayout)

		GridLayout existingProjectsLayout = new GridLayout()
		contentLayout.addComponent(existingProjectsLayout)
		existingProjectsLayout.setColumns(6)
		existingProjectsLayout.setMargin(true)
		existingProjectsLayout.setSpacing(true)
		existingProjectsLayout.setSizeUndefined()

		for (def checkout : Checkout.list()) {
			if (checkout.published) existingProjectsLayout.addComponent(new ProjectLink(checkout))
		}

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
