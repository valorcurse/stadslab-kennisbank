package kennisbank.utils

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

	DownloadLink(String path, String name) {
		addClickListener(this)
		setStyleName(Reindeer.BUTTON_LINK)

		File file = new File(path)
		setCaption(name)

		FileResource fileResource = new FileResource(file)
		FileDownloader downloader = new FileDownloader(fileResource)
		downloader.extend(this)

	}

	public void buttonClick(ClickEvent event) {
	}
	
}
