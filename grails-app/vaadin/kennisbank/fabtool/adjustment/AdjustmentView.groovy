package kennisbank.fabtool.adjustment

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

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import kennisbank.equipment.*
import kennisbank.*
import kennisbank.utils.*
import kennisbank.checkin.Checkout


/**
 * Window where the administrator can add or edit equipments, materials and materialtypes.
 *
 * @author Nilson Xavier da Luz
 */
class AdjustmentView extends VerticalLayout{
	
	
	/**
	 * Fragment used to bookmark this page.
	 */	
	String uriFragment


	/**
	 * Constructor of the AdjustmentView class.
	 */		
	AdjustmentView() {


		//Main layout
		VerticalLayout view = new VerticalLayout()
		

		uriFragment = "#!"
		UI.getCurrent().getPage().getCurrent().setLocation(uriFragment)

		setMargin(true)
		setSizeFull()

		Panel panel = new Panel()
		panel.setPrimaryStyleName("island-panel")

		VerticalLayout layout = new VerticalLayout()
		layout.setSpacing(true)
		layout.setMargin(true)
		layout.setSizeFull()

		panel.setContent(layout)

		//----------------------------title------------------------------------------------------------
		Label titleLabel = new Label("<h1><b>Aanpassingen</b></h1>", ContentMode.HTML)
		titleLabel.setWidth("100%")
		layout.addComponent(titleLabel)

		//--------------------------------------------- Add new equipment or material -----------------------------------------------
		Panel addEquipmentPanel = new Panel("Apparaten en materialen")
		addEquipmentPanel.setPrimaryStyleName("embedded-panel")
		addEquipmentPanel.addStyleName(Runo.PANEL_LIGHT)
		layout.addComponent(addEquipmentPanel)
		
		AddMaterialButton rootAddMaterialButton
		Checkout checkout

		HorizontalLayout treesLayout = new HorizontalLayout()
		layout.addComponent(treesLayout)
		treesLayout.setMargin(true)
		treesLayout.setSpacing(true)

		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Material table >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		TreeTable materialTreeTable = new TreeTable()
		treesLayout.addComponent(materialTreeTable)
		materialTreeTable.setWidth("350px")
		materialTreeTable.setPageLength(0)
		materialTreeTable.setImmediate(true)

		HierarchicalContainer materialsContainer = new HierarchicalContainer()
		materialsContainer.addContainerProperty("Materiaal", Component.class, "")
		materialTreeTable.setContainerDataSource(materialsContainer)

		def addMaterialTypeTextField = { parent, value ->

			Boolean materialTypeIsUsed = false
			Setting.withTransaction {
				materialTypeIsUsed = Setting.findWhere(materialType: value) != null
			}

			ExtendedText materialTypeTextField = new ExtendedText(value, true, !materialTypeIsUsed, false)
			Item materialTItem = materialsContainer.addItem(materialTypeTextField)
			materialTItem.getItemProperty("Materiaal").setValue(materialTypeTextField)
			materialsContainer.setParent(materialTypeTextField, parent)
			materialTreeTable.setCollapsed(parent, false)
			materialTreeTable.setChildrenAllowed(materialTypeTextField, false)

			materialTypeTextField.saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent materialTypeEvent) {
					if (materialTypeTextField.textField.getValue() != "") {
						if (materialTypeTextField.object == null) {
							MaterialType.withTransaction {
								MaterialType newMaterialType = new MaterialType(name: materialTypeTextField.textField.getValue())

								Material currentMaterial = Material.findById(parent.object.id)
								currentMaterial.addToMaterialTypes(newMaterialType)
								currentMaterial = currentMaterial.merge()

								if (currentMaterial?.save(failOnError: true, flush: true)) {
									materialTypeTextField.object = newMaterialType
								}
							}

						} else {
							if (materialTypeTextField.textField.getValue() != materialTypeTextField.object.name) {
								Material.withTransaction {
									MaterialType currentMaterialType = MaterialType.findById(materialTypeTextField.object.id)
									currentMaterialType.name = materialTypeTextField.textField.getValue()
									currentMaterialType = currentMaterialType.merge()
									currentMaterialType.save()
								}
							}
						}

						Notification.show(materialTypeTextField.object.name + " is opgeslagen")
				
					} else {
						Notification.show("Kies eerst een naam")
					}
				}
			})
			
			materialTypeTextField.removeButton?.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent equipmendButtonEvent) {
					if (materialTypeTextField.object) {
						
						try {
							MaterialType currentMaterialType = MaterialType.findByName(materialTypeTextField.textField.getValue())
							
							if (currentMaterialType.delete()) {
								Notification.show(materialTypeTextField.textField.getValue() + " is verwijderd")
								materialsContainer.removeItem(materialTypeTextField)
								materialTreeTable.removeItem(materialTypeTextField)
							}
						} catch(Exception e) {
							Notification.show("Materiaal soort wordt ergens gebruikt. Kan niet worden verwijderd.")
						}
					}
				}
			})
		}

		def addMaterialTextField = { parent, value ->

			Boolean materialIsUsed = false
			Setting.withTransaction {
				Setting.list().each {
					if (it.materialType.id in value.materialTypes*.id) {
						materialIsUsed = true
						return
					}
				}
			}


			ExtendedText materialTextField = new ExtendedText(value, true, !materialIsUsed, true)
			Item materialItem = materialsContainer.addItem(materialTextField)
			materialItem.getItemProperty("Materiaal").setValue(materialTextField)
			materialsContainer.setParent(materialTextField, parent)	

			materialTextField.saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent equipmentButtonEvent) {
					if (materialTextField.textField.getValue() != "") {
						Material.withTransaction {
							new Material(name: materialTextField.textField.getValue()).save(failOnError: true)
							Notification.show(materialTextField.textField.getValue() + " is opgeslagen")
						}	
					}
					else {
						Notification.show("Kies eerst een apparaat.")
					}
				}
			})

			materialTextField.plusButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent materialEvent) {
					if (materialTextField.textField.getValue() != "" && materialTextField.object) {
						addMaterialTypeTextField(materialTextField, null)
					}
					else{
						Notification.show("Kies eerst een apparaat.")
					}
				}
			})

			if (!materialIsUsed) {
				//-----------------------remove material------------------------------------------
				materialTextField.removeButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent equipmentButtonEvent) {
						def childrenToDelete = []
						for (child in materialsContainer.getChildren(materialTextField)) {
							childrenToDelete.add(child)
						}
						for (child in childrenToDelete) {
							materialsContainer.removeItem(child)
							materialTreeTable.removeItem(child)
						}
						
						if (materialTextField.object) {
							try {
								materialTextField.object.delete()
								Notification.show(materialTextField.textField.getValue() + " is verwijderd")
							} catch(Exception e) {
								Notification.show("Materiaal wordt ergens gebruikt. Kan niet worden verwijderd.")
							}
						}

						materialsContainer.removeItem(materialTextField)
						materialTreeTable.removeItem(materialTextField)
					}
				})
			}

			return materialTextField
		}

		rootAddMaterialButton = new AddMaterialButton("Voeg een Materiaal toe")
		Item rootItem = materialsContainer.addItem(rootAddMaterialButton)
		rootItem.getItemProperty("Materiaal").setValue(rootAddMaterialButton)
		materialTreeTable.setCollapsed(rootAddMaterialButton, false)

		// ------------------------------------------ Existing materials -------------------------------------------------
		for (material in Material.list()) {
			ExtendedText materialTextField = addMaterialTextField(rootAddMaterialButton, material)		

			// ------------------------------------------- Existing materialtype -------------------------------------------
			for (materialType in material.materialTypes) {
				addMaterialTypeTextField(materialTextField, materialType)
			}
		}

		// ----------------------------------- Add new materials and materialtypes ------------------------------------------
		rootAddMaterialButton.button.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

 				// ComboBox to choose kind of material used
				ExtendedText materialTextField = new ExtendedText(null, true, true, true)
				materialTextField.textField.setImmediate(true)
				materialTextField.textField.setInputPrompt("Maak een materiaal aan")

				// Add the ComboBox to the table
				Item equipmentItem = materialsContainer.addItem(materialTextField)
				equipmentItem.getItemProperty("Materiaal").setValue(materialTextField)
				materialsContainer.setParent(materialTextField, rootAddMaterialButton)
				materialTreeTable.setCollapsed(rootAddMaterialButton, false)
				
				// ------------------------------------------------ Save material ------------------------------------------------
				materialTextField.saveButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent equipmentButtonEvent) {
						if (materialTextField.textField.getValue() != "") {
							Material.withTransaction {
								Material newMaterial = new Material(name: materialTextField.textField.getValue()).save(failOnError: true)
								materialTextField.object = newMaterial
								Notification.show(materialTextField.textField.getValue() + " is opgeslagen")
							}	
						} else {
							Notification.show("Kies eerst een apparaat.")
						}
					}
				})

				materialTextField.plusButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent equipmentButtonEvent) {
						if (materialTextField.textField.getValue() != "") {
							addMaterialTypeTextField(materialTextField, null)
						} else {
							Notification.show("Kies eerst een naam.")
						}
					}
				})
				
				//----------------------remove new material------------------------------
				materialTextField.removeButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent equipmentButtonEvent) {
						
						if (materialTextField.textField.getValue() != "" && materialTextField.object != null) {
							def childrenToDelete = []
							for (child in materialsContainer.getChildren(materialTextField)) {
								childrenToDelete.add(child)
							}
							
							for (child in childrenToDelete) {
								try {
							
									MaterialType.findByName(child.textField.getValue()).delete(flush: true)
									materialsContainer.removeItem(child)
									materialTreeTable.removeItem(child)
							
								} catch(Exception e) {
									Notification.show("Materiaal soort wordt ergens gebruikt. Kan niet worden verwijderd.", Notification.TYPE_ERROR_MESSAGE)
								}
							}
							
							materialsContainer.removeItem(materialTextField)
							materialTreeTable.removeItem(materialTextField)
						}
						
						if (materialTextField.object) {
							materialTextField.object.delete()
							Notification.show(materialTextField.textField.getValue() + " is verwijderd")
						}

						materialsContainer.removeItem(materialTextField)
						materialTreeTable.removeItem(materialTextField)

					}
				})
			}
		})


		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Equipment table >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

		TreeTable equipmentTreeTable = new TreeTable()
		treesLayout.addComponent(equipmentTreeTable) 
		equipmentTreeTable.setWidth("350px")
		equipmentTreeTable.setPageLength(0)

		HierarchicalContainer equipmentsContainer = new HierarchicalContainer()
		equipmentsContainer.addContainerProperty("Apparaat", Component.class, "")
		equipmentTreeTable.setContainerDataSource(equipmentsContainer)

		AddMaterialButton rootAddEquipmentButton
		rootAddEquipmentButton = new AddMaterialButton("Voeg een Apparaat toe")
		Item rootEquipmentItem = equipmentsContainer.addItem(rootAddEquipmentButton)
		rootEquipmentItem.getItemProperty("Apparaat").setValue(rootAddEquipmentButton)
		equipmentTreeTable.setCollapsed(rootAddEquipmentButton, false)

		def checkDisabled = { component, container ->
				if (container.hasChildren(component)) {
					component.comboBox.setEnabled(false)
				}
				else {
					component.comboBox.setEnabled(true)
				}
		}

		def removeChildren = { component, treeTable ->
			def childrenToDelete = []
			def container = treeTable.getContainerDataSource()
			for (child in container.getChildren(component)) {
				childrenToDelete.add(child)
				for (secondChild in treeTable.getChildren(child)) {
					for (thirdChild in treeTable.getChildren(secondChild)) {
						childrenToDelete.add(thirdChild)
					}
					childrenToDelete.add(secondChild)
				}
			}

			for (child in childrenToDelete) {
				container.removeItem(child)
				treeTable.removeItem(child)
			}
			container.removeItem(component)
			treeTable.removeItem(component)
		}

		def addSetting = { parent, equipment, value ->

			Boolean settingTypeIsUsed = false
			Setting.withTransaction {
				settingTypeIsUsed = Setting.findWhere(settingType: value) != null
			}

			ExtendedText settingsTextField = new ExtendedText(value, true, !settingTypeIsUsed, false)

			Item settingsItem = equipmentsContainer.addItem(settingsTextField)
			settingsItem.getItemProperty("Apparaat").setValue(settingsTextField)
			equipmentsContainer.setParent(settingsTextField, parent)
			equipmentTreeTable.setCollapsed(parent, false)
			equipmentTreeTable.setChildrenAllowed(settingsTextField, false)

			Equipment currentEquipment = Equipment.findById(equipment.id)
			
			//-----------------------------save new setting--------------------------------
			settingsTextField.saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent settingremoveEvent) {
					
					if (settingsTextField.textField.getValue() != "") 
					{
						SettingType.withTransaction {
							
							SettingType newSetting = new SettingType(name: settingsTextField.textField.getValue())
							currentEquipment.addToSettingTypes(newSetting)
							currentEquipment = currentEquipment.merge()
							
							if (currentEquipment.save(failOnError: true)) {
								settingsTextField.object = newSetting
							}

							Notification.show(settingsTextField.textField.getValue() + " is toegevoegd")
						}	
					}
					else{

						Notification.show("Kies eerst een setting.")
					}
				}
			})

			if (!settingTypeIsUsed) {
				//-------------------------remove new setting-------------------------------------
				settingsTextField.removeButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent settingremoveEvent) {
						
						
						if (settingsTextField.textField.getValue() != "" && settingsTextField.object != null) {
							try {
								equipmentsContainer.removeItem(settingsTextField)
								equipmentTreeTable.removeItem(settingsTextField)
								SettingType.findByName(settingsTextField.textField.getValue()).delete(flush: true)
							} catch(Exception e) {
								Notification.show("Instelling wordt ergens gebruikt. Kan niet worden verwijderd.")
							}
						} else {
							equipmentsContainer.removeItem(settingsTextField)
							equipmentTreeTable.removeItem(settingsTextField)
						}
					}
				})
			}
		}

		def addMaterialType = { parent, equipment, value ->

			ExtendedComboBoxwithCheck materialTypeComboBox = new ExtendedComboBoxwithCheck(value, parent.object.materialTypes*.name, true, true, false)
			materialTypeComboBox.comboBox.setNullSelectionAllowed(false)
			materialTypeComboBox.comboBox.setImmediate(true)
			materialTypeComboBox.comboBox.setInputPrompt("Kies een materialtype")
			
			Item materialTypeItem = equipmentsContainer.addItem(materialTypeComboBox)
			materialTypeItem.getItemProperty("Apparaat").setValue(materialTypeComboBox)
			equipmentsContainer.setParent(materialTypeComboBox, parent)
			equipmentTreeTable.setCollapsed(parent, false)
			equipmentTreeTable.setChildrenAllowed(materialTypeComboBox, false)

			//--------------------------save new materialtype---------------------------------
			materialTypeComboBox.saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent materialTypedeleteEvent) {
					
					if (materialTypeComboBox.comboBox.getValue() != null) {
						MaterialType.withTransaction {
							MaterialType newMaterialType = MaterialType.findByName(materialTypeComboBox.comboBox.getValue())
							def currentEquipment = Equipment.findById(equipment.id)
							currentEquipment.addToMaterialTypes(newMaterialType)
							currentEquipment = currentEquipment.merge()
							if (currentEquipment.save(flush: true)) {
								materialTypeComboBox.object = newMaterialType
							}
							Notification.show(materialTypeComboBox.object.name + " is opgeslagen")
						}
					} else {
						Notification.show("Kies eerst een type.")
					}
				}
			})	

			//------------------------delete materialtype------------------------------------------
			materialTypeComboBox.removeButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent materialTypedeleteEvent) {
					
					if (materialTypeComboBox.comboBox.getValue() != null && materialTypeComboBox.object != null) {
						MaterialType.withTransaction {
							def currentEquipment = Equipment.findById(equipment.id)
							currentEquipment.removeFromMaterialTypes(MaterialType.findById(materialTypeComboBox.object.id))
							currentEquipment.save()
						}

						equipmentsContainer.removeItem(materialTypeComboBox)
						equipmentTreeTable.removeItem(materialTypeComboBox)

					} else {
						equipmentsContainer.removeItem(materialTypeComboBox)
						equipmentTreeTable.removeItem(materialTypeComboBox)
					}
					checkDisabled(parent, equipmentsContainer)
				}
			})
		}

		def addMaterial = { parent, equipment, value ->
			ExtendedComboBoxwithCheck materialComboBox = new ExtendedComboBoxwithCheck(value, Material.list()*.name, false, true, true)
			materialComboBox.comboBox.setNullSelectionAllowed(false)
			materialComboBox.comboBox.setImmediate(true)
			materialComboBox.comboBox.setInputPrompt("Kies een material")
			
			Item materialItem = equipmentsContainer.addItem(materialComboBox)
			materialItem.getItemProperty("Apparaat").setValue(materialComboBox)
			equipmentsContainer.setParent(materialComboBox, parent)
			equipmentTreeTable.setCollapsed(parent, false)

			materialComboBox.comboBox.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(final ValueChangeEvent comboEvent) {
					materialComboBox.object = Material.findByName(materialComboBox.comboBox.getValue())
					addMaterialType(materialComboBox, equipment, null)
					checkDisabled(materialComboBox, equipmentsContainer)
				}
			})

			materialComboBox.plusButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent addMaterialTypeEvent) {
					if (materialComboBox.object) {
						addMaterialType(materialComboBox, equipment, null)
						checkDisabled(materialComboBox, equipmentsContainer)
					}
					else {
						Notification.show("Kies eerst een materiaal")
					}
				}
			})

			materialComboBox.removeButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent addMaterialTypeEvent) {
					for (child in equipmentsContainer.getChildren(materialComboBox)) {
						if (child.object) {
							Equipment.withTransaction {
								Equipment currentEquipment = Equipment.findById(equipment.id)
								currentEquipment.removeFromMaterialTypes(MaterialType.findById(child.object.id))
								currentEquipment.save()
							}
						}
					}
					removeChildren(materialComboBox, equipmentTreeTable)
				}
			})
		}

		// ------------------------------------------------- Existing Equipment -------------------------------------------------
		for (equipment in Equipment.list()) {

			Boolean equipmentIsUsed = false
			Setting.withTransaction {
				equipmentIsUsed = Setting.findWhere(equipment: equipment) != null
			}

			ExtendedText equipmentTextField = new ExtendedText(equipment, true, !equipmentIsUsed, false)
			Item equipmentItem = equipmentsContainer.addItem(equipmentTextField)
			equipmentItem.getItemProperty("Apparaat").setValue(equipmentTextField)
			equipmentTreeTable.setCollapsed(rootAddEquipmentButton, false)
			equipmentsContainer.setParent(equipmentTextField, rootAddEquipmentButton)	
		
			equipmentTextField.saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent settingsaveEvent) {
					if (equipmentTextField.textField.getValue() != "") {
						
						Equipment.withTransaction {

							Equipment newEquipment = new Equipment(name: equipmentTextField.textField.getValue())
							
							if (newEquipment.save(failOnError: true)) { 
								equipmentTextField.object = newEquipment
								equipmentTreeTable.setCollapsed(equipmentTextField, false)
								Notification.show(equipmentTextField.textField.getValue() + " is toegevoegd")
							}
							else {
								Notification.show("Apparaat \"" + equipmentTextField.textField.getValue() + "\" opslaan is nit gelukt", 
									Notification.TYPE_ERROR_MESSAGE)
							}
						}

					} else {
						Notification.show("Kies eerst een apparaatnaam.")
					}
				}
			})
			
			// ------------------------- Remove Equipment and children -----------------------------------------
			equipmentTextField.removeButton?.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent equipmentdeleteEvent) {
					try {
						Equipment.findById(equipmentTextField.object.id).delete()
						removeChildren(equipmentTextField, equipmentTreeTable)
						Notification.show("Apparaat is verwijderd")
					} catch(Exception e) {
						Notification.show("Materiaal soort wordt ergens gebruikt. Kan niet worden verwijderd.")
					}
				}
			})	

			// -------------------------------------------------  Add new materials -------------------------------------------------
			AddMaterialButton addMaterialsButton = new AddMaterialButton("Materialen", equipment)
			Item addMaterialsButtonItem = equipmentsContainer.addItem(addMaterialsButton)
			addMaterialsButtonItem.getItemProperty("Apparaat").setValue(addMaterialsButton)
			equipmentsContainer.setParent(addMaterialsButton, equipmentTextField)	 

			//-------------------------------------- Add new material --------------------------------------------------------
			addMaterialsButton.button.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent addMaterialEvent) {
					addMaterial(addMaterialsButton, equipmentTextField.object, null)
				}
			})
			
			// ---------------------------------- Add new setting ------------------------------------------------
			AddMaterialButton addSettingsButton = new AddMaterialButton("Instellingen", equipment)
			Item settingsButtonItem = equipmentsContainer.addItem(addSettingsButton)
			settingsButtonItem.getItemProperty("Apparaat").setValue(addSettingsButton)
			equipmentsContainer.setParent(addSettingsButton, equipmentTextField)

			addSettingsButton.button.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					addSetting(addSettingsButton, addSettingsButton.object, null)
				}
			})
		
			// --------------------------------------- Placing existing settings ---------------------------------------
			for (settingType in equipment.settingTypes) {
				addSetting(addSettingsButton, addSettingsButton.object, settingType)
			}

			// --------------------------------------- Placing existing materials ---------------------------------------
			for (material in equipment.materialTypes.groupBy { it.material.name }) {
				Material currentMaterial = Material.findByName(material.key)
				ExtendedComboBoxwithCheck materialComboBox = new ExtendedComboBoxwithCheck(currentMaterial, Material.list()*.name, false, true,true)
				materialComboBox.comboBox.setValue(material.key)
				materialComboBox.comboBox.setNullSelectionAllowed(false)

				Item materialItem = equipmentsContainer.addItem(materialComboBox)
				materialItem.getItemProperty("Apparaat").setValue(materialComboBox)
				equipmentsContainer.setParent(materialComboBox, addMaterialsButton)	
				equipmentTreeTable.setCollapsed(equipmentTextField, false)

				materialComboBox.comboBox.addValueChangeListener(new ValueChangeListener() {
					@Override
					public void valueChange(final ValueChangeEvent comboEvent) {
						materialComboBox.object = Material.findByName(materialComboBox.comboBox.getValue())
						addMaterialType(materialComboBox, equipment, null)
						checkDisabled(materialComboBox, equipmentsContainer)
					}
				})

				materialComboBox.plusButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent addMaterialTypeEvent) {
						addMaterialType(materialComboBox, equipment, null)
						checkDisabled(materialComboBox, equipmentsContainer)
					}
				})

				materialComboBox.removeButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent addMaterialTypeEvent) {
						for (child in equipmentsContainer.getChildren(materialComboBox)) {
							if (child.object) {
								Equipment.withTransaction {
									Equipment currentEquipment = Equipment.findById(equipmentTextField.object.id)
									currentEquipment.removeFromMaterialTypes(MaterialType.findById(child.object.id))
									currentEquipment.save()
								}
							}
						}
						removeChildren(materialComboBox, equipmentTreeTable)
					}
				})

				// --------------------------------- Placing existing materialtype in existing material ---------------------------------
				for (materialType in equipment.materialTypes.findAll { it.material.id == currentMaterial.id }) {
					addMaterialType(materialComboBox, equipment, materialType)
				}

				// Disable material combobox if it has children
				checkDisabled(materialComboBox, equipmentsContainer)
			}
		}

		// ----------------------------- Button to add new equipment & childs ---------------------------------------
		rootAddEquipmentButton.button.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

 				// ComboBox to choose kind of material used
				ExtendedText equipmentTextField = new ExtendedText(null, true, true, false)
				equipmentTextField.textField.setImmediate(true)
				equipmentTextField.textField.setInputPrompt("Maak een apparaat aan")

				// Add the ComboBox to the table
				Item equipmentItem = equipmentsContainer.addItem(equipmentTextField)
				equipmentItem.getItemProperty("Apparaat").setValue(equipmentTextField)
				equipmentsContainer.setParent(equipmentTextField, rootAddEquipmentButton)
				equipmentTreeTable.setCollapsed(rootAddEquipmentButton, false)

				// ---------------------------------- Equipmentbutton functions ----------------------------------
				equipmentTextField.saveButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent settingsaveEvent) {
						if (equipmentTextField.textField.getValue() != "") {
							
							Equipment.withTransaction {
								Equipment newEquipment = new Equipment(name: equipmentTextField.textField.getValue())
								if (newEquipment.save(failOnError: true)) { 
									equipmentTextField.object = newEquipment
									Notification.show(equipmentTextField.textField.getValue() + " is toegevoegd")
									equipmentTreeTable.setCollapsed(equipmentTextField, false)
									equipmentTextField.object = newEquipment
								}
								else {
									Notification.show("Saving equipment \"" + equipmentTextField.textField.getValue() + "\" failed", 
										Notification.TYPE_ERROR_MESSAGE)
								}

								AddMaterialButton addMaterialsButton = new AddMaterialButton("Materialen", newEquipment)
								Item addMaterialsButtonItem = equipmentsContainer.addItem(addMaterialsButton)
								addMaterialsButtonItem.getItemProperty("Apparaat").setValue(addMaterialsButton)
								equipmentTreeTable.setCollapsed(rootAddEquipmentButton, false)
								equipmentsContainer.setParent(addMaterialsButton, equipmentTextField)	

								addMaterialsButton.button.addClickListener(new Button.ClickListener() {
									@Override
									public void buttonClick(ClickEvent materialevent) {
										addMaterial(addMaterialsButton, newEquipment, null)
									}
								})

								
								AddMaterialButton addSettingsButton = new AddMaterialButton("Instellingen", newEquipment)
								Item settingsButtonItem = equipmentsContainer.addItem(addSettingsButton)
								settingsButtonItem.getItemProperty("Apparaat").setValue(addSettingsButton)
								equipmentTreeTable.setCollapsed(rootAddEquipmentButton, false)
								equipmentsContainer.setParent(addSettingsButton, equipmentTextField)

								// ---------------------------------- Add new setting ----------------------------------
								addSettingsButton.button.addClickListener(new Button.ClickListener() {
									@Override
									public void buttonClick(ClickEvent settingsevent) {
										addSetting(addSettingsButton, newEquipment, null)
									}
								})
							}
							
						} else {
							Notification.show("Kies eerst een apparaat.")
						}
					}
				})

				// ---------------------------------- Remove equipment ----------------------------------
				equipmentTextField.removeButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent equipmentremoveEvent) {
						try {
							Equipment.findById(equipmentTextField.object.id).delete()
							removeChildren(equipmentTextField, equipmentTreeTable)
						} catch(Exception e) {
							Notification.show("Apparaat wordt ergens gebruikt. Kan niet worden verwijderd.")
						}
					}
				})
			}
		})
	 
		layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
		addComponent(panel)
	}
}


class ExtendedComboBoxwithCheck extends HorizontalLayout {

	Button plusButton, removeButton, saveButton
	ComboBox comboBox
	List children
	Object object

	ExtendedComboBoxwithCheck(Object object, List list, Boolean saveIcon, Boolean removeIcon, Boolean plusIcon) {

		setSpacing(true)
		setStyleName("extendedcombobox")
		children = []

		this.object = object

		comboBox = new ComboBox(caption, list)
		comboBox.setValue(object == null ? "" : object.name)
		addComponent(comboBox)

		if (saveIcon) {
			saveButton = new Button()
			addComponent(saveButton)
			saveButton.setDescription("Klik hier om dit apparaat weg te halen")
			saveButton.setIcon(new ThemeResource("check.jpg"))
			saveButton.setStyleName(Reindeer.BUTTON_LINK)
		}
		
		if (removeIcon) {
			removeButton = new Button()
			addComponent(removeButton)
			removeButton.setDescription("Klik hier om dit apparaat weg te halen")
			removeButton.setIcon(new ThemeResource("Red-X.svg"))
			removeButton.setStyleName(Reindeer.BUTTON_LINK)
		}

		if (plusIcon) {
			plusButton = new Button()
			addComponent(plusButton)
			plusButton.setDescription("Klik hier om een materiaal aan dit apparaat toe te voegen")
			plusButton.setIcon(new ThemeResource("plus.png"))
			plusButton.setStyleName(Reindeer.BUTTON_LINK)
		}
	}
}