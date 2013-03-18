package kennisbank

import grails.plugin.mail.MailService

class EmailService {

	  // Dynamic injection happens here.

	  MailService mailService

	  def sendEmail() {

        sendMail ({
        	to "lmsdklfldkfldksflsd@gmail.com"
        	from "youraccount@gmail.com"
        	subject "Test mail"
        	html """Hello!
        	This is a test email. """
        })
    }

    def serviceMethod() {

    }
}
