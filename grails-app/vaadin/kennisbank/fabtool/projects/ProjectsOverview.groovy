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
import com.vaadin.ui.TabSheet.Tab
import com.vaadin.shared.ui.combobox.FilteringMode
import com.vaadin.event.LayoutEvents.LayoutClickEvent
import com.vaadin.event.LayoutEvents.LayoutClickListener
import kennisbank.*
import kennisbank.projects.*
import kennisbank.equipment.*
import kennisbank.checkin.Checkout
import kennisbank.utils.*

/**
 * Display and provide the option to search for {@link kennisbank.checkin.Checkout Checkouts}.
 *
 * @author Marcelo Dias Avelino
 */
class ProjectsOverview extends VerticalLayout {

	/**
	 * Holds all the {@link kennisbank.fabtool.projects.Query} objects and manages the corresponding {@link #QueryTag QueryTags} 
	 *
	 * @author Marcelo Dias Avelino
	 */
	class Queries {
		
		/**
		 * Map of {@link kennisbank.fabtool.projects.Query} objects and corresponding {@link ProjectsOverview.QueryTag QueryTags}.
		 */
		private def queries

		/**
		 * Constructor of Queries class.
		 */
		Queries() {
			queries = [:]
		}

		/**
		 * Adds a {@link kennisbank.fabtool.projects.Query} to the map and creates 
		 * a corresponding {@link #QueryTag}.
		 *
		 * @param query The {@link kennisbank.fabtool.projects.Query} to be added.
		 */
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

		/**
		 * Removes the provided {@link kennisbank.fabtool.projects.Query} from the map. 
		 *
		 * @param query The {@link kennisbank.fabtool.projects.Query} to be removed.
		 */
		void remove(Query query) {
			queries.remove(query)
			executeQueries()
		}

		/**
		 * Executes all the {@link kennisbank.fabtool.projects.Query queries} and updates the {@link kennisbank.checkin.Checkout checkouts} displayed. 
		 */		
		List executeQueries() {
			def checkouts = Checkout.createCriteria().list(max: 20) { }
			queries.keySet().toList().each {
				checkouts = it.executeQuery(checkouts)
			}

			updateProjectsList(checkouts)
		}
	}

	/**
	 * Graphical component of a {@link kennisbank.fabtool.projects.Query} which can be removed.
	 *
	 * @author Marcelo Dias Avelino
	 */
	class QueryTag extends HorizontalLayout {

		/**
		 * Button used to remove the component.
		 */		
		Button removeButton

		/**
		 * Constructor of the QueryTag class.
		 */		
		QueryTag(Query query) {
			setStyleName("projectQueryTag")
			setSpacing(true)

			addComponent(new Label(query.queryType.caption + ": " + 
				(query.extraValue ? query.value + " - " + query.extraValue : query.value)))
			
			removeButton = new Button()
			addComponent(removeButton)
			removeButton.setIcon(new ThemeResource("Red-X.svg"))
			removeButton.setStyleName(Reindeer.BUTTON_LINK)
		}
	}

	/**
	 * Fragment used to bookmark this page.
	 */		
	String uriFragment
	
	/**
	 * Layout where the {@link #QueryTag QueryTags} are displayed.  
	 */
	GridLayout queriesLayout

	/**
	 * Layout where the {@link kennisbank.fabtool.projects.ProjectLink ProjectLinks} are displayed.  
	 */
	private GridLayout projectsLayout

	/**
	 * Object where the {@link kennisbank.fabtool.projects.Query Queries} themselves and their logic are stored.  
	 */
	private Queries queries

	/**
	 * Constructor of the ProjectsOverview class.
	 */		
	ProjectsOverview() {

		setMargin(true)
		setSizeFull()

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

		Label titleLabel = new Label("<h1><b>Projecten</b></h1>", ContentMode.HTML)
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
		searchTextLayout.setStyleName("searchTextLayout")

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

		VerticalLayout searchSettingLayout = new VerticalLayout()
		searchLayout.addComponent(searchSettingLayout)
		searchSettingLayout.setSpacing(true)
		searchSettingLayout.setStyleName("searchSettingLayout")

		ComboBox settingTypeComboBox = new ComboBox("Instelling", settingTypes.keySet().toList())
		searchSettingLayout.addComponent(settingTypeComboBox)

		HorizontalLayout searchSettingBottomLayout = new HorizontalLayout()
		searchSettingLayout.addComponent(searchSettingBottomLayout)
		searchSettingLayout.setComponentAlignment(searchSettingBottomLayout, Alignment.MIDDLE_RIGHT)
		searchSettingBottomLayout.setSpacing(true)

		Label settingValueLabel = new Label("Waarde")
		searchSettingBottomLayout.addComponent(settingValueLabel)

		TextField settingValueTextField = new TextField()
		searchSettingBottomLayout.addComponent(settingValueTextField)
		settingValueTextField.setWidth("50px")

		Button addSettingTypeQueryButton = new Button()
		searchSettingBottomLayout.addComponent(addSettingTypeQueryButton)
		addSettingTypeQueryButton.setDescription("Klik hier om een tekst query term to te voegen")
		addSettingTypeQueryButton.setIcon(new ThemeResource("plus.png"))
		addSettingTypeQueryButton.setStyleName(Reindeer.BUTTON_LINK)
		addSettingTypeQueryButton.addStyleName("addTextQueryButton")

		addSettingTypeQueryButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent equipmentButtonEvent) {
				if (settingTypeComboBox.getValue() != null) {
					queries.add(new Query(Query.QueryType.SETTING, settingTypes.get(settingTypeComboBox.getValue()).name, 
						settingValueTextField.getValue()?.trim() ? settingValueTextField.getValue() : null))
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
		projectsLayout.setColumns(4)
		projectsLayout.setMargin(true)
		projectsLayout.setSpacing(true)
		projectsLayout.setSizeUndefined()

		print projectsLayout.getWidth()

		updateProjectsList(Checkout.list())

	}

	/**
	 * Displays a new list of {@link kennisbank.fabtool.projects.ProjectLink ProjectLinks} on {@link #projectsLayout}.
	 *
	 * @param checkouts New list of {@link kennisbank.checkin.Checkout checkouts} to be displayed.
	 */
	void updateProjectsList(List checkouts) {
		projectsLayout.removeAllComponents()

		for (checkout in checkouts) {
			ProjectLink newLink = new ProjectLink(checkout)
			projectsLayout.addComponent(newLink)

			newLink.addLayoutClickListener(new LayoutClickListener() {
				@Override
				public void layoutClick(LayoutClickEvent event) {
					TabSheet tabs = UI.getCurrent().mainView.topTabs
					Tab tab = tabs.addTab(new ProjectView(newLink.checkout), "Project: " + 	checkout.title)
					tab.setClosable(true)
					tabs.setSelectedTab(tab)
				}
			})
		}
	}

	void removeQueryTag(QueryTag tag) {
		queriesLayout.removeComponent(tag)		
	}
}