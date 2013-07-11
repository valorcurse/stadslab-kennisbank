package kennisbank.fabtool.projects

import com.vaadin.server.ExternalResource
import com.vaadin.event.LayoutEvents.LayoutClickListener
import com.vaadin.event.LayoutEvents.LayoutClickEvent
import com.vaadin.ui.*
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.TabSheet.Tab
import groovy.transform.InheritConstructors
import com.vaadin.ui.themes.Reindeer
import com.vaadin.server.FileResource
import kennisbank.*
import kennisbank.checkin.Checkout

/**
 * Provide a graphical link to a {@link kennisbank.checkin.Checkout}. <br>
 * This component is used to open a new tab where all the information of the <b>Checkout</b> is linked with.
 *
 * @author Marcelo Dias Avelino
 */

class ProjectLink extends VerticalLayout implements LayoutClickListener {
	
	/**
	 * This is the <b>{@link kennisbank.checkin.Checkout}</b> this component points to.
	 */
	public Checkout checkout

	/**
	 * Constructor of ProjectLink class.
	 */
	ProjectLink(Checkout checkout) {
		addLayoutClickListener(this)
		setStyleName("projectButton")
		this.checkout = checkout

		setSizeFull()

		Image picture = new Image();
		addComponent(picture);
		picture.setId("picture");
		picture.setSource(new FileResource(new File(checkout.picturePath)))

		Label title = new Label(checkout.title)
		addComponent(title)
		setComponentAlignment(title, Alignment.TOP_CENTER)
		
	}

	/**
	 * Click event fired when the component is clicked. <br>
	 * Opens a new tab with a <b>{@link kennisbank.fabtool.projects.ProjectView}</b> of the {@link #checkout}.
	 */
	void layoutClick(LayoutClickEvent event) {
	}

}
