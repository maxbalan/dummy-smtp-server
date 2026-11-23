package com.github.maxbalan.smtp.dummy.server.email;

public class EmailQueryException extends RuntimeException {

    private final String code;

    public EmailQueryException(String code, String message) {
        super(message);
        this.code = code;
    }

    public EmailQueryException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
