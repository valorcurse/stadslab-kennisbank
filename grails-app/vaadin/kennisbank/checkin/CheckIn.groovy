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

		Panel checkinChoicePanel = new Panel()
		checkinChoicePanel.setPrimaryStyleName("island-panel")
		view.addComponent(checkinChoicePanel)
		checkinChoicePanel.setWidth("700px")
		view.setComponentAlignment(checkinChoicePanel, Alignment.MIDDLE_CENTER)

		VerticalLayout checkInLayout = new VerticalLayout()
		checkinChoicePanel.setContent(checkInLayout)
		checkInLayout.setSizeFull()
		checkInLayout.setSpacing(true)
		checkInLayout.setMargin(true)


		// ------------------------------------------------------- Greeting -------------------------------------------------------

		Label greetingMessageLabel = new Label()
		checkInLayout.addComponent(greetingMessageLabel)
		checkInLayout.setComponentAlignment(greetingMessageLabel, Alignment.TOP_CENTER)
		greetingMessageLabel.setSizeUndefined()
		greetingMessageLabel.setValue("Ben je een student of een bedrijf?")


		// ------------------------------------------------------- Buttons -------------------------------------------------------

		HorizontalLayout buttonsLayout = new HorizontalLayout()
		checkInLayout.addComponent(buttonsLayout)
		checkInLayout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_CENTER)
		buttonsLayout.setSpacing(true)


		// ------------------------------------------------------- Student Button -------------------------------------------------------

		Button studentButton = new Button("Check In");
		buttonsLayout.addComponent(studentButton);
		buttonsLayout.setComponentAlignment(studentButton, Alignment.MIDDLE_CENTER)
		//studentButton.setStyleName(Reindeer.BUTTON_LINK);
		//studentButton.addStyleName("image-button");
		//studentButton.setIcon(new ThemeResource("student.jpg"));
		studentButton.addClickListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent secondEvent) {
						CheckinWindow window = new CheckinWindow()
						UI.getCurrent().addWindow(window)
					}
				});


		// ------------------------------------------------------- Company Button -------------------------------------------------------

		Button companyButton = new Button("Check Uit");
		buttonsLayout.addComponent(companyButton);
		buttonsLayout.setComponentAlignment(companyButton, Alignment.MIDDLE_CENTER)
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
