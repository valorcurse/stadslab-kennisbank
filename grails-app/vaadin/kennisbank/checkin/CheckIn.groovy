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
import com.vaadin.data.Item
import com.vaadin.data.util.IndexedContainer
import com.vaadin.data.Property.ValueChangeListener
import com.vaadin.data.Property.ValueChangeEvent

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
		// checksChoicePanel.setWidth("700px")
		checksChoicePanel.setSizeUndefined()
		view.setComponentAlignment(checksChoicePanel, Alignment.MIDDLE_CENTER)

		VerticalLayout mainLayout = new VerticalLayout()
		checksChoicePanel.setContent(mainLayout)
		mainLayout.setSizeFull()
		mainLayout.setSpacing(true)
		mainLayout.setMargin(true)

		// ------------------------------------------------------- Greeting -------------------------------------------------------

		Label greetingMessageLabel = new Label()
		mainLayout.addComponent(greetingMessageLabel)
		mainLayout.setComponentAlignment(greetingMessageLabel, Alignment.TOP_CENTER)
		greetingMessageLabel.setSizeUndefined()
		greetingMessageLabel.setValue("Ben je een student of een bedrijf?")

		HorizontalLayout checkInOutLayout = new HorizontalLayout()
		mainLayout.addComponent(checkInOutLayout)
		checkInOutLayout.setSizeFull()

		// ------------------------------------------------------- Check in -------------------------------------------------------

		VerticalLayout checkInLayout = new VerticalLayout()
		checkInOutLayout.addComponent(checkInLayout)
		checkInOutLayout.setComponentAlignment(checkInLayout, Alignment.TOP_CENTER)
		checkInLayout.setSpacing(true)
		checkInLayout.setMargin(true)
		//checkInLayout.setSizeUndefined()

		
		Label checkInLabel = new Label("Hier kun je in checken", ContentMode.HTML)
		checkInLayout.addComponent(checkInLabel)
		checkInLabel.setSizeUndefined()
		checkInLayout.setComponentAlignment(checkInLabel, Alignment.TOP_CENTER)

		// ------------------------------------------------------- Student Button -------------------------------------------------------

		Button studentButton = new Button("Student");
		checkInLayout.addComponent(studentButton);
		checkInLayout.setComponentAlignment(studentButton, Alignment.MIDDLE_CENTER)
		studentButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent secondEvent) {
				StudentCheckinWindow window = new StudentCheckinWindow()
				UI.getCurrent().addWindow(window)
			}
			});


		// ------------------------------------------------------- Company Button -------------------------------------------------------

		Button companyButton = new Button("Bedrijf");
		checkInLayout.addComponent(companyButton);
		checkInLayout.setComponentAlignment(companyButton, Alignment.MIDDLE_CENTER)
		companyButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				CompanyCheckinWindow window = new CompanyCheckinWindow()
				UI.getCurrent().addWindow(window)
			}
			})


		// ------------------------------------------------------- Check out -------------------------------------------------------

		VerticalLayout checkOutLayout = new VerticalLayout()
		checkInOutLayout.addComponent(checkOutLayout)
		checkOutLayout.setMargin(true)
		checkOutLayout.setSizeUndefined()

		Table checkOutTable = new Table()
		checkOutLayout.addComponent(checkOutTable)
		checkOutTable.setWidth("100%")
		checkOutTable.setSelectable(true)
		checkOutTable.setImmediate(true)
		checkOutTable.addStyleName(Reindeer.TABLE_BORDERLESS)

		IndexedContainer checkOutContainer = new IndexedContainer()
		checkOutContainer.addContainerProperty("Naam", String.class, "")
		checkOutContainer.addContainerProperty("E-mail", String.class, "")
		checkOutContainer.addContainerProperty("Datum", String.class, "")
		checkOutContainer.addContainerProperty("Uit checken", Button.class, "")
		checkOutTable.setContainerDataSource(checkOutContainer)

		// Sort the checkouts with latest on top
		List<Checkout> checkouts = Checkout.list().sort { it.dateCreated }.reverse()

		for (def checkout : checkouts) {
			Item item = checkOutContainer.addItem(checkout)
			item.getItemProperty("Naam").setValue(checkout.checkin.firstName + " " + checkout.checkin.lastName)
			item.getItemProperty("E-mail").setValue(checkout.checkin.email)
			item.getItemProperty("Datum").setValue(checkout.checkin.dateCreated.format('hh:mm dd MMMM yyyy').toString())
			item.getItemProperty("Uit checken").setValue(new Button("Uit checken", (new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					CheckoutWindow window = new CheckoutWindow(checkout)
					UI.getCurrent().addWindow(window)
				}
				})))

		}

		// companyButton.addClickListener(new ClickListener() {
		// 	@Override
		// 	public void buttonClick(ClickEvent event) {
		// 		CheckoutWindow window = new CheckoutWindow()
		// 		UI.getCurrent().addWindow(window)
		// 	}
		// 	});


setContent(viewPanel)
}
}
