package com.github.maxbalan.smtp.dummy.server.smtp

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter
import org.subethamail.smtp.server.SMTPServer

/**
 * 03/06/18
 *
 * @author maxbalan
 * */
class SMTPDummyServer {

    private final Logger LOG = LoggerFactory.getLogger(SMTPDummyServer)

    private int smtpPort
    private final SMTPServer server

    public SMTPDummyServer(MailStorage mailStorage, int smtpPort) {
        this.smtpPort = smtpPort
        server = new SMTPServer(new SimpleMessageListenerAdapter(new MailHandler(mailStorage: mailStorage)),
                                new SMTPAuthHandlerFactory())
    }

    public void start(InetAddress addr) {
        try {
//            server.setBindAddress(addr)
            server.setPort(smtpPort)
//            server.setEnableTLS(true)
            server.start()

            LOG.info("SMTP server started")
        } catch (Exception e) {
            LOG.error("Something is wrong ", e)
        }
    }

}
