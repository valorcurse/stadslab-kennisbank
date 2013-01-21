package kennisbank.projects

import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.Runo

class Update extends Panel {
	
	Update(String m) {
		setSizeFull()
		setPrimaryStyleName("updates-panel")
		setStyleName(Runo.PANEL_LIGHT)
		
		VerticalLayout layout = new VerticalLayout()
		Label message = new Label(m)
		layout.addComponent(message)
		
		setContent(layout)
	}
}
