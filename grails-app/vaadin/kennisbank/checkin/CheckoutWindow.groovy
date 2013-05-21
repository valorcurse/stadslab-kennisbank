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
import com.vaadin.event.FieldEvents.TextChangeListener
import com.vaadin.event.FieldEvents.TextChangeEvent
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.TabSheet.Tab
import kennisbank.equipment.*
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

		layout.addComponent(new Label("Voeg een project toe:"))

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
				checkoutForm.tab = tab
			}
		})

		return layout
	}

}

class CheckoutForm extends Panel {
	
	Tab tab
	def settings

	CheckoutForm(Checkin checkin) { 

		setStyleName(Reindeer.PANEL_LIGHT)
		setHeight("600px")

		settings = []

		Checkout checkout = new Checkout(checkin: checkin)
		UploadReceiver receiver = new UploadReceiver(checkout) // Receiver that handles the data stream

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
				pictureButton.setSource(new FileResource(new File(checkout.picturePath)))
				Notification.show("Uploaden geslaagd!")	
			}
			})
		
		pictureUpload.addFailedListener(new Upload.FailedListener() {
			public void uploadFailed(FailedEvent event) {
				Notification.show("Uploaden niet gelukt!")	
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

		// ------------------------------------------------------- Description -------------------------------------------------------

		TextArea descriptionTextArea = new TextArea("Korte omschrijving")
		gridLayout.addComponent(descriptionTextArea, 0, 2, 1, 2) // Column 0, Row 2 to Column 1, Row 2
		descriptionTextArea.setWidth("100%")

		// ------------------------------------------------------- Material -------------------------------------------------------
		
		TreeTable materialTreeTable = new TreeTable()
		gridLayout.addComponent(materialTreeTable, 0, 3, 1, 3) // Column 0, Row 3 to Column 1, Row 3
		materialTreeTable.setWidth("100%")
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

			AddMaterialButton addMaterialButton = new AddMaterialButton(equipmentUsed.name)
			equipmentItem.getItemProperty("Apparatuur").setValue(addMaterialButton)

			addMaterialButton.button.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					def equipmentUsedSettings = []

					equipmentUsed.settingTypes.each {
						def newSetting = new Setting(equipment: equipmentUsed, settingType: it)
						equipmentUsedSettings.add(newSetting)
					}

					settings.add(equipmentUsedSettings)

					def settingsList = settings

					// ComboBox to choose kind of material used
					ComboBox materialComboBox = new ComboBox(null, Material.list()*.name)
					materialComboBox.setNullSelectionAllowed(false)
					materialComboBox.setImmediate(true)
					materialComboBox.setInputPrompt("Kies een materiaal")

					// Add the ComboBox to the table
					Item materialItem = container.addItem(materialComboBox)
					materialItem.getItemProperty("Apparatuur").setValue(materialComboBox)
					container.setParent(materialComboBox, equipmentUsed)
					materialTreeTable.setCollapsed(equipmentUsed, false)
					
					// ---------------------------- Choose material ----------------------------
					materialComboBox.addValueChangeListener(new ValueChangeListener() {
						@Override
						public void valueChange(final ValueChangeEvent comboEvent) {

							def material = Material.findByName(comboEvent.getProperty().getValue())

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
										for (def settingUsed : equipmentUsed.settingTypes.asList()) {
											Label newSettingLabel = new Label(settingUsed.name)
											Item settingItem = container.addItem(newSettingLabel)
											settingItem.getItemProperty("Materiaal").setValue(newSettingLabel)

											TextField valueTextField = new TextField()
											valueTextField.setWidth("99%")
											valueTextField.setCaption(settingUsed.name)

											settingItem.getItemProperty("Instellingen").setValue(valueTextField)
 
											container.setParent(newSettingLabel, materialComboBox)
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

		// ------------------------------------------------------- Made By Label -------------------------------------------------------

		Label madeByLabel = new Label("Gemaakt door: <i>" + 
			checkin.firstName + " " + checkin.lastName + 
			" (<a href=\"mailto:" + checkin.email + "\">"+ checkin.email +"</a>)" +
			" op " + checkin.dateCreated.format('dd MMMM yyyy') + "</i>", ContentMode.HTML)
		
		gridLayout.addComponent(madeByLabel, 0, 4, 1, 4) // Column 1, Row 1
		gridLayout.setComponentAlignment(madeByLabel, Alignment.TOP_CENTER)
		madeByLabel.setWidth("-1")

	}
	
	public boolean save() {
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
	Checkout checkout

	public UploadReceiver(Checkout checkout) {
		this.checkout = checkout
	}

	@Override
	public OutputStream receiveUpload(String strFilename, String strMIMEType) {
		
		File file

		try {

			file = File.createTempFile(strFilename, ".tmp")

			checkout.picturePath = file.absolutePath

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
