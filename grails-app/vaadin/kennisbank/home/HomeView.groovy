package kennisbank.home

import com.vaadin.server.ThemeResource
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent
import com.vaadin.ui.TabSheet.Tab
import com.vaadin.navigator.Navigator
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import kennisbank.projects.*
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.themes.Reindeer


class HomeView extends VerticalLayout {

	String uriFragment

	String tabName() {
		return uriFragment
	}

	public HomeView() {
		uriFragment = "#!"
		UI.getCurrent().getPage().getCurrent().setLocation(uriFragment)
	}

}
