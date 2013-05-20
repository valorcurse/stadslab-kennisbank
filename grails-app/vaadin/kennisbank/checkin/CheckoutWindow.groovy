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
import kennisbank.equipment.*

class CheckoutWindow extends Window {

	Checkout checkout
	def settings

	CheckoutWindow(Checkin checkin) {

		// checkout = new Checkout(checkin: checkin)
		checkout = checkin.checkout
		settings = []


		setCaption("Check out") 
		setPrimaryStyleName("check-out")
		setModal(true)
		setStyleName(Reindeer.WINDOW_LIGHT)

		setCloseShortcut(KeyCode.ESCAPE, null);

		setContent(checkoutForm(checkin))
		// setContent(equipmentSection(checkin))
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
		titleTextField.addTextChangeListener(new TextChangeListener() {
			@Override
			public void textChange(final TextChangeEvent textChangeEvent) {
					checkout.title = textChangeEvent.getText()
				}
			})


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

		UploadReceiver receiver = new UploadReceiver(checkout) // Receiver that handles the data stream
		Upload upload = new Upload(null, receiver) // Upload button
		
		upload.addSucceededListener(new Upload.SucceededListener() {
			public void uploadSucceeded(SucceededEvent event) {
				pictureButton.setSource(new FileResource(new File(checkout.picturePath)))
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

					// Add the ComboBox to the table
					Item materialItem = container.addItem(materialComboBox)
					materialItem.getItemProperty("Apparatuur").setValue(materialComboBox)
					container.setParent(materialComboBox, equipmentUsed)
					materialTreeTable.setCollapsed(equipmentUsed, false)
					
					// ############################ Choose material ############################
					materialComboBox.addValueChangeListener(new ValueChangeListener() {
						@Override
						public void valueChange(final ValueChangeEvent comboEvent) {

							def material = Material.findByName(comboEvent.getProperty().getValue())

							// ComboBox to choose the type of the material
							ComboBox materialTypeComboBox = new ComboBox(null, material.materialTypes*.name)
							materialTypeComboBox.setNullSelectionAllowed(false)
							materialTypeComboBox.setImmediate(true)
							materialTreeTable.setCollapsed(materialComboBox, false)
							
							//  Add the ComboBox to the table
							materialItem.getItemProperty("Materiaal").setValue(materialTypeComboBox)

							// ############################ Choose material type ############################
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

		Button saveButton = new Button("Opslaan")
		formLayout.addComponent(saveButton, 0, 3, 1, 3)  // Column 0, Row 3 to Column 1, Row 3
		formLayout.setComponentAlignment(saveButton, Alignment.TOP_CENTER)
		
		saveButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				
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
		})

		return formLayout
	}

	private Layout equipmentSection(Checkin checkin) {
		VerticalLayout layout = new VerticalLayout()
		layout.setHeight("500px")
		layout.setWidth("700px")
		layout.setMargin(true)
		layout.setSpacing(true)

		Accordion accordion = new Accordion()
		accordion.addStyleName(Runo.ACCORDION_LIGHT)
		layout.addComponent(accordion)

		// Layout tabLayout = checkoutForm(checkin)
		// tabLayout.setSizeFull()
		accordion.addTab(checkoutForm(checkin), "Tab 1")

		// Layout tabLayout = checkoutForm(checkin)
		// tabLayout.setSizeFull()
		accordion.addTab(checkoutForm(checkin), "Tab 2")




		return layout
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
