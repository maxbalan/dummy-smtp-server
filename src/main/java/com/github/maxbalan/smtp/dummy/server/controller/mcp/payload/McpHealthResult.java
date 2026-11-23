package com.github.maxbalan.smtp.dummy.server.controller.mcp.payload;

public record McpHealthResult(String status, boolean smtpRunning, String mcpEndpoint) {}
