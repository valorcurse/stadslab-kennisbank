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

			addComponent(new Label(query.queryType.caption + ": " + (query.extraValue ? query.value + " - " + query.extraValue : query.value)))
			
			removeButton = new Button()
			addComponent(removeButton)
			removeButton.setIcon(new ThemeResource("Red-X.svg"))
			removeButton.setStyleName(Reindeer.BUTTON_LINK)

			
		}
	}

	String uriFragment
	GridLayout projectsLayout, queriesLayout
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

		HorizontalLayout mainLayout = new HorizontalLayout()
		layout.addComponent(mainLayout)

		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Search >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

		VerticalLayout searchLayout = new VerticalLayout()
		mainLayout.addComponent(searchLayout)
		searchLayout.setMargin(true)
		searchLayout.setSpacing(true)

		// ------------------------------------------------------- Text -------------------------------------------------------

		HorizontalLayout searchTextLayout = new HorizontalLayout()
		searchLayout.addComponent(searchTextLayout)
		TextField searchTextField = new TextField("Zoek")
		searchTextLayout.addComponent(searchTextField)

		Button addTextQueryButton = new Button()
		searchTextLayout.addComponent(addTextQueryButton)
		addTextQueryButton.setDescription("Klik hier om een tekst query term to te voegen")
		addTextQueryButton.setIcon(new ThemeResource("plus.png"))
		addTextQueryButton.setStyleName(Reindeer.BUTTON_LINK)
		addTextQueryButton.addStyleName("addTextQueryButton")

		addTextQueryButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent equipmentButtonEvent) {
				if (searchTextField.getValue() != "") {
					queries.add(new Query(Query.QueryType.TEXT, searchTextField.getValue()))
				}
			}
		})

		// ------------------------------------------------------- Equipment -------------------------------------------------------

		def equipment = [:]
		Equipment.list().each() {
			equipment[(it.name)] = it
		}

		ExtendedComboBox equipmentComboBox = new ExtendedComboBox("Apparaat", equipment.keySet().toList(), false, true)
		searchLayout.addComponent(equipmentComboBox)
		equipmentComboBox.plusButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent equipmentButtonEvent) {
				if (equipmentComboBox.comboBox.getValue() != null) {
				
					def value = equipment.get(equipmentComboBox.comboBox.getValue()).name

					queries.add(new Query(Query.QueryType.EQUIPMENT, value))
				}
			}
		})

		// ------------------------------------------------------- Materials -------------------------------------------------------

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

		// ------------------------------------------------------- Setting Types -------------------------------------------------------

		def settingTypes = [:]
		SettingType.list().each() {
			settingTypes[(it.name)] = it
		}

		HorizontalLayout searchSettingLayout = new HorizontalLayout()
		searchLayout.addComponent(searchSettingLayout)

		ComboBox settingTypeComboBox = new ComboBox("Instelling", settingTypes.keySet().toList())
		searchSettingLayout.addComponent(settingTypeComboBox)

		TextField settingValueTextField = new TextField()
		searchSettingLayout.addComponent(settingValueTextField)

		Button addSettingTypeQueryButton = new Button()
		searchSettingLayout.addComponent(addSettingTypeQueryButton)
		addSettingTypeQueryButton.setDescription("Klik hier om een tekst query term to te voegen")
		addSettingTypeQueryButton.setIcon(new ThemeResource("plus.png"))
		addSettingTypeQueryButton.setStyleName(Reindeer.BUTTON_LINK)
		addSettingTypeQueryButton.addStyleName("addTextQueryButton")

		addSettingTypeQueryButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent equipmentButtonEvent) {
				if (settingTypeComboBox.getValue() != null) {
					queries.add(new Query(Query.QueryType.SETTING, settingTypes.get(settingTypeComboBox.getValue()).name, settingValueTextField.getValue()?.trim() ? settingValueTextField.getValue() : null))
				}
			}
		})



		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Content >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

		VerticalLayout contentLayout = new VerticalLayout()
		mainLayout.addComponent(contentLayout)

		// ------------------------------------------------------- Queries -------------------------------------------------------

		queriesLayout = new GridLayout()
		contentLayout.addComponent(queriesLayout)
		queriesLayout.setSpacing(true)
		queriesLayout.setMargin(true)
		queriesLayout.setColumns(8)

		// ------------------------------------------------------- Projects -------------------------------------------------------

		projectsLayout = new GridLayout()
		contentLayout.addComponent(projectsLayout)
		projectsLayout.setColumns(6)
		projectsLayout.setMargin(true)
		projectsLayout.setSpacing(true)
		projectsLayout.setSizeUndefined()

		updateProjectsList(Checkout.list())

		if(UI.getCurrent().loggedIn) {
			revealHiddenComponents()
		}
		else {
			hideRevealedComponents()
		}
	}

	void updateProjectsList(List checkouts) {
		projectsLayout.removeAllComponents()

		for (checkout in checkouts) {
			projectsLayout.addComponent(new ProjectLink(checkout))
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
