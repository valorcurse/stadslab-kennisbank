package kennisbank.checkin

import com.vaadin.ui.*
import com.vaadin.annotations.*
import com.vaadin.server.VaadinRequest
import com.vaadin.data.util.IndexedContainer
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.data.Item


@Theme("HRO")
class CheckInOutView extends UI {

	@Override
	public void init(VaadinRequest request) {
		setPrimaryStyleName("check-in")

		VerticalLayout bodyLayout = new VerticalLayout()
		setContent(bodyLayout)
		bodyLayout.setSizeFull()

		Panel centerPanel = new Panel()
		bodyLayout.addComponent(centerPanel)
		bodyLayout.setComponentAlignment(centerPanel, Alignment.MIDDLE_CENTER)
		centerPanel.setHeight("50%")
		centerPanel.setWidth("50%")

		HorizontalLayout centerPanelLayout = new HorizontalLayout()
		centerPanel.setContent(centerPanelLayout)
		// centerPanelLayout.setSizeFull()
		centerPanelLayout.setSpacing(true)
		centerPanelLayout.setMargin(true)

		// ####################################################### Checkin #######################################################

		VerticalLayout leftLayout = new VerticalLayout()
		centerPanelLayout.addComponent(leftLayout)
		leftLayout.setSizeFull()
		// leftLayout.setSizeUndefined()
		leftLayout.setSpacing(true)

		Button studentCheckinButton = new Button("Student")
		leftLayout.addComponent(studentCheckinButton)
		leftLayout.setComponentAlignment(studentCheckinButton, Alignment.TOP_CENTER)

		studentCheckinButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) { 
				StudentCheckinWindow window = new StudentCheckinWindow()
				UI.getCurrent().addWindow(window)
			}
			})

		// ####################################################### Checkout #######################################################

		VerticalLayout rightLayout = new VerticalLayout()
		centerPanelLayout.addComponent(rightLayout)
		// rightLayout.setSizeFull()

		Table checkoutTable = new Table()
		rightLayout.addComponent(checkoutTable)
		rightLayout.setComponentAlignment(checkoutTable, Alignment.TOP_CENTER)
		checkoutTable.setPageLength(10)

		IndexedContainer container = new IndexedContainer()
		container.addContainerProperty("Naam", String.class, "")
		container.addContainerProperty("E-mail", String.class, "")
		container.addContainerProperty("Datum", String.class, "")
		container.addContainerProperty("", Button.class, "")
		checkoutTable.setContainerDataSource(container)

		for (def checkin : Checkin.list()) {
			print checkin.checkout.published
			if (!checkin.checkout.published) {
				Item item = container.addItem(checkin)
				item.getItemProperty("Naam").setValue(checkin.firstName + " " + checkin.lastName)
				item.getItemProperty("E-mail").setValue(checkin.email)
				item.getItemProperty("Datum").setValue(checkin.dateCreated.toString())
				item.getItemProperty("").setValue(new Button("Uit checken", new Button.ClickListener() {
					public void buttonClick(ClickEvent event) { 
						CheckoutWindow window = new CheckoutWindow(checkin)
						UI.getCurrent().addWindow(window)
					} 
				}))
			}
		}

		// checkoutTable.setColumnExpandRatio("Apparatuur", 0.5)
		// checkoutTable.setColumnExpandRatio("Materiaal", 0.5)
	}
	
}