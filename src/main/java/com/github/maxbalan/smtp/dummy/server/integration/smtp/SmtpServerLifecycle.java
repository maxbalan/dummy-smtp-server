package com.github.maxbalan.smtp.dummy.server.integration.smtp;

import com.github.maxbalan.smtp.dummy.server.email.EmailStore;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.server.SMTPServer;

@Component
public class SmtpServerLifecycle implements SmartLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmtpServerLifecycle.class);

    private final SMTPServer server;
    private volatile boolean running;

    public SmtpServerLifecycle(EmailStore emailStore, SmtpServerProperties properties) {
        this.server = createServer(emailStore, properties);
    }

    @Override
    public synchronized void start() {
        if (running) {
            return;
        }
        try {
            server.start();

            running = true;

            LOGGER.info("SMTP server listening on {}", server.getDisplayableLocalSocketAddress());
        } catch (RuntimeException exception) {
            throw new SmtpServerException("Failed to start SMTP server", exception);
        }
    }

    @Override
    public synchronized void stop() {
        if (!running) {
            return;
        }
        server.stop();

        running = false;

        LOGGER.info("SMTP server stopped");
    }

    @Override
    public boolean isRunning() {
        return running && server.isRunning();
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE;
    }

    public int allocatedPort() {
        return server.getPortAllocated();
    }

    private static SMTPServer createServer(
            EmailStore emailStore, SmtpServerProperties properties) {
        try {
            return SMTPServer.port(properties.port())
                             .bindAddress(InetAddress.getByName(properties.host()))
                             .softwareName("dummy-smtp-server")
                             .maxMessageSize(properties.maxMessageSizeBytes())
                             .maxConnections(properties.maxConnections())
                             .insertReceivedHeaders(false)
                             .simpleMessageListener(new SmtpMessageListener(emailStore))
                             .build();
        } catch (UnknownHostException exception) {
            throw new SmtpServerException(
                    "Invalid SMTP bind host: " + properties.host(), exception);
        }
    }
}
