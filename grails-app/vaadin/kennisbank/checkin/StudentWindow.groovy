package kennisbank.checkin

import com.vaadin.ui.CheckBox
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.NativeButton
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.Alignment
import com.vaadin.shared.ui.label.ContentMode

class StudentWindow extends Window {

	StudentWindow() {
		setCaption("Check-in voor studenten")
		setPrimaryStyleName("check-in")
		setModal(true)
		setStyleName(Reindeer.WINDOW_LIGHT)

		VerticalLayout windowLayout = new VerticalLayout()
		setContent(windowLayout)
		windowLayout.setSpacing(true)
		windowLayout.setMargin(true)
		windowLayout.addComponent(new Label("<i><span style=\"color:red\">Alle vakken zijn vereist</span></i>", ContentMode.HTML))

		HorizontalLayout firstAndLastMameLayout = new HorizontalLayout()
		windowLayout.addComponent(firstAndLastMameLayout)
		firstAndLastMameLayout.setSpacing(true)
		firstAndLastMameLayout.addComponent(new TextField("Voornaam"))
		firstAndLastMameLayout.addComponent(new TextField("Achternaam"))

		HorizontalLayout studentNumberAndEmailLayout = new HorizontalLayout()
		windowLayout.addComponent(studentNumberAndEmailLayout)
		studentNumberAndEmailLayout.setSpacing(true)
		studentNumberAndEmailLayout.addComponent(new TextField("Student nummer"))
		studentNumberAndEmailLayout.addComponent(new TextField("E-mail adres"))

		HorizontalLayout classAndCourseLayout = new HorizontalLayout()
		windowLayout.addComponent(classAndCourseLayout)
		classAndCourseLayout.setSpacing(true)
		classAndCourseLayout.addComponent(new TextField("Vak"))
		classAndCourseLayout.addComponent(new TextField("Opleiding"))

		HorizontalLayout teacherAndInstituteLayout = new HorizontalLayout()
		windowLayout.addComponent(teacherAndInstituteLayout)
		teacherAndInstituteLayout.setSpacing(true)
		teacherAndInstituteLayout.addComponent(new TextField("Docent"))
		teacherAndInstituteLayout.addComponent(new TextField("Instituut"))

		VerticalLayout equipmentLayout = new VerticalLayout()
		windowLayout.addComponent(equipmentLayout)
		equipmentLayout.addComponent(new Label("Waarmee ga je werken?"))
		def equipmentList = ["3D printer", "Laser snijder", "Folie snijder", "Elektronica", "Gereedschap"]

		for (equipment in equipmentList) {
			equipmentLayout.addComponent(new CheckBox(equipment))
		}

		NativeButton checkinButton = new NativeButton("In-checken", new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {

					}
				})
		windowLayout.addComponent(checkinButton)
		windowLayout.setComponentAlignment(checkinButton, Alignment.MIDDLE_CENTER)
	}
}
