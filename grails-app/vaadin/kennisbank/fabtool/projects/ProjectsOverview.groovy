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
import com.vaadin.shared.ui.combobox.FilteringMode
import kennisbank.*
import kennisbank.project.*
import kennisbank.equipment.*
import kennisbank.checkin.Checkout
import kennisbank.utils.*


class ProjectsOverview extends VerticalLayout {

	String uriFragment
	GridLayout existingProjectsLayout

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

		ExtendedComboBox equipmentComboBox = new ExtendedComboBox("Apparaat", Equipment.list()*.name, false, true)
		searchLayout.addComponent(equipmentComboBox)
		// equipmentComboBox.plusButton.addClickListener(new Button.ClickListener() {
		// 			@Override
		// 			public void buttonClick(ClickEvent equipmentButtonEvent) {
		// 				existingProjectsLayout.removeComponents()

		// 				def checkouts = Checkout.findAll {
		// 					settings.settings.equipment.name == equipmentComboBox.comboBox.getValue()
		// 				}


		// 				for (def checkout : checkouts) {
		// 					existingProjectsLayout.addComponent(new ProjectLink(checkout))
		// 				}
		// 			}
		// 		})

		def materials = [:]

		Material.list().each() {
			materials[(it.name)] = it
			it.materialTypes.each { 	
				materials[(" - " + it.name)] = it
			}
		}

		ExtendedComboBox materialComboBox = new ExtendedComboBox("Materiaal", materials.keySet().toList() , false, true)
		searchLayout.addComponent(materialComboBox)
		materialComboBox.comboBox.setFilteringMode(FilteringMode.CONTAINS)
		materialComboBox.plusButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent equipmentButtonEvent) {
				if (materialComboBox.comboBox.getValue() != null) {
					existingProjectsLayout.removeAllComponents()

					Checkout.withTransaction {
						
						def value = materials.get(materialComboBox.comboBox.getValue()).name

						def checkouts = Checkout.createCriteria().listDistinct {
							settings {
								or {
									materialType {
										eq("name", value)
									}
									materialType {
										material {
											eq("name", value)
										}
									}
								}
							}
						}


						for (def checkout : checkouts) {
							existingProjectsLayout.addComponent(new ProjectLink(checkout))
						}
					}
				}
			}
		})

		ExtendedComboBox settingTypeComboBox = new ExtendedComboBox("Instelling", SettingType.list()*.name, false, true)
		searchLayout.addComponent(settingTypeComboBox)

		// ------------------------------------------------------- Content -------------------------------------------------------

		VerticalLayout contentLayout = new VerticalLayout()
		mainLayout.addComponent(contentLayout)

		existingProjectsLayout = new GridLayout()
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
