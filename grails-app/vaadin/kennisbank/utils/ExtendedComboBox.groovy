package kennisbank.utils

import com.vaadin.server.ThemeResource
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.*

class ExtendedComboBox extends HorizontalLayout {

	Button plusButton, removeButton
	ComboBox comboBox
	List children

	ExtendedComboBox(String caption, List list, Boolean removeIcon, Boolean plusIcon) {

		setSpacing(true)
		setStyleName("extendedcombobox") ? (caption == null) : setStyleName("extendedcomboboxcaption")

		children = []

		comboBox = new ComboBox(caption, list)
		addComponent(comboBox)

		if (removeIcon) {
			removeButton = new Button()
			addComponent(removeButton)
			removeButton.setDescription("Klik hier om dit apparaat weg te halen")
			removeButton.setIcon(new ThemeResource("Red-X.svg"))
			removeButton.setStyleName(Reindeer.BUTTON_LINK)
		}

		if (plusIcon) {
			plusButton = new Button()
			addComponent(plusButton)
			plusButton.setDescription("Klik hier om een materiaal aan dit apparaat toe te voegen")
			plusButton.setIcon(new ThemeResource("plus.png"))
			plusButton.setStyleName(Reindeer.BUTTON_LINK)
		}
	}
}