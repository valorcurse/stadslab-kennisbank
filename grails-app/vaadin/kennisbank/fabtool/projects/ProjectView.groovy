package kennisbank.fabtool.projects

import java.rmi.server.UID;
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.*
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import kennisbank.checkin.Checkout
import kennisbank.ProjectMemberService
import kennisbank.ProjectService
import kennisbank.project.*
import kennisbank.fabtool.*
import com.vaadin.ui.TabSheet.Tab
import com.vaadin.ui.Upload.Receiver
import com.vaadin.ui.Upload.StartedEvent
import com.vaadin.ui.Upload.SucceededEvent
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.themes.Reindeer
import com.vaadin.server.ExternalResource
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.server.ThemeResource
import com.vaadin.server.FileResource
import com.vaadin.server.Sizeable.Unit
import com.vaadin.data.util.HierarchicalContainer
import com.vaadin.data.Item

class ProjectView extends VerticalLayout {

	String uriFragment, oldPicturePath
	Checkout project
	def hiddenComponents

	String tabName() {
		return uriFragment
	}

	public ProjectView(Checkout project) {

		this.project = Checkout.findByUniqueID(project.uniqueID)
		hiddenComponents = []
		
		uriFragment = "#!/project/" + project.uniqueID
		UI.getCurrent().getPage().getCurrent().setLocation(uriFragment)

		setSizeFull()
		setMargin(true)

		Panel viewPanel = GenerateView()

		addComponent(viewPanel)
		setComponentAlignment(viewPanel, Alignment.TOP_CENTER)
	}

	private Panel GenerateView() {
		

		return panel
	}

	void revealHiddenComponents() {
		for (c in hiddenComponents) {
			c.setVisible(true)
		}
	}

	void hideRevealedComponents() {
		for (c in hiddenComponents) {
			c.setVisible(false)
		}
	}
}