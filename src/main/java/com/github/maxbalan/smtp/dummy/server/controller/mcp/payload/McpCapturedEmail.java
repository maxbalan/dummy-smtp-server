package com.github.maxbalan.smtp.dummy.server.controller.mcp.payload;

import java.time.Instant;

public record McpCapturedEmail(String id,
                               Instant receivedAt,
                               String sender,
                               String recipient,
                               String content) {}
