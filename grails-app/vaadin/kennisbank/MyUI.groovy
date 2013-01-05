package kennisbank

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
import com.vaadin.annotations.Theme
import com.vaadin.ui.HorizontalSplitPanel
import com.vaadin.server.ThemeResource
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.server.Page
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import kennisbank.project.Member
import kennisbank.project.ProjectView

/**
 *
 *
 * @author
 */

//Theme being used, default is Reindeer
//@Theme("hogeschool")

class MyUI extends UI {

	@Override
	public void init(VaadinRequest request) {

		Navigator navigator = new Navigator(this, this) // Create a navigator used to handle URI fragments

		navigator.addView("", new MainView()) // Add the main view

		navigator.navigateTo("") // Go to the main view

	}
}