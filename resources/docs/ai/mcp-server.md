# Embedded MCP server

Read when changing the Streamable HTTP MCP transport, tools, tool schemas, annotations, endpoint,
security assumptions, or MCP integration tests.

## Runtime contract

Spring AI 2.1.0 embeds a synchronous Streamable HTTP MCP server in the same Spring Boot process as
the REST API and SMTP listener. It starts and stops with the application and listens at:

```text
POST/GET/DELETE /mcp
```

The transport uses MCP protocol version negotiation and session IDs. Clients must send both
`application/json` and `text/event-stream` in `Accept` because tool responses can be delivered as
server-sent events. The endpoint is enabled by default and can be disabled with
`DUMMY_SMTP_MCP_ENABLED=false`.

The MCP server is intentionally unauthenticated, matching the service's trusted test-network scope.
Do not expose it to an untrusted network. Any remote or shared deployment requires a separate
Spring Security, reverse-proxy, bearer-token, or OAuth design before exposure.

## Tool ownership

`controller.mcp.DummySmtpMcpTools` owns the MCP-published contract and maps shared domain results to
MCP-specific payload records. It must not publish REST response records directly.

The registered tools are:

| Tool | Behavior | Hints |
| --- | --- | --- |
| `list_captured_emails` | Exact-recipient, newest-first cursor query | read-only, idempotent, closed-world |
| `send_test_email` | Sends plain text through the embedded SMTP listener | non-read-only, non-destructive, non-idempotent, closed-world |
| `get_service_health` | Reports service and SMTP listener readiness | read-only, idempotent, closed-world |

`list_captured_emails` uses the shared `EmailQueryService`, but owns its own output shape. Its
pagination cursor is an empty string when `hasNext` is false because generated MCP output schemas do
not allow a nullable string. Clients must use `hasNext` as the authoritative continuation signal.

`send_test_email` delegates to `integration.smtp.SmtpTestMessageSender`, which connects to the actual
embedded SMTP listener. Do not bypass the SMTP path by writing directly to `EmailStore`.

## Shared query boundary

`email.EmailQueryService` owns recipient, limit, and cursor validation for both REST and MCP callers.
It returns internal `EmailQueryPage` values. Each transport maps that result into its own published
payload and wraps `EmailQueryException` in its own exception type.

## Verification

Changes must cover:

- MCP initialize, initialized notification, tool listing, and tool invocation over `/mcp`;
- exact registered tool names and annotations;
- send-through-SMTP followed by MCP retrieval;
- cursor and validation failures as MCP tool errors;
- output schema compatibility, especially empty pagination cursors;
- startup and graceful shutdown with MCP enabled and disabled;
- README and feature documentation synchronization.
