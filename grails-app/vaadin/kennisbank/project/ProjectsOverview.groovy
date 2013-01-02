package kennisbank.project

import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.*

class ProjectsOverview extends CssLayout {

	ProjectsOverview() {
		
		setSizeFull()
		
		VerticalLayout layout = new VerticalLayout()
		layout.setSpacing(true)
		layout.setMargin(true)
		
		Label titleLabel = new Label("<h1><b>Projects</b></h1>", Label.CONTENT_XHTML)
		titleLabel.setWidth("100%")
		
		Table projectsTable = new Table()
		//projectsTable.addStyleName(Reindeer.TABLE_BORDERLESS)
		
		projectsTable.setWidth("95%");
		projectsTable.setHeight("170px");
		
		layout.addComponent(titleLabel)
		layout.addComponent(projectsTable)
		
		layout.setComponentAlignment(projectsTable, Alignment.MIDDLE_CENTER)
		layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
		
		addComponent(layout)
	}
	
}
