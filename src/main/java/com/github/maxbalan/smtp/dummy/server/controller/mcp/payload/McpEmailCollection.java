package com.github.maxbalan.smtp.dummy.server.controller.mcp.payload;

import java.util.List;

public record McpEmailCollection(List<McpCapturedEmail> items, McpPagination pagination) {

    public McpEmailCollection {
        items = List.copyOf(items);
    }
}
