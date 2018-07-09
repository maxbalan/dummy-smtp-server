package com.github.maxbalan.smtp.dummy.server.smtp

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.subethamail.smtp.AuthenticationHandler
import org.subethamail.smtp.AuthenticationHandlerFactory

/**
 * 03/06/18
 *
 * @author maxbalan
 * */
class SMTPAuthHandlerFactory implements AuthenticationHandlerFactory {
    private final Logger LOG = LoggerFactory.getLogger(SMTPDummyServer)

    private static final String LOGIN_MECHANISM = "LOGIN"

    @Override
    List<String> getAuthenticationMechanisms() {
        LOG.info("Authentication mechanism ... ")
//        [LOGIN_MECHANISM]
        ["PLAIN"]
    }

    @Override
    AuthenticationHandler create() {
        LOG.info("authentication handler creating ...")
        new SMTPAuthHandler()
    }

}
