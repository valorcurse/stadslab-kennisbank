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

/*import com.vaadin.data.Property.ValueChangeListener
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.Button.ActionHandler
import com.vaadin.server.ThemeResource
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.*
import kennisbank.*
import kennisbank.fabtool.home.HomeView
import kennisbank.equipment.*
import kennisbank.utils.*
import kennisbank.fabtool.projects.*
import com.vaadin.shared.ui.label.ContentMode
*/
//import com.google.gwt.user.client.Command
//import com.google.gwt.user.client.ui.TabBar.Tab
//import com.vaadin.data.Property
//import com.vaadin.event.FieldEvents.TextChangeEvent
//import com.vaadin.event.FieldEvents.TextChangeListener
//import com.vaadin.server.ClassResource
//import com.vaadin.server.ExternalResource
//import java.io.OutputStream;
//import java.sql.PreparedStatement



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
		

		uriFragment = "#!/Aanpassingen"
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
		Panel addEquipmentPanel = new Panel("Voeg nieuwe apperaat of materiaal toe")
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


		TreeTable materialTreeTable = new TreeTable()
		treesLayout.addComponent(materialTreeTable)
		materialTreeTable.setWidth("350px")
		materialTreeTable.setPageLength(0)

		HierarchicalContainer materialsContainer = new HierarchicalContainer()
		materialsContainer.addContainerProperty("Materiaal", Component.class, "")
		
		materialTreeTable.setContainerDataSource(materialsContainer)

		rootAddMaterialButton = new AddMaterialButton("Voeg een Materiaal toe")
		Item rootItem = materialsContainer.addItem(rootAddMaterialButton)
		rootItem.getItemProperty("Materiaal").setValue(rootAddMaterialButton)
		materialTreeTable.setCollapsed(rootAddMaterialButton, false)

		for (material in Material.list()) {
			ExtendedText materialTextField = new ExtendedText(material, true, true, true)
			//Notification.show("hallo")
			Item materialItem = materialsContainer.addItem(material)
			materialItem.getItemProperty("Materiaal").setValue(materialTextField)
			materialsContainer.setParent(material, rootAddMaterialButton)	

			for (materialType in material.materialTypes) {
				ExtendedText materialtypeTextField = new ExtendedText(materialType, true, true, false)
				Item materialTypeItem = materialsContainer.addItem(materialType)
				materialTypeItem.getItemProperty("Materiaal").setValue(materialtypeTextField)
				//materialTypeItem.getItemProperty("Materiaal").setValue(new Label("<b>" + materialType.key.name + "</b>", ContentMode.HTML))
				materialsContainer.setParent(materialType, material)	
				materialTreeTable.setCollapsed(material, false)
				materialTreeTable.setChildrenAllowed(materialType, false)

				materialtypeTextField.removeButton.addClickListener(new Button.ClickListener() {
							@Override
							public void buttonClick(ClickEvent equipmendButtonEvent) {
								
								
							
								print "mamamamamam"
								
								
								materialsContainer.removeItem(materialtypeTextField.object)
								materialTreeTable.removeItem(materialtypeTextField.object)

								MaterialType.findByName(materialTextField.textField.getValue()).delete(flush: true)
							}
						})
			}

			materialTextField.saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent equipmentButtonEvent) {
					
					if (materialTextField.textField.getValue() != "") 
					{
						/*Material.withTransaction 
						{
							new Material(name: materialTextField.textField.getValue()).save(failOnError: true)
							Notification.show(materialTextField.textField.getValue() + " is toegevoegd")
						}	*/
					}
					else{

						Notification.show("Kies eerst een apparaat.")
					}

						
					}
				})

			

			materialTextField.plusButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent equipmentButtonEvent) {
					if (materialTextField.textField.getValue() != "") 
					{
						ExtendedText materialtypesTextField = new ExtendedText(null, true, true, true)
						Item materialTItem = materialsContainer.addItem(materialtypesTextField)
						materialTItem.getItemProperty("Materiaal").setValue(materialtypesTextField)
						materialsContainer.setParent(materialtypesTextField, materialTextField.object)

							
					}
					else{

						Notification.show("Kies eerst een apparaat.")
					}
				}
			})

		

			materialTextField.removeButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent equipmentButtonEvent) {
					def childrenToDelete = []
					for (child in materialsContainer.getChildren(materialTextField.object)) {
						childrenToDelete.add(child)
					}
					for (child in childrenToDelete) {
						materialsContainer.removeItem(child)
						materialTreeTable.removeItem(child)

						//MaterialType.findByName(child).delete(flush: true)
						//print child
					}
					//Material.removeItem(materialTextField.object)
					
					//Material.delete()
					//Material(name: materialTextField.textField.getValue()).delete(flush: true)
				
					
					
					materialsContainer.removeItem(materialTextField.object)
					materialTreeTable.removeItem(materialTextField.object)

					Material.findByName(materialTextField.textField.getValue()).delete(flush: true)
				}
			})
		
		}

		rootAddMaterialButton.button.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				//def settingsList = settings
				//Checkout checkout = checkout
				def materialComboBoxesToRemove = []
				def materialSettingsToRemove = []

 				// ComboBox to choose kind of material used
				ExtendedText materialTextField = new ExtendedText(null, true, true, true)
				//materialTextField.textField.setNullSelectionAllowed(false)
				materialTextField.textField.setImmediate(true)
				materialTextField.textField.setInputPrompt("Maak een materiaal aan")

				//TextField materialTextField = new TextField()
				//materialTextField.setInputPrompt("Maak een materiaal aan")

				// Add the ComboBox to the table
				Item equipmentItem = materialsContainer.addItem(materialTextField)
				equipmentItem.getItemProperty("Materiaal").setValue(materialTextField)
				materialsContainer.setParent(materialTextField, rootAddMaterialButton)
				materialTreeTable.setCollapsed(rootAddMaterialButton, false)
				
			
				
				// Remove previously added child components if equipment selection changed
				



				materialTextField.saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent equipmentButtonEvent) {
					
					if (materialTextField.textField.getValue() != "") 
					{
						Material.withTransaction 
						{
							Material newMaterial = new Material(name: materialTextField.textField.getValue()).save(failOnError: true)
							materialTextField.object = newMaterial
							Notification.show(materialTextField.textField.getValue() + " is toegevoegd")
						}	
					}
					else{

						Notification.show("Kies eerst een apparaat.")
					}

						
					}
				})
				materialTextField.plusButton.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent equipmentButtonEvent) {
						if (materialTextField.textField.getValue() != "" && materialTextField.object != null) {
							ExtendedText materialtypesTextField = new ExtendedText(null, true, true, false)
							Item materialTItem = materialsContainer.addItem(materialtypesTextField)
							materialTItem.getItemProperty("Materiaal").setValue(materialtypesTextField)
							materialsContainer.setParent(materialtypesTextField, materialTextField)
							materialTreeTable.setCollapsed(materialTextField, false)
							materialTreeTable.setChildrenAllowed(materialtypesTextField, false)



						}
						else {
							Notification.show("Materiaal moet eerst worden opgeslagen.")
						}
					}
				})

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

						materialsContainer.removeItem(materialTextField)
						materialTreeTable.removeItem(materialTextField)
					}
				})
			}
		})

		// --------------------------------- Other table ---------------------------------

		TreeTable equipmentTreeTable = new TreeTable()
		treesLayout.addComponent(equipmentTreeTable) // Column 0, Row 3 to Column 1, Row 3
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
			//Notification.show("hallo")
			Item equipmentItem = equipmentsContainer.addItem(equipment)
			equipmentItem.getItemProperty("Apparaat").setValue(equipmentTextField)
			equipmentsContainer.setParent(equipment, rootAddEquipmentButton)	
		
			//-----------------------------button to add new materials/ tree for extisting materials--------------------
			AddMaterialButton materialsButton
			materialsButton = new AddMaterialButton("Materialen")
			Item materialsButtonItem = equipmentsContainer.addItem(materialsButton)
			materialsButtonItem.getItemProperty("Apparaat").setValue(materialsButton)
			equipmentTreeTable.setCollapsed(rootAddEquipmentButton, false)
			equipmentsContainer.setParent(materialsButton, equipment)	 

			//--------------------------------------add new material--------------------------------------------------------
			materialsButton.button.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					ExtendedComboBoxwithCheck materialComboBox = new ExtendedComboBoxwithCheck(null, Material.list()*.name, true, true,true)
					materialComboBox.comboBox.setNullSelectionAllowed(false)
					materialComboBox.comboBox.setImmediate(true)
					materialComboBox.comboBox.setInputPrompt("Kies een material")
					
					Item materialItem = equipmentsContainer.addItem(materialComboBox)
					materialItem.getItemProperty("Apparaat").setValue(materialComboBox)
					equipmentsContainer.setParent(materialComboBox, materialsButton)
					equipmentTreeTable.setCollapsed(equipmentTextField, false)

					//-----------------------------add new materialtype to nw material------------------------------
					materialComboBox.plusButton.addClickListener(new Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent mtButtonEvent) {
							//if (equipmentTextField.textField.getValue() != "") 
							//{
								//ExtendedText materialtypesTextField = new ExtendedText(null, true, true, true)
									ExtendedComboBoxwithCheck materialTypeComboBox = new ExtendedComboBoxwithCheck(null, MaterialType.list()*.name, true, false,true)
									materialTypeComboBox.comboBox.setNullSelectionAllowed(false)
									materialTypeComboBox.comboBox.setImmediate(true)
									materialTypeComboBox.comboBox.setInputPrompt("Kies een materialtype")
									
									Item materialTypeItem = equipmentsContainer.addItem(materialTypeComboBox)
									materialTypeItem.getItemProperty("Apparaat").setValue(materialTypeComboBox)
									equipmentsContainer.setParent(materialTypeComboBox, materialComboBox)
									equipmentTreeTable.setCollapsed(equipmentTextField, false)

											
							
							//}
							//else{

							//	Notification.show("Kies eerst een apparaat.")
							//}
						}
					})	

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
					
					ExtendedText settingsTextField = new ExtendedText(null, true, false, true)
					Item settingsItem = equipmentsContainer.addItem(settingsTextField)
					settingsItem.getItemProperty("Apparaat").setValue(settingsTextField)
					equipmentsContainer.setParent(settingsTextField, settingsButton)
				}
			})
		
			//---------------------------placing existing settings-------------------------------------
			for (settingType in equipment.settingTypes) {
				

				ExtendedText settingsTextField = new ExtendedText(settingType, true, true, false)
				Item settingsTypeItem = equipmentsContainer.addItem(settingType)
				settingsTypeItem.getItemProperty("Apparaat").setValue(settingsTextField)
				equipmentsContainer.setParent(settingType, settingsButton)	
				equipmentTreeTable.setChildrenAllowed(settingType, false)

			}

			//--------------------------placing existing materials-------------------------------------------------
			for (material in equipment.materialTypes.groupBy { it.material.name }) {
				Material currentMaterial = Material.findByName(material.key)
				ExtendedComboBoxwithCheck materialComboBox = new ExtendedComboBoxwithCheck(currentMaterial, Material.list()*.name, false, true,true)
				materialComboBox.comboBox.setValue(material.key)
				materialComboBox.comboBox.setNullSelectionAllowed(false)

				Item materialItem = equipmentsContainer.addItem(currentMaterial)
				materialItem.getItemProperty("Apparaat").setValue(materialComboBox)
				equipmentsContainer.setParent(currentMaterial, materialsButton)	
				equipmentTreeTable.setCollapsed(equipment, false)

				//------------------------------adding new materialtype to existing material------------------------------------
				materialComboBox.plusButton.addClickListener(new Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent mtButtonEvent) {
							//if (equipmentTextField.textField.getValue() != "") 
							//{
								//ExtendedText materialtypesTextField = new ExtendedText(null, true, true, true)
									ExtendedComboBoxwithCheck materialTypeComboBox = new ExtendedComboBoxwithCheck(currentMaterial, MaterialType.list()*.name, true, true, false)
									materialTypeComboBox.comboBox.setNullSelectionAllowed(false)
									materialTypeComboBox.comboBox.setImmediate(true)
									materialTypeComboBox.comboBox.setInputPrompt("Kies een materialtype")
									
									Item materialTypeItem = equipmentsContainer.addItem(materialTypeComboBox)
									materialTypeItem.getItemProperty("Apparaat").setValue(materialTypeComboBox)
									equipmentsContainer.setParent(materialTypeComboBox, currentMaterial)
									equipmentTreeTable.setCollapsed(equipmentTextField, false)
									equipmentTreeTable.setChildrenAllowed(materialTypeComboBox, false)


											
							
							//}
							//else{

							//	Notification.show("Kies eerst een apparaat.")
							//}
						}
					})	

				//---------------------------------placing existing materialtype in existing material---------------------------------
				for(materialType in Material.findByName(material.key).materialTypes)

				{
					//MaterialType currentMaterialType = MaterialType.findByName(materialType.name)
					ExtendedComboBoxwithCheck materialTypeComboBox = new ExtendedComboBoxwithCheck(materialType, 
						MaterialType.list()*.name, true, true, false)
					materialTypeComboBox.comboBox.setNullSelectionAllowed(false)
					materialTypeComboBox.comboBox.setImmediate(true)
					materialTypeComboBox.comboBox.setValue(materialType.name)

					
					Item materialTypeItem = equipmentsContainer.addItem(materialTypeComboBox)
					materialTypeItem.getItemProperty("Apparaat").setValue(materialTypeComboBox)
					equipmentsContainer.setParent(materialTypeComboBox, currentMaterial)
					equipmentTreeTable.setCollapsed(equipmentTextField, false)
					equipmentTreeTable.setChildrenAllowed(materialTypeComboBox, false)

				}
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


				AddMaterialButton materialsButton
				materialsButton = new AddMaterialButton("Materialen")
				Item materialsButtonItem = equipmentsContainer.addItem(materialsButton)
				materialsButtonItem.getItemProperty("Apparaat").setValue(materialsButton)
				equipmentTreeTable.setCollapsed(rootAddEquipmentButton, false)
				equipmentsContainer.setParent(materialsButton, equipmentTextField)	

				materialsButton.button.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent materialevent) {
						//if (equipmentTextField.textField.getValue() != "") 
						//{
							//ExtendedText materialtypesTextField = new ExtendedText(null, true, true, true)
						 

						ExtendedComboBoxwithCheck materialComboBox = new ExtendedComboBoxwithCheck(null, Material.list()*.name, false, true,true)
						materialComboBox.comboBox.setNullSelectionAllowed(false)
						materialComboBox.comboBox.setImmediate(true)
						materialComboBox.comboBox.setInputPrompt("Kies een material")
						
						Item materialItem = equipmentsContainer.addItem(materialComboBox)
						materialItem.getItemProperty("Apparaat").setValue(materialComboBox)
						equipmentsContainer.setParent(materialComboBox, materialsButton)
						equipmentTreeTable.setCollapsed(equipmentTextField, false)

						//-----------------------------add new materialtype to nw material------------------------------
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
												
								
								//}
								//else{

								//	Notification.show("Kies eerst een apparaat.")
								//}
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