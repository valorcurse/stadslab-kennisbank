package kennisbank.checkin

import com.vaadin.ui.CheckBox
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.NativeButton
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Layout
import com.vaadin.ui.Window
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.Alignment
import com.vaadin.ui.Notification
import com.vaadin.ui.TabSheet
import com.vaadin.ui.TextArea
import com.vaadin.shared.ui.label.ContentMode
import org.springframework.context.MessageSource
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.vaadin.server.UserError

/**
 * Window where the functionality for companies to login.
 *
 * @author Marcelo Dias Avelino
 */
class CompanyCheckinWindow extends CheckinWindow {

	/**
	 * Method that must be overriden to add content to the window
	 */
	@Override
	Layout windowContent() {

		def textFields = []

		VerticalLayout layout = new VerticalLayout()
		layout.setSpacing(true)

		String warningsDefaultMessage = "<i><span style=\"color:red\">Alle vakken zijn vereist</span></i>"

		Label warningsLabel = new Label(warningsDefaultMessage, ContentMode.HTML)
		layout.addComponent(warningsLabel)

		// Company name
		TextField companyNameTextField = new TextField("Bedrijfsnaam")
		layout.addComponent(companyNameTextField)
		textFields.add(companyNameTextField)

		// Contact person and Email
		HorizontalLayout contactPersonAndEmail = new HorizontalLayout()
		layout.addComponent(contactPersonAndEmail)
		contactPersonAndEmail.setSpacing(true)

		TextField contactPersonTextField = new TextField("Contact persoon")
		contactPersonAndEmail.addComponent(contactPersonTextField)
		textFields.add(contactPersonTextField)

		TextField emailTextField = new TextField("E-mail adres")
		contactPersonAndEmail.addComponent(emailTextField)
		textFields.add(emailTextField)

		// Number of workers
		TextField numberOfWorkersTextField = new TextField("Hoeveel personen zijn er aanwezig namens uw bedrijf?")
		layout.addComponent(numberOfWorkersTextField)
		textFields.add(numberOfWorkersTextField)

		TextArea projectDescriptionTextArea = new TextArea("Project omschrijving")
		layout.addComponent(projectDescriptionTextArea)
		textFields.add(projectDescriptionTextArea)
		projectDescriptionTextArea.setRows(10)
		projectDescriptionTextArea.setColumns(30)
		
		NativeButton checkinButton = new NativeButton("In-checken", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				warningsLabel.setValue(warningsDefaultMessage)
				for (textField in textFields) {
					textField.setComponentError(null)
				}

				CompanyCheckin.withTransaction {
					CompanyCheckin checkin = new CompanyCheckin(
						companyName: companyNameTextField.getValue(),
						contactPerson: contactPersonTextField.getValue(),
						email: emailTextField.getValue(),
						numberOfWorkers: numberOfWorkersTextField.getValue(),
						projectDescription: projectDescriptionTextArea.getValue())

					if (checkin.save()) {
						checkinSuccessful = true
						Notification.show("In-checken geslaagd!")
						close()
					}
					else {
						Boolean blankOnce = false
						MessageSource messageSource = ApplicationHolder.application.mainContext.getBean('messageSource')
						checkin.errors.allErrors.each {
							print it

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

								if (it.getField() == "numberOfWorkers") { 
									numberOfWorkersTextField.setComponentError(new UserError(messageSource.getMessage(it, Locale.getDefault())))
								}

								if (it.getField() == "email") {
									emailTextField.setComponentError(new UserError(messageSource.getMessage(it, Locale.getDefault())))
								}
							}
						}
					}
				}
			}
		})
		
		layout.addComponent(checkinButton)
		layout.setComponentAlignment(checkinButton, Alignment.MIDDLE_CENTER)

		return layout
	}
}