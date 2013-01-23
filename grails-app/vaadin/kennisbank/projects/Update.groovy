package kennisbank.projects

import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.Runo

class Update extends VerticalLayout {
	
	Update(String m) {
	}
	
	void addMessage(String message) {
		Panel panel = new Panel()
		panel.setPrimaryStyleName("updates-panel")
		panel.setStyleName(Runo.PANEL_LIGHT)
		VerticalLayout layout = new VerticalLayout()
		layout.addComponent(new Label(message))
		panel.setContent(layout)
		addComponent(panel)
	}
	
}
