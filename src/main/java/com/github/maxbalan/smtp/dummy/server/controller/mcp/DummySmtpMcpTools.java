package com.github.maxbalan.smtp.dummy.server.controller.mcp;

import com.github.maxbalan.smtp.dummy.server.controller.mcp.payload.McpCapturedEmail;
import com.github.maxbalan.smtp.dummy.server.controller.mcp.payload.McpEmailCollection;
import com.github.maxbalan.smtp.dummy.server.controller.mcp.payload.McpHealthResult;
import com.github.maxbalan.smtp.dummy.server.controller.mcp.payload.McpPagination;
import com.github.maxbalan.smtp.dummy.server.controller.mcp.payload.McpSendEmailResult;
import com.github.maxbalan.smtp.dummy.server.email.CapturedEmail;
import com.github.maxbalan.smtp.dummy.server.email.EmailQueryException;
import com.github.maxbalan.smtp.dummy.server.email.EmailQueryService;
import com.github.maxbalan.smtp.dummy.server.integration.smtp.SmtpServerException;
import com.github.maxbalan.smtp.dummy.server.integration.smtp.SmtpServerLifecycle;
import com.github.maxbalan.smtp.dummy.server.integration.smtp.SmtpTestMessageSender;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class DummySmtpMcpTools {

    private static final int DEFAULT_PAGE_SIZE = 50;

    private final EmailQueryService emailQueryService;
    private final SmtpTestMessageSender messageSender;
    private final SmtpServerLifecycle smtpServerLifecycle;

    public DummySmtpMcpTools(EmailQueryService emailQueryService,
                             SmtpTestMessageSender messageSender,
                             SmtpServerLifecycle smtpServerLifecycle) {
        this.emailQueryService = emailQueryService;
        this.messageSender = messageSender;
        this.smtpServerLifecycle = smtpServerLifecycle;
    }

    @McpTool(
            name = "list_captured_emails",
            title = "List captured emails",
            description = "List captured SMTP messages for an exact recipient, newest first, "
                    + "with opaque cursor pagination.",
            generateOutputSchema = true,
            annotations = @McpTool.McpAnnotations(
                    title = "List captured emails",
                    readOnlyHint = true,
                    destructiveHint = false,
                    idempotentHint = true,
                    openWorldHint = false))
    public McpEmailCollection listCapturedEmails(
            @McpToolParam(
                    description = "Exact SMTP envelope recipient, for example user@example.test")
                    String recipient,
            @McpToolParam(
                    required = false,
                    description = "Maximum messages to return from 1 to 100; defaults to 50")
                    Integer limit,
            @McpToolParam(
                    required = false,
                    description = "Opaque cursor returned by a previous list_captured_emails call")
                    String cursor) {
        try {
            var pageSize = limit == null ? DEFAULT_PAGE_SIZE : limit;
            var page = emailQueryService.findByRecipient(recipient, pageSize, cursor);
            var items = page.items().stream().map(DummySmtpMcpTools::toMcpEmail).toList();

            var nextCursor = page.cursor() == null ? "" : page.cursor();

            return new McpEmailCollection(items, new McpPagination(nextCursor, page.hasNext()));
        } catch (EmailQueryException exception) {
            throw new McpToolException(exception.getMessage(), exception);
        }
    }

    @McpTool(
            name = "send_test_email",
            title = "Send a test email",
            description = "Send a plain-text message through the embedded SMTP listener so it can "
                    + "be retrieved as a captured email.",
            generateOutputSchema = true,
            annotations = @McpTool.McpAnnotations(
                    title = "Send a test email",
                    readOnlyHint = false,
                    destructiveHint = false,
                    idempotentHint = false,
                    openWorldHint = false))
    public McpSendEmailResult sendTestEmail(
            @McpToolParam(description = "SMTP envelope sender address") String sender,
            @McpToolParam(description = "SMTP envelope recipient address") String recipient,
            @McpToolParam(description = "Message subject") String subject,
            @McpToolParam(description = "Plain-text message body") String body) {
        validateMessage(sender, recipient, subject, body);

        try {
            messageSender.send(sender, recipient, subject, body);

            return new McpSendEmailResult("accepted", sender, recipient, subject);
        } catch (SmtpServerException exception) {
            throw new McpToolException(exception.getMessage(), exception);
        }
    }

    @McpTool(
            name = "get_service_health",
            title = "Get service health",
            description = "Check whether the embedded dummy SMTP service and its SMTP listener are running.",
            generateOutputSchema = true,
            annotations = @McpTool.McpAnnotations(
                    title = "Get service health",
                    readOnlyHint = true,
                    destructiveHint = false,
                    idempotentHint = true,
                    openWorldHint = false))
    public McpHealthResult getServiceHealth() {
        var smtpRunning = smtpServerLifecycle.isRunning();

        return new McpHealthResult(smtpRunning ? "UP" : "DEGRADED", smtpRunning, "/mcp");
    }

    private static McpCapturedEmail toMcpEmail(CapturedEmail email) {
        return new McpCapturedEmail(Long.toString(email.id()),
                                    email.receivedAt(),
                                    email.sender(),
                                    email.recipient(),
                                    email.content());
    }

    private static void validateMessage(String sender,
                                        String recipient,
                                        String subject,
                                        String body) {
        if (sender == null || sender.isBlank()) {
            throw new McpToolException("Sender must not be blank");
        }

        if (recipient == null || recipient.isBlank()) {
            throw new McpToolException("Recipient must not be blank");
        }

        if (subject == null || subject.isBlank()) {
            throw new McpToolException("Subject must not be blank");
        }

        if (body == null) {
            throw new McpToolException("Body must not be null");
        }
    }
}
