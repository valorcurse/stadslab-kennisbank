package kennisbank.fabtool.projects

import com.vaadin.data.Property
import com.vaadin.event.FieldEvents.TextChangeEvent
import com.vaadin.event.FieldEvents.TextChangeListener
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.server.ExternalResource
import com.vaadin.server.ThemeResource
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Field.ValueChangeEvent
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.*
import com.vaadin.shared.ui.combobox.FilteringMode
import kennisbank.*
import kennisbank.projects.*
import kennisbank.equipment.*
import kennisbank.checkin.Checkout
import kennisbank.utils.*


class ProjectsOverview extends VerticalLayout {

	class Queries {
		def queries

		Queries() {
			queries = [:]
		}

		void add(Query query) {
			QueryTag newTag = new QueryTag(query)
			queries[query] = newTag
			queriesLayout.addComponent(newTag)
			executeQueries()

			newTag.removeButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					remove(query)
					removeQueryTag(newTag)
				}
			})
		}

		void remove(Query query) {
			queries.remove(query)
			executeQueries()
		}

		List executeQueries() {
			def checkouts = Checkout.createCriteria().list(max: 20) { }
			queries.keySet().toList().each {
				checkouts = it.executeQuery(checkouts)
			}

			updateProjectsList(checkouts)
		}
	}

	class QueryTag extends HorizontalLayout {

		Button removeButton
		Query query

		QueryTag(Query query) {
			setStyleName("projectQueryTag")
			setSpacing(true)

			this.query = query

			addComponent(new Label(query.queryType.caption + ": " + query.value))
			
			removeButton = new Button()
			addComponent(removeButton)
			removeButton.setIcon(new ThemeResource("Red-X.svg"))
			removeButton.setStyleName(Reindeer.BUTTON_LINK)

			
		}
	}

	String uriFragment
	GridLayout existingProjectsLayout
	HorizontalLayout queriesLayout
	Queries queries

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

		queries = new Queries()

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
						
					def value = materials.get(materialComboBox.comboBox.getValue()).name

					queries.add(new Query(Query.QueryType.MATERIAL, value))
				}
			}
		})

		ExtendedComboBox settingTypeComboBox = new ExtendedComboBox("Instelling", SettingType.list()*.name, false, true)
		searchLayout.addComponent(settingTypeComboBox)

		// ------------------------------------------------------- Content -------------------------------------------------------

		VerticalLayout contentLayout = new VerticalLayout()
		mainLayout.addComponent(contentLayout)

		queriesLayout = new HorizontalLayout()
		contentLayout.addComponent(queriesLayout)
		queriesLayout.setSpacing(true)
		queriesLayout.setMargin(true)

		existingProjectsLayout = new GridLayout()
		contentLayout.addComponent(existingProjectsLayout)
		existingProjectsLayout.setColumns(6)
		existingProjectsLayout.setMargin(true)
		existingProjectsLayout.setSpacing(true)
		existingProjectsLayout.setSizeUndefined()

		updateProjectsList(Checkout.list())

		if(UI.getCurrent().loggedIn) {
			revealHiddenComponents()
		}
		else {
			hideRevealedComponents()
		}
	}

	void updateProjectsList(List checkouts) {
		existingProjectsLayout.removeAllComponents()

		for (checkout in checkouts) {
			existingProjectsLayout.addComponent(new ProjectLink(checkout))
		}
	}

	void removeQueryTag(QueryTag tag) {
		queriesLayout.removeComponent(tag)		
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
