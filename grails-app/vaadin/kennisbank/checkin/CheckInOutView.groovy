package kennisbank.checkin

import com.vaadin.ui.*
import com.vaadin.annotations.*
import com.vaadin.server.VaadinRequest
import com.vaadin.data.util.IndexedContainer
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Window.CloseEvent
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.data.Item

/**
 * Main view where the logic for checkins and checkouts are implemented.
 *
 * @author Marcelo Dias Avelino
 */

@Theme("HRO")
class CheckInOutView extends UI {

	/**
	 * Initializer of the view that must be overriden to add content to the window
	 */
	@Override
	public void init(VaadinRequest request) {
		setPrimaryStyleName("check-in")

		VerticalLayout bodyLayout = new VerticalLayout()
		setContent(bodyLayout)
		bodyLayout.setSizeFull()


		Panel centerPanel = new Panel()
		bodyLayout.addComponent(centerPanel)
		bodyLayout.setComponentAlignment(centerPanel, Alignment.MIDDLE_CENTER)
		centerPanel.setSizeUndefined()

		VerticalLayout panelLayout = new VerticalLayout()
		centerPanel.setContent(panelLayout)

		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Welcome Message >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

		VerticalLayout welcomeMessageLayout = new VerticalLayout()
		panelLayout.addComponent(welcomeMessageLayout)
		panelLayout.setComponentAlignment(welcomeMessageLayout, Alignment.TOP_CENTER)
		welcomeMessageLayout.setWidth("100%")
		welcomeMessageLayout.setMargin(true)
		welcomeMessageLayout.setSpacing(true)

		Label titleLabel = new Label("<h1>Welkom bij het Stadslab</h1>", ContentMode.HTML)
		welcomeMessageLayout.addComponent(titleLabel)
		welcomeMessageLayout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)

		Label welcomeMessageLabel = new Label("Voordat je gebruik van het lab mag maken, verzoeken wij je om in te checken.<br>" +
			"Dit wordt gedaan door hieronder op de knop van de juiste categorie te klikken. <br><br>" + 
			"Als je vervolgens klaar bent, dan graag uitchecken door je eigen checkin in de tabel te zoeken " + 
			"en daarna de verzocht informatie van je project invullen.", ContentMode.HTML)
		welcomeMessageLayout.addComponent(welcomeMessageLabel)
		welcomeMessageLayout.setComponentAlignment(welcomeMessageLabel, Alignment.MIDDLE_CENTER)
		welcomeMessageLabel.setWidth("425px")

		HorizontalLayout bottomPanelLayout = new HorizontalLayout()
		panelLayout.addComponent(bottomPanelLayout)
		bottomPanelLayout.setSpacing(true)
		bottomPanelLayout.setMargin(true)

		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Container >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

		IndexedContainer container = new IndexedContainer()
		container.addContainerProperty("Naam", String.class, "")
		container.addContainerProperty("E-mail", String.class, "")
		container.addContainerProperty("Datum", String.class, "")
		container.addContainerProperty("", Button.class, "")

		def updateCheckinList
		updateCheckinList =	 {
			container.removeAllItems()
			
			// TODO: Add method to domain objects which return the display name
			// Change the text displayed in the name column depending on the type of checkin
			for (def checkin : Checkin.list()) {
				if (!checkin.closed) {
					Item item = container.addItem(checkin)

					def name = ""
					switch(checkin.getClass()) {
					 	case StudentCheckin:
					 	name = checkin.firstName + " " + checkin.lastName
					 	break

					 	case CompanyCheckin:
						name = checkin.contactPerson + " : " + checkin.companyName			 		
					 	break
					} 

					item.getItemProperty("Naam").setValue(name)
					item.getItemProperty("E-mail").setValue(checkin.email)
					item.getItemProperty("Datum").setValue(checkin.dateCreated.format("hh:mm dd/MM/yyyy").toString())
					item.getItemProperty("").setValue(new Button("Uit checken", new Button.ClickListener() {
						public void buttonClick(ClickEvent event) { 
							CheckoutWindow window = new CheckoutWindow(checkin)
							UI.getCurrent().addWindow(window)

							window.addCloseListener(new Window.CloseListener() {
					            public void windowClose(CloseEvent e) {
									if (window.checkoutSuccessful) {
										updateCheckinList()
									}
								}
							})
						} 
					}))
				}
			}
		}

		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Checkin >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

		Panel leftPanel = new Panel("Checkin")
		bottomPanelLayout.addComponent(leftPanel)
		leftPanel.setSizeFull()

		VerticalLayout leftLayout = new VerticalLayout()
		leftPanel.setContent(leftLayout)
		// leftLayout.setSizeFull()
		leftLayout.setSpacing(true)
		leftLayout.setMargin(true)

		// Create a checkin for a student
		Button studentCheckinButton = new Button("Student")
		leftLayout.addComponent(studentCheckinButton)
		leftLayout.setComponentAlignment(studentCheckinButton, Alignment.TOP_CENTER)

		studentCheckinButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) { 
				StudentCheckinWindow window = new StudentCheckinWindow()
				UI.getCurrent().addWindow(window)

				window.addCloseListener(new Window.CloseListener() {
		            public void windowClose(CloseEvent e) {
						if (window.checkinSuccessful) {
							updateCheckinList()
						}
					}
				})
			}
		})

		// Create a checkin for a company
		Button companyCheckinButton = new Button("Bedrijf & Overige")
		leftLayout.addComponent(companyCheckinButton)
		leftLayout.setComponentAlignment(companyCheckinButton, Alignment.TOP_CENTER)

		companyCheckinButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) { 
				CompanyCheckinWindow window = new CompanyCheckinWindow()
				UI.getCurrent().addWindow(window)

				window.addCloseListener(new Window.CloseListener() {
		            public void windowClose(CloseEvent e) {
						if (window.checkinSuccessful) {
							updateCheckinList()
						}
					}
				})
			}
		})

		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Checkout >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

		Panel rightPanel = new Panel("Checkout")
		bottomPanelLayout.addComponent(rightPanel)

		VerticalLayout rightLayout = new VerticalLayout()
		rightPanel.setContent(rightLayout)
		rightLayout.setSizeFull()

		// Table where checkins are displayed
		Table checkoutTable = new Table()
		rightLayout.addComponent(checkoutTable)
		rightLayout.setComponentAlignment(checkoutTable, Alignment.TOP_CENTER)
		checkoutTable.setWidth("500px")
		checkoutTable.setColumnExpandRatio("Naam", 0.3)
		checkoutTable.setColumnExpandRatio("E-mail", 0.3)
		checkoutTable.setColumnExpandRatio("Datum", 0.3)
		checkoutTable.setPageLength(8)
		checkoutTable.setContainerDataSource(container)

		updateCheckinList()

	}
	
}