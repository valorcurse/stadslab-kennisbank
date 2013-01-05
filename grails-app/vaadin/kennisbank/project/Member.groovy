package kennisbank.project

import com.vaadin.server.ThemeResource
import com.vaadin.ui.*;

class Member extends VerticalLayout {

	def members = []
	String name
	
	public Member(String n) {
		name = n
	}
	
	Layout addMember(String n) {
		VerticalLayout layout = new VerticalLayout()
		GridLayout grid = new GridLayout(2, 1)
		Panel memberPanel = new Panel()
		memberPanel.setContent(grid)
		grid.setMargin(true)
		grid.setSpacing(true)
		Label nameLabel = new Label(n)
		Embedded e = new Embedded(null, new ThemeResource("../runo/icons/32/user.png"));
		grid.addComponent(e, 0, 0)
		grid.addComponent(nameLabel, 1, 0)
		
		grid.setComponentAlignment(nameLabel, Alignment.MIDDLE_CENTER)
		
		layout.addComponent(memberPanel)
		
		return layout
	}
	
	String getName() {
		return name
	}
	
	Layout getLayout() {
		VerticalLayout layout = new VerticalLayout()
		def panels = []
		for (Member m in members) {
			GridLayout grid = new GridLayout(2, 1)
			grid.setMargin(true)
			grid.setSpacing(true)
			Panel panel = new Panel()
			panel.setContent(grid)
			Label nameLabel = new Label(m.getName())
			Embedded e = new Embedded(null, new ThemeResource("../runo/icons/32/user.png"))
			grid.addComponent(e, 0, 0)
			grid.addComponent(nameLabel, 1, 0)
			grid.setComponentAlignment(nameLabel, Alignment.MIDDLE_CENTER)
			panels.add(grid)
		}
		
		return layout
	}
	
}
