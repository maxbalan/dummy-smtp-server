package com.github.maxbalan.smtp.dummy.server.controller.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.maxbalan.smtp.dummy.server.email.EmailCursorCodec;
import com.github.maxbalan.smtp.dummy.server.email.EmailQueryService;
import com.github.maxbalan.smtp.dummy.server.email.EmailStorageProperties;
import com.github.maxbalan.smtp.dummy.server.email.EmailStore;
import com.github.maxbalan.smtp.dummy.server.integration.smtp.SmtpServerLifecycle;
import com.github.maxbalan.smtp.dummy.server.integration.smtp.SmtpServerProperties;
import com.github.maxbalan.smtp.dummy.server.integration.smtp.SmtpTestMessageSender;
import java.time.Clock;
import org.junit.jupiter.api.Test;

class DummySmtpMcpToolsTest {

    @Test
    void sendsAndListsMessagesThroughTheEmbeddedSmtpFlow() {
        var properties = new SmtpServerProperties("127.0.0.1",
                                                  0,
                                                  1_048_576,
                                                  5);
        var store = new EmailStore(Clock.systemUTC(), new EmailStorageProperties(10));
        var lifecycle = new SmtpServerLifecycle(store, properties);
        var queryService = new EmailQueryService(store, new EmailCursorCodec());
        var sender = new SmtpTestMessageSender(lifecycle, properties);
        var tools = new DummySmtpMcpTools(queryService, sender, lifecycle);
        lifecycle.start();

        try {
            var sendResult = tools.sendTestEmail(
                    "sender@example.test",
                    "user@example.test",
                    "MCP test",
                    "Hello from MCP");
            var messages = tools.listCapturedEmails("user@example.test", null, null);
            var health = tools.getServiceHealth();

            assertThat(sendResult.status()).isEqualTo("accepted");
            assertThat(messages.items()).hasSize(1);
            assertThat(messages.items().getFirst().content())
                    .contains("Subject: MCP test", "Hello from MCP");
            assertThat(messages.pagination().hasNext()).isFalse();
            assertThat(messages.pagination().cursor()).isEmpty();
            assertThat(health.status()).isEqualTo("UP");
            assertThat(health.smtpRunning()).isTrue();
        } finally {
            lifecycle.stop();
        }
    }
}
