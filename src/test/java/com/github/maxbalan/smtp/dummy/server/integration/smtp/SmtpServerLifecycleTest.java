package com.github.maxbalan.smtp.dummy.server.integration.smtp;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.maxbalan.smtp.dummy.server.email.EmailStorageProperties;
import com.github.maxbalan.smtp.dummy.server.email.EmailStore;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import org.junit.jupiter.api.Test;

class SmtpServerLifecycleTest {

    @Test
    void acceptsAndStoresAnSmtpMessage() throws Exception {
        var store = new EmailStore(Clock.systemUTC(), new EmailStorageProperties(10));
        var properties = new SmtpServerProperties("127.0.0.1",
                                                  0,
                                                  1_048_576,
                                                  5);
        var lifecycle = new SmtpServerLifecycle(store, properties);
        lifecycle.start();

        try (var socket = new Socket("127.0.0.1", lifecycle.allocatedPort());
                var reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
                var writer = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII))) {
            assertCode(reader, 220);
            command(writer,
                    reader,
                    "HELO localhost",
                    250);
            command(writer,
                    reader,
                    "MAIL FROM:<sender@example.test>",
                    250);
            command(writer,
                    reader,
                    "RCPT TO:<user@example.test>",
                    250);
            command(writer,
                    reader,
                    "DATA",
                    354);
            writer.write("Subject: Integration test\r\n\r\nHello\r\n.\r\n");
            writer.flush();
            assertCode(reader, 250);
            command(writer,
                    reader,
                    "QUIT",
                    221);
        } finally {
            lifecycle.stop();
        }

        var messages = store.findByRecipient("user@example.test", null, 10).items();
        assertThat(messages).hasSize(1);
        assertThat(messages.getFirst().content())
                .startsWith("Subject: Integration test")
                .contains("Hello")
                .doesNotContain("Received:");
    }

    private static void command(BufferedWriter writer,
                                BufferedReader reader,
                                String command,
                                int expectedCode) throws Exception {
        writer.write(command + "\r\n");
        writer.flush();
        assertCode(reader, expectedCode);
    }

    private static void assertCode(BufferedReader reader, int expectedCode) throws Exception {
        var line = reader.readLine();
        assertThat(line).startsWith(Integer.toString(expectedCode));
    }
}
