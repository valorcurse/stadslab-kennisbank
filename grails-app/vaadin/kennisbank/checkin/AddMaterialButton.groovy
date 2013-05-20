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

	Button button

	AddMaterialButton(String caption) {

		setSpacing(true)

		Label label = new Label(caption)
		addComponent(label)

		button = new Button()
		addComponent(button)
		button.setId("Material-plus-icon")
		button.setDescription("Klik hier om een materiaal aan dit apparaat toe te voegen")
		button.setIcon(new ThemeResource("plus.png"))
		button.setStyleName(Reindeer.BUTTON_LINK)
	}
}