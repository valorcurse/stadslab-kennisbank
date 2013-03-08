package kennisbank.fabtool.projects

import com.vaadin.server.ExternalResource
import com.vaadin.ui.Button
import com.vaadin.ui.Link
import com.vaadin.ui.TabSheet
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.TabSheet.Tab
import groovy.transform.InheritConstructors
import com.vaadin.ui.themes.Reindeer
import kennisbank.*
import kennisbank.project.Document;

import com.vaadin.server.FileResource
import com.vaadin.server.FileDownloader



class DownloadLink extends Button implements Button.ClickListener {

	String documentName
	Document document
	FileDownloader downloader
	FileResource file

	DownloadLink(Document document) {
		addClickListener(this)
		file = new FileResource(new File(document.getPath()))
		downloader = new FileDownloader(file)
		downloader.extend(this)
		setStyleName(Reindeer.BUTTON_LINK)
		setCaption(document.getTitle())
		this.document = document
		documentName = document.getTitle()
	}

	public void buttonClick(ClickEvent event) {
	}
	
}
