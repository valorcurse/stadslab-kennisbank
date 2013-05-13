package kennisbank.checkin

import com.vaadin.ui.*
import com.vaadin.annotations.*
import com.vaadin.server.VaadinRequest
import com.vaadin.data.util.IndexedContainer


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
		centerPanelLayout.setSizeFull()

		// ####################################################### Checkin #######################################################

		VerticalLayout leftLayout = new VerticalLayout()
		centerPanelLayout.addComponent(leftLayout)
		leftLayout.setSizeFull()
		leftLayout.setSpacing(true)

		Button studentCheckinButton = new Button("Student")
		leftLayout.addComponent(studentCheckinButton)
		leftLayout.setComponentAlignment(studentCheckinButton, Alignment.MIDDLE_CENTER)

		// ####################################################### Checkout #######################################################

		VerticalLayout rightLayout = new VerticalLayout()
		centerPanelLayout.addComponent(rightLayout)
		rightLayout.setSizeFull()

		Table checkoutTable = new Table()
		rightLayout.addComponent(checkoutTable)
		rightLayout.setComponentAlignment(checkoutTable, Alignment.MIDDLE_CENTER)

		IndexedContainer container = new IndexedContainer()
		container.addContainerProperty("Naam", String.class, "")
		container.addContainerProperty("E-mail", String.class, "")
		container.addContainerProperty("Datum", String.class, "")
		container.addContainerProperty("", Button.class, "")
		
		checkoutTable.setContainerDataSource(container)
		// checkoutTable.setColumnExpandRatio("Apparatuur", 0.5)
		// checkoutTable.setColumnExpandRatio("Materiaal", 0.5)
	}
	
}