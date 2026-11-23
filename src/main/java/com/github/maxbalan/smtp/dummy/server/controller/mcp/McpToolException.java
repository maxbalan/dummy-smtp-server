package com.github.maxbalan.smtp.dummy.server.controller.mcp;

public class McpToolException extends RuntimeException {

    public McpToolException(String message) {
        super(message);
    }

    public McpToolException(String message, Throwable cause) {
        super(message, cause);
    }
}
