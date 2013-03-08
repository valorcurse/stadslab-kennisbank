package kennisbank.fabtool.projects

import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.Runo

class Update extends VerticalLayout {
	
	Update(String m) {
	}
	
	void addSystemMessage(String message) {
		Panel panel = new Panel()
		//panel.setPrimaryStyleName("updates-panel")
		panel.setStyleName(Runo.PANEL_LIGHT)
		VerticalLayout layout = new VerticalLayout()
		layout.setMargin(true)
		layout.setSpacing(true)
		
		def today = new Date()
		
		Label updateMessage = new Label("<b>" + today + "</b>" +
			"<br>" + message, Label.CONTENT_XHTML)
		layout.addComponent(updateMessage)
		panel.setContent(layout)
		addComponent(panel)
	}
	
	void addMessage(String message) {
		Panel panel = new Panel()
		//panel.setPrimaryStyleName("updates-panel")
		panel.setStyleName(Runo.PANEL_LIGHT)
		VerticalLayout layout = new VerticalLayout()
		layout.setMargin(true)
		layout.setSpacing(true)
		
		def today = new Date()
		
		Label updateMessage = new Label("<b>" + UI.getCurrent().getLoggedInUser().getUsername()+"</b> on <b>" + today + "</b>" +
			"<br>"+message, Label.CONTENT_XHTML)
		layout.addComponent(updateMessage)
		panel.setContent(layout)
		addComponent(panel)
	}
	
}
