package kennisbank.checkin

import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Label
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Notification
import com.vaadin.ui.Window
import com.vaadin.ui.ComboBox
import com.vaadin.ui.TextField
import com.vaadin.server.ThemeResource
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.TreeTable
import com.vaadin.ui.Label
import com.vaadin.data.Property.ValueChangeListener
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.data.Container
import com.vaadin.data.Item
import kennisbank.equipment.*

class AddMaterialButton extends HorizontalLayout {

	AddMaterialButton(Equipment equipment, TreeTable treeTable, CheckoutInfo checkoutInfo) {

		boolean settingsAdded = false

		Container container = treeTable.getContainerDataSource()

		setSpacing(true)

		def materials = []
		def info = [][]

		for (def material : Material.list()) {
			materials.add(material.name)
		}

		info.add(equipment)

		Label label = new Label(equipment.name)
		addComponent(label)

		Button addMaterial = new Button()
		addComponent(addMaterial)
		addMaterial.setId("Material-plus-icon")
		addMaterial.setDescription("Klik hier om een materiaal aan dit apparaat toe te voegen")
		addMaterial.setIcon(new ThemeResource("plus.png"))
		addMaterial.setStyleName(Reindeer.BUTTON_LINK)
		
		addMaterial.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {

				// ComboBox to choose kind of material used
				ComboBox materialComboBox = new ComboBox(null, materials)
				materialComboBox.setNullSelectionAllowed(false)
				materialComboBox.setImmediate(true)

				// Add the ComboBox to the table
				Item materialItem = container.addItem(materialComboBox)
				materialItem.getItemProperty("Apparatuur").setValue(materialComboBox)
				container.setParent(materialComboBox, equipment)
				treeTable.setCollapsed(equipment, false)
				
				materialComboBox.addValueChangeListener(new ValueChangeListener() {
					@Override
					public void valueChange(final ValueChangeEvent comboEvent) {


						def material = Material.findByName(comboEvent.getProperty().getValue())
						def materialTypes = []
						// info[0].add(material)

						for (def materialType : material.materialTypes) {
							materialTypes.add(materialType.name)
						}

						// ComboBox to choose the type of the material
						ComboBox materialTypeComboBox = new ComboBox(null, materialTypes)
						materialTypeComboBox.setNullSelectionAllowed(false)
						materialTypeComboBox.setImmediate(true)
						
						//  Add the ComboBox to the table
						materialItem.getItemProperty("Materiaal").setValue(materialTypeComboBox)

						treeTable.setCollapsed(materialComboBox, false)

						materialTypeComboBox.addValueChangeListener(new ValueChangeListener() {
							@Override
							public void valueChange(final ValueChangeEvent comboTypeEvent) { 

								print treeTable.getChildren(materialComboBox)

								if (!settingsAdded) {
									for (def setting : equipment.settings.asList()) {

										Label newSettingLabel = new Label(setting.name)
										Item settingItem = container.addItem(newSettingLabel)
										settingItem.getItemProperty("Materiaal").setValue(newSettingLabel)

										TextField valueTextField = new TextField()
										valueTextField.setWidth("99%")

										settingItem.getItemProperty("Instellingen").setValue(valueTextField)
										container.setParent(newSettingLabel, materialComboBox)

										treeTable.setChildrenAllowed(newSettingLabel, false)
									}
									settingsAdded = true
								}
								else {
									for (def child : treeTable.getChildren(materialComboBox)) {
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
}