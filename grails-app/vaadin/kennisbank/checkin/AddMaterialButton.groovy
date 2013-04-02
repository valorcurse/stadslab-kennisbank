package kennisbank.checkin

import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Label
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Notification
import com.vaadin.server.ThemeResource
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.TreeTable
import com.vaadin.ui.Label
import com.vaadin.data.Container
import com.vaadin.data.Item


class AddMaterialButton extends HorizontalLayout {

	AddMaterialButton(String title, Container container) {

		setSpacing(true)

		def laserCutterSettings = ["Power", "Speed", "Passes"]


		Label label = new Label(title)
		addComponent(label)
		Button addMaterial = new Button()
		addComponent(addMaterial)
		addMaterial.setId("Material-plus-icon")
		addMaterial.setDescription("Klik hier om een materiaal aan deze apparaat toe te voegen")
		addMaterial.setIcon(new ThemeResource("plus.png"))
		addMaterial.setStyleName(Reindeer.BUTTON_LINK)
		addMaterial.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				Label caption = new Label("Hout")

				Item item = container.addItem(caption)
				item.getItemProperty("Apparatuur").setValue(caption)
				container.setParent(caption, title)

				for (def setting : laserCutterSettings) {
					Label settingCaption = new Label(setting)

					Item settingItem = container.addItem(settingCaption)
					container.setChildrenAllowed(settingCaption
						, false)
					settingItem.getItemProperty("Apparatuur").setValue(settingCaption)
					container.setParent(settingCaption, caption)
				}
			}
			})

	}
}