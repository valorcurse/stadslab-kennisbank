package kennisbank.fabtool

import com.vaadin.ui.*
import com.vaadin.ui.MenuBar.Command
import com.vaadin.ui.MenuBar.MenuItem
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent
import com.vaadin.ui.TabSheet.Tab
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.themes.ChameleonTheme
import com.vaadin.ui.themes.Runo
import com.vaadin.navigator.*
import com.vaadin.server.Resource
import com.vaadin.server.VaadinRequest
import com.vaadin.annotations.*
import com.vaadin.ui.HorizontalSplitPanel
import com.vaadin.server.ThemeResource
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.server.Page
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import kennisbank.auth.*

import com.vaadin.grails.*
import java.lang.SecurityException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Class where all other components and views are drawn upon.
 *
 * @author Marcelo Dias Avelino
 */

@Theme("HRO")
@PreserveOnRefresh
// TODO - Error is thrown when changing title
@Title("Kennisbank")
class MyUI extends UI {

	/**
	 * {@link kennisbank.MainView} where every component is drawn upon.
	 */
	MainView mainView

	/**
	 * Class which handles URI's.
	 */
	Navigator navigator
	
	/**
	* Initializes the UI.
	*/

	private static SecurityService security = (SecurityService) Grails.get(SecurityService)

	@Override
	public void init(VaadinRequest request) {

		mainView = new MainView()
		
		navigator = new Navigator(this, this) // Create a navigator used to handle URI fragments

		navigator.addView("", mainView) // Add the main view

		def state =  navigator.getState()
		// If a URI is specified, navigate there
		if (state) {
			navigator.navigateTo(state) // Go to the main view
		}
		// Else navigate to mainview
		else {
			navigator.navigateTo("") // Go to the main view
		}
	}
}