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

	AddMaterialButton(Equipment equipment, TreeTable treeTable) {

		Container container = treeTable.getContainerDataSource()

		setSpacing(true)

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
				Item materialItem = container.addItem(chooseMaterialLayout)
				materialItem.getItemProperty("Apparatuur").setValue(chooseMaterialLayout)
				container.setParent(chooseMaterialLayout, equipment)
				treeTable.setCollapsed(equipment, false)

				ComboBox materialComboBox = new ComboBox(null, materials)
				chooseMaterialLayout.addComponent(materialComboBox)
				materialComboBox.setNullSelectionAllowed(false)
				materialComboBox.setImmediate(true)
				materialComboBox.addValueChangeListener(new ValueChangeListener() {
					@Override
					public void valueChange(final ValueChangeEvent comboEvent) {
						final String valueString = String.valueOf(comboEvent.getProperty().getValue());

						def material = Material.findByName(comboEvent.getProperty().getValue())
						def materialTypes = []

						for (def materialType : material.materialTypes) {
							materialTypes.add(materialType.name)
						}

						ComboBox materialTypeComboBox = new ComboBox(null, materialTypes)
						materialTypeComboBox.setNullSelectionAllowed(false)
						materialTypeComboBox.setImmediate(true)
						materialItem.getItemProperty("Materiaal").setValue(materialTypeComboBox)

						treeTable.setCollapsed(chooseMaterialLayout, false)


						materialTypeComboBox.addValueChangeListener(new ValueChangeListener() {
							@Override
							public void valueChange(final ValueChangeEvent comboTypeEvent) { 

								for (def setting : equipment.settings.asList()) {
									Label newSettingLabel = new Label(setting.name)
									Item settingItem = container.addItem(newSettingLabel)
									settingItem.getItemProperty("Materiaal").setValue(newSettingLabel)

									TextField valueTextField = new TextField()
									valueTextField.setWidth("99%")

									settingItem.getItemProperty("Instellingen").setValue(valueTextField)
									container.setParent(newSettingLabel, chooseMaterialLayout)

									treeTable.setChildrenAllowed(newSettingLabel, false)
								}
							}
							})
					}
					})

Button acceptMaterial = new Button()
chooseMaterialLayout.addComponent(acceptMaterial)
acceptMaterial.setId("check-button")
acceptMaterial.setDescription("Klik hier om dit materiaal te accepteren")
acceptMaterial.setIcon(new ThemeResource("check.jpg"))
acceptMaterial.setStyleName(Reindeer.BUTTON_LINK)

acceptMaterial.addClickListener(new Button.ClickListener() {
	public void buttonClick(ClickEvent event2) {

		chooseMaterialLayout.removeAllComponents()
		chooseMaterialLayout.addComponent(new Label(materialComboBox.getValue()))

		materials.remove(materials.indexOf(materialComboBox.getValue()))

		for (def setting : equipment.settings.asList()) {
			Label newSettingLabel = new Label(setting.name)
			Item settingItem = container.addItem(newSettingLabel)
			settingItem.getItemProperty("Apparatuur").setValue(newSettingLabel)

			TextField valueTextField = new TextField()
			valueTextField.setWidth("99%")

			settingItem.getItemProperty("Instellingen").setValue(valueTextField)
			container.setParent(newSettingLabel, chooseMaterialLayout)

			treeTable.setCollapsed(chooseMaterialLayout, false)
			treeTable.setChildrenAllowed(newSettingLabel, false)
		}
	}
	})



}
})
}
}