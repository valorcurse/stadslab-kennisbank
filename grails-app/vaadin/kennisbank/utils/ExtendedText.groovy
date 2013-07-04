package kennisbank.utils

import com.vaadin.server.ThemeResource
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.*

class ExtendedText extends HorizontalLayout {

	Button plusButton, removeButton, saveButton
	ComboBox comboBox
	TextField textField
	Object object
	


	ExtendedText(Object object, Boolean removeIcon, Boolean plusIcon, Boolean saveIcon) {

		setStyleName("extendedcombobox")
		setSpacing(true)
		
		this.object = object

		textField = new TextField()
		textField.setValue(object == null ? "" : object.name)
		addComponent(textField)

		if (saveIcon) {
			saveButton = new Button()
			addComponent(saveButton)
			saveButton.setDescription("Klik hier om dit materiaal op te slaan")
			saveButton.setIcon(new ThemeResource("check.jpg"))
			saveButton.setStyleName(Reindeer.BUTTON_LINK)
		}

		if (removeIcon) {
			removeButton = new Button()
			addComponent(removeButton)
			removeButton.setDescription("Klik hier om dit materiaal weg te halen")
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