package com.github.maxbalan.smtp.dummy.server.integration.smtp;

import com.github.maxbalan.smtp.dummy.server.email.EmailStore;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;

class SmtpMessageListener implements SimpleMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmtpMessageListener.class);

    private final EmailStore emailStore;

    SmtpMessageListener(EmailStore emailStore) {
        this.emailStore = emailStore;
    }

    @Override
    public boolean accept(String sender, String recipient) {
        return true;
    }

    @Override
    public void deliver(String sender, String recipient, InputStream data)
            throws TooMuchDataException, IOException {
        var email = emailStore.save(sender, recipient, data);

        LOGGER.info("Captured SMTP message id={} sender={} recipient={}", email.id(), sender, recipient);
    }
}
