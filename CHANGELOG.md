[1.0.0]
  Features
    - Add a versioned cursor-paginated API for retrieving captured messages with standardized problem responses
    - Add bounded in-memory retention and graceful SMTP lifecycle management without a database
    - Add Make workflows for local image builds, archive export, OpenAPI validation, and remote registry publishing
    - Embed a Streamable HTTP MCP server with tools for sending, listing, and health-checking test email flows
  Improvements
    - Migrate the service from Groovy and Dropwizard to Java 26, Gradle 9.9.2, and Spring Boot 4.2.0
    - Add a canonical OpenAPI 3.1 contract, Swagger UI, health endpoint, feature documentation, and automated tests
    - Build and run the container as a non-root user with a Java 26 multi-stage image
