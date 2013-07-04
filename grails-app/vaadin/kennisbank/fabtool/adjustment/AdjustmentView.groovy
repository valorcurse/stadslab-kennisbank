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
		
		def settings
		AddMaterialButton rootAddMaterialButton
		Checkout checkout


		TreeTable settingsTreeTable = new TreeTable("Voeg hier de apparaten, materialen en bijbehorend instellingen die gebruikt zijn")
		layout.addComponent(settingsTreeTable) // Column 0, Row 3 to Column 1, Row 3
		settingsTreeTable.setWidth("100%")
		settingsTreeTable.setPageLength(0)

		HierarchicalContainer materialsContainer = new HierarchicalContainer()
		materialsContainer.addContainerProperty("Apparatuur", Component.class, "")
		//materialsContainer.addContainerProperty("Materiaal", Component.class, "")
		//materialsContainer.addContainerProperty("Instellingen", TextField.class, "")
		settingsTreeTable.setContainerDataSource(materialsContainer)
		//settingsTreeTable.setColumnExpandRatio("Apparatuur", 0.6)
		//settingsTreeTable.setColumnExpandRatio("Materiaal", 0.4)

		//print Material.list()*.toString()
		

		rootAddMaterialButton = new AddMaterialButton("Voeg een Materiaal toe")
		Item rootItem = materialsContainer.addItem(rootAddMaterialButton)
		rootItem.getItemProperty("Apparatuur").setValue(rootAddMaterialButton)
		settingsTreeTable.setCollapsed(rootAddMaterialButton, false)



		for (material in Material.list()) {
			ExtendedText materialTextField = new ExtendedText(material, true, true, true)
			//Notification.show("hallo")
			Item materialItem = materialsContainer.addItem(material)
			materialItem.getItemProperty("Apparatuur").setValue(materialTextField)
			materialsContainer.setParent(material, rootAddMaterialButton)	

			for (materialType in material.materialTypes) {
				ExtendedText materialtypeTextField = new ExtendedText(materialType, true, false, true)
				Item materialTypeItem = materialsContainer.addItem(materialType)
				materialTypeItem.getItemProperty("Apparatuur").setValue(materialtypeTextField)
				//materialTypeItem.getItemProperty("Materiaal").setValue(new Label("<b>" + materialType.key.name + "</b>", ContentMode.HTML))
				materialsContainer.setParent(materialType, material)	
				settingsTreeTable.setCollapsed(material, false)
			}

			materialTextField.saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent equipmentButtonEvent) {
					
					if (materialTextField.textField.getValue() != "") 
					{
						Material.withTransaction 
						{
							new Material(name: materialTextField.textField.getValue()).save(failOnError: true)
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
					if (materialTextField.textField.getValue() != "") 
					{
						ExtendedText materialtypesTextField = new ExtendedText(null, true, true, true)
						Item materialTItem = materialsContainer.addItem(materialtypesTextField)
						materialTItem.getItemProperty("Apparatuur").setValue(materialtypesTextField)
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
						settingsTreeTable.removeItem(child)
					}

					materialsContainer.removeItem(materialTextField.object)
					settingsTreeTable.removeItem(materialTextField.object)
				}
			})
		
		}

		rootAddMaterialButton.button.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				def settingsList = settings
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
				equipmentItem.getItemProperty("Apparatuur").setValue(materialTextField)
				materialsContainer.setParent(materialTextField, rootAddMaterialButton)
				settingsTreeTable.setCollapsed(rootAddMaterialButton, false)
				
			
				
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
							ExtendedText materialtypesTextField = new ExtendedText(null, true, true, true)
							Item materialTItem = materialsContainer.addItem(materialtypesTextField)
							materialTItem.getItemProperty("Apparatuur").setValue(materialtypesTextField)
							materialsContainer.setParent(materialtypesTextField, materialTextField)
							settingsTreeTable.setCollapsed(materialTextField, false)
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
							settingsTreeTable.removeItem(child)
						}

						materialsContainer.removeItem(materialTextField)
						settingsTreeTable.removeItem(materialTextField)
					}
				})

				// ---------------------------- Choose equipment ----------------------------
			

				
			}
		})

		// --------------------------------- Made By Label ---------------------------------

		

	
	
	/*private void comboBoxContent(Equipment equipment, List settingsList,
								TreeTable settingsTreeTable, ExtendedComboBox equipmentComboBox
								) {
		
		IndexedContainer materialsContainer = settingsTreeTable.getContainerDataSource()

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
		Item materialItem = materialsContainer.addItem(materialComboBox)
		materialItem.getItemProperty("Apparatuur").setValue(materialComboBox)
		materialsContainer.setParent(materialComboBox, equipmentComboBox)
		settingsTreeTable.setCollapsed(equipmentComboBox, false)
		
		// ---------------------------- Choose material ----------------------------
		materialComboBox.comboBox.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent comboEvent) {
				for (child in materialComboBox.children) {
					materialsContainer.removeItem(child)					
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
								
								Item settingItem = materialsContainer.addItem(newSettingLabel)
								materialComboBox.children.add(newSettingLabel)
								settingItem.getItemProperty("Materiaal").setValue(newSettingLabel)

								TextField valueTextField = new TextField()
								materialComboBox.children.add(valueTextField)
								valueTextField.setWidth("99%")
								valueTextField.setCaption(settingUsed.name)

								settingItem.getItemProperty("Instellingen").setValue(valueTextField)
			 
								materialsContainer.setParent(newSettingLabel, materialComboBox)
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
								Item item = materialsContainer.getItem(child)
								item.getItemProperty("Instellingen").getValue().setValue("")
							}
						}
					}
				})
			}
		})*/
	 
		layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
		addComponent(panel)
	}
}


//-----------------------------------Equipment-------------------------------------------
		/*HorizontalLayout equipmentLayout = new HorizontalLayout()
		layout.addComponent(equipmentLayout)
		equipmentLayout.setSpacing(true)
		equipmentLayout.setMargin(true)
		
		TextField equipmentTextField = new TextField()
		equipmentLayout.addComponent(equipmentTextField)
		equipmentLayout.setComponentAlignment(equipmentTextField, Alignment.BOTTOM_LEFT)
		equipmentTextField.setInputPrompt("Apparaat")
		
		Button addequipmentButton = new Button("Toevoegen", new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event) 
			{
				if (equipmentTextField.getValue() != "") 
				{
					Equipment.withTransaction 
					{

						SettingType passes = new SettingType(name: "Passes")
						SettingType power = new SettingType(name: "Power")
						SettingType dikte = new SettingType(name: "Dikte")

						new Equipment(name: equipmentTextField.getValue()).addToSettingTypes(passes)
						.addToSettingTypes(power)
						.addToSettingTypes(dikte).save(failOnError: true)

						Notification.show(equipmentTextField.getValue() + " is toegevoegd")
					}
				}
				else
				{
					Notification.show("Vak is leeg. Vul apperaat toe ")
				}
			}
		})
		equipmentLayout.addComponent(addequipmentButton)
		equipmentLayout.setComponentAlignment(addequipmentButton, Alignment.TOP_LEFT)
		
		//---------------------------------material------------------------------------------------
		HorizontalLayout materialLayout = new HorizontalLayout()
		layout.addComponent(materialLayout)
		materialLayout.setSpacing(true)
		materialLayout.setMargin(true)
		
		TextField materialTextField = new TextField()
		materialLayout.addComponent(materialTextField)
		materialLayout.setComponentAlignment(materialTextField, Alignment.BOTTOM_LEFT)
		materialTextField.setInputPrompt("Materiaal")
		
		Button addmaterialButton = new Button("Toevoegen", new ClickListener() 
		{
			@Override
			public void buttonClick(ClickEvent event) 
			{

				if (materialTextField.getValue() != "") 
				{
					Material.withTransaction 
					{
						new Material(name: materialTextField.getValue()).save(failOnError: true)
						Notification.show(materialTextField.getValue() + " is toegevoegd")
					}
				}
				else
				{
					Notification.show("Vak is leeg. Vul materiaal in")
				}
			}			
		})
		
		materialLayout.addComponent(addmaterialButton)
		materialLayout.setComponentAlignment(addmaterialButton, Alignment.MIDDLE_LEFT)

		//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Edit>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		Panel editMaterialPanel = new Panel("materiaal aanpassen")
		editMaterialPanel.setPrimaryStyleName("embedded-panel")
		editMaterialPanel.addStyleName(Runo.PANEL_LIGHT)
		layout.addComponent(editMaterialPanel)

		HorizontalLayout editLayout = new HorizontalLayout()
		layout.addComponent(editLayout)
		editLayout.setSpacing(true)

		VerticalLayout equipmentListlayout = new VerticalLayout()
		editLayout.addComponent(equipmentListlayout)
		equipmentListlayout.setMargin(true)
		equipmentListlayout.setSpacing(true)

		//----------------------------equipmentList-------------------------------------------
		List<Equipment> eq = Equipment.list()	
		ListSelect equipmentList = new ListSelect("Apparaat",eq.name);
        equipmentListlayout.addComponent(equipmentList)
        equipmentList.setRows(6); // perfect length in out case
        equipmentList.setNullSelectionAllowed(false); // user can not 'unselect'
        equipmentList.setImmediate(true); // send the change to the server at once

        VerticalLayout materialListlayout = new VerticalLayout()
		editLayout.addComponent(materialListlayout)
		materialListlayout.setMargin(true)
		materialListlayout.setSpacing(true)

		equipmentList.addValueChangeListener(new ValueChangeListener() 
		{
            @Override
            public void valueChange(final ValueChangeEvent equipment) 
            {
                
                final String selectedEquipment = String.valueOf(equipment.getProperty().getValue());
                Notification.show(selectedEquipment);
               
                //------------------------------materialList-------------------------------------
                materialListlayout.removeAllComponents()

				List<Material> mat = Material.list()	
				ListSelect materialList = new ListSelect("Materiaal",mat.name);
		        materialListlayout.addComponent(materialList)
		        materialList.setRows(6); // perfect length in out case
		        materialList.setNullSelectionAllowed(false); // user can not 'unselect'
		        materialList.setImmediate(true); // send the change to the server at once
		        materialList.setVisible(true)

		        VerticalLayout materialEditLayout = new VerticalLayout()
				editLayout.addComponent(materialEditLayout)
				materialEditLayout.setMargin(true)
				materialEditLayout.setSpacing(true)

		        materialList.addValueChangeListener(new ValueChangeListener() 
		        {
		            @Override
		            public void valueChange(final ValueChangeEvent event) 
		            {
		                final String selectedMaterial = String.valueOf(event.getProperty().getValue());
		                Notification.show(selectedMaterial);

		                materialEditLayout.removeAllComponents()
		                
		                //-------------------------------material title----------------------------------------
		                HorizontalLayout materialtitleLayout = new HorizontalLayout()
		                materialEditLayout.addComponent(materialtitleLayout)
		                materialEditLayout.setStyleName("searchTextLayout")
						//materialtitleLayout.setMargin(true)
						//materialtitleLayout.setSpacing(true)

		                Label materialTitleLabel = new Label("<u><b>"+selectedMaterial+"<b><u>", ContentMode.HTML)
						materialTitleLabel.setWidth("100%")
						materialtitleLayout.addComponent(materialTitleLabel)
						materialEditLayout.setVisible(true)

						Button deletematerialButton = new Button()
						materialtitleLayout.addComponent(deletematerialButton)
						deletematerialButton.setDescription("Verwijder materiaal")
						deletematerialButton.setIcon(new ThemeResource("Red-X.svg"))
						deletematerialButton.setStyleName(Reindeer.BUTTON_LINK)
						deletematerialButton.addStyleName("deletematerialButton")

						deletematerialButton.addClickListener(new Button.ClickListener() {
							@Override
							public void buttonClick(ClickEvent equipmentButtonEvent) 
							{
									
								Material.findByName(selectedMaterial).delete(flush: true)
							}
						})
					

						//-----------------existing materialtype checkbox------------------------------------------

						def materialtypeList = []
						def material = Material.findByName(selectedMaterial)

						for (def materialtype : material.materialTypes.toList()) {
							materialtypeList.add(new CheckBox(materialtype.name))
						}

						for (materialtype in materialtypeList) {
							materialEditLayout.addComponent(materialtype)
						}

						Button deletematerialtypeButton = new Button("Verwijderen", new ClickListener() {
						@Override
						public void buttonClick(ClickEvent deletematerialtype) {

							for (component in materialtypeList) {
								if (component.booleanValue()) {
								print MaterialType.findByName(component.getCaption())
								materialEditLayout.removeComponent(component)
								MaterialType.withTransaction {
									List<MaterialType> chec = MaterialType.list()

									 MaterialType.findByName(component.getCaption()).delete(flush: true)
									/*for (MaterialType che : chec) {
										print "dit "+che.name
										if(che.name == component.getCaption())
										{

											print "halllllloooooooooooooooooooooooooooo"
											MaterialType.removeItem(name: MaterialType.findByName(che.name));
										}
									}
									//print "heeeee " +chec.name
									//print component.getCaption()
									//MaterialType.remove(component.getCaption());
									//MaterialType.removeItem(name: MaterialType.findByName(component.getCaption()));
									//print component.getCaption()
									//new MaterialType(name: MaterialType.findByName(component.getCaption())).delete(failOnError: true)
									//new MaterialType(name: MaterialType.findByName(component.getCaption())).delete(failOnError: true)
									//MaterialType.delete(MaterialType.findByName(component.getCaption()))

								}
								
								}
							}	
							
						}			
						})
		
						materialEditLayout.addComponent(deletematerialtypeButton)
						materialEditLayout.setComponentAlignment(deletematerialtypeButton, Alignment.MIDDLE_LEFT)

						//---------------------------add materialType-----------------------------------
						HorizontalLayout addmaterialTextLayout = new HorizontalLayout()
						materialEditLayout.addComponent(addmaterialTextLayout)
						addmaterialTextLayout.setStyleName("searchTextLayout")

						TextField addmaterialTextField = new TextField("nieuw")
						addmaterialTextLayout.addComponent(addmaterialTextField)
						addmaterialTextField.setInputPrompt("Materialtype")

						Button addTextQueryButton = new Button()
						addmaterialTextLayout.addComponent(addTextQueryButton)
						addTextQueryButton.setDescription("Klik hier om een tekst query term to te voegen")
						addTextQueryButton.setIcon(new ThemeResource("plus.png"))
						addTextQueryButton.setStyleName(Reindeer.BUTTON_LINK)
						addTextQueryButton.addStyleName("addTextQueryButton")

						addTextQueryButton.addClickListener(new Button.ClickListener() {
							@Override
							public void buttonClick(ClickEvent equipmentButtonEvent) 
							{
									
								if (addmaterialTextField.getValue() != "") 
								{
							
									MaterialType.withTransaction 
									{
										def equipment2
										
										MaterialType nee = new MaterialType(name: addmaterialTextField.getValue(), material: Material.findByName(selectedMaterial)).save(failOnError: true)
										
										equipment2 = Equipment.findByName(selectedEquipment)

										equipment2.addToMaterialTypes(nee).save(failOnError: true)
										//selectedMaterial.addToMaterialTypes(nee)
										//new Equipment(name: selectedEquipment).addToMaterialTypes(nee).save(failOnError: true)
										//print addmaterialTextField.getValue() + " is toegevoegd aan " + selectedMaterial + "in" + selectedEquipment

									}
								}
								else
								{
									Notification.show("Vak is leeg. Voeg materiaaltype toe")
								}
							}
						})
						//-----------------------------------------------------------
					}
		        });
		}	
    });*/