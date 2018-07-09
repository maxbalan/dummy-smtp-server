package com.github.maxbalan.smtp.dummy.server.smtp

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.subethamail.smtp.TooMuchDataException
import org.subethamail.smtp.helper.SimpleMessageListener

/**
 * 02/06/18
 *
 * @author maxbalan
 * */
class MailHandler implements SimpleMessageListener {
    private final Logger LOG = LoggerFactory.getLogger(SMTPDummyServer)

    private MailStorage mailStorage

    @Override
    boolean accept(String from, String recipient) {
        true
    }

    @Override
    void deliver(String from, String recipient, InputStream data) throws TooMuchDataException, IOException {
        LOG.info("Handling incoming email ... ")
        mailStorage.saveEmail(from, recipient, data)
    }

}
