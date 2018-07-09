package com.github.maxbalan.smtp.dummy.server.smtp

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.subethamail.smtp.AuthenticationHandler

/**
 * 03/06/18
 *
 * @author maxbalan
 * */
class SMTPAuthHandler implements AuthenticationHandler {
    private final Logger LOG = LoggerFactory.getLogger(SMTPDummyServer)

    private static final String USER_IDENTITY = "User"

    //must be converted to base 64 enc
    private static final String PROMPT_USERNAME = "user"
    private static final String PROMPT_PASSWORD = "pass"

    private int pass = 0

    @Override
    public String auth(String clientInput) {
        LOG.info("Handling auth ...")
        String prompt

        if (++pass == 1) {
            prompt = SMTPAuthHandler.PROMPT_USERNAME
        } else if (pass == 2) {
            prompt = SMTPAuthHandler.PROMPT_PASSWORD
        } else {
            pass = 0
            prompt = null
        }
        return prompt
    }

    @Override
    public Object getIdentity() {
        LOG.info("Get auth identity ...")
        return SMTPAuthHandler.USER_IDENTITY
    }
}
