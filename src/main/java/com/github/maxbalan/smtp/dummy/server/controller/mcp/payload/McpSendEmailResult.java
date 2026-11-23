package com.github.maxbalan.smtp.dummy.server.controller.mcp.payload;

public record McpSendEmailResult(String status,
                                 String sender,
                                 String recipient,
                                 String subject) {}
