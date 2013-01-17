package kennisbank.projects

import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.VerticalLayout

class Update extends Panel {
	
	Update(String m) {
		setSizeFull()
		
		VerticalLayout layout = new VerticalLayout()
		Label message = new Label(m)
		layout.addComponent(message)
		
		setContent(layout)
	}
}
