package kennisbank.checkin

import com.vaadin.ui.CheckBox
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.NativeButton
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.Alignment
import com.vaadin.ui.Notification
import com.vaadin.shared.ui.label.ContentMode
import org.springframework.context.MessageSource
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.vaadin.server.UserError
import kennisbank.EmailService

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
		String warningsDefaultMessage = "<i><span style=\"color:red\">Alle vakken zijn vereist</span></i>"
		Label warningsLabel = new Label(warningsDefaultMessage, ContentMode.HTML)
		windowLayout.addComponent(warningsLabel)

		def textFields = []

		// First and Last name
		HorizontalLayout firstAndLastNameLayout = new HorizontalLayout()
		windowLayout.addComponent(firstAndLastNameLayout)
		firstAndLastNameLayout.setSpacing(true)
		
		TextField firstNameTextField = new TextField("Voornaam")
		firstAndLastNameLayout.addComponent(firstNameTextField)
		textFields.add(firstNameTextField)
		
		TextField lastNameTextField = new TextField("Achternaam")
		firstAndLastNameLayout.addComponent(lastNameTextField)
		textFields.add(lastNameTextField)

		// Student number and email
		HorizontalLayout studentNumberAndEmailLayout = new HorizontalLayout()
		windowLayout.addComponent(studentNumberAndEmailLayout)
		studentNumberAndEmailLayout.setSpacing(true)
		
		TextField studentNumberTextField = new TextField("Student nummer")
		studentNumberAndEmailLayout.addComponent(studentNumberTextField)
		textFields.add(studentNumberTextField)

		TextField emailTextField = new TextField("E-mail adres")
		studentNumberAndEmailLayout.addComponent(emailTextField)
		textFields.add(emailTextField)

		// Class and course
		HorizontalLayout studyAndCourseLayout = new HorizontalLayout()
		windowLayout.addComponent(studyAndCourseLayout)
		studyAndCourseLayout.setSpacing(true)
		
		TextField courseTextField = new TextField("Vak")
		studyAndCourseLayout.addComponent(courseTextField)
		textFields.add(courseTextField)

		TextField studyTextField = new TextField("Opleiding")
		studyAndCourseLayout.addComponent(studyTextField)
		textFields.add(studyTextField)

		// Teacher and Institute
		HorizontalLayout teacherAndInstituteLayout = new HorizontalLayout()
		windowLayout.addComponent(teacherAndInstituteLayout)
		teacherAndInstituteLayout.setSpacing(true)

		TextField teacherTextField = new TextField("Docent")
		teacherAndInstituteLayout.addComponent(teacherTextField)
		textFields.add(teacherTextField)

		TextField instituteTextField = new TextField("Instituut")
		teacherAndInstituteLayout.addComponent(instituteTextField)
		textFields.add(instituteTextField)

		// Equipment
		VerticalLayout equipmentLayout = new VerticalLayout()
		windowLayout.addComponent(equipmentLayout)
		Label equipmentLabel = new Label("Waarmee ga je werken?")
		equipmentLayout.addComponent(equipmentLabel)
		equipmentLabel.setSizeUndefined()
		
		def equipmentList = [	
		new CheckBox("3D printer"), 
		new CheckBox("Laser snijder"), 
		new CheckBox("Folie snijder"), 
		new CheckBox("Elektronica"), 
		new CheckBox("Gereedschap")
		]

		for (equipment in equipmentList) {
			equipmentLayout.addComponent(equipment)
		}

		NativeButton checkinButton = new NativeButton("In-checken", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				EmailService emailService = new EmailService()

				emailService.sendEmail()

				// Set components back to default, i.e. no error messages
				warningsLabel.setValue(warningsDefaultMessage)
				equipmentLabel.setComponentError(null)
				for (textField in textFields) {
					textField.setComponentError(null)
				}

				// Check which checkboxes were checked (Checkception o.O)
				def equipmentValues = []
				for (component in equipmentList) {
					if (component.booleanValue()) {
						equipmentValues.add(component.getCaption())
					}
				}	

				StudentCheckin.withTransaction {
					StudentCheckin checkin = new StudentCheckin(	
						studentNumber: studentNumberTextField.getValue(),
						firstName: 	firstNameTextField.getValue(),
						lastName: lastNameTextField.getValue(),
						email: emailTextField.getValue(),
						institute: instituteTextField.getValue(),
						study: studyTextField.getValue(),
						course: courseTextField.getValue(),
						teacher: teacherTextField.getValue(),
						equipment: equipmentValues
						)
					
					if (checkin.save()) {
						close()
						Notification.show("In-checken geslaagd!")
					}
					else {
						Boolean blankOnce = false
						MessageSource messageSource = ApplicationHolder.application.mainContext.getBean('messageSource')
						checkin.errors.allErrors.each {
							
							if (it.toString().matches(".*blank.error.*")) {
								if (!blankOnce) {
									warningsLabel.setValue(
										warningsLabel.getValue() + 
										"<br><font size=\"1\"><i><span style=\"color:red\">" + 
										messageSource.getMessage("kennisbank.checkin.StudentCheckin.blank.error", null, Locale.getDefault()) + 
										"</span></font></i>"
										)

									for (textField in textFields) {
										if (textField.getValue() == "") {
											textField.setComponentError(new UserError("Vak is leeg."))
										}
									}

									blankOnce = true
								}
							} 
							else {
								warningsLabel.setValue(
									warningsLabel.getValue() + 
									"<br><font size=\"1\"><i><span style=\"color:red\">" + 
									messageSource.getMessage(it, Locale.getDefault()) + 
									"</span></font></i>"
									)

								if (it.getField() == "studentNumber") { 
									studentNumberTextField.setComponentError(new UserError(messageSource.getMessage(it, Locale.getDefault())))
								}

								if (it.getField() == "email") {
									emailTextField.setComponentError(new UserError(messageSource.getMessage(it, Locale.getDefault())))
								}

								if (it.getField() == "equipment") {
									equipmentLabel.setComponentError(new UserError(messageSource.getMessage(it, Locale.getDefault())))
								}
							}
						}
					}
				}
			}
		}
		)
windowLayout.addComponent(checkinButton)
windowLayout.setComponentAlignment(checkinButton, Alignment.MIDDLE_CENTER)
}
}
