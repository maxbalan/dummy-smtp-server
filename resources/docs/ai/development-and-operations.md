# Development and operations

Read when changing Java or dependency versions, Gradle, configuration, tests, OpenAPI validation,
Docker packaging, Make targets, or publishing workflows.

## Toolchain and build

The service uses Java 26, Gradle 9.9.2, Spring Boot 4.2.0, and Spring AI 2.1.0. The Gradle Java
toolchain requires Java 26. The executable artifact is one Spring Boot jar containing REST, MCP, and
SMTP interfaces.

```bash
make test
make build
make run
```

`make test` runs unit, controller, Spring context, OpenAPI presence, real socket-level SMTP, direct
MCP tool, and Streamable HTTP MCP protocol tests. `make build` runs a clean `bootJar` build.

## Runtime configuration

`src/main/resources/application.yaml` maps environment variables to validated configuration:

| Purpose | Environment variable | Default |
| --- | --- | ---: |
| HTTP API | `DUMMY_SMTP_SERVICE_PORT` | `9086` |
| SMTP bind host | `DUMMY_SMTP_SERVER_HOST` | `0.0.0.0` |
| SMTP port | `DUMMY_SMTP_SERVER_PORT` | `6969` |
| Maximum message bytes | `DUMMY_SMTP_MAX_MESSAGE_SIZE_BYTES` | `1048576` |
| Maximum SMTP connections | `DUMMY_SMTP_MAX_CONNECTIONS` | `20` |
| Global retained messages | `DUMMY_SMTP_MAX_TOTAL_MESSAGES` | `100` |
| Embedded MCP server | `DUMMY_SMTP_MCP_ENABLED` | `true` |

Spring Actuator exposes health and info endpoints. Graceful shutdown has a ten-second phase timeout.
Swagger UI reads the committed contract copied into the jar at build time and served through the
explicit `/openapi/openapi.yaml` classpath resource mapping. Spring AI exposes the synchronous
Streamable HTTP MCP endpoint at `/mcp` on the same HTTP port.

## OpenAPI workflow

`resources/oas/openapi.yaml` is a monolithic OpenAPI 3.1 contract. Validate it with pinned Redocly CLI
through Docker:

```bash
make oas-validate
```

Any API change must update the controller, payloads, error handling, tests, OpenAPI contract, feature
document, and public README in the same change.

## Container workflow

The multi-stage image at `resources/docker/app/Dockerfile` builds with Java 26 and runs on a Java 26
JRE as a non-root user. It exposes HTTP 9086 and SMTP 6969.

```bash
make docker-build
make docker-save DOCKER_IMAGE_TAG=1.0.0
make docker-release DOCKER_REPOSITORY=ghcr.io/example DOCKER_IMAGE_TAG=1.0.0
```

`docker-build` tags the image in the local daemon as
`$(DOCKER_REPOSITORY)/$(DOCKER_IMAGE_NAME):$(DOCKER_IMAGE_TAG)`; defaults are
`localhost:5000/dummy-smtp-server:latest`. `docker-save` exports that image to a tar archive.
`docker-release` rebuilds and pushes to the configured registry and must only be run when a push is
explicitly authorized.

## Proportionate validation

- Java or configuration: `make test` and `make build`.
- API contract: add `make oas-validate`.
- Dockerfile or image wiring: add `make docker-build` and smoke-test the health and SMTP ports when
  the environment supports it.
- Documentation-only: validate local links, Mermaid fences, and `git diff --check`.
