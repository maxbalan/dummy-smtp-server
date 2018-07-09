package com.github.maxbalan.smtp.dummy.server

import com.github.maxbalan.smtp.dummy.server.resources.EmailResource
import com.github.maxbalan.smtp.dummy.server.smtp.MailStorage
import com.github.maxbalan.smtp.dummy.server.smtp.SMTPDummyServer
import io.dropwizard.Application
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 02/06/18
 *
 * @author maxbalan
 * */
class DummySMTPServer extends Application<DummySMTPConfig> {

    private final Logger LOG = LoggerFactory.getLogger(DummySMTPServer.class)

    public static void main(String[] args) {
        new DummySMTPServer().run(args)
    }

    @Override
    void run(DummySMTPConfig configuration, Environment environment) throws Exception {
        def addr = InetAddress.getLocalHost()

        def smtpConfig = configuration.getSmtpServerConfig()

        LOG.info("Start SMTP Server with configuration[ {} ]", configuration.smtpServerConfig)

        def mailStorage = new MailStorage()
        def smtpServer = new SMTPDummyServer(mailStorage, smtpConfig.getPort())
        smtpServer.start(addr)

        // register resources
        LOG.info("Register resources!")
        environment.jersey().register(new EmailResource(mailStorage:  mailStorage))
    }

    @Override
    void initialize(Bootstrap<DummySMTPConfig> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                                               new EnvironmentVariableSubstitutor(false)
                )
        )
    }

}
