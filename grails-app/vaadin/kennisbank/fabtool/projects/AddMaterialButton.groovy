package kennisbank.fabtool.projects

import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Label
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Notification
import com.vaadin.server.ThemeResource
import com.vaadin.ui.themes.Reindeer


class AddMaterialButton extends HorizontalLayout {

	AddMaterialButton(String title) {

		setSpacing(true)

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
				Notification.show("Button pressed")
			}
			})

	}
}