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
import com.vaadin.data.Container
import com.vaadin.data.Item
import kennisbank.equipment.Material
import kennisbank.equipment.Equipment

class AddMaterialButton extends HorizontalLayout {

	AddMaterialButton(Equipment equipment, TreeTable treeTable) {

		Container container = treeTable.getContainerDataSource()

		setSpacing(true)

		def laserCutterSettings = ["Power", "Speed", "Passes"]

		def materials = []

		for (def material : Material.list()) {
			materials.add(material.name)
		}

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

				HorizontalLayout chooseMaterialLayout = new HorizontalLayout()

				ComboBox materialComboBox = new ComboBox(null, materials)
				chooseMaterialLayout.addComponent(materialComboBox)
				materialComboBox.setNullSelectionAllowed(false);

				Button acceptMaterial = new Button()
				chooseMaterialLayout.addComponent(acceptMaterial)
				acceptMaterial.setId("check-button")
				acceptMaterial.setDescription("Klik hier om dit materiaal te accepteren")
				acceptMaterial.setIcon(new ThemeResource("check.jpg"))
				acceptMaterial.setStyleName(Reindeer.BUTTON_LINK)

				acceptMaterial.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event2) {

						Equipment.withTransaction {
							equipment.addToMaterials(new Material(name: materialComboBox.getValue()))

							if (equipment.save()) {
								chooseMaterialLayout.removeAllComponents()
								chooseMaterialLayout.addComponent(new Label(materialComboBox.getValue()))
								materials.remove(materials.indexOf(materialComboBox.getValue()))
							}
						}

						for (def setting : equipment.settings.asList()) {
							Label newSettingLabel = new Label(setting.name)
							Item item = container.addItem(newSettingLabel)
							item.getItemProperty("Apparatuur").setValue(newSettingLabel)
							
							TextField valueTextField = new TextField()
							valueTextField.setWidth("99%")
							
							item.getItemProperty("Instellingen").setValue(valueTextField)
							container.setParent(newSettingLabel, chooseMaterialLayout)
							
							treeTable.setCollapsed(chooseMaterialLayout, false)
							treeTable.setChildrenAllowed(newSettingLabel, false)
						}
					}
					})

				Item item = container.addItem(chooseMaterialLayout)
				item.getItemProperty("Apparatuur").setValue(chooseMaterialLayout)
				container.setParent(chooseMaterialLayout, equipment)
				treeTable.setCollapsed(equipment, false)

			}
			})
}
}