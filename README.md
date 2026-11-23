# Dummy SMTP Server

An in-memory SMTP capture service for local development and automated tests. Applications send mail
to the SMTP listener, then clients retrieve captured messages through a versioned HTTP API or the
embedded Streamable HTTP MCP server. No database, authentication, or external mail provider is
required.

## Requirements

- Java 26 for local Gradle execution
- Docker for image and OpenAPI validation targets
- GNU Make for the convenience commands

The Gradle wrapper downloads Gradle 9.9.2 automatically.

## Run locally

```bash
make run
```

Default listeners:

| Service | Address |
| --- | --- |
| SMTP | `localhost:6969` |
| HTTP API | `http://localhost:9086` |
| MCP | `http://localhost:9086/mcp` |
| Swagger UI | `http://localhost:9086/swagger-ui.html` |
| OpenAPI contract | `http://localhost:9086/openapi/openapi.yaml` |
| Health | `http://localhost:9086/actuator/health` |

Send a message with any SMTP client, without authentication or TLS. For example:

```bash
curl --url smtp://localhost:6969 \
  --mail-from sender@example.test \
  --mail-rcpt user@example.test \
  --upload-file - <<'MAIL'
Subject: Test message

Hello from the dummy SMTP server.
MAIL
```

## API

The canonical OpenAPI 3.1 contract is [`resources/oas/openapi.yaml`](resources/oas/openapi.yaml).
The running service exposes the packaged contract at `/openapi/openapi.yaml` for Swagger UI and
other API tooling. The API uses path-based major versioning and RFC-style problem responses.

```http
GET /api/v1/emails?recipient=user@example.test&limit=50&cursor=<opaque-cursor>
```

Messages are returned newest first. `recipient` is required, `limit` defaults to 50 and is capped at
100, and `cursor` is the opaque value from the previous page.

```json
{
  "items": [
    {
      "id": "42",
      "receivedAt": "2026-01-01T12:00:00Z",
      "sender": "sender@example.test",
      "recipient": "user@example.test",
      "content": "Subject: Test message\r\n\r\nHello"
    }
  ],
  "pagination": {
    "cursor": "djE6NDI",
    "hasNext": true
  }
}
```

Unknown recipients return HTTP 200 with an empty `items` array. Invalid parameters or cursors return
HTTP 400 with `application/problem+json`.

## MCP

The MCP server starts in the same Spring Boot process and uses Streamable HTTP at:

```text
http://localhost:9086/mcp
```

It exposes these tools:

| Tool | Purpose |
| --- | --- |
| `list_captured_emails` | List exact-recipient messages with cursor pagination |
| `send_test_email` | Send a plain-text message through the embedded SMTP listener |
| `get_service_health` | Check service and SMTP listener readiness |

Connect from Pi after starting the service:

```bash
pi mcp add -l dummy-smtp --url http://localhost:9086/mcp
pi mcp list
```

`list_captured_emails` returns an empty pagination cursor when `hasNext` is false. Treat `hasNext` as
the continuation signal.

The MCP endpoint is unauthenticated. It is intended only for trusted development and test networks.
Disable it when unnecessary or add a dedicated security layer before remote or untrusted exposure.

## Configuration

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `DUMMY_SMTP_SERVICE_PORT` | `9086` | HTTP API port |
| `DUMMY_SMTP_SERVER_HOST` | `0.0.0.0` | SMTP bind address |
| `DUMMY_SMTP_SERVER_PORT` | `6969` | SMTP listener port |
| `DUMMY_SMTP_MAX_MESSAGE_SIZE_BYTES` | `1048576` | Maximum SMTP message size |
| `DUMMY_SMTP_MAX_CONNECTIONS` | `20` | Maximum simultaneous SMTP connections |
| `DUMMY_SMTP_MAX_TOTAL_MESSAGES` | `100` | Global in-memory retention limit |
| `DUMMY_SMTP_MCP_ENABLED` | `true` | Enable the embedded MCP endpoint |

Messages are process-local. The oldest message is evicted when the global retention limit is
exceeded, and all messages disappear when the process stops.

## Build and test

```bash
make test
make build
make oas-validate
```

The executable jar is created under `build/libs/`.

## Container image

Build and retain the image in the local Docker daemon:

```bash
make docker-build
```

Export that image to local file storage:

```bash
make docker-save DOCKER_IMAGE_TAG=1.0.0
```

The default archive is `build/docker/dummy-smtp-server-1.0.0.tar`. Override
`DOCKER_ARCHIVE=/path/image.tar` when needed.

Push to a remote registry by overriding the repository and tag:

```bash
make docker-release \
  DOCKER_REPOSITORY=ghcr.io/example \
  DOCKER_IMAGE_TAG=1.0.0
```

Run the default locally tagged image:

```bash
docker run --rm \
  -p 9086:9086 \
  -p 6969:6969 \
  localhost:5000/dummy-smtp-server:latest
```

`docker-release` performs a registry push. Authenticate with the target registry before invoking it.
