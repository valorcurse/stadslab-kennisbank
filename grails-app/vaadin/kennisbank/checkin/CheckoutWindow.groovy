 package kennisbank.checkin

import com.vaadin.ui.*
import com.vaadin.data.Property.ValueChangeListener
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.Tree.ExpandEvent
import kennisbank.fabtool.projects.ProjectLink
import com.vaadin.server.FileResource
import java.nio.channels.FileChannel
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
import com.vaadin.event.FieldEvents.TextChangeListener
import com.vaadin.event.FieldEvents.TextChangeEvent
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.TabSheet.Tab
import kennisbank.equipment.*
import kennisbank.*
import kennisbank.utils.*



class CheckoutWindow extends Window {

	def checkoutForms

	CheckoutWindow(Checkin checkin) {

		checkoutForms = []

		setCaption("Check out") 
		setPrimaryStyleName("check-out")
		setModal(true)
		setResizable(false)
		setStyleName(Reindeer.WINDOW_LIGHT)

		setCloseShortcut(KeyCode.ESCAPE, null);

		setContent(equipmentTab(checkin))
	}

	private Layout equipmentTab(Checkin checkin) {
		VerticalLayout layout = new VerticalLayout()
		layout.setWidth("700px")
		layout.setMargin(true)
		layout.setSpacing(true)

		layout.addComponent(new Label("Als je aan meer dan één project hebt gewerkt, voeg een nieuw project toe met deze knop"))

		Button newProject = new Button("Nieuw project")
		layout.addComponent(newProject)

		TabSheet tabSheet = new TabSheet()
		layout.addComponent(tabSheet)
		tabSheet.addStyleName(Reindeer.TABSHEET_MINIMAL)
		tabSheet.setSizeFull()

		
		CheckoutForm defaultCheckoutForm = new CheckoutForm(checkin)
		checkoutForms.add(defaultCheckoutForm)
		Tab defaultTab = tabSheet.addTab(defaultCheckoutForm, "Project")
		defaultCheckoutForm.tab = defaultTab

		newProject.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				CheckoutForm checkoutForm = new CheckoutForm(checkin)
				checkoutForms.add(checkoutForm)
				Tab tab = tabSheet.addTab(checkoutForm, "Project")
				tab.setClosable(true)
				checkoutForm.tab = tab
			}
		})

		Button saveButton = new Button("Opslaan")
		layout.addComponent(saveButton)
		layout.setComponentAlignment(saveButton, Alignment.TOP_CENTER)

		return layout
	}

}

class UploadHelper {
	String uploadPath, name, size
}

class CheckoutForm extends Panel {
	
	Tab tab
	UploadHelper uploadHelper
	def settings

	CheckoutForm(Checkin checkin) { 

		setStyleName(Reindeer.PANEL_LIGHT)
		setHeight("600px")

		settings = []

		String uploadPath = ""

		uploadHelper = new UploadHelper()

		Checkout checkout = new Checkout(checkin: checkin)
		UploadReceiver receiver = new UploadReceiver(uploadHelper) // Receiver that handles the data stream

		GridLayout gridLayout = new GridLayout(2, 5)
		setContent(gridLayout)
		gridLayout.setSpacing(true)
		gridLayout.setMargin(true)
		gridLayout.setWidth("100%")
		gridLayout.setColumnExpandRatio(1, 1)

		VerticalLayout titleLayout = new VerticalLayout()
		gridLayout.addComponent(titleLayout, 0, 0, 1, 0) // Column 0, Row 0 to Column 1, Row 0
		
		TextField titleTextField = new TextField()
		titleLayout.addComponent(titleTextField)
		titleLayout.setComponentAlignment(titleTextField, Alignment.TOP_CENTER)
		titleTextField.setInputPrompt("Kies een titel")
		titleTextField.setImmediate(true)
		titleTextField.addTextChangeListener(new TextChangeListener() {
			@Override
			public void textChange(final TextChangeEvent textChangeEvent) {
					tab.setCaption(textChangeEvent.getText())
					checkout.title = textChangeEvent.getText()
				}
			})

		// ------------------------------------------------------- Picture -------------------------------------------------------

		VerticalLayout pictureLayout = new VerticalLayout()
		gridLayout.addComponent(pictureLayout, 0, 1) // Column 0, Row 1
		pictureLayout.setPrimaryStyleName("embedded-panel")
		pictureLayout.setSpacing(true)

		Image pictureButton = new Image();
		pictureLayout.addComponent(pictureButton);
		// pictureButton.setStyleName(Reindeer.BUTTON_LINK);
		pictureButton.setId("picture");
		pictureButton.setSource((checkout.picturePath == "emptyImage.gif") ? 
			new ThemeResource("emptyImage.gif") :
			new FileResource(new File(checkout.picturePath)));

		Upload pictureUpload = new Upload(null, receiver) // Upload button
		
		pictureUpload.addSucceededListener(new Upload.SucceededListener() {
			public void uploadSucceeded(SucceededEvent event) {
				checkout.picturePath = uploadHelper.uploadPath
				print "Picture path: " + uploadHelper.uploadPath
				pictureButton.setSource(new FileResource(new File(checkout.picturePath)))
				Notification.show("Uploaden geslaagd!", Notification.TYPE_TRAY_NOTIFICATION)	
			}
		})
		
		pictureUpload.addFailedListener(new Upload.FailedListener() {
			public void uploadFailed(FailedEvent event) {
				Notification.show("Uploaden niet gelukt!", Notification.TYPE_TRAY_NOTIFICATION)	
			}
		})


		pictureLayout.setWidth("-1")
		pictureLayout.addComponent(pictureUpload)
		pictureUpload.setImmediate(true) // Starts to upload immediately after choosing file

		// ------------------------------------------------------- Uploads -------------------------------------------------------

		VerticalLayout uploadsLayout = new VerticalLayout()
		gridLayout.addComponent(uploadsLayout, 1, 1)
		uploadsLayout.setSpacing(true)

		Table uploadsTable = new Table()
		uploadsLayout.addComponent(uploadsTable)
		
		uploadsTable.setWidth("100%")
		uploadsTable.setHeight("170px")

		Upload filesUpload = new Upload(null, receiver) // Upload button
		uploadsLayout.addComponent(filesUpload)
		filesUpload.setImmediate(true)

		IndexedContainer uploadsContainer = new IndexedContainer()
		uploadsContainer.addContainerProperty("Naam", Component.class, "")
		uploadsContainer.addContainerProperty("Grootte", String.class, "")
		uploadsTable.setContainerDataSource(uploadsContainer)

		filesUpload.addSucceededListener(new Upload.SucceededListener() {
			public void uploadSucceeded(SucceededEvent event) {
				checkout.addToFiles(new AttachedFile(name: uploadHelper.name, path: uploadHelper.uploadPath))

				Item uploadItem = uploadsContainer.addItem(uploadHelper.uploadPath)
				uploadItem.getItemProperty("Naam").setValue(new DownloadLink(uploadHelper.uploadPath, uploadHelper.name))
				uploadItem.getItemProperty("Grootte").setValue(uploadHelper.size)
				Notification.show("Uploaden geslaagd!", Notification.TYPE_TRAY_NOTIFICATION)
			}
		})
		
		filesUpload.addFailedListener(new Upload.FailedListener() {
			public void uploadFailed(FailedEvent event) {
				Notification.show("Uploaden niet gelukt!", Notification.TYPE_TRAY_NOTIFICATION)
			}
		})

		// ------------------------------------------------------- Description -------------------------------------------------------

		TextArea descriptionTextArea = new TextArea("Korte omschrijving")
		gridLayout.addComponent(descriptionTextArea, 0, 2, 1, 2) // Column 0, Row 2 to Column 1, Row 2
		descriptionTextArea.setWidth("100%")

		// ------------------------------------------------------- Material -------------------------------------------------------
		
		TreeTable materialTreeTable = new TreeTable()
		gridLayout.addComponent(materialTreeTable, 0, 3, 1, 3) // Column 0, Row 3 to Column 1, Row 3
		materialTreeTable.setWidth("100%")
		materialTreeTable.setPageLength(0)

		HierarchicalContainer materialContainer = new HierarchicalContainer()
		materialContainer.addContainerProperty("Apparatuur", Component.class, "")
		materialContainer.addContainerProperty("Materiaal", Component.class, "")
		materialContainer.addContainerProperty("Instellingen", TextField.class, "")
		materialTreeTable.setContainerDataSource(materialContainer)
		materialTreeTable.setColumnExpandRatio("Apparatuur", 0.5)
		materialTreeTable.setColumnExpandRatio("Materiaal", 0.5)

		AddMaterialButton rootAddMaterialButton = new AddMaterialButton("Voeg een apparaat toe")
		Item rootItem = materialContainer.addItem(rootAddMaterialButton)
		rootItem.getItemProperty("Apparatuur").setValue(rootAddMaterialButton)

		rootAddMaterialButton.button.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				def settingsList = settings
				def materialComboBoxesToRemove = []
				def materialSettingsToRemove = []

 				// ComboBox to choose kind of material used
				ExtendedComboBox equipmentComboBox = new ExtendedComboBox(Equipment.list()*.name)
				equipmentComboBox.comboBox.setNullSelectionAllowed(false)
				equipmentComboBox.comboBox.setImmediate(true)
				equipmentComboBox.comboBox.setInputPrompt("Kies een apparaat")

				// Add the ComboBox to the table
				Item equipmentItem = materialContainer.addItem(equipmentComboBox)
				equipmentItem.getItemProperty("Apparatuur").setValue(equipmentComboBox)
				materialContainer.setParent(equipmentComboBox, rootAddMaterialButton)
				materialTreeTable.setCollapsed(rootAddMaterialButton, false)
						
				// ---------------------------- Choose equipment ----------------------------
				equipmentComboBox.comboBox.addValueChangeListener(new ValueChangeListener() {
					@Override
					public void valueChange(final ValueChangeEvent equipmentComboEvent) {

						// Remove previously added children if equipment selection changed
						for (comboBox in materialComboBoxesToRemove) {
							materialContainer.removeItem(comboBox)
						}
						for (setting in materialSettingsToRemove) {
							materialContainer.removeItem(setting)
						}

						def equipmentUsedSettings = []

						def equipment = Equipment.findByName(equipmentComboEvent.getProperty().getValue())

						equipment.settingTypes.each {
							def newSetting = new Setting(equipment: equipment, settingType: it)
							equipmentUsedSettings.add(newSetting)
						}

						settingsList.add(equipmentUsedSettings)

						// ComboBox to choose kind of material used
						ComboBox materialComboBox = new ComboBox(null, equipment.materialTypes*.material.name)
						materialComboBoxesToRemove.add(materialComboBox)
						materialComboBox.setNullSelectionAllowed(false)
						materialComboBox.setImmediate(true)
						materialComboBox.setInputPrompt("Kies een materiaal")

						// Add the ComboBox to the table
						Item materialItem = materialContainer.addItem(materialComboBox)
						materialItem.getItemProperty("Apparatuur").setValue(materialComboBox)
						materialContainer.setParent(materialComboBox, equipmentComboBox)
						materialTreeTable.setCollapsed(equipmentComboBox, false)
						
						// ---------------------------- Choose material ----------------------------
						materialComboBox.addValueChangeListener(new ValueChangeListener() {
							@Override
							public void valueChange(final ValueChangeEvent comboEvent) {
								for (setting in materialSettingsToRemove) {
									materialContainer.removeItem(setting)
								}
								def material = Material.findByName(comboEvent.getProperty().getValue())

								comboBoxContent(material, materialTreeTable, 
												materialComboBox, materialItem, 
												equipmentUsedSettings, equipment)
							}
						})
					}
				})



			}
		})

		// --------------------------------- Made By Label ---------------------------------

		Label madeByLabel = new Label("Gemaakt door: <i>" + 
			checkin.firstName + " " + checkin.lastName + 
			" (<a href=\"mailto:" + checkin.email + "\">"+ checkin.email +"</a>)" +
			" op " + checkin.dateCreated.format('dd MMMM yyyy') + "</i>", ContentMode.HTML)
		
		gridLayout.addComponent(madeByLabel, 0, 4, 1, 4) // Column 1, Row 1
		gridLayout.setComponentAlignment(madeByLabel, Alignment.TOP_CENTER)
		madeByLabel.setWidth("-1")

	}
	
	private void comboBoxContent(Material material, TreeTable materialTreeTable, 
								ComboBox materialComboBox, Item materialItem, 
								List equipmentUsedSettings, Equipment equipment) {

		HierarchicalContainer materialContainer = materialTreeTable.getContainerDataSource()

		// ComboBox to choose the type of the material
		ComboBox materialTypeComboBox = new ComboBox(null, material.materialTypes*.name)
		materialTypeComboBox.setNullSelectionAllowed(false)
		materialTypeComboBox.setImmediate(true)
		materialTypeComboBox.setInputPrompt("Kies een materiaal type")

		materialTreeTable.setCollapsed(materialComboBox, false)
								
		//  Add the ComboBox to the table
		materialItem.getItemProperty("Materiaal").setValue(materialTypeComboBox)

		// ---------------------------- Choose material type ----------------------------
		materialTypeComboBox.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent comboTypeEvent) { 

				def materialType = comboTypeEvent.getProperty().getValue()

				equipmentUsedSettings.each { it.materialType = MaterialType.findByName(materialType) }

				if (!materialTreeTable.hasChildren(materialComboBox)) {
					for (def settingUsed : equipment.settingTypes.asList()) {
						Label newSettingLabel = new Label(settingUsed.name)
						Item settingItem = materialContainer.addItem(newSettingLabel)
						materialSettingsToRemove.add(newSettingLabel)
						settingItem.getItemProperty("Materiaal").setValue(newSettingLabel)

						TextField valueTextField = new TextField()
						valueTextField.setWidth("99%")
						valueTextField.setCaption(settingUsed.name)

						settingItem.getItemProperty("Instellingen").setValue(valueTextField)
	 
						materialContainer.setParent(newSettingLabel, materialComboBox)
						materialTreeTable.setChildrenAllowed(newSettingLabel, false)

						valueTextField.addTextChangeListener(new TextChangeListener() {
							@Override
							public void textChange(final TextChangeEvent textChangeEvent) {
								def currentSetting = equipmentUsedSettings.find {
									it.settingType.name == textChangeEvent.getComponent().getCaption()
								}

								currentSetting.value = textChangeEvent.getText()
							}
						})
					}
				} else {
					// Reset the values on the settings' TextFields 
					for (child in materialTreeTable.getChildren(materialComboBox)) {
						print child.getType()
						Item item = materialContainer.getItem(child)
						item.getItemProperty("Instellingen").getValue().setValue("")
					}
				}
			}
		})
	}

	private boolean save() {
		Checkout.withTransaction {

			// Add all the settings to the checkout
			settings.each {
				it.each {
					checkout.addToSettings(it) 
				}
			}

			if (checkout.validate()) {
				checkout.published = true
				checkout = checkout.merge()
				checkout.save()
				close()
				print "Checkout saved"
			}
			else {
				checkout.errors.each {
					println it
				}
			}
		}
	}
}

public class UploadReceiver implements Receiver {

	OutputStream outputFile = null
	UploadHelper uploadHelper

	public UploadReceiver(UploadHelper uploadHelper) {
		this.uploadHelper = uploadHelper
	}

	@Override
	public OutputStream receiveUpload(String strFilename, String strMIMEType) {
		
		File file

		try {

			file = File.createTempFile(strFilename, ".tmp")

			uploadHelper.uploadPath = file.absolutePath
			uploadHelper.name = strFilename

			outputFile =  new FileOutputStream(file)
			
			// finalize()

		} catch (IOException e) {
			e.printStackTrace()
		}

		return outputFile
	}

	protected void finalize() {
		print "test"
		try {
			print "Got so far"
			super.finalize()

			if(outputFile != null) {
				// FileChannel fc = outputFile.getChannel()
				// print "Size: " + fc.size()

				// uploadHelper.size = fc.size()
				outputFile.close()
			}
		} catch (Throwable exception) {
			exception.printStackTrace()
		}
	}
}
