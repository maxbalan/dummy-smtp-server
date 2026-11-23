# AI guidance index

Detailed AI-facing project guidance lives in this directory.

Do not preload or read every linked document. Links in this file are a routing index, not an
instruction to load their contents. Read only the document whose scope matches the current task.
If the work expands into another area, read that area's document only when it becomes relevant.

## Routing

| Task area | Read |
| --- | --- |
| Project purpose, Spring Boot wiring, package boundaries, or source layout | [Project overview](project-overview.md) |
| SMTP capture, retention, cursor pagination, errors, or `GET /api/v1/emails` | [SMTP capture and retrieval](smtp-capture-and-retrieval.md) |
| Streamable HTTP `/mcp`, MCP tools, schemas, hints, or MCP security assumptions | [Embedded MCP server](mcp-server.md) |
| Java, Gradle, configuration, tests, OpenAPI validation, Docker, or Make | [Development and operations](development-and-operations.md) |
| End-to-end email capture behavior and interaction diagram | [Email capture feature](../email-capture/feature.md) |
| End-to-end MCP behavior and interaction diagram | [MCP access feature](../mcp-access/feature.md) |
| Executable HTTP contract | [OpenAPI contract](../../oas/openapi.yaml) |

For REST API changes, read the SMTP capture guidance and OpenAPI contract together. For MCP changes,
read the embedded MCP server guidance and MCP access feature document. For packaging or runtime
changes that alter service behavior, also read development and operations.

## Maintenance responsibilities

Update the narrowest topic document whenever its implementation or contract changes. Add or change
a routing entry when a new independently relevant area is introduced. Keep `AGENTS.md` limited to
repository-wide instructions, and synchronize the feature document, OpenAPI contract, and public
`README.md` with their implemented behavior.
