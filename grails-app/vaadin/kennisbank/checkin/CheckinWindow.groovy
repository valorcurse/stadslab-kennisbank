package kennisbank.checkin

import com.vaadin.ui.CheckBox
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.NativeButton
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Layout
import com.vaadin.ui.Window
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.Alignment
import com.vaadin.ui.Notification
import com.vaadin.ui.TabSheet
import com.vaadin.shared.ui.label.ContentMode
import org.springframework.context.MessageSource
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.vaadin.server.UserError

/**
 * Abstract class for checkins.
 *
 * @author Marcelo Dias Avelino
 */
abstract class CheckinWindow extends Window {

	/**
	 * Boolean to set if the checkin was successful.
	 */
	protected Boolean checkinSuccessful = false

	/**
	 * Constructor of the CheckinWindow class.
	 */
	CheckinWindow() {	
		setCaption("Check-in")
		setPrimaryStyleName("check-in")
		setModal(true)
		setStyleName(Reindeer.WINDOW_LIGHT)

		VerticalLayout windowLayout = new VerticalLayout()
		setContent(windowLayout)
		windowLayout.setMargin(true)

		windowLayout.addComponent(windowContent())
	}

	/**
	 * Method that must be overriden to add content to the window.
	 *
	 * @return Layout with the generated layout.
	 */
	protected abstract Layout windowContent();
}
