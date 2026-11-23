# Repository guidance

This repository provides a Java 26/Spring Boot service that captures SMTP messages in bounded
process memory and exposes them through a versioned HTTP API.

Detailed project guidance lives in `resources/docs/ai/`.

Do not preload or read every linked document. Links in this file are a routing index, not an
instruction to load their contents. Before modifying or reviewing an area, read only the linked
document whose scope matches the current task. If the work expands into another area, read that
area's document only when it becomes relevant.

The files under `resources/docs/ai/` are the canonical detailed AI memory.

## Working agreements

- Keep the service database-free unless a task explicitly changes that product boundary.
- Preserve feature-oriented packages and keep HTTP payloads separate from internal email types.
- Treat `resources/oas/openapi.yaml` as the canonical REST API contract and update implementation,
  tests, examples, and Swagger annotations with every REST contract change.
- Keep MCP transport payloads separate from REST payloads and preserve the `/mcp` Streamable HTTP
  protocol contract when shared email behavior changes.
- Do not add real SMTP delivery, credentials, secrets, generated build output, or runtime logs.

## Documentation maintenance

Update the relevant canonical topic document when functionality changes. Update root guidance files
only when repository-wide agreements, routing, or always-loaded instructions change.

Maintain `resources/docs/email-capture/feature.md` and `resources/docs/mcp-access/feature.md`,
including their embedded Mermaid diagrams, whenever those features are created, changed, renamed,
or removed. Keep `README.md` synchronized with user-facing setup, API, MCP, configuration, and image
publishing behavior.

## Essential verification

Use the checks appropriate to the change:

```bash
make test
make build
make oas-validate
```

Run `make docker-build` when the application image or its build wiring changes. Do not run
`docker-release` unless a registry push is explicitly requested.
