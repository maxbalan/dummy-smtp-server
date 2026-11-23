package com.github.maxbalan.smtp.dummy.server.controller.email;

public class EmailException extends RuntimeException {

    private final String code;

    public EmailException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
