package kennisbank.checkin

import com.vaadin.server.ThemeResource
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.*

class ExtendedComboBox extends HorizontalLayout {

	Button plusButton, removeButton
	ComboBox comboBox
	List children

	ExtendedComboBox(List list, Boolean parent) {

		setSpacing(true)
		setStyleName("extendedcombobox")

		children = []

		comboBox = new ComboBox(null, list)
		addComponent(comboBox)

		if (parent) {
			removeButton = new Button()
			addComponent(removeButton)
			removeButton.setDescription("Klik hier om dit apparaat weg te halen")
			removeButton.setIcon(new ThemeResource("Red-X.svg"))
			removeButton.setStyleName(Reindeer.BUTTON_LINK)

			plusButton = new Button()
			addComponent(plusButton)
			plusButton.setDescription("Klik hier om een materiaal aan dit apparaat toe te voegen")
			plusButton.setIcon(new ThemeResource("plus.png"))
			plusButton.setStyleName(Reindeer.BUTTON_LINK)
		}
	}
}