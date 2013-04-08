package kennisbank.checkin

import com.vaadin.ui.*
import com.vaadin.data.Property.ValueChangeListener
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.Tree.ExpandEvent
import kennisbank.fabtool.projects.ProjectLink
import com.vaadin.server.FileResource
import com.vaadin.server.ThemeResource
import com.vaadin.ui.Upload.SucceededEvent
import com.vaadin.ui.Upload.FailedEvent
import com.vaadin.ui.Upload.Receiver
import com.vaadin.data.Item
import com.vaadin.data.util.HierarchicalContainer
import com.vaadin.data.util.IndexedContainer
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Upload.Receiver
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.event.ShortcutListener

class CheckoutWindow extends Window {

	CheckoutWindow(Checkout checkout) {

		setCaption("Check out")
		setPrimaryStyleName("check-out")
		setModal(true)
		setStyleName(Reindeer.WINDOW_LIGHT)

		setCloseShortcut(KeyCode.ESCAPE, null);

		setContent(checkoutForm(checkout))

		// ------------------------------------------------------- Checkout List -------------------------------------------------------

	}

	private Layout checkoutForm(Checkout checkout) { 

		GridLayout formLayout = new GridLayout(2, 4)
		formLayout.setSpacing(true)
		formLayout.setMargin(true)

		VerticalLayout titleLayout = new VerticalLayout()
		formLayout.addComponent(titleLayout, 0, 0, 1, 0) // Column 0, Row 0 to Column 1, Row 0
		
		TextField titleTextField = new TextField()
		titleLayout.addComponent(titleTextField)
		titleLayout.setComponentAlignment(titleTextField, Alignment.TOP_CENTER)
		titleTextField.setInputPrompt("Voeg een titel toe")
		// titleTextField.setSizeUndefined()

		Label madeByLabel = new Label("Gemaakt door: <br><i>" + 
			checkout.checkin.firstName + " " + checkout.checkin.lastName + 
			"<br>(<A HREF=\"mailto:" + checkout.checkin.email + "\">"+ checkout.checkin.email +"</A>)" +
			"<br> op " + checkout.checkin.dateCreated.format('dd MMMM yyyy') + "</i>", ContentMode.HTML)
		
		// Column 1, Row 1
		formLayout.addComponent(madeByLabel, 1, 1)
		madeByLabel.setWidth("-1")

		// ------------------------------------------------------- Picture -------------------------------------------------------

		VerticalLayout uploadLayout = new VerticalLayout()
		formLayout.addComponent(uploadLayout, 0, 1) // Column 0, Row 1
		uploadLayout.setPrimaryStyleName("embedded-panel")
		uploadLayout.setSpacing(true)

		Image pictureButton = new Image();
		uploadLayout.addComponent(pictureButton);
		// pictureButton.setStyleName(Reindeer.BUTTON_LINK);
		pictureButton.setId("picture");
		pictureButton.setSource((checkout.picturePath == "emptyImage.gif") ? 
			new ThemeResource("emptyImage.gif") :
			new FileResource(new File(checkout.picturePath)));

		UploadReceiver receiver = new UploadReceiver(checkout) // Receiver that handles the data stream
		Upload upload = new Upload(null, receiver) // Upload button
		
		upload.addSucceededListener(new Upload.SucceededListener() {
			public void uploadSucceeded(SucceededEvent event) {
				Checkout currentCheckout = Checkout.findByUniqueID(checkout.uniqueID)
				pictureButton.setSource(new FileResource(new File(currentCheckout.picturePath)))
				Notification.show("Uploaden geslaagd!")	
			}
			})
		
		upload.addFailedListener(new Upload.FailedListener() {
			public void uploadFailed(FailedEvent event) {
				Notification.show("Uploaden niet gelukt!")	
			}
			})


		uploadLayout.setWidth("-1")
		uploadLayout.addComponent(upload)
		upload.setImmediate(true) // Starts to upload immediately after choosing file

		// ------------------------------------------------------- Material -------------------------------------------------------
		
		TreeTable materialTreeTable = new TreeTable()
		formLayout.addComponent(materialTreeTable, 0, 2, 1, 2) // Column 0, Row 2 to Column 1, Row 2
		materialTreeTable.setWidth("100%")
		materialTreeTable.setPageLength(0)

		HierarchicalContainer container = new HierarchicalContainer()
		container.addContainerProperty("Apparatuur", Component.class, "")
		container.addContainerProperty("Instellingen", TextField.class, "")
		materialTreeTable.setContainerDataSource(container)
		materialTreeTable.setColumnExpandRatio("Apparatuur", 1)


		for (def equipmentUsed : checkout.checkin.equipment) {
			Item equipmentItem = container.addItem(equipmentUsed)
			equipmentItem.getItemProperty("Apparatuur").setValue(new AddMaterialButton(equipmentUsed, materialTreeTable))
		}

		HorizontalLayout buttonsLayout = new HorizontalLayout()
		formLayout.addComponent(buttonsLayout, 0, 3, 1, 3)  // Column 0, Row 3 to Column 1, Row 3
		formLayout.setComponentAlignment(buttonsLayout, Alignment.TOP_CENTER)
		buttonsLayout.setSpacing(true)
		buttonsLayout.setWidth("100%")

		Button saveButton = new Button("Opslaan")
		buttonsLayout.addComponent(saveButton)
		buttonsLayout.setComponentAlignment(saveButton, Alignment.TOP_CENTER)		

		return formLayout
	}

}

public class UploadReceiver implements Receiver {

	OutputStream outputFile = null
	Checkout checkout = null

	public UploadReceiver(Checkout checkout) {
		this.checkout = checkout
	}

	@Override
	public OutputStream receiveUpload(String strFilename, String strMIMEType) {
		File file

		try {

			new File('uploads/'+checkout.uniqueID).mkdirs()
			file = new File("uploads/"+checkout.uniqueID+"/"+strFilename)

			print file.absolutePath

			if(!file.exists()) {
				file.createNewFile()
				Checkout.withTransaction {
					String oldPicturePath = checkout.picturePath
					checkout.picturePath = file.absolutePath
					checkout = checkout.merge()
					if (checkout.save(failOnError: true)) {
						print checkout.picturePath
						new File(oldPicturePath).delete()
					}
					else {
						return null
					}
				}
			}
			else { 
				return null
			}

			outputFile =  new FileOutputStream(file)
			} catch (IOException e) {
				e.printStackTrace()
			}
			return outputFile
		}

		protected void finalize() {
			try {
				super.finalize()
				if(outputFile != null) {
					outputFile.close()
				}
				} catch (Throwable exception) {
					exception.printStackTrace()
				}
			}
		}
