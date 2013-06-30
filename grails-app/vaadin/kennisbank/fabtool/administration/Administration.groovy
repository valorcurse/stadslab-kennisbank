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
//import com.vaadin.ui.TabSheet.SelectedTabChangeEvents
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.*
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.MenuItem

import java.awt.MenuItem
import java.io.OutputStream;
import java.sql.PreparedStatement
import java.util.Calendar;
import kennisbank.checkin.*
import kennisbank.*
import kennisbank.fabtool.home.HomeView
import static java.util.Calendar.*


/*import com.vaadin.addon.charts.Chart;
 import com.vaadin.addon.charts.demoandtestapp.AbstractVaadinChartExample;
 import com.vaadin.addon.charts.model.ChartType;
 import com.vaadin.addon.charts.model.Configuration;
 import com.vaadin.addon.charts.model.Cursor;
 import com.vaadin.addon.charts.model.DataSeries;
 import com.vaadin.addon.charts.model.DataSeriesItem;
 import com.vaadin.addon.charts.model.Labels;
 import com.vaadin.addon.charts.model.PlotOptionsPie;
 import com.vaadin.addon.charts.model.Tooltip;
 import com.vaadin.ui.Component;*/



class Administration extends VerticalLayout{
	String uriFragment
	//TabSheet ATabs
	String tabName() {
		return uriFragment

	}
	void menuSelected(com.vaadin.ui.MenuBar MenuItem)
	{

	}
	
	public String getDescription() {
		return "Pie chart";
	}

	Table checkedInTable


	Administration() {


		//Main layout
		VerticalLayout view = new VerticalLayout()
		//view.setSizeFull() // Set layout to cover the whole screen

		uriFragment = "#!/administration"
		UI.getCurrent().getPage().getCurrent().setLocation(uriFragment)

		setMargin(true)
		setSizeFull()

		Panel panel = new Panel()
		panel.setPrimaryStyleName("island-panel")
//----------------------------title------------------------------------------
		VerticalLayout layout = new VerticalLayout()
		layout.setSpacing(true)
		layout.setMargin(true)
		layout.setSizeFull()

		panel.setContent(layout)

		Label titleLabel = new Label("<h1><b>Administration</b></h1>", ContentMode.HTML)
		titleLabel.setWidth("100%")
		layout.addComponent(titleLabel)
		
		//--------------------------------Datepicker--------------------------------
		def calendar = new GregorianCalendar()
		def dayofWeek = calendar.get(Calendar.DAY_OF_WEEK)
		def dayofMonth = calendar.get(Calendar.DAY_OF_MONTH)
		def dayofYear = calendar.get(Calendar.DAY_OF_YEAR)
		//print (new Date() - (dayofMonth-1))
		PopupDateField startDate = new PopupDateField();
		//startDate.setValue(new Date() -(dayofWeek -(dayofWeek - 1)));//
		startDate.setImmediate(true);
		startDate.setShowISOWeekNumbers(true);
		//sample.setTimeZone(TimeZone.getTimeZone("UTC"));
		//sample.setLocale(Locale.US);
		//sample.setResolution(Resolution.MINUTE);
		
		PopupDateField endDate = new PopupDateField();
		
		endDate.setImmediate(true);
//		endDate.setShowISOWeekNumbers(true);
		
		startDate.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent event) {
			
				final String valueString = String.valueOf(event.getProperty().getValue().format('MM/dd/yy'));
					checkedInTable.removeAllItems()
					print startDate
					//println "lslsls: " +  event.getProperty().getValue()
					startDate.setValue(event.getProperty().getValue());
					Notification.show("Value changed:"+valueString);
				
				List<StudentCheckin> checks = StudentCheckin.list()
				for (StudentCheckin check : checks) {
					//println check.studentNumber
					//println check.dateCreated
					
					Date date = new Date(check.dateCreated.getTime())
					
					println 'date :' +date.format('MM/dd/yy')
					println 'selecteddate: ' + valueString
					//println endDate.getValue().format('MM/dd/yy')
					
					//if(check.dateCreated.toString().contains(startDate))// && check.dateCreated > endDate)
					if( date.format('MM/dd/yy') >= valueString) //&& date.format('MM/dd/yy') < endDate.getValue().format('MM/dd/yy'))
					{
					println checkedInTable.getItem(check.equipment*.name).toString()
					checkedInTable.addItem(	[check.studentNumber, check.firstName, check.lastName, check.email,
						check.institute, check.study, check.course, check.teacher,  check.equipment*.name.toString(), check.dateCreated.toString()
					] as Object[],
					new Integer(checkedInTable.size()+1))
				}
				}
				
				//stmt.execute();
				//stmt.close();
				
			}
		});

		endDate.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent event) {
			
				final String valueString = String.valueOf(event.getProperty().getValue().format('MM/dd/yy'));
					checkedInTable.removeAllItems()
					print startDate
					//println "lslsls: " +  event.getProperty().getValue()
					endDate.setValue(event.getProperty().getValue());
					Notification.show("Value changed:"+valueString);
				
				List<StudentCheckin> checks = StudentCheckin.list()
				for (StudentCheckin check : checks) {
					//println check.studentNumber
					//println check.dateCreated
					
					Date date = new Date(check.dateCreated.getTime())
					
					println 'date :' +date.format('MM/dd/yy')
					println 'selecteddate: ' + valueString
					//println endDate.getValue().format('MM/dd/yy')
					
					//if(check.dateCreated.toString().contains(startDate))// && check.dateCreated > endDate)
					if( date.format('MM/dd/yy') <= valueString && date.format('MM/dd/yy') >= startDate.getValue().format('MM/dd/yy') ) //&& date.format('MM/dd/yy') < endDate.getValue().format('MM/dd/yy'))
					{
					println checkedInTable.getItem(check.equipment*.name).toString()
					checkedInTable.addItem(	[check.studentNumber, check.firstName, check.lastName, check.email,
						check.institute, check.study, check.course, check.teacher,  check.equipment*.name.toString(), check.dateCreated.toString()
					] as Object[],
					new Integer(checkedInTable.size()+1))
				}
				}
				
				//stmt.execute();
				//stmt.close();
				
			}
		});




		
		
	
	
			
		//---------------------------------table------------------------------
		Panel checkedInPanel = new Panel("All check-ins")
		checkedInPanel.setPrimaryStyleName("embedded-panel")
		checkedInPanel.addStyleName(Runo.PANEL_LIGHT)

		VerticalLayout checkedInLayout = new VerticalLayout()
		checkedInPanel.setContent(checkedInLayout)
		checkedInLayout.setMargin(true)
		checkedInLayout.setSpacing(true)
		
		HorizontalLayout dateLayout = new HorizontalLayout()
		checkedInLayout.addComponent(dateLayout)
		dateLayout.addComponent(startDate)
		dateLayout.addComponent(endDate)

		checkedInTable = new Table()
		//checkedInTable.addStyleName(Reindeer.TABLE_BORDERLESS)
		checkedInTable.setHeight("350px")
		checkedInTable.setWidth("100%")

		checkedInTable.addContainerProperty("StudentNumber", String.class, null)
		checkedInTable.addContainerProperty("FirstName", String.class, null)
		checkedInTable.addContainerProperty("lastName", String.class, null)
		checkedInTable.addContainerProperty("email", String.class, null)
		checkedInTable.addContainerProperty("institute", String.class, null)
		checkedInTable.addContainerProperty("study", String.class, null)
		checkedInTable.addContainerProperty("course", String.class, null)
		checkedInTable.addContainerProperty("teacher", String.class, null)
		checkedInTable.addContainerProperty("equip", String.class, null)
		checkedInTable.addContainerProperty("Date", String.class, null)




		List<StudentCheckin> checks = StudentCheckin.list()

		//List<StudentCheckin> checks = StudentCheckin


		for (StudentCheckin check : checks) {
			//println check.studentNumber
			//println check.dateCreated
			
			// println checkedInTable.getItem(check.equipment[0]).toString()
			checkedInTable.addItem(	[check.studentNumber, check.firstName, check.lastName, check.email,
				check.institute, check.study, check.course, check.teacher,  check.equipment*.name.toString(), check.dateCreated.toString()
			] as Object[],
			new Integer(checkedInTable.size()+1))
		}

		//------------------------------buttons-----------------------------
		Panel bottonsPanel = new Panel()
		bottonsPanel.setPrimaryStyleName("embedded-panel")
		bottonsPanel.addStyleName(Runo.PANEL_LIGHT)

		HorizontalLayout bottonLayout = new HorizontalLayout()
		bottonsPanel.setContent(bottonLayout)
		//buttonLayout.setMargin(true)
		//buttonLayout.setSpacing(true)
		//weeknumbers(layout);
		NativeButton AllButton = new NativeButton("All")
		AllButton.setWidth("100px")
		//TodayButton.setHeight("50px")
		AllButton.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						for (StudentCheckin check : checks) {
							println check.studentNumber
							println checkedInTable.getItem(check.equipment*.name).toString()
							checkedInTable.addItem(	[check.studentNumber, check.firstName, check.lastName, check.email,
								check.institute, check.study, check.course, check.teacher,  check.equipment*.name, check.dateCreated.toString()
							] as Object[],
							new Integer(checkedInTable.size()+1))
						}
					}
				})

		NativeButton TodayButton = new NativeButton("Today")
		TodayButton.setWidth("100px")
		//TodayButton.setHeight("50px")
		TodayButton.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						startDate.setValue(new Date())
						endDate.setValue(new Date())
					}
				})
		NativeButton WeekButton = new NativeButton("Week")
		WeekButton.setWidth("100px")
		//TodayButton.setHeight("50px")
		WeekButton.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
					
						startDate.setValue(new Date())
						endDate.setValue(new Date())
						
						
					}
				})
		NativeButton MonthButton = new NativeButton("Month")
		MonthButton.setWidth("100px")
		//TodayButton.setHeight("50px")
		MonthButton.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						startDate.setValue((new Date() -(dayofMonth - 1)));
						endDate.setValue(new Date())
						checkedInTable.removeAllItems()
						
						//List<StudentCheckin> checks = StudentCheckin.list()
						for (StudentCheckin check : checks) {
							//println check.studentNumber
							//println check.dateCreated
							
							Date date = new Date(check.dateCreated.getTime())
						if( date.format('MM/dd/yy')  >= startDate.getValue().format('MM/dd/yy') && date.format('MM/dd/yy') <= endDate.getValue().format('MM/dd/yy'))
						{
							println checkedInTable.getItem(check.equipment*.name).toString()
							checkedInTable.addItem(	[check.studentNumber, check.firstName, check.lastName, check.email,
								check.institute, check.study, check.course, check.teacher,  check.equipment*.name.toString(), check.dateCreated.toString()
							] as Object[],
							new Integer(checkedInTable.size()+1))
						}
						
					//Filter f = new Filter(startDate)
						/*if( date >= startDate.getValue().format('MM/dd/yy') && date..format('MM/dd/yy') < endDate.getValue().format('MM/dd/yy'))
						{
						println checkedInTable.getItem(check.equipment[0]).toString()
						checkedInTable.addItem(	[check.studentNumber, check.firstName, check.lastName, check.email,
							check.institute, check.study, check.course, check.teacher,  check.equipment[0], check.dateCreated.toString()
						] as Object[],
						new Integer(checkedInTable.size()+1))
				 	}
					}*/
					}
				}
		})
		NativeButton YearButton = new NativeButton("Year")
		YearButton.setWidth("100px")
		//TodayButton.setHeight("50px")
		YearButton.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						startDate.setValue(new Date() -(dayofYear - 1));
						endDate.setValue(new Date())

					}
				})

		bottonLayout.addComponent(AllButton)
		bottonLayout.addComponent(TodayButton)
		bottonLayout.addComponent(WeekButton)
		bottonLayout.addComponent(MonthButton)
		bottonLayout.addComponent(YearButton)
		layout.addComponent(bottonsPanel)

		
		checkedInLayout.addComponent(checkedInTable)
		layout.addComponent(checkedInPanel)

	//----------------------------add Material--------------------------------

		
		
		//layout.addComponent(sample)
		

		layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
		layout.setComponentAlignment(checkedInPanel, Alignment.TOP_CENTER)
		addComponent(panel)


	}

	void weeknumbers(VerticalLayout layout) {
		// BEGIN-EXAMPLE: component.datefield.weeknumbers
		InlineDateField df = new InlineDateField("Select Date");
		df.setResolution(DateField.RESOLUTION_DAY);

		// Enable week numbers
		df.setShowISOWeekNumbers(true);
		// END-EXAMPLE: component.datefield.weeknumbers
		layout.addComponent(df);
	}
}
	/*public class Filter{
		public OutputStream(startDate,endDate, checkedInTable){
		List<StudentCheckin> checks = StudentCheckin.list()
				for (StudentCheckin check : checks) {
					//println check.studentNumber
					//println check.dateCreated
					
					Date date = new Date(check.dateCreated.getTime())
					
					println 'date :' +date.format('MM/dd/yy')
					//println 'selecteddate: ' + valueString
					//println endDate.getValue().format('MM/dd/yy')
					
					//if(check.dateCreated.toString().contains(startDate))// && check.dateCreated > endDate)
					if( date >= startDate.getValue().format('MM/dd/yy') && date.format('MM/dd/yy') < endDate.getValue().format('MM/dd/yy'))
					{
					println checkedInTable.getItem(check.equipment[0]).toString()
					checkedInTable.addItem(	[check.studentNumber, check.firstName, check.lastName, check.email,
						check.institute, check.study, check.course, check.teacher,  check.equipment[0], check.dateCreated.toString()
					] as Object[],
					new Integer(checkedInTable.size()+1))
				}
				}
				}
	}*/



