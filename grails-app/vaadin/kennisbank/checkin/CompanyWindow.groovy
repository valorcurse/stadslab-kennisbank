package kennisbank.checkin

import com.vaadin.ui.CheckBox
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.NativeButton
import com.vaadin.ui.TextArea
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.Alignment
import com.vaadin.shared.ui.label.ContentMode

class CompanyWindow extends Window {

	CompanyWindow() {
		setCaption("Check-in voor bedrijven")
		setPrimaryStyleName("check-in")
		setModal(true)
		setStyleName(Reindeer.WINDOW_LIGHT)

		VerticalLayout windowLayout = new VerticalLayout()
		setContent(windowLayout)
		windowLayout.setSpacing(true)
		windowLayout.setMargin(true)
		windowLayout.addComponent(new Label("<i><span style=\"color:red\">Alle vakken zijn vereist</span></i>", ContentMode.HTML))

		// Company name
		TextField companyNameTextField = new TextField("Bedrijfsnaam")
		windowLayout.addComponent(companyNameTextField)

		// Contact person and Email
		HorizontalLayout contactPersonAndEmail = new HorizontalLayout()
		windowLayout.addComponent(contactPersonAndEmail)
		contactPersonAndEmail.setSpacing(true)

		TextField contactPersonTextField = new TextField("Contact persoon")
		contactPersonAndEmail.addComponent(contactPersonTextField)

		TextField emailTextField = new TextField("E-mail adres")
		contactPersonAndEmail.addComponent(emailTextField)

		// Number of workers
		TextField numberOfWorkersTextField = new TextField("Hoeveel personen zijn er aanwezig namens uw bedrijf?")
		windowLayout.addComponent(numberOfWorkersTextField)

		TextArea projectDescriptionTextArea = new TextArea("Project omschrijving")
		windowLayout.addComponent(projectDescriptionTextArea)
		projectDescriptionTextArea.setRows(10)
		projectDescriptionTextArea.setColumns(30)
		
		NativeButton checkinButton = new NativeButton("In-checken", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				CompanyCheckin.withTransaction {
					new CompanyCheckin(
						companyName: companyNameTextField.getValue(),
						contactPerson: contactPersonTextField.getValue(),
						email: emailTextField.getValue(),
						numberOfWorkers: numberOfWorkersTextField.getValue(),
						projectDescription: projectDescriptionTextArea.getValue())
				}
				close()
			}
		}
		)
		windowLayout.addComponent(checkinButton)
		windowLayout.setComponentAlignment(checkinButton, Alignment.MIDDLE_CENTER)
	}

}
