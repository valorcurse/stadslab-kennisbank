package kennisbank.fabtool.administration

import com.google.gwt.user.client.Command
import com.google.gwt.user.client.ui.TabBar.Tab
import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeListener
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.event.FieldEvents.TextChangeEvent
import com.vaadin.event.FieldEvents.TextChangeListener
import com.vaadin.server.ClassResource
import com.vaadin.server.ExternalResource
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.*
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.MenuItem
import java.awt.MenuItem
import java.io.OutputStream
import java.sql.PreparedStatement
import java.util.Calendar
import kennisbank.checkin.*
import kennisbank.*
import kennisbank.fabtool.home.HomeView
import static java.util.Calendar.*


/**
 * Window where the administrator can see who checked in and filter it
 *
 * @author Nilson Xavier da Luz
 */

class Administration extends VerticalLayout {
	
	/**
	 * Fragment used to bookmark this page.
	 */	
	String uriFragment
		
	def calendar = new GregorianCalendar()
	def dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
	def dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
	def dayfYear = calendar.get(Calendar.DAY_OF_YEAR)

	/**
	 * Constructor of the Administration class.
	 */		
	Administration() {

		uriFragment = "#!/"
		UI.getCurrent().getPage().getCurrent().setLocation(uriFragment)

		setMargin(true)
		setSizeFull()

		Panel panel = new Panel()
		addComponent(panel)
		panel.setPrimaryStyleName("island-panel")
		
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Title >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>		

		VerticalLayout mainLayout = new VerticalLayout()
		mainLayout.setSpacing(true)
		mainLayout.setMargin(true)
		mainLayout.setSizeFull()

		panel.setContent(mainLayout)

		Label titleLabel = new Label("<h1><b>Administratie</b></h1>", ContentMode.HTML)
		mainLayout.addComponent(titleLabel)
		mainLayout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
		titleLabel.setWidth("100%")
		
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Student Check-ins >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>		
		
		def studentFields = ["Studentnummer", "Voornaam", "Achternaam", "Email", "Instituut", "Opleiding", "Vak", "Docent", "Apparaten", "Datum"]
		mainLayout.addComponent(generateCheckinsPanel("Student check-ins", StudentCheckin.list(), studentFields))

		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Company & Others Check-ins >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>		
		
		def companyFields = ["Bedrijfnaam", "Contact persoon", "Email", "Aantal medewerkers", "Omschrijving", "Datum"]
		mainLayout.addComponent(generateCheckinsPanel("Bedrijf check-ins", CompanyCheckin.list(), companyFields))
	}

	/**
	 * Generates a table with all the specified checkins
	 * 
	 * @param caption - The title to be displayed above the table
	 * @param checkins - A list with all the checkins to be displayed
	 * @param fields - A strings list with the name of each field. The correct number of fields must be in the list or the table won't display anything.
	 */
	// TODO: Check if number of fields is equal to number of data from checkins
	private Panel generateCheckinsPanel(caption, checkins, fields) {
		Panel checkinsPanel = new Panel(caption)
		checkinsPanel.setPrimaryStyleName("embedded-panel")
		checkinsPanel.addStyleName(Runo.PANEL_LIGHT)

		VerticalLayout checkInsLayout = new VerticalLayout()
		checkinsPanel.setContent(checkInsLayout)
		checkInsLayout.setMargin(true)
		checkInsLayout.setSpacing(true)

		PopupDateField startDate
		PopupDateField endDate
		Table checkInsTable

		HorizontalLayout timeButtonsLayout = new HorizontalLayout()
	
		NativeButton allButton = new NativeButton("All")
		timeButtonsLayout.addComponent(allButton)
		allButton.setWidth("100px")
		allButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				startDate.setValue(null)
				endDate.setValue(null)
			}
		})

		NativeButton todayButton = new NativeButton("Vandaag")
		timeButtonsLayout.addComponent(todayButton)
		todayButton.setWidth("100px")
		todayButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				startDate.setValue(new Date())
				endDate.setValue(new Date())
			}
		})

		NativeButton weekButton = new NativeButton("Week")
		timeButtonsLayout.addComponent(weekButton)
		weekButton.setWidth("100px")
		weekButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				startDate.setValue(new Date() - (dayOfWeek - 1))
				endDate.setValue(new Date())
			}
		})

		NativeButton monthButton = new NativeButton("Maand")
		timeButtonsLayout.addComponent(monthButton)
		monthButton.setWidth("100px")
		monthButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				startDate.setValue((new Date() - (dayOfMonth - 1)));
				endDate.setValue(new Date())
			}
		})

		NativeButton yearButton = new NativeButton("Jaar")
		timeButtonsLayout.addComponent(yearButton)
		yearButton.setWidth("100px")
		yearButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				startDate.setValue(new Date() - (dayfYear - 1));
				endDate.setValue(new Date())
			}
		})

		checkInsLayout.addComponent(timeButtonsLayout)

		HorizontalLayout dateLayout = new HorizontalLayout()
		checkInsLayout.addComponent(dateLayout)
		dateLayout.setSpacing(true)

		startDate = new PopupDateField("Begin datum")
		dateLayout.addComponent(startDate)
		startDate.setImmediate(true)
		startDate.setShowISOWeekNumbers(true)
		startDate.setDateFormat('dd/MM/yy')

		endDate = new PopupDateField("Eind datum")
		dateLayout.addComponent(endDate)
		endDate.setDateFormat('dd/MM/yy')
		endDate.setImmediate(true);
		
		def refreshTable = {
			if (startDate.getValue() && endDate.getValue()) {
				checkInsTable.removeAllItems()

				for (Checkin checkin in checkins) {
					if (checkin.dateCreated.clearTime() >= startDate?.getValue().clearTime() && checkin.dateCreated.clearTime() <= endDate?.getValue().clearTime()) {
						checkInsTable.addItem(checkin.getInfo(), new Integer(checkInsTable.size()+1))
					}
				}
			} else {
				checkInsTable.removeAllItems()

				for (Checkin checkin in checkins) {
					checkInsTable.addItem(checkin.getInfo(), new Integer(checkInsTable.size()+1))
				}
			}	
		}

		startDate.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent event) {
				refreshTable()
			}
		})

		endDate.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent event) {
				refreshTable()
			}
		})

		// ------------------------------------------------------- Table -------------------------------------------------------		
		
		checkInsTable = new Table()
		checkInsLayout.addComponent(checkInsTable)
		checkInsTable.setHeight("350px")
		checkInsTable.setWidth("100%")

		fields.each {	
			checkInsTable.addContainerProperty(it, String.class, null)
		}

		refreshTable()

		return checkinsPanel
	}
}




