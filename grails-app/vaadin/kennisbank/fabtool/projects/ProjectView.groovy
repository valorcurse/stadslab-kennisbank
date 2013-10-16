package kennisbank.fabtool.projects

import java.rmi.server.UID;
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.*
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TabSheet.Tab
import com.vaadin.ui.Upload.Receiver
import com.vaadin.ui.Upload.StartedEvent
import com.vaadin.ui.Upload.SucceededEvent
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.themes.Reindeer
import com.vaadin.server.ExternalResource
import com.vaadin.server.ThemeResource
import com.vaadin.server.FileResource
import com.vaadin.server.Sizeable.Unit
import com.vaadin.data.util.HierarchicalContainer
import com.vaadin.data.util.IndexedContainer
import com.vaadin.data.Item
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.event.ShortcutAction.KeyCode
import org.hibernate.engine.Status

import kennisbank.checkin.*
import kennisbank.fabtool.*

import kennisbank.equipment.*
import kennisbank.utils.*

/**
 * Display all the information of the corresponding {@link kennisbank.checkin.Checkout}.
 *
 * @author Marcelo Dias Avelino
 */

class ProjectView extends VerticalLayout {

	/**
	 * Fragment used to bookmark this page.
	 */
	String uriFragment

	/**
	 * The corresponding {@link kennisbank.checkin.Checkout}.
	 */
	private Checkout checkout

	/**
	 * The constructor of ProjectView. 
	 *
	 * @param checkout The {@link kennisbank.checkin.Checkout} linked to this view.
	 */
	public ProjectView(Checkout checkout) {

		this.checkout = checkout
		
		uriFragment = "#!/project/" + checkout.title.replace(" ", "-")
		UI.getCurrent().getPage().getCurrent().setLocation(uriFragment)

		setMargin(true)

		Panel viewPanel = new Panel()
		addComponent(viewPanel)
		setComponentAlignment(viewPanel, Alignment.TOP_CENTER)
		viewPanel.setSizeUndefined()

		GridLayout gridLayout = new GridLayout(2, 6)
		viewPanel.setContent(gridLayout)
		gridLayout.setSpacing(true)
		gridLayout.setMargin(true)
		gridLayout.setColumnExpandRatio(1, 1)
		gridLayout.setStyleName("projectLayout")

		// ------------------------------------------------------- Title -------------------------------------------------------		
		
		VerticalLayout titleLayout = new VerticalLayout()
		gridLayout.addComponent(titleLayout, 0, 0, 1, 0) // Column 0, Row 0 to Column 1, Row 0

		Label titleLabel = new Label("<h1>" + checkout.title + "</h1>", ContentMode.HTML)
		titleLayout.addComponent(titleLabel)
		titleLayout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
		titleLabel.setSizeUndefined()

		// ------------------------------------------------------- Picture -------------------------------------------------------

		Image picture = new Image();
		gridLayout.addComponent(picture, 0, 1) // Column 0, Row 1
		picture.setStyleName("picture");
		picture.setSource(new FileResource(new File(checkout.picturePath)))

		// ------------------------------------------------------- Uploads -------------------------------------------------------

		Table uploadsTable = new Table()
		gridLayout.addComponent(uploadsTable, 1, 1)
		uploadsTable.setWidth("260px")
		uploadsTable.setHeight("170px")

		IndexedContainer uploadsContainer = new IndexedContainer()
		uploadsContainer.addContainerProperty("Naam", Component.class, "")
		uploadsContainer.addContainerProperty("Grootte", String.class, "")
		uploadsTable.setContainerDataSource(uploadsContainer)
		uploadsTable.setColumnExpandRatio("Naam", 0.7)
		uploadsTable.setColumnExpandRatio("Grootte", 0.3)


		for (def file : checkout.files) {
				Item item = uploadsContainer.addItem(file)
				item.getItemProperty("Naam").setValue(new DownloadLink(file.path, file.name))
				item.getItemProperty("Grootte").setValue(Utils.humanReadableByteCount(new File(file.path).length()))
		}

		// ------------------------------------------------------- Description -------------------------------------------------------

		Label descriptionLabel = new Label(checkout.description)
		gridLayout.addComponent(descriptionLabel, 0, 2, 1, 2) // Column 0, Row 2 to Column 1, Row 2
		descriptionLabel.setWidth("100%")
		descriptionLabel.setStyleName("description")

		// ------------------------------------------------------- Material -------------------------------------------------------
		
		TreeTable settingsTreeTable = new TreeTable()
		gridLayout.addComponent(settingsTreeTable, 0, 3, 1, 3) // Column 0, Row 3 to Column 1, Row 3
		settingsTreeTable.setWidth("100%")
		settingsTreeTable.setPageLength(5)

		HierarchicalContainer settingsContainer = new HierarchicalContainer()
		settingsContainer.addContainerProperty("Apparatuur", Label.class, "")
		settingsContainer.addContainerProperty("Materiaal", Label.class, "")
		settingsContainer.addContainerProperty("Instellingen", String.class, "")
		settingsTreeTable.setContainerDataSource(settingsContainer)
		settingsTreeTable.setColumnExpandRatio("Apparatuur", 0.6)
		settingsTreeTable.setColumnExpandRatio("Materiaal", 0.4)

		// Group all settings by equipment
		for (equipment in checkout.settings.groupBy { it.equipment }) {
			Item equipmentItem = settingsContainer.addItem(equipment.key)
			equipmentItem.getItemProperty("Apparatuur").setValue(new Label("<b>" + equipment.key.name + "</b>", ContentMode.HTML))

			// Group again by materialType
			for (materialType in equipment.getValue().groupBy { it.materialType }) {
				// Check if equipment uses materials
				if (materialType.key) {
					Item materialTypeItem = settingsContainer.addItem(materialType.key)
					materialTypeItem.getItemProperty("Apparatuur").setValue(new Label(materialType.key.material.name))
					materialTypeItem.getItemProperty("Materiaal").setValue(new Label("<b>" + materialType.key.name + "</b>", ContentMode.HTML))
					settingsContainer.setParent(materialType.key, equipment.key)	
					settingsTreeTable.setCollapsed(equipment.key, false)

					for (setting in materialType.getValue()) {
						if (setting.settingType) {
							Item settingItem = settingsContainer.addItem(setting)
							settingItem.getItemProperty("Materiaal").setValue(new Label(setting.settingType.name))
							settingItem.getItemProperty("Instellingen").setValue(setting.value)
							settingsContainer.setParent(setting, materialType.key)	
							settingsTreeTable.setCollapsed(materialType.key, false)
						}
						else {
							settingsTreeTable.setChildrenAllowed(materialType.key, false)
						}
					}
				}
			}
		}

		// --------------------------------- Made By Label ---------------------------------

		def name = ""
		switch(checkout.checkin.getClass()) {
		 	case StudentCheckin:
		 	name = checkout.checkin.firstName + " " + checkout.checkin.lastName
		 	break

		 	case CompanyCheckin:
			name = checkout.checkin.contactPerson + " : " + checkout.checkin.companyName			 		
		 	break
		} 

		Label madeByLabel = new Label("Gemaakt door: <i>" + 
			name + " (<a href=\"mailto:" + checkout.checkin.email + "\">"+ checkout.checkin.email +"</a>)" +
			" op " + checkout.checkin.dateCreated.format('dd MMMM yyyy') + "</i>", ContentMode.HTML)
		
		gridLayout.addComponent(madeByLabel, 0, 4, 1, 4) // Column 1, Row 1
		gridLayout.setComponentAlignment(madeByLabel, Alignment.TOP_CENTER)
		madeByLabel.setWidth("-1")

		if (MyUI.security.isSignedIn()) {
			HorizontalLayout adminLayout = new HorizontalLayout()
			gridLayout.addComponent(adminLayout, 0, 5, 1, 5) // Column 0, Row 3 to Column 1, Row 3
			adminLayout.setMargin(true)
			adminLayout.setSpacing(true)
			adminLayout.setWidth("100%")

			NativeButton deleteProjectButton = new NativeButton("Verwijderen")
			adminLayout.addComponent(deleteProjectButton)
			adminLayout.setComponentAlignment(deleteProjectButton, Alignment.MIDDLE_RIGHT)
			deleteProjectButton.setStyleName("button")

			deleteProjectButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						checkout.delete(flush: true)

						if (Checkout.findById(checkout.id) == null) {
							Notification.show("Project is verwijderd. Sluit deze tab.")
						}
					} catch (Exception e) {
						print e
					}
				}
			})
		}

	}
}