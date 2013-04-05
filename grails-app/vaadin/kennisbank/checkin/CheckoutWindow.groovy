package kennisbank.checkin

import com.vaadin.ui.Window
import com.vaadin.ui.Layout
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.CssLayout
import com.vaadin.ui.Table
import com.vaadin.data.Property.ValueChangeListener
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.ui.themes.Reindeer
import com.vaadin.ui.Image
import com.vaadin.ui.TreeTable
import com.vaadin.ui.Button
import com.vaadin.ui.Upload
import com.vaadin.ui.Label
import com.vaadin.ui.GridLayout
import com.vaadin.ui.Notification
import com.vaadin.ui.PopupDateField
import com.vaadin.ui.Alignment
import com.vaadin.ui.Component
import kennisbank.fabtool.projects.ProjectLink
import com.vaadin.server.FileResource
import com.vaadin.server.ThemeResource
import com.vaadin.ui.Upload.SucceededEvent
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

	private Layout checkoutForm(Checkout project) { 

		GridLayout formLayout = new GridLayout(2, 4)
		formLayout.setSpacing(true)
		formLayout.setMargin(true)

		VerticalLayout titleLayout = new VerticalLayout()
		formLayout.addComponent(titleLayout, 0, 0, 1, 0) // Column 0, Row 0 to Column 1, Row 0
		Label titleLabel = new Label("<h1><b>"+project.uniqueID+"</b></h1>", ContentMode.HTML)
		titleLayout.addComponent(titleLabel)
		titleLayout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER)
		titleLabel.setSizeUndefined()

		Label madeByLabel = new Label("Gemaakt door: <br><i>" + 
			project.checkin.firstName + " " + project.checkin.lastName + 
			"<br>(<A HREF=\"mailto:" + project.checkin.email + "\">"+ project.checkin.email +"</A>)" +
			"<br> op " + project.checkin.dateCreated.format('dd MMMM yyyy') + "</i>", ContentMode.HTML)
		
		// Column 1, Row 1
		formLayout.addComponent(madeByLabel, 1, 1)
		madeByLabel.setWidth("-1")

		// ------------------------------------------------------- Picture -------------------------------------------------------

		VerticalLayout uploadLayout = new VerticalLayout()
		formLayout.addComponent(uploadLayout, 0, 1) // Column 0, Row 1
		uploadLayout.setPrimaryStyleName("embedded-panel")
		uploadLayout.setSpacing(true)

		Button pictureButton = new Button();
		uploadLayout.addComponent(pictureButton);
		pictureButton.setStyleName(Reindeer.BUTTON_LINK);
		pictureButton.setId("picture");
		pictureButton.setIcon((project.picturePath == "emptyImage.gif") ? new ThemeResource("emptyImage.gif") : new FileResource(new File(project.picturePath)));

		UploadReceiver receiver = new UploadReceiver(project) // Receiver that handles the data stream
		Upload upload = new Upload(null, receiver) // Upload button
		upload.addSucceededListener(new Upload.SucceededListener() {
			public void uploadSucceeded(SucceededEvent event) {
				Notification.show("Uploaden geslaagd!")	
			}
			})
		uploadLayout.setWidth("-1")
		uploadLayout.addComponent(upload)
		upload.setImmediate(true) // Starts to upload immediately after choosing file

		// ------------------------------------------------------- Material -------------------------------------------------------
		
		//VerticalLayout materialLayout = new VerticalLayout()
		//layout.addComponent(materialLayout, 0, 2, 1, 2) // Column 0, Row 2 to Column 1, Row 2
		//materialLayout.setMargin(true)

		TreeTable materialTreeTable = new TreeTable()
		formLayout.addComponent(materialTreeTable, 0, 2, 1, 2) // Column 0, Row 2 to Column 1, Row 2
		//materialLayout.addComponent(materialTreeTable)
		materialTreeTable.setWidth("100%")
		materialTreeTable.setPageLength(0)
		
		HierarchicalContainer container = new HierarchicalContainer()
		container.addContainerProperty("Apparatuur", Component.class, "")
		container.addContainerProperty("Instellingen", String.class, "")
		materialTreeTable.setContainerDataSource(container)

		for (def equipmentUsed : project.checkin.equipment) {
			Item item = container.addItem(equipmentUsed)
			item.getItemProperty("Apparatuur").setValue(new AddMaterialButton(equipmentUsed, container))
		}

		HorizontalLayout buttonsLayout = new HorizontalLayout()
		formLayout.addComponent(buttonsLayout, 0, 3, 1, 3)  // Column 0, Row 3 to Column 1, Row 3
		formLayout.setComponentAlignment(buttonsLayout, Alignment.TOP_CENTER)
		buttonsLayout.setSpacing(true)
		buttonsLayout.setWidth("100%")

		Button saveButton = new Button("Opslaan")
		buttonsLayout.addComponent(saveButton)
		buttonsLayout.setComponentAlignment(saveButton, Alignment.TOP_CENTER)		

		Button saveDraftButton = new Button("Tijdelijk opslaan")
		buttonsLayout.addComponent(saveDraftButton)
		buttonsLayout.setComponentAlignment(saveDraftButton, Alignment.TOP_LEFT)

		return formLayout
	}

}

public class UploadReceiver implements Receiver {

	OutputStream outputFile = null
	Checkout project

	public UploadReceiver(Checkout project) {
		this.project = project
	}

	@Override
	public OutputStream receiveUpload(String strFilename, String strMIMEType) {
		File file

		try {

			new File('uploads/'+project.uniqueID).mkdirs()
			file = new File("uploads/"+project.uniqueID+"/"+strFilename)

			if(!file.exists()) {
				file.createNewFile()
				Checkout.withTransaction {
					String oldPicturePath = project.picturePath
					project.picturePath = file.absolutePath
					project = project.merge()
					project.save()
					new File(oldPicturePath).delete()
				}
			}
			else { 
				Notification.show("This file has already been uploaded!") 
				return
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
