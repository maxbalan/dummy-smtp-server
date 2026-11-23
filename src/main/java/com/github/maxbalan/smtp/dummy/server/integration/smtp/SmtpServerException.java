package com.github.maxbalan.smtp.dummy.server.integration.smtp;

public class SmtpServerException extends RuntimeException {

    public SmtpServerException(String message) {
        super(message);
    }

    public SmtpServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
