package kennisbank.checkin

import com.vaadin.server.ExternalResource
import com.vaadin.server.ThemeResource
import com.vaadin.server.VaadinRequest
import com.vaadin.ui.Button;
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout
import com.vaadin.annotations.*
import com.vaadin.ui.*
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.themes.Reindeer
import com.vaadin.shared.ui.label.ContentMode

@Theme("HRO")
@Title("Stadslab Check-In")
class CheckIn extends UI {

	@Override
	public void init(VaadinRequest request) {
		Panel viewPanel = new Panel()
		viewPanel.setSizeFull()

		VerticalLayout view = new VerticalLayout()
		viewPanel.setContent(view)
		view.setSizeFull()
		view.setPrimaryStyleName("check-in")

		Panel checksChoicePanel = new Panel()
		checksChoicePanel.setPrimaryStyleName("island-panel")
		view.addComponent(checksChoicePanel)
		checksChoicePanel.setWidth("700px")
		view.setComponentAlignment(checksChoicePanel, Alignment.MIDDLE_CENTER)

		VerticalLayout mainLayout = new VerticalLayout()
		checksChoicePanel.setContent(mainLayout)
		mainLayout.setSizeFull()

		// ------------------------------------------------------- Greeting -------------------------------------------------------

		Label greetingMessageLabel = new Label()
		mainLayout.addComponent(greetingMessageLabel)
		mainLayout.setComponentAlignment(greetingMessageLabel, Alignment.TOP_CENTER)
		greetingMessageLabel.setSizeUndefined()
		greetingMessageLabel.setValue("Ben je een student of een bedrijf?")

		HorizontalLayout checkInOutLayout = new HorizontalLayout()
		mainLayout.addComponent(checkInOutLayout)

		VerticalLayout checkInLayout = new VerticalLayout()
		checkInOutLayout.addComponent(checkInLayout)
		checkInLayout.setSizeFull()
		checkInLayout.setSpacing(true)
		checkInLayout.setMargin(true)

		// ------------------------------------------------------- Check in -------------------------------------------------------

		Label checkInLabel = new Label("Hier kun je in checken", ContentMode.HTML)
		checkInLayout.addComponent(checkInLabel)

		// ------------------------------------------------------- Student Button -------------------------------------------------------

		Button studentButton = new Button("Check In");
		checkInLayout.addComponent(studentButton);
		checkInLayout.setComponentAlignment(studentButton, Alignment.MIDDLE_CENTER)
		//studentButton.setStyleName(Reindeer.BUTTON_LINK);
		//studentButton.addStyleName("image-button");
		//studentButton.setIcon(new ThemeResource("student.jpg"));
		studentButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent secondEvent) {
				CheckinWindow window = new CheckinWindow()
				UI.getCurrent().addWindow(window)
				window.focus()
			}
			});


		// ------------------------------------------------------- Company Button -------------------------------------------------------

		Button companyButton = new Button("Check Out");
		checkInLayout.addComponent(companyButton);
		checkInLayout.setComponentAlignment(companyButton, Alignment.MIDDLE_CENTER)
		// companyButton.setStyleName(Reindeer.BUTTON_LINK);
		// companyButton.addStyleName("image-button");
		// companyButton.setIcon(new ThemeResource("company.jpg"))
		companyButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				CheckoutWindow window = new CheckoutWindow()
				UI.getCurrent().addWindow(window)
			}
			});


		setContent(viewPanel)
	}
}
