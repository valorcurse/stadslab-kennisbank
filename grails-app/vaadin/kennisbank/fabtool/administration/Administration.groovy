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

class Administration extends VerticalLayout{
	
	/**
	 * Fragment used to bookmark this page.
	 */	
	String uriFragment
		
	/**
	 * Table where all the checked-in data is placed
	 */	
	Table checkedInTable

	PopupDateField startDate
	PopupDateField endDate

	def calendar = new GregorianCalendar()
	def dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
	def dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
	def dayfYear = calendar.get(Calendar.DAY_OF_YEAR)

	/**
	 * Constructor of the Administration class.
	 */		
	Administration() {


		//Main layout
		VerticalLayout view = new VerticalLayout()

		uriFragment = "#!/"
		UI.getCurrent().getPage().getCurrent().setLocation(uriFragment)

		setMargin(true)
		setSizeFull()

		Panel panel = new Panel()
		panel.setPrimaryStyleName("island-panel")
		
		// ------------------------------------------------------- Title -------------------------------------------------------		

		VerticalLayout layout = new VerticalLayout()
		layout.setSpacing(true)
		layout.setMargin(true)
		layout.setSizeFull()

		panel.setContent(layout)

		Label titleLabel = new Label("<h1><b>Administration</b></h1>", ContentMode.HTML)
		layout.addComponent(titleLabel)
		layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
		titleLabel.setWidth("100%")
		
		// ------------------------------------------------------- Time Buttons -------------------------------------------------------		

		Panel buttonsPanel = new Panel()
		layout.addComponent(buttonsPanel)
		buttonsPanel.setPrimaryStyleName("embedded-panel")
		buttonsPanel.addStyleName(Runo.PANEL_LIGHT)

		HorizontalLayout bottonLayout = new HorizontalLayout()
		buttonsPanel.setContent(bottonLayout)
	
		NativeButton allButton = new NativeButton("All")
		bottonLayout.addComponent(allButton)
		allButton.setWidth("100px")
		allButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				startDate.setValue(null)
				endDate.setValue(null)
			}
		})

		NativeButton todayButton = new NativeButton("Vandaag")
		bottonLayout.addComponent(todayButton)
		todayButton.setWidth("100px")
		todayButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				startDate.setValue(new Date())
				endDate.setValue(new Date())
			}
		})

		NativeButton weekButton = new NativeButton("Week")
		bottonLayout.addComponent(weekButton)
		weekButton.setWidth("100px")
		weekButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				print dayOfWeek
				startDate.setValue(new Date() - (dayOfWeek - 1))
				endDate.setValue(new Date())
			}
		})

		NativeButton monthButton = new NativeButton("Maand")
		bottonLayout.addComponent(monthButton)
		monthButton.setWidth("100px")
		monthButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				startDate.setValue((new Date() - (dayOfMonth - 1)));
				endDate.setValue(new Date())
			}
		})

		NativeButton yearButton = new NativeButton("Jaar")
		bottonLayout.addComponent(yearButton)
		yearButton.setWidth("100px")
		yearButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				startDate.setValue(new Date() - (dayfYear - 1));
				endDate.setValue(new Date())
			}
		})

		// ------------------------------------------------------- Date Picker -------------------------------------------------------		
		
		Panel checkedInPanel = new Panel("Student check-ins")
		layout.addComponent(checkedInPanel)
		layout.setComponentAlignment(checkedInPanel, Alignment.TOP_CENTER)
		checkedInPanel.setPrimaryStyleName("embedded-panel")
		checkedInPanel.addStyleName(Runo.PANEL_LIGHT)

		VerticalLayout checkedInLayout = new VerticalLayout()
		checkedInPanel.setContent(checkedInLayout)
		checkedInLayout.setMargin(true)
		checkedInLayout.setSpacing(true)
		
		HorizontalLayout dateLayout = new HorizontalLayout()
		checkedInLayout.addComponent(dateLayout)
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
		
		checkedInTable = new Table()
		checkedInLayout.addComponent(checkedInTable)
		checkedInTable.setHeight("350px")
		checkedInTable.setWidth("100%")

		checkedInTable.addContainerProperty("Studentnummer", String.class, null)
		checkedInTable.addContainerProperty("Voornaam", String.class, null)
		checkedInTable.addContainerProperty("Achternaam", String.class, null)
		checkedInTable.addContainerProperty("Email", String.class, null)
		checkedInTable.addContainerProperty("Instituut", String.class, null)
		checkedInTable.addContainerProperty("Opleiding", String.class, null)
		checkedInTable.addContainerProperty("Vak", String.class, null)
		checkedInTable.addContainerProperty("Docent", String.class, null)
		checkedInTable.addContainerProperty("Apparaten", String.class, null)
		checkedInTable.addContainerProperty("Datum", String.class, null)

		for (StudentCheckin check in StudentCheckin.list()) {
		
			def newItem = [check.studentNumber, check.firstName, check.lastName, check.email,
				check.institute, check.study, check.course, check.teacher,  check.equipment*.name.toString(), 
				check.dateCreated.toString()] as Object[]

			checkedInTable.addItem(newItem, new Integer(checkedInTable.size()+1))
		}

		addComponent(panel)
	}

	private void refreshTable() {
		if (startDate.getValue() && endDate.getValue()) {
		
			checkedInTable.removeAllItems()

			for (StudentCheckin checkin in StudentCheckin.list()) {
				
				if (checkin.dateCreated.clearTime() >= startDate?.getValue().clearTime() && checkin.dateCreated.clearTime() <= endDate?.getValue().clearTime()) {
					def newItem = [checkin.studentNumber, checkin.firstName, checkin.lastName, checkin.email,
						checkin.institute, checkin.study, checkin.course, checkin.teacher,  checkin.equipment*.name.toString(), 
						checkin.dateCreated.toString()] as Object[]
					
					checkedInTable.addItem(newItem, new Integer(checkedInTable.size()+1))
				}
			}
		}
		else {
			checkedInTable.removeAllItems()

			for (StudentCheckin checkin in StudentCheckin.list()) {
				
				def newItem = [checkin.studentNumber, checkin.firstName, checkin.lastName, checkin.email,
					checkin.institute, checkin.study, checkin.course, checkin.teacher,  checkin.equipment*.name.toString(), 
					checkin.dateCreated.toString()] as Object[]
					
				checkedInTable.addItem(newItem, new Integer(checkedInTable.size()+1))
			}
		}	
	}
}




