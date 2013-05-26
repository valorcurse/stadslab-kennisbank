package kennisbank.fabtool.tabletabs

import com.google.gwt.user.client.Command
import com.google.gwt.user.client.ui.TabBar.Tab
import com.vaadin.data.Property
import com.vaadin.event.FieldEvents.TextChangeEvent
import com.vaadin.event.FieldEvents.TextChangeListener
import com.vaadin.server.ExternalResource
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Field.ValueChangeEvent
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.themes.Runo
import com.vaadin.ui.*
import com.vaadin.shared.ui.label.ContentMode
import kennisbank.checkin.StudentCheckin



class Today extends VerticalLayout {
	
	Today(){
		Panel checkedInPanel = new Panel("All check-ins")
		checkedInPanel.setPrimaryStyleName("embedded-panel")
		checkedInPanel.addStyleName(Runo.PANEL_LIGHT)
	
		VerticalLayout checkedInLayout = new VerticalLayout()
		checkedInPanel.setContent(checkedInLayout)
		checkedInLayout.setMargin(true)
		checkedInLayout.setSpacing(true)
	
		Table checkedInTable = new Table()
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
		checkedInTable.addContainerProperty("Date", String.class, null)
		
	
		
	
			List<StudentCheckin> checks = StudentCheckin.list()
			//List<StudentCheckin> checks = StudentCheckin
	
		for (StudentCheckin check : checks) {
			checkedInTable.addItem(	[check.studentNumber, check.firstName, check.lastName, check.email,
				 check.institute,
				check.study, check.course, check.teacher,  check.dateCreated, "",
				""] as Object[],
			new Integer(checkedInTable.size()+1))
		}
		checkedInLayout.addComponent(checkedInTable)
	}
	
	
	}
