package kennisbank.fabtool.adjustment

import com.google.gwt.user.client.Command
import com.google.gwt.user.client.ui.TabBar.Tab
import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeListener
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.event.FieldEvents.TextChangeEvent
import com.vaadin.event.FieldEvents.TextChangeListener
import com.vaadin.server.ClassResource
import com.vaadin.server.ExternalResource
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
//import com.vaadin.ui.TabSheet.SelectedTabChangeEvents
import com.vaadin.server.ThemeResource
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.*
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.MenuItem

import java.awt.MenuItem
import java.io.OutputStream;
import java.sql.PreparedStatement
import kennisbank.*
import kennisbank.fabtool.home.HomeView

import kennisbank.equipment.*
//import kennisbank.equipment.Material
import kennisbank.utils.*
import kennisbank.fabtool.projects.*





class AdjustmentView extends VerticalLayout{
	
	

	String uriFragment

	
	//TabSheet ATabs
	String tabName() {
		return uriFragment

	}

	AdjustmentView() {


		//Main layout
		VerticalLayout view = new VerticalLayout()
		//view.setSizeFull() // Set layout to cover the whole screen

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
	
		//-----------------------------------Equipment-------------------------------------------
		HorizontalLayout equipmentLayout = new HorizontalLayout()
		layout.addComponent(equipmentLayout)
		equipmentLayout.setSpacing(true)
		
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
		                Label materialTitleLabel = new Label(selectedMaterial, ContentMode.HTML)
						materialTitleLabel.setWidth("100%")
						materialEditLayout.addComponent(materialTitleLabel)
						materialEditLayout.setVisible(true)
					

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
									print "heeeee " +chec.name
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

										new MaterialType(name: addmaterialTextField.getValue(), material: Material.findByName(selectedMaterial)).save(failOnError: true)
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
    });
        
	layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
	addComponent(panel)
	}
}