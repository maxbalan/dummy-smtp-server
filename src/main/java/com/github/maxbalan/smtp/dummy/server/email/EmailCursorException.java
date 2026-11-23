package com.github.maxbalan.smtp.dummy.server.email;

public class EmailCursorException extends RuntimeException {

    public EmailCursorException(String message) {
        super(message);
    }

    public EmailCursorException(String message, Throwable cause) {
        super(message, cause);
    }
}
