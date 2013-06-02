package kennisbank.checkin

import com.vaadin.ui.Button
import com.vaadin.ui.HorizontalLayout
import com.vaadin.server.ThemeResource
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.Label

class AddMaterialButton extends HorizontalLayout {

	Button button

	AddMaterialButton(String caption) {

		setSpacing(true)
		setStyleName("addmaterialbutton")

		Label label = new Label(caption)
		addComponent(label)

		button = new Button()
		addComponent(button)
		button.setDescription("Klik hier om een apparaat toe te voegen")
		button.setIcon(new ThemeResource("plus.png"))
		button.setStyleName(Reindeer.BUTTON_LINK)
	}
}