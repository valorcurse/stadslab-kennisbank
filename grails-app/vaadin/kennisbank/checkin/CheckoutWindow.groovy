package kennisbank.checkin

import com.vaadin.ui.*
import com.vaadin.data.Property.ValueChangeListener
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.Tree.ExpandEvent
import kennisbank.fabtool.projects.ProjectLink
import com.vaadin.server.FileResource
import com.vaadin.server.ThemeResource
import com.vaadin.ui.Upload.SucceededEvent
import com.vaadin.ui.Upload.FailedEvent
import com.vaadin.ui.Upload.Receiver
import com.vaadin.data.Item
import com.vaadin.data.util.HierarchicalContainer
import com.vaadin.data.util.IndexedContainer
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Upload.Receiver
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.event.ShortcutListener
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import kennisbank.equipment.*

class CheckoutInfo {

	String picturePath, title
	def equipmentInfo

	CheckoutInfo() {
		picturePath = ""
		title = ""
		equipmentInfo = [][][]
	}
}

class CheckoutWindow extends Window {

	
	Checkout checkout
	CheckoutInfo checkoutInfo

	CheckoutWindow(Checkin checkin) {

		checkout = new Checkout()
		checkoutInfo = new CheckoutInfo()

		setCaption("Check out") 
		setPrimaryStyleName("check-out")
		setModal(true)
		setStyleName(Reindeer.WINDOW_LIGHT)

		setCloseShortcut(KeyCode.ESCAPE, null);

		setContent(checkoutForm(checkin))

	}

	private Layout checkoutForm(Checkin checkin) { 

		GridLayout formLayout = new GridLayout(2, 4)
		formLayout.setSpacing(true)
		formLayout.setMargin(true)

		VerticalLayout titleLayout = new VerticalLayout()
		formLayout.addComponent(titleLayout, 0, 0, 1, 0) // Column 0, Row 0 to Column 1, Row 0
		
		TextField titleTextField = new TextField()
		titleLayout.addComponent(titleTextField)
		titleLayout.setComponentAlignment(titleTextField, Alignment.TOP_CENTER)
		titleTextField.setInputPrompt("Voeg een titel toe")
		// titleTextField.setSizeUndefined()

		Label madeByLabel = new Label("Gemaakt door: <br><i>" + 
			checkin.firstName + " " + checkin.lastName + 
			"<br>(<A HREF=\"mailto:" + checkin.email + "\">"+ checkin.email +"</A>)" +
			"<br> op " + checkin.dateCreated.format('dd MMMM yyyy') + "</i>", ContentMode.HTML)
		
		// Column 1, Row 1
		formLayout.addComponent(madeByLabel, 1, 1)
		madeByLabel.setWidth("-1")

		// ------------------------------------------------------- Picture -------------------------------------------------------

		VerticalLayout uploadLayout = new VerticalLayout()
		formLayout.addComponent(uploadLayout, 0, 1) // Column 0, Row 1
		uploadLayout.setPrimaryStyleName("embedded-panel")
		uploadLayout.setSpacing(true)

		Image pictureButton = new Image();
		uploadLayout.addComponent(pictureButton);
		// pictureButton.setStyleName(Reindeer.BUTTON_LINK);
		pictureButton.setId("picture");
		pictureButton.setSource((checkout.picturePath == "emptyImage.gif") ? 
			new ThemeResource("emptyImage.gif") :
			new FileResource(new File(checkout.picturePath)));

		UploadReceiver receiver = new UploadReceiver(checkoutInfo) // Receiver that handles the data stream
		Upload upload = new Upload(null, receiver) // Upload button
		
		upload.addSucceededListener(new Upload.SucceededListener() {
			public void uploadSucceeded(SucceededEvent event) {
				pictureButton.setSource(new FileResource(new File(checkoutInfo.picturePath)))
				Notification.show("Uploaden geslaagd!")	
			}
			})
		
		upload.addFailedListener(new Upload.FailedListener() {
			public void uploadFailed(FailedEvent event) {
				Notification.show("Uploaden niet gelukt!")	
			}
			})


		uploadLayout.setWidth("-1")
		uploadLayout.addComponent(upload)
		upload.setImmediate(true) // Starts to upload immediately after choosing file

		// ------------------------------------------------------- Material -------------------------------------------------------
		
		TreeTable materialTreeTable = new TreeTable()
		formLayout.addComponent(materialTreeTable, 0, 2, 1, 2) // Column 0, Row 2 to Column 1, Row 2
		materialTreeTable.setWidth("600px")
		materialTreeTable.setPageLength(0)

		HierarchicalContainer container = new HierarchicalContainer()
		container.addContainerProperty("Apparatuur", Component.class, "")
		container.addContainerProperty("Materiaal", Component.class, "")
		container.addContainerProperty("Instellingen", TextField.class, "")
		materialTreeTable.setContainerDataSource(container)
		materialTreeTable.setColumnExpandRatio("Apparatuur", 0.5)
		materialTreeTable.setColumnExpandRatio("Materiaal", 0.5)


		for (def equipmentUsed : checkin.equipment) {
			Item equipmentItem = container.addItem(equipmentUsed)

			AddMaterialButton addMaterialButton = new AddMaterialButton(equipmentUsed, materialTreeTable, checkoutInfo)
			equipmentItem.getItemProperty("Apparatuur").setValue(addMaterialButton)

			addMaterialButton.button.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					def equipment = addMaterialButton.equipment
					def materials = Material.list()*.name

					// ComboBox to choose kind of material used
					ComboBox materialComboBox = new ComboBox(null, materials)
					materialComboBox.setNullSelectionAllowed(false)
					materialComboBox.setImmediate(true)

					// Add the ComboBox to the table
					Item materialItem = container.addItem(materialComboBox)
					materialItem.getItemProperty("Apparatuur").setValue(materialComboBox)
					container.setParent(materialComboBox, equipment)
					materialTreeTable.setCollapsed(equipment, false)
					
					materialComboBox.addValueChangeListener(new ValueChangeListener() {
						@Override
						public void valueChange(final ValueChangeEvent comboEvent) {

							def material = Material.findByName(comboEvent.getProperty().getValue())
							def materialTypes = []

							def chosenMaterial = material.name

							for (def materialType : material.materialTypes) {
								materialTypes.add(materialType.name)
							}

							// ComboBox to choose the type of the material
							ComboBox materialTypeComboBox = new ComboBox(null, materialTypes)
							materialTypeComboBox.setNullSelectionAllowed(false)
							materialTypeComboBox.setImmediate(true)
							materialTreeTable.setCollapsed(materialComboBox, false)
							
							//  Add the ComboBox to the table
							materialItem.getItemProperty("Materiaal").setValue(materialTypeComboBox)

							materialTypeComboBox.addValueChangeListener(new ValueChangeListener() {
								@Override
								public void valueChange(final ValueChangeEvent comboTypeEvent) { 

									def materialType = comboTypeEvent.getProperty().getValue()

									if (!materialTreeTable.hasChildren(materialComboBox)) {
										for (def setting : equipmentUsed.settings.asList()) {
											Label newSettingLabel = new Label(setting.name)
											Item settingItem = container.addItem(newSettingLabel)
											settingItem.getItemProperty("Materiaal").setValue(newSettingLabel)

											TextField valueTextField = new TextField()
											valueTextField.setWidth("99%")

											settingItem.getItemProperty("Instellingen").setValue(valueTextField)
											container.setParent(newSettingLabel, materialComboBox)

											materialTreeTable.setChildrenAllowed(newSettingLabel, false)
										}
									}
									else {
										// Reset the values on the settings' TextFields 
										for (def child : materialTreeTable.getChildren(materialComboBox)) {
											Item item = container.getItem(child)
											item.getItemProperty("Instellingen").getValue().setValue("")
										}
									}
								}
							})
						}
					})
				}
			})
		}

		Button saveButton = new Button("Opslaan")
		formLayout.addComponent(saveButton, 0, 3, 1, 3)  // Column 0, Row 3 to Column 1, Row 3
		formLayout.setComponentAlignment(saveButton, Alignment.TOP_CENTER)
		saveButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Checkout.withTransaction {
					checkout.title = titleTextField.getValue()

					def treeTableContainer = materialTreeTable.getContainerDataSource()

					for (equipment in treeTableContainer.rootItemIds()) {
						print equipment
						checkout.addToEquipment(new Equipment(name: equipment.name))

						for (material in treeTableContainer.getChildren(equipment)) {
							// checkout.equipment.addToMaterials(new Material(name: material.name))
							for (setting in treeTableContainer.getChildren(material)) {
								print setting
							}
						}
					}

					if (checkout.save()) {
						print "Checkout saved"
					}

				}
			}
		})


		return formLayout
	}

}

public class UploadReceiver implements Receiver {

	OutputStream outputFile = null
	CheckoutInfo checkoutInfo

	public UploadReceiver(CheckoutInfo checkoutInfo) {
		this.checkoutInfo = checkoutInfo
	}

	@Override
	public OutputStream receiveUpload(String strFilename, String strMIMEType) {
		
		File file

		try {

			file = File.createTempFile(strFilename, ".tmp")

			checkoutInfo.picturePath = file.absolutePath

			outputFile =  new FileOutputStream(file)

			} catch (IOException e) {
				e.printStackTrace()
			}

			return outputFile
		}

		protected void finalize() {
			try {
				super.finalize()
				if(outputFile != null) {
					outputFile.close()
				}
				} catch (Throwable exception) {
					exception.printStackTrace()
				}
			}
		}
