package kennisbank.checkin

import com.vaadin.server.ExternalResource
import com.vaadin.server.ThemeResource
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout
import com.vaadin.annotations.*
import com.vaadin.ui.*
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.themes.Reindeer
import com.vaadin.shared.ui.label.ContentMode

@Theme("HRO")
@Title("Stadslab Check-In")
class CheckIn extends UI {

	@Override
	public void init(VaadinRequest request) {
		Panel viewPanel = new Panel()
		viewPanel.setSizeFull()

		VerticalLayout view = new VerticalLayout()
		viewPanel.setContent(view)
		view.setSizeFull()
		view.setPrimaryStyleName("check-in")

		Panel checkinChoicePanel = new Panel()
		checkinChoicePanel.setPrimaryStyleName("island-panel")
		view.addComponent(checkinChoicePanel)
		checkinChoicePanel.setWidth("700px")
		view.setComponentAlignment(checkinChoicePanel, Alignment.MIDDLE_CENTER)

		VerticalLayout checkInLayout = new VerticalLayout()
		checkinChoicePanel.setContent(checkInLayout)
		checkInLayout.setSizeFull()
		checkInLayout.setSpacing(true)
		checkInLayout.setMargin(true)


		// ------------------------------------------------------- Greeting -------------------------------------------------------

		Label greetingMessageLabel = new Label()
		checkInLayout.addComponent(greetingMessageLabel)
		checkInLayout.setComponentAlignment(greetingMessageLabel, Alignment.TOP_CENTER)
		greetingMessageLabel.setSizeUndefined()
		greetingMessageLabel.setValue("Ben je een student of een bedrijf?")


		// ------------------------------------------------------- Buttons -------------------------------------------------------

		HorizontalLayout buttonsLayout = new HorizontalLayout()
		checkInLayout.addComponent(buttonsLayout)
		checkInLayout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_CENTER)
		buttonsLayout.setSpacing(true)


		// ------------------------------------------------------- Student Button -------------------------------------------------------

		Button studentButton = new Button();
		buttonsLayout.addComponent(studentButton);
		buttonsLayout.setComponentAlignment(studentButton, Alignment.MIDDLE_CENTER)
		studentButton.setStyleName(Reindeer.BUTTON_LINK);
		studentButton.addStyleName("image-button");
		studentButton.setIcon(new ThemeResource("student.jpg"));
		studentButton.addClickListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent secondEvent) {
						StudentWindow window = new StudentWindow()
						UI.getCurrent().addWindow(window)
					}
				});


		// ------------------------------------------------------- Company Button -------------------------------------------------------

		Button companyButton = new Button();
		buttonsLayout.addComponent(companyButton);
		buttonsLayout.setComponentAlignment(companyButton, Alignment.MIDDLE_CENTER)
		companyButton.setStyleName(Reindeer.BUTTON_LINK);
		companyButton.addStyleName("image-button");
		companyButton.setIcon(new ThemeResource("company.jpg"))
		companyButton.addClickListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						Window window = new Window("Check-in voor studenten")
						window.setModal(true)
						window.setStyleName(Reindeer.WINDOW_LIGHT)

						VerticalLayout windowLayout = new VerticalLayout()
						window.setContent(windowLayout)
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

						UI.getCurrent().addWindow(window)
					}
				});


		setContent(viewPanel)
	}
}
