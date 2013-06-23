 package kennisbank.checkin

import com.vaadin.ui.*
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.TabSheet.Tab
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.Tree.ExpandEvent
import com.vaadin.ui.Upload.SucceededEvent
import com.vaadin.ui.Upload.FailedEvent
import com.vaadin.ui.Upload.Receiver
import com.vaadin.ui.Button.ClickEvent

import com.vaadin.data.Property.ValueChangeListener
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.data.Item
import com.vaadin.data.util.HierarchicalContainer
import com.vaadin.data.util.IndexedContainer

import com.vaadin.server.FileResource
import com.vaadin.server.DefaultErrorHandler
import com.vaadin.server.UserError
import com.vaadin.server.ThemeResource

import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.event.ShortcutListener
import com.vaadin.event.FieldEvents.TextChangeListener
import com.vaadin.event.FieldEvents.TextChangeEvent

import java.nio.channels.FileChannel
import com.vaadin.shared.ui.label.ContentMode
import org.springframework.context.MessageSource
import org.codehaus.groovy.grails.commons.ApplicationHolder

import kennisbank.equipment.*
import kennisbank.*
import kennisbank.utils.*



class CheckoutWindow extends Window {

	def checkoutForms
	def checkoutSuccessful
	Checkin checkin

	CheckoutWindow(Checkin checkin) {

		checkoutForms = []
		this.checkin = checkin
		checkoutSuccessful = false

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

		tabSheet.setCloseHandler(new TabSheet.CloseHandler() {
			public void onTabClose(TabSheet tabsheet, Component tabContent) {
				checkoutForms.remove(tabContent)
				tabsheet.removeComponent(tabContent)
			}
		})
		
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
		saveButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (save()) {
					checkoutSuccessful = true
					close()
				}
			}
		})

		return layout
	}

	private boolean save() {
		def checkouts = []

		for (form in checkoutForms) {
			Checkout.withTransaction {

				Checkout checkout = form.checkout
				checkouts.add(checkout)

				def errorComponents = [form.titleTextField, form.pictureUpload, form.filesUpload, 
										form.descriptionTextArea, form.rootAddMaterialButton.button,
										form.tab]

				for (component in errorComponents) {
					component.setComponentError(null)
				}

				if (checkout.validate()) {
					checkout.save()
				
				} else {
					MessageSource messageSource = ApplicationHolder.application.mainContext.getBean('messageSource')

					checkout.errors.allErrors.each {

						form.tab.setComponentError(new UserError("Er is een fout in dit project."))

						if (it.getField() == "title") {
							form.titleTextField.setComponentError(new UserError("Er moet een titel ingevuld worden."))
						}

						if (it.getField() == "picturePath") {
							form.pictureUpload.setComponentError(new UserError("Er moet een foto geüpload worden."))
						}

						if (it.getField() == "files") {
							form.filesUpload.setComponentError(new UserError("Er moeten bestanden geüpload worden."))
						}

						if (it.getField() == "description") {
							form.descriptionTextArea.setComponentError(new UserError("Er moet een korte beschrijving ingevoerd worden."))
						}

						if (it.getField() == "settings") {
							form.rootAddMaterialButton.button.setComponentError(new UserError("Er moeten instellingen worden toegevoegd."))
						}

						if (it.getField() ==~ /settings.*/) {
							form.rootAddMaterialButton.button.setComponentError(new UserError("Er zijn verkeerd ingevuld of lege velden."))
						}
					}
				}
			}
		}

		for (checkout in checkouts) {
			if (checkout.id) {
				checkin.addToCheckouts(checkout)
			} else {
				return false
			}
		}
		return true
	}
}

class CheckoutForm extends Panel {
	
	Tab tab
	UploadHelper uploadHelper
	def settings
	Checkout checkout

	TextField titleTextField
	Upload pictureUpload, filesUpload
	TextArea descriptionTextArea
	AddMaterialButton rootAddMaterialButton

	CheckoutForm(Checkin checkin) { 

		setStyleName(Reindeer.PANEL_LIGHT)
		setHeight("600px")

		settings = []

		String uploadPath = ""

		uploadHelper = new UploadHelper()

		checkout = new Checkout(checkin: checkin)
		UploadReceiver receiver = new UploadReceiver(uploadHelper) // Receiver that handles the data stream

		GridLayout gridLayout = new GridLayout(2, 5)
		setContent(gridLayout)
		gridLayout.setSpacing(true)
		gridLayout.setMargin(true)
		gridLayout.setWidth("100%")
		gridLayout.setColumnExpandRatio(1, 1)

		VerticalLayout titleLayout = new VerticalLayout()
		gridLayout.addComponent(titleLayout, 0, 0, 1, 0) // Column 0, Row 0 to Column 1, Row 0

		// ------------------------------------------------------- Title -------------------------------------------------------		
		titleTextField = new TextField()
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
		// pictureLayout.setPrimaryStyleName("embedded-panel")
		pictureLayout.setSpacing(true)

		Image projectImage = new Image("Upload hier een foto van het project");
		pictureLayout.addComponent(projectImage);
		projectImage.setStyleName("picture");
		projectImage.setSource(new ThemeResource("emptyImage.gif"))

		pictureUpload = new Upload(null, receiver) // Upload button
		pictureLayout.addComponent(pictureUpload)
		pictureLayout.setWidth("-1")
		pictureUpload.setImmediate(true) // Starts to upload immediately after choosing file

		pictureUpload.addSucceededListener(new Upload.SucceededListener() {
			public void uploadSucceeded(SucceededEvent event) {
				checkout.picturePath = uploadHelper.filePath
				projectImage.setSource(new FileResource(new File(checkout.picturePath)))
				Notification.show("Uploaden geslaagd!", Notification.TYPE_TRAY_NOTIFICATION)	
			}
		})
		
		pictureUpload.addFailedListener(new Upload.FailedListener() {
			public void uploadFailed(FailedEvent event) {
				Notification.show("Uploaden niet gelukt!", Notification.TYPE_TRAY_NOTIFICATION)	
			}
		})

		// ------------------------------------------------------- Uploads -------------------------------------------------------

		VerticalLayout uploadsLayout = new VerticalLayout()
		gridLayout.addComponent(uploadsLayout, 1, 1)
		uploadsLayout.setSpacing(true)



		Table uploadsTable = new Table("Upload hier de bron and overige bestanden")
		uploadsLayout.addComponent(uploadsTable)
		
		uploadsTable.setWidth("100%")
		uploadsTable.setHeight("170px")

		filesUpload = new Upload(null, receiver) // Upload button
		uploadsLayout.addComponent(filesUpload)
		filesUpload.setImmediate(true)

		IndexedContainer uploadsContainer = new IndexedContainer()
		uploadsContainer.addContainerProperty("Naam", Component.class, "")
		uploadsContainer.addContainerProperty("Grootte", String.class, "")
		uploadsTable.setContainerDataSource(uploadsContainer)

		filesUpload.addSucceededListener(new Upload.SucceededListener() {
			public void uploadSucceeded(SucceededEvent event) {
				checkout.addToFiles(new AttachedFile(name: uploadHelper.name, path: uploadHelper.filePath))

				Item uploadItem = uploadsContainer.addItem(uploadHelper.filePath)
				uploadItem.getItemProperty("Naam").setValue(new DownloadLink(uploadHelper.filePath, uploadHelper.name))
				uploadItem.getItemProperty("Grootte").setValue(Utils.humanReadableByteCount(new File(uploadHelper.filePath).length()))
				Notification.show("Uploaden geslaagd!", Notification.TYPE_TRAY_NOTIFICATION)
			}
		})
		
		filesUpload.addFailedListener(new Upload.FailedListener() {
			public void uploadFailed(FailedEvent event) {
				Notification.show("Uploaden niet gelukt!", Notification.TYPE_TRAY_NOTIFICATION)
			}
		})

		// ------------------------------------------------------- Description -------------------------------------------------------

		descriptionTextArea = new TextArea("Korte omschrijving")
		gridLayout.addComponent(descriptionTextArea, 0, 2, 1, 2) // Column 0, Row 2 to Column 1, Row 2
		descriptionTextArea.setWidth("100%")
		descriptionTextArea.addTextChangeListener(new TextChangeListener() {
			@Override
			public void textChange(final TextChangeEvent textChangeEvent) {
					checkout.description = textChangeEvent.getText()
				}
			})

		// ------------------------------------------------------- Material -------------------------------------------------------
		
		TreeTable settingsTreeTable = new TreeTable("Voeg hier de apparaten, materialen en bijbehorend instellingen die gebruikt zijn")
		gridLayout.addComponent(settingsTreeTable, 0, 3, 1, 3) // Column 0, Row 3 to Column 1, Row 3
		settingsTreeTable.setWidth("100%")
		settingsTreeTable.setPageLength(0)

		HierarchicalContainer settingsContainer = new HierarchicalContainer()
		settingsContainer.addContainerProperty("Apparatuur", Component.class, "")
		settingsContainer.addContainerProperty("Materiaal", Component.class, "")
		settingsContainer.addContainerProperty("Instellingen", TextField.class, "")
		settingsTreeTable.setContainerDataSource(settingsContainer)
		settingsTreeTable.setColumnExpandRatio("Apparatuur", 0.6)
		settingsTreeTable.setColumnExpandRatio("Materiaal", 0.4)

		rootAddMaterialButton = new AddMaterialButton("Voeg een apparaat toe")
		Item rootItem = settingsContainer.addItem(rootAddMaterialButton)
		rootItem.getItemProperty("Apparatuur").setValue(rootAddMaterialButton)

		rootAddMaterialButton.button.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				def settingsList = settings
				Checkout checkout = checkout
				def materialComboBoxesToRemove = []
				def materialSettingsToRemove = []

 				// ComboBox to choose kind of material used
				ExtendedComboBox equipmentComboBox = new ExtendedComboBox(null, Equipment.list()*.name, true, true)
				equipmentComboBox.comboBox.setNullSelectionAllowed(false)
				equipmentComboBox.comboBox.setImmediate(true)
				equipmentComboBox.comboBox.setInputPrompt("Kies een apparaat")

				// Add the ComboBox to the table
				Item equipmentItem = settingsContainer.addItem(equipmentComboBox)
				equipmentItem.getItemProperty("Apparatuur").setValue(equipmentComboBox)
				settingsContainer.setParent(equipmentComboBox, rootAddMaterialButton)
				settingsTreeTable.setCollapsed(rootAddMaterialButton, false)
				
				def equipment
				
				// Remove previously added child components if equipment selection changed
				def removeChildren = {
					def childrenToDelete = []
					for (child in equipmentComboBox.children) {
						settingsContainer.removeItem(child)
						childrenToDelete.add(child)
						for (secondChild in child.children) {
							settingsContainer.removeItem(secondChild)
						}
					}
					for (child in childrenToDelete) {
						child.children.clear()
					}
					equipmentComboBox.children.clear()
				}

				// ---------------------------- Choose equipment ----------------------------
				equipmentComboBox.comboBox.addValueChangeListener(new ValueChangeListener() {
					@Override
					public void valueChange(final ValueChangeEvent equipmentComboEvent) {
						equipment = Equipment.findByName(equipmentComboEvent.getProperty().getValue())

						removeChildren()

						comboBoxContent(equipment, settingsList,
										settingsTreeTable, equipmentComboBox,
										checkout)
					}
				})

				equipmentComboBox.plusButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent equipmentButtonEvent) {
						if (equipment == null) {
							Notification.show("Kies eerst een apparaat.")
						}
						else {
							comboBoxContent(equipment, settingsList,
											settingsTreeTable, equipmentComboBox,
											checkout)
						}
					}
				})

				equipmentComboBox.removeButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent equipmentButtonEvent) {
						removeChildren()
						settingsTreeTable.removeItem(equipmentComboBox)
					}
				})
			}
		})

		// --------------------------------- Made By Label ---------------------------------

		def name = ""
		switch(checkin.getClass()) {
		 	case StudentCheckin:
		 	name = checkin.firstName + " " + checkin.lastName
		 	break

		 	case CompanyCheckin:
			name = checkin.contactPerson + " : " + checkin.companyName			 		
		 	break
		} 

		Label madeByLabel = new Label("Gemaakt door: <i>" + 
			name + " (<a href=\"mailto:" + checkin.email + "\">"+ checkin.email +"</a>)" +
			" op " + checkin.dateCreated.format('dd MMMM yyyy') + "</i>", ContentMode.HTML)
		
		gridLayout.addComponent(madeByLabel, 0, 4, 1, 4) // Column 1, Row 1
		gridLayout.setComponentAlignment(madeByLabel, Alignment.TOP_CENTER)
		madeByLabel.setWidth("-1")

	}
	
	private void comboBoxContent(Equipment equipment, List settingsList,
								TreeTable settingsTreeTable, ExtendedComboBox equipmentComboBox,
								Checkout checkout) {
		
		IndexedContainer settingsContainer = settingsTreeTable.getContainerDataSource()

		def equipmentUsedSettings = []
		equipment.settingTypes.each {
			def newSetting = new Setting(equipment: equipment, settingType: it)
			checkout.addToSettings(newSetting)
			equipmentUsedSettings.add(newSetting)
		}

		settingsList.add(equipmentUsedSettings)

		// ComboBox to choose kind of material used
		ExtendedComboBox materialComboBox = new ExtendedComboBox(null, equipment.materialTypes*.material.name, false, false)
		equipmentComboBox.children.add(materialComboBox)
		materialComboBox.comboBox.setNullSelectionAllowed(false)
		materialComboBox.comboBox.setImmediate(true)
		materialComboBox.comboBox.setInputPrompt("Kies een materiaal")

		// Add the ComboBox to the table
		Item materialItem = settingsContainer.addItem(materialComboBox)
		materialItem.getItemProperty("Apparatuur").setValue(materialComboBox)
		settingsContainer.setParent(materialComboBox, equipmentComboBox)
		settingsTreeTable.setCollapsed(equipmentComboBox, false)
		
		// ---------------------------- Choose material ----------------------------
		materialComboBox.comboBox.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent comboEvent) {
				for (child in materialComboBox.children) {
					settingsContainer.removeItem(child)					
				}

				def material = Material.findByName(comboEvent.getProperty().getValue())

				// ComboBox to choose the type of the material
				ComboBox materialTypeComboBox = new ComboBox(null, material.materialTypes*.name)
				materialTypeComboBox.setNullSelectionAllowed(false)
				materialTypeComboBox.setImmediate(true)
				materialTypeComboBox.setInputPrompt("Kies een materiaal type")

				settingsTreeTable.setCollapsed(materialComboBox, false)
										
				//  Add the ComboBox to the table
				materialItem.getItemProperty("Materiaal").setValue(materialTypeComboBox)

				// ---------------------------- Choose material type ----------------------------
				materialTypeComboBox.addValueChangeListener(new ValueChangeListener() {
					@Override
					public void valueChange(final ValueChangeEvent comboTypeEvent) { 

						def materialType = comboTypeEvent.getProperty().getValue()

						equipmentUsedSettings.each { it.materialType = MaterialType.findByName(materialType) }

						if (!settingsTreeTable.hasChildren(materialComboBox)) {
							for (def settingUsed : equipment.settingTypes.asList()) {
								Label newSettingLabel = new Label(settingUsed.name)
								
								Item settingItem = settingsContainer.addItem(newSettingLabel)
								materialComboBox.children.add(newSettingLabel)
								settingItem.getItemProperty("Materiaal").setValue(newSettingLabel)

								TextField valueTextField = new TextField()
								materialComboBox.children.add(valueTextField)
								valueTextField.setWidth("99%")
								valueTextField.setCaption(settingUsed.name)

								settingItem.getItemProperty("Instellingen").setValue(valueTextField)
			 
								settingsContainer.setParent(newSettingLabel, materialComboBox)
								settingsTreeTable.setChildrenAllowed(newSettingLabel, false)

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
							for (child in settingsTreeTable.getChildren(materialComboBox)) {
								Item item = settingsContainer.getItem(child)
								item.getItemProperty("Instellingen").getValue().setValue("")
							}
						}
					}
				})
			}
		})
	}
}

class UploadHelper {
	String name, size, filePath
}

class UploadReceiver implements Receiver {

	OutputStream outputFile = null
	UploadHelper uploadHelper

	public UploadReceiver(UploadHelper uploadHelper) {
		this.uploadHelper = uploadHelper
	}

	@Override
	public OutputStream receiveUpload(String strFilename, String strMIMEType) {
		
		File file

		try {

			file = File.createTempFile(strFilename, null)

			uploadHelper.filePath = file.absolutePath
			uploadHelper.name = strFilename

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
