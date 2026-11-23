package com.github.maxbalan.smtp.dummy.server.integration.smtp;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.springframework.stereotype.Component;

@Component
public class SmtpTestMessageSender {

    private final SmtpServerLifecycle lifecycle;
    private final SmtpServerProperties properties;

    public SmtpTestMessageSender(
            SmtpServerLifecycle lifecycle, SmtpServerProperties properties) {
        this.lifecycle = lifecycle;
        this.properties = properties;
    }

    public void send(String sender,
                     String recipient,
                     String subject,
                     String body) {
        if (!lifecycle.isRunning()) {
            throw new SmtpServerException("SMTP server is not running");
        }

        try {
            var session = Session.getInstance(mailProperties());
            var message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject, StandardCharsets.UTF_8.name());
            message.setText(body, StandardCharsets.UTF_8.name());

            Transport.send(message);
        } catch (Exception exception) {
            throw new SmtpServerException("Failed to send test SMTP message", exception);
        }
    }

    private Properties mailProperties() {
        var mailProperties = new Properties();
        mailProperties.setProperty("mail.smtp.host", connectionHost());
        mailProperties.setProperty("mail.smtp.port", Integer.toString(lifecycle.allocatedPort()));
        mailProperties.setProperty("mail.smtp.auth", "false");
        mailProperties.setProperty("mail.smtp.starttls.enable", "false");
        mailProperties.setProperty("mail.smtp.connectiontimeout", "5000");
        mailProperties.setProperty("mail.smtp.timeout", "5000");

        return mailProperties;
    }

    private String connectionHost() {
        return switch (properties.host()) {
            case "0.0.0.0", "::", "[::]" -> "127.0.0.1";
            default -> properties.host();
        };
    }
}
