# Project overview

Read when changing the application's purpose, Spring Boot startup, package boundaries, or source
layout.

## Purpose

`dummy-smtp-server` is a database-free test support service. Applications send messages to its SMTP
listener, and tests or developers retrieve those messages through HTTP. Captured data is ephemeral
and bounded by a configurable global in-memory retention limit.

## Runtime composition

`DummySmtpServerApplication` starts Spring Boot and enables validated SMTP and storage properties.
Spring creates one `EmailStore`, which is shared by:

1. `integration.smtp.SmtpServerLifecycle`, which starts and stops SubEtha SMTP with the application;
2. `email.EmailQueryService`, which owns shared recipient and cursor query behavior;
3. `controller.email.EmailService`, which maps query results to the REST contract;
4. `controller.mcp.DummySmtpMcpTools`, which exposes MCP-specific tools and payloads.

A UTC `Clock` bean supplies timestamps and makes time deterministic in tests. Graceful Spring
shutdown stops the SMTP listener before the process exits.

## Package ownership

- `controller/email`: versioned HTTP controller, transport service, capability exception, and REST
  response payloads.
- `controller/mcp`: embedded MCP tools, MCP-specific payloads, and tool exception boundary.
- `controller/ApiError` and `controller/GlobalExceptionHandler`: the sole global HTTP error model and
  translation boundary.
- `email`: internal captured-message model, bounded store, shared query service, page models, and
  opaque cursor codec.
- `integration/smtp`: SMTP configuration, listener adapter, lifecycle, test-message sender, and
  integration exception.
- `src/main/resources/application.yaml`: runtime defaults and environment bindings.
- `resources/oas/openapi.yaml`: canonical OpenAPI 3.1 contract.
- `resources/docker/app/Dockerfile`: multi-stage application image.

HTTP payload records intentionally do not expose the internal `CapturedEmail` record directly.
`EmailService` owns the single conversion seam.

## Architecture boundaries

- No database or durable queue is used. Restarting the process clears all messages.
- The store uses a write/read lock around recipient indexes and global eviction, giving each capture
  a monotonically increasing process-local ID.
- The oldest message is evicted when `email.storage.max-total-messages` is exceeded.
- SMTP accepts every sender and recipient without authentication or TLS.
- The public REST API is read-only and versioned under `/api/v1`.
- The same process exposes an unauthenticated Streamable HTTP MCP endpoint at `/mcp` for trusted
  development and test networks.

For exact SMTP and HTTP behavior, read
[SMTP capture and retrieval](smtp-capture-and-retrieval.md). For toolchain and image operations, read
[Development and operations](development-and-operations.md).
