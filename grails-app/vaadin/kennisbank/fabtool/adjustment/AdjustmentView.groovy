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

		//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Add new equipment or material>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		Panel addEquipmentPanel = new Panel("Apparaten en materialen")
		addEquipmentPanel.setPrimaryStyleName("embedded-panel")
		addEquipmentPanel.addStyleName(Runo.PANEL_LIGHT)
		layout.addComponent(addEquipmentPanel)
		
		//def settings
		AddMaterialButton rootAddMaterialButton
		Checkout checkout

		HorizontalLayout treesLayout = new HorizontalLayout()
		layout.addComponent(treesLayout)
		treesLayout.setMargin(true)
		treesLayout.setSpacing(true)

		//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Material table>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		TreeTable materialTreeTable = new TreeTable()
		treesLayout.addComponent(materialTreeTable)
		materialTreeTable.setWidth("350px")
		materialTreeTable.setPageLength(0)
		materialTreeTable.setImmediate(true)

		HierarchicalContainer materialsContainer = new HierarchicalContainer()
		materialsContainer.addContainerProperty("Materiaal", Component.class, "")
		
		materialTreeTable.setContainerDataSource(materialsContainer)

		rootAddMaterialButton = new AddMaterialButton("Voeg een Materiaal toe")
		Item rootItem = materialsContainer.addItem(rootAddMaterialButton)
		rootItem.getItemProperty("Materiaal").setValue(rootAddMaterialButton)
		materialTreeTable.setCollapsed(rootAddMaterialButton, false)

		//---------------------existing material-------------------------------------------------
		for (material in Material.list()) {
			ExtendedText materialTextField = new ExtendedText(material, true, true, true)
			Item materialItem = materialsContainer.addItem(materialTextField)
			materialItem.getItemProperty("Materiaal").setValue(materialTextField)
			materialsContainer.setParent(materialTextField, rootAddMaterialButton)	

			//--------------------existing materialtype-------------------------------------------
			for (materialType in material.materialTypes) {
				ExtendedText materialTypeTextField = new ExtendedText(materialType, true, true, false)
				Item materialTypeItem = materialsContainer.addItem(materialTypeTextField)
				materialTypeItem.getItemProperty("Materiaal").setValue(materialTypeTextField)
				materialsContainer.setParent(materialTypeTextField, materialTextField)	
				materialTreeTable.setCollapsed(materialTextField, false)
				materialTreeTable.setChildrenAllowed(materialTypeTextField, false)

				materialTypeTextField.saveButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent equipmendButtonEvent) {
						if (materialTypeTextField.textField.getValue() != "") {
							Material.withTransaction {
								if (materialTypeTextField.object == null) {
									MaterialType newMaterialType = new MaterialType(name: materialTypeTextField.textField.getValue())
									materialTextField.object.addToMaterialTypes(newMaterialType).save()
									materialTypeTextField.object = newMaterialType
								}
								else {
									if (materialTypeTextField.textField.getValue() != materialTypeTextField.object?.name) {
										materialTypeTextField.object.name = materialTypeTextField.textField.getValue()
										materialTypeTextField.object = materialTypeTextField.object.merge()
										materialTypeTextField.object.save()
									}
								}
								Notification.show(materialTextField.object.name + " is opgeslagen")
							}
						}
					}
				})

				materialTypeTextField.removeButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent equipmendButtonEvent) {
						materialsContainer.removeItem(materialTypeTextField)
						materialTreeTable.removeItem(materialTypeTextField)
						if (materialTypeTextField.object) {
							materialTypeTextField.object.delete()
							Notification.show(materialTextField.textField.getValue() + " is verwijderd")
						}
					}
				})
			}

			//-----------------------------replace materialname--------------------------------
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
						ExtendedText materialTypeTextField = new ExtendedText(null, true, true, false)
						Item materialTItem = materialsContainer.addItem(materialTypeTextField)
						materialTItem.getItemProperty("Materiaal").setValue(materialTypeTextField)
						materialsContainer.setParent(materialTypeTextField, materialTextField)
						materialTreeTable.setCollapsed(materialTextField, false)
						materialTreeTable.setChildrenAllowed(materialTypeTextField, false)

						materialTypeTextField.saveButton.addClickListener(new Button.ClickListener() {
							@Override
							public void buttonClick(ClickEvent materialTypeEvent) {
								if (materialTypeTextField.textField.getValue() != "") {
									Material.withTransaction {
										if (materialTypeTextField.object == null) {
											MaterialType newMaterialType = new MaterialType(name: materialTypeTextField.textField.getValue())
											materialTextField.object.addToMaterialTypes(newMaterialType).save()
											materialTypeTextField.object = newMaterialType
										}
										else {
											if (materialTypeTextField.textField.getValue() != materialTypeTextField.object?.name) {
												materialTypeTextField.object.name = materialTypeTextField.textField.getValue()
												materialTypeTextField.object = materialTypeTextField.object.merge()
												materialTypeTextField.object.save()
											}
										}
										Notification.show(materialTextField.object.name + " is opgeslagen")
									}
								}
							}
						})
					}
					else{
						Notification.show("Kies eerst een apparaat.")
					}
				}
			})

		
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
						materialTextField.object.delete()
						Notification.show(materialTextField.textField.getValue() + " is verwijderd")
					}

					materialsContainer.removeItem(materialTextField)
					materialTreeTable.removeItem(materialTextField)
				}
			})
		
		}
		// ------------------------button to add new material and materialtypes-------------------------------
		rootAddMaterialButton.button.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				
				def materialComboBoxesToRemove = []
				def materialSettingsToRemove = []

 				// ComboBox to choose kind of material used
				ExtendedText materialTextField = new ExtendedText(null, true, true, true)
				//materialTextField.textField.setNullSelectionAllowed(false)
				materialTextField.textField.setImmediate(true)
				materialTextField.textField.setInputPrompt("Maak een materiaal aan")

			

				// Add the ComboBox to the table
				Item equipmentItem = materialsContainer.addItem(materialTextField)
				equipmentItem.getItemProperty("Materiaal").setValue(materialTextField)
				materialsContainer.setParent(materialTextField, rootAddMaterialButton)
				materialTreeTable.setCollapsed(rootAddMaterialButton, false)
				

				//------------------------save material------------------------------------
				materialTextField.saveButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent equipmentButtonEvent) {
						if (materialTextField.textField.getValue() != "") {
							Material.withTransaction {
								Material newMaterial = new Material(name: materialTextField.textField.getValue()).save(failOnError: true)
								materialTextField.object = newMaterial
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
					public void buttonClick(ClickEvent equipmentButtonEvent) {
						if (materialTextField.textField.getValue() != "") {
							ExtendedText materialTypeTextField = new ExtendedText(null, true, true, false)
							Item materialTItem = materialsContainer.addItem(materialTypeTextField)
							materialTItem.getItemProperty("Materiaal").setValue(materialTypeTextField)
							materialsContainer.setParent(materialTypeTextField, materialTextField)
							materialTreeTable.setCollapsed(materialTextField, false)
							materialTreeTable.setChildrenAllowed(materialTypeTextField, false)

							materialTypeTextField.saveButton.addClickListener(new Button.ClickListener() {
								@Override
								public void buttonClick(ClickEvent materialTypeEvent) {
									if (materialTypeTextField.textField.getValue() != "") {
										Material.withTransaction {
											if (materialTypeTextField.object == null) {
												MaterialType newMaterialType = new MaterialType(name: materialTypeTextField.textField.getValue())
												materialTextField.object.addToMaterialTypes(newMaterialType).save()
												materialTypeTextField.object = newMaterialType
											}
											else {
												if (materialTypeTextField.textField.getValue() != materialTypeTextField.object?.name) {
													materialTypeTextField.object.name = materialTypeTextField.textField.getValue()
													materialTypeTextField.object = materialTypeTextField.object.merge()
													materialTypeTextField.object.save()
												}
											}
											Notification.show(materialTypeTextField.object.name + " is opgeslagen")
										}
									}
								}
							})
						}
						else {
							Notification.show("Kies eerst een naam.")
						}
					}
				})
				
				//----------------------remove new material------------------------------
				materialTextField.removeButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent equipmentButtonEvent) {
						
						if(materialTextField.textField.getValue() != "" && materialTextField.object != null){
							print "giruhgier"
							def childrenToDelete = []
							for (child in materialsContainer.getChildren(materialTextField)) {
								childrenToDelete.add(child)
							}
							
							for (child in childrenToDelete) {
								materialsContainer.removeItem(child)
								materialTreeTable.removeItem(child)

								print child.textField.getValue()
								MaterialType.findByName(child.textField.getValue()).delete(flush: true)
								//child.delete(flush: true)
								
							}
							
							materialsContainer.removeItem(materialTextField)
							materialTreeTable.removeItem(materialTextField)

							//Material.findByName(materialTextField.textField.getValue()).delete(flush: true)

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

		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Equipment table >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

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


		//-----------------------------Existing Equipment-----------------------------------------------
		for (equipment in Equipment.list()) {
			ExtendedText equipmentTextField = new ExtendedText(equipment, true, true, false)
			Item equipmentItem = equipmentsContainer.addItem(equipment)
			equipmentItem.getItemProperty("Apparaat").setValue(equipmentTextField)
			equipmentsContainer.setParent(equipment, rootAddEquipmentButton)	
		
			//-----------------------------button to add new materials/ tree for extisting materials--------------------
			AddMaterialButton addMaterialsButton
			addMaterialsButton = new AddMaterialButton("Materialen")
			
			Item addMaterialsButtonItem = equipmentsContainer.addItem(addMaterialsButton)
			addMaterialsButtonItem.getItemProperty("Apparaat").setValue(addMaterialsButton)
			equipmentTreeTable.setCollapsed(rootAddEquipmentButton, false)
			equipmentsContainer.setParent(addMaterialsButton, equipment)	 


			def checkDisabled = { component, container ->
				if (container.hasChildren(component)) {
					component.comboBox.setEnabled(false)
				}
				else {
					component.comboBox.setEnabled(true)
				}
			}

			def addMaterialType = { parent, value->
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

								equipment.addToMaterialTypes(newMaterialType)
								equipment = equipment.merge()
								print equipment
								if (equipment.save()) {
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
							equipmentsContainer.removeItem(materialTypeComboBox)
							equipmentTreeTable.removeItem(materialTypeComboBox)
							
							MaterialType.withTransaction {
								def currentEquipment = Equipment.findById(equipment.id)
								currentEquipment.removeFromMaterialTypes(MaterialType.findById(materialTypeComboBox.object.id))
								currentEquipment.save()
							}


						} else {
							equipmentsContainer.removeItem(materialTypeComboBox)
							equipmentTreeTable.removeItem(materialTypeComboBox)
						}
						checkDisabled(parent, equipmentsContainer)
					}
				})
			}

			def removeChildren = { component, treeTable ->
				def childrenToDelete = []
				def container = treeTable.getContainerDataSource()
				for (child in container.getChildren(component)) {
					container.removeItem(child)
					childrenToDelete.add(child)
				}
				for (child in childrenToDelete) {
					treeTable.removeItem(child)
				}
				treeTable.removeItem(component)
			}

			//--------------------------------------add new material--------------------------------------------------------
			addMaterialsButton.button.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent addMaterialEvent) {

					ExtendedComboBoxwithCheck materialComboBox = new ExtendedComboBoxwithCheck(null, Material.list()*.name, false, true, true)
					materialComboBox.comboBox.setNullSelectionAllowed(false)
					materialComboBox.comboBox.setImmediate(true)
					materialComboBox.comboBox.setInputPrompt("Kies een material")
					
					Item materialItem = equipmentsContainer.addItem(materialComboBox)
					materialItem.getItemProperty("Apparaat").setValue(materialComboBox)
					equipmentsContainer.setParent(materialComboBox, addMaterialsButton)
					equipmentTreeTable.setCollapsed(equipmentTextField, false)

					materialComboBox.comboBox.addValueChangeListener(new ValueChangeListener() {
						@Override
						public void valueChange(final ValueChangeEvent comboEvent) {
							materialComboBox.object = Material.findByName(materialComboBox.comboBox.getValue())
							addMaterialType(materialComboBox, null)
							checkDisabled(materialComboBox, equipmentsContainer)
						}
					})

					materialComboBox.plusButton.addClickListener(new Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent addMaterialTypeEvent) {
							addMaterialType(materialComboBox, null)
							checkDisabled(materialComboBox, equipmentsContainer)
						}
					})

					materialComboBox.removeButton.addClickListener(new Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent addMaterialTypeEvent) {
							for (child in equipmentsContainer.getChildren(materialComboBox)) {
								if (child.object) {
									equipmentTextField.object.removeFromMaterialTypes(MaterialType.findById(child.object.id))
								}
							}
							removeChildren(materialComboBox, equipmentTreeTable)
						}
					})
				}
			})
			
			//-------------------------Remove Equipment and children-----------------------------------------
			equipmentTextField.removeButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent equipmentdeleteEvent) {
					
					equipmentsContainer.removeItem(equipmentTextField.object)
					equipmentTreeTable.removeItem(equipmentTextField.object)

					Equipment.findByName(equipmentTextField.textField.getValue()).delete(flush: true)

				}
			})	
			
			//-----------------------add new setting/tree for existig settings-----------------------------
			AddMaterialButton settingsButton
			settingsButton = new AddMaterialButton("Settings")
			Item settingsButtonItem = equipmentsContainer.addItem(settingsButton)
			settingsButtonItem.getItemProperty("Apparaat").setValue(settingsButton)
			equipmentTreeTable.setCollapsed(rootAddEquipmentButton, false)
			equipmentsContainer.setParent(settingsButton, equipment)

			//---------------------add new setting------------------------------------------------
			settingsButton.button.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					
					ExtendedText settingsTextField = new ExtendedText(null, true, true, false)
					Item settingsItem = equipmentsContainer.addItem(settingsTextField)
					settingsItem.getItemProperty("Apparaat").setValue(settingsTextField)
					equipmentsContainer.setParent(settingsTextField, settingsButton)

					//-----------------save new setting-------------------------------------------------
					settingsTextField.saveButton.addClickListener(new Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent settingremoveEvent) {
							
							if (settingsTextField.textField.getValue() != "") 
							{
								SettingType.withTransaction 
								{
									
									SettingType newSetting = new SettingType(name: settingsTextField.textField.getValue())//.save(failOnError: true)
									equipment.addToSettingTypes(newSetting)
									if(equipment.save(failOnError: true)){
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

					//-------------------------------delete new setting---------------------------------------
					settingsTextField.removeButton.addClickListener(new Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent settingremoveEvent) {
							
							
							if (settingsTextField.textField.getValue() != "" && settingsTextField.object != null)
							{
								
								equipmentsContainer.removeItem(settingsTextField)
								equipmentTreeTable.removeItem(settingsTextField)
								SettingType.findByName(settingsTextField.textField.getValue()).delete(flush: true)

							}
							else{
								
								equipmentsContainer.removeItem(settingsTextField)
								equipmentTreeTable.removeItem(settingsTextField)


							}
							//SettingType.removeItem(settingsTextField)
							//MaterialType.findByName(materialTextField.textField.getValue()).delete(flush: true)
						}
					})
				}
			})
		
			//---------------------------placing existing settings-------------------------------------
			for (settingType in equipment.settingTypes) {

				ExtendedText settingsTextField = new ExtendedText(settingType, true, true, false)
				Item settingsTypeItem = equipmentsContainer.addItem(settingType)
				settingsTypeItem.getItemProperty("Apparaat").setValue(settingsTextField)
				equipmentsContainer.setParent(settingType, settingsButton)	
				equipmentTreeTable.setChildrenAllowed(settingType, false)

				//--------------------replace existing setting--------------------------------------
				settingsTextField.saveButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent settingremoveEvent) {
					}
				})

				//-------------------------------remove existing setting-----------------------------------------
				settingsTextField.removeButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent settingremoveEvent) {
						print settingsTextField.textField.getValue()
						equipmentsContainer.removeItem(settingType)
						equipmentTreeTable.removeItem(settingType)
						
						SettingType.findByName(settingsTextField.textField.getValue()).delete(flush: true)
					}
				})				

			}

			//--------------------------placing existing materials-------------------------------------------------
			for (material in equipment.materialTypes.groupBy { it.material.name }) {
				Material currentMaterial = Material.findByName(material.key)
				ExtendedComboBoxwithCheck materialComboBox = new ExtendedComboBoxwithCheck(currentMaterial, Material.list()*.name, false, true,true)
				materialComboBox.comboBox.setValue(material.key)
				materialComboBox.comboBox.setNullSelectionAllowed(false)

				Item materialItem = equipmentsContainer.addItem(materialComboBox)
				materialItem.getItemProperty("Apparaat").setValue(materialComboBox)
				equipmentsContainer.setParent(materialComboBox, addMaterialsButton)	
				equipmentTreeTable.setCollapsed(equipment, false)

				materialComboBox.comboBox.addValueChangeListener(new ValueChangeListener() {
					@Override
					public void valueChange(final ValueChangeEvent comboEvent) {
						materialComboBox.object = Material.findByName(materialComboBox.comboBox.getValue())
						addMaterialType(materialComboBox, null)
						checkDisabled(materialComboBox, equipmentsContainer)
					}
				})

				materialComboBox.plusButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent addMaterialTypeEvent) {
						addMaterialType(materialComboBox, null)
						checkDisabled(materialComboBox, equipmentsContainer)
					}
				})

				materialComboBox.removeButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent addMaterialTypeEvent) {
						for (child in equipmentsContainer.getChildren(materialComboBox)) {
							if (child.object) {
								equipmentTextField.object.removeFromMaterialTypes(MaterialType.findById(child.object.id))
							}
						}
						removeChildren(materialComboBox, equipmentTreeTable)
					}
				})

				//---------------------------------placing existing materialtype in existing material---------------------------------
				for (materialType in equipment.materialTypes.findAll { it.material.id == currentMaterial.id }) {
					addMaterialType(materialComboBox, materialType)
					// ExtendedComboBoxwithCheck materialTypeComboBox = new ExtendedComboBoxwithCheck(materialType, 
					// 	currentMaterial.materialTypes*.name, true, true, false)

					// materialTypeComboBox.comboBox.setNullSelectionAllowed(false)
					// materialTypeComboBox.comboBox.setImmediate(true)
					// materialTypeComboBox.comboBox.setValue(materialType.name)

					// Item materialTypeItem = equipmentsContainer.addItem(materialTypeComboBox)
					// materialTypeItem.getItemProperty("Apparaat").setValue(materialTypeComboBox)
					// equipmentsContainer.setParent(materialTypeComboBox, materialComboBox)
					// equipmentTreeTable.setCollapsed(equipmentTextField, false)
					// equipmentTreeTable.setChildrenAllowed(materialTypeComboBox, false)

				}

				checkDisabled(materialComboBox, equipmentsContainer)
			}
		}

		//-----------------------------button to add new equipment & childs---------------------------------------
		rootAddEquipmentButton.button.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				//def settingsList = settings
				def materialComboBoxesToRemove = []
				def materialSettingsToRemove = []

 				// ComboBox to choose kind of material used
				ExtendedText equipmentTextField = new ExtendedText(null, true, true, false)
				equipmentTextField.textField.setImmediate(true)
				equipmentTextField.textField.setInputPrompt("Maak een apparaat aan")

				// Add the ComboBox to the table
				Item equipmentItem = equipmentsContainer.addItem(equipmentTextField)
				equipmentItem.getItemProperty("Apparaat").setValue(equipmentTextField)
				equipmentsContainer.setParent(equipmentTextField, rootAddEquipmentButton)
				equipmentTreeTable.setCollapsed(rootAddEquipmentButton, false)

				//---------------------------Equipmentbutton functions----------------------------------------------------
				equipmentTextField.saveButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent settingsaveEvent) {
						if (equipmentTextField.textField.getValue() != "") 
						{
							Equipment.withTransaction 
							{
								Equipment newEquipment = new Equipment(name: equipmentTextField.textField.getValue()).save(failOnError: true)
																	
								equipmentTextField.object = newEquipment
								
								//equipment.addToMaterialTypes(nee).save(failOnError: true)
								Notification.show(equipmentTextField.textField.getValue() + " is toegevoegd")

								//-----------------------add new material/tree for existig materials-----------------------------
								def equipment

								equipment = Equipment.findByName(equipmentTextField.textField.getValue())
								

								AddMaterialButton addMaterialsButton
								addMaterialsButton = new AddMaterialButton("Materialen")
								Item addMaterialsButtonItem = equipmentsContainer.addItem(addMaterialsButton)
								addMaterialsButtonItem.getItemProperty("Apparaat").setValue(addMaterialsButton)
								equipmentTreeTable.setCollapsed(rootAddEquipmentButton, false)
								equipmentsContainer.setParent(addMaterialsButton, equipmentTextField)	

								addMaterialsButton.button.addClickListener(new Button.ClickListener() {
									@Override
									public void buttonClick(ClickEvent materialevent) {
										//if (equipmentTextField.texzzztField.getValue() != "") 
										//{
											//ExtendedText materialtypesTextField = new ExtendedText(null, true, true, true)
										 

										ExtendedComboBoxwithCheck materialComboBox = new ExtendedComboBoxwithCheck(null, Material.list()*.name, false, true,true)
										materialComboBox.comboBox.setNullSelectionAllowed(false)
										materialComboBox.comboBox.setImmediate(true)
										materialComboBox.comboBox.setInputPrompt("Kies een material")
										
										Item materialItem = equipmentsContainer.addItem(materialComboBox)
										materialItem.getItemProperty("Apparaat").setValue(materialComboBox)
										equipmentsContainer.setParent(materialComboBox, addMaterialsButton)
										equipmentTreeTable.setCollapsed(equipmentTextField, false)

										//-----------------------------add new materialtype to nw material------------------------------
										
											def removeChildren = {
												def childrenToDelete = []
												print "first step"
												print materialComboBox.children
												for (child in materialComboBox.children) {
													print "fdffdsfdfdfdsfd"
													equipmentsContainer.removeItem(child)
													childrenToDelete.add(child)
													for (secondChild in child.children) {
														equipmentsContainer.removeItem(secondChild)
													}
												}
												for (child in childrenToDelete) {
													child.children.clear()
												}
												materialComboBox.children.clear()
											}

										//-----------------------add new materialtype------------------------------------
										materialComboBox.plusButton.addClickListener(new Button.ClickListener() {
											@Override
											public void buttonClick(ClickEvent materialTypeButtonEvent) {
												//if (equipmentTextField.textField.getValue() != "") 
												//{
													//ExtendedText materialtypesTextField = new ExtendedText(null, true, true, true)
												ExtendedComboBoxwithCheck materialTypeComboBox = new ExtendedComboBoxwithCheck(null, MaterialType.list()*.name, true, true, false)
												materialTypeComboBox.comboBox.setNullSelectionAllowed(false)
												materialTypeComboBox.comboBox.setImmediate(true)
												materialTypeComboBox.comboBox.setInputPrompt("Kies een materialtype")
												
												Item materialTypeItem = equipmentsContainer.addItem(materialTypeComboBox)
												materialTypeItem.getItemProperty("Apparaat").setValue(materialTypeComboBox)
												equipmentsContainer.setParent(materialTypeComboBox, materialComboBox)
												equipmentTreeTable.setCollapsed(equipmentTextField, false)
												equipmentTreeTable.setChildrenAllowed(materialTypeComboBox, false)

											
													
												//----------------------------save new materialtype-----------------------------
												materialTypeComboBox.saveButton.addClickListener(new Button.ClickListener() {
													@Override
													public void buttonClick(ClickEvent materialTypedeleteEvent) {
														
														if (materialTypeComboBox.comboBox.getValue() != null) 
														{
															MaterialType.withTransaction 
															{
																//MaterialType newMaterial = new MaterialType(name: materialTypeComboBox.comboBox.getValue(), material: Material.findByName(materialComboBox.comboBox.getValue()))
																def newMaterial

																newMaterial = MaterialType.findByName(materialTypeComboBox.comboBox.getValue())
																equipment.addToMaterialTypes(newMaterial)
																
																if(equipment.merge()){
																	
																	materialTypeComboBox.object = newMaterial
																}
																Notification.show("works.")
															}	
														}
														else{

															Notification.show("Kies eerst een type.")
														}
													}
												})	

												//---------------------remove new materialtype---------------------------
												materialTypeComboBox.removeButton.addClickListener(new Button.ClickListener() {
													@Override
													public void buttonClick(ClickEvent materialTypedeleteEvent) {
														
														if (materialTypeComboBox.comboBox.getValue() != null && materialTypeComboBox.object != null)
														{
															
															equipmentsContainer.removeItem(materialTypeComboBox)
															equipmentTreeTable.removeItem(materialTypeComboBox)
															MaterialType.withTransaction 
															{
																print MaterialType.findByName(materialTypeComboBox.comboBox.getValue())
																//MaterialType newMaterial = new MaterialType(name: materialTypeComboBox.comboBox.getValue(), material: Material.findByName(materialComboBox.comboBox.getValue()))
																MaterialType mt = MaterialType.findByName(settingsTextField.textField.getValue())//.delete(flush: true)
																equipment.removeFromMaterialTypes(mt)
															}
															//SettingType.findByName(settingsTextField.textField.getValue()).delete(flush: true)

														}
														else{
															
															equipmentsContainer.removeItem(materialTypeComboBox)
															equipmentTreeTable.removeItem(materialTypeComboBox)


														}

													}
												})			
												
												
												/*materialComboBox.removeButton.addClickListener(new Button.ClickListener() {
												@Override
												public void buttonClick(ClickEvent materialTypedeleteEvent) {
													print "hihihihihihih"
													/*def childrenToDelete = []
													for (child in equipmentsContainer.getChildren(materialComboBox.object)) {
														childrenToDelete.add(child)
													}
													
													for (child in childrenToDelete) {
														equipmentsContainer.removeItem(child)
														equipmentTreeTable.removeItem(child)

														print "1 "child.textField.getValue()
														print "2 "child
														//MaterialType.findByName(child.textField.getValue()).delete(flush: true)
														//child.delete(flush: true)
														
													}


													removeChildren()
													equipmentsContainer.removeItem(materialComboBox)
													equipmentTreeTable.removeItem(materialComboBox)
													
													
												}
											})*/
										
											}
										})				
										
										//------------------------------delete material and child---------------------------
										
										materialComboBox.removeButton.addClickListener(new Button.ClickListener() {
											@Override
											public void buttonClick(ClickEvent materialTypedeleteEvent) {
												print "hihihihihihih"
												/*def childrenToDelete = []
												for (child in equipmentsContainer.getChildren(materialComboBox.object)) {
													childrenToDelete.add(child)
												}
												
												for (child in childrenToDelete) {
													equipmentsContainer.removeItem(child)
													equipmentTreeTable.removeItem(child)

													print "1 "child.textField.getValue()
													print "2 "child
													//MaterialType.findByName(child.textField.getValue()).delete(flush: true)
													//child.delete(flush: true)
													
												}*/


												removeChildren()
												equipmentsContainer.removeItem(materialComboBox)
												equipmentTreeTable.removeItem(materialComboBox)
												
												
											}
										})	
										//}
										//else{

										//	Notification.show("Kies eerst een apparaat.")
										//}
									}
								})

								
								AddMaterialButton settingsButton
								settingsButton = new AddMaterialButton("Settings")
								Item settingsButtonItem = equipmentsContainer.addItem(settingsButton)
								settingsButtonItem.getItemProperty("Apparaat").setValue(settingsButton)
								equipmentTreeTable.setCollapsed(rootAddEquipmentButton, false)
								equipmentsContainer.setParent(settingsButton, equipmentTextField)

								//---------------------add new setting------------------------------------------------
								settingsButton.button.addClickListener(new Button.ClickListener() {
									@Override
									public void buttonClick(ClickEvent settingsevent) {
										
										ExtendedText settingsTextField = new ExtendedText(null, true, true, false)
										Item settingsItem = equipmentsContainer.addItem(settingsTextField)
										settingsItem.getItemProperty("Apparaat").setValue(settingsTextField)
										equipmentsContainer.setParent(settingsTextField, settingsButton)
										equipmentTreeTable.setChildrenAllowed(settingsTextField, false)

										
										//-----------------------------save new setting--------------------------------
										settingsTextField.saveButton.addClickListener(new Button.ClickListener() {
											@Override
											public void buttonClick(ClickEvent settingremoveEvent) {
												
												if (settingsTextField.textField.getValue() != "") 
												{
													SettingType.withTransaction 
													{
														
														SettingType newSetting = new SettingType(name: settingsTextField.textField.getValue())//.save(failOnError: true)
														equipment.addToSettingTypes(newSetting)
														if(equipment.save(failOnError: true)){
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

										//-------------------------remove new setting-------------------------------------
										settingsTextField.removeButton.addClickListener(new Button.ClickListener() {
											@Override
											public void buttonClick(ClickEvent settingremoveEvent) {
												
												
												if (settingsTextField.textField.getValue() != "" && settingsTextField.object != null)
												{
													
													equipmentsContainer.removeItem(settingsTextField)
													equipmentTreeTable.removeItem(settingsTextField)
													SettingType.findByName(settingsTextField.textField.getValue()).delete(flush: true)

												}
												else{
													
													equipmentsContainer.removeItem(settingsTextField)
													equipmentTreeTable.removeItem(settingsTextField)


												}
												//SettingType.removeItem(settingsTextField)
												//MaterialType.findByName(materialTextField.textField.getValue()).delete(flush: true)
											}
										})
									}
								})
								

								//-------------remove equipment and die child buttons---------------------------------------
								equipmentTextField.removeButton.addClickListener(new Button.ClickListener() {
									@Override
									public void buttonClick(ClickEvent equipmentremoveEvent) {
										

										if (equipmentTextField.textField.getValue() != "" && equipmentTextField.object != null)
										{
											Notification.show("verwijderen uit database.")
											equipmentsContainer.removeItem(settingsButton)
											equipmentTreeTable.removeItem(settingsButton)
											equipmentsContainer.removeItem(addMaterialsButton)
											equipmentTreeTable.removeItem(addMaterialsButton)
											equipmentsContainer.removeItem(equipmentTextField)
											equipmentTreeTable.removeItem(equipmentTextField)
											Equipment.findByName(equipmentTextField.textField.getValue()).delete(flush: true)

										}
										else{
											
											Notification.show("verwijderen.")
											equipmentsContainer.removeItem(equipmentTextField)
											equipmentTreeTable.removeItem(equipmentTextField)


										}
									}
								})


								//---------
							}	
						}
						else{

							Notification.show("Kies eerst een apparaat.")
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