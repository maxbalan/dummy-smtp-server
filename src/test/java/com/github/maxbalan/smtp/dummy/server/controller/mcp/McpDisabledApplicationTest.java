package com.github.maxbalan.smtp.dummy.server.controller.mcp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "smtp.server.port=0",
    "spring.ai.mcp.server.enabled=false"
})
class McpDisabledApplicationTest {

    @Test
    void contextLoadsWithMcpDisabled() {}
}
