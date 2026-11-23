# SMTP capture and retrieval

Read when changing SMTP acceptance, retention, recipient lookup, cursor pagination, HTTP errors, or
the captured-email response.

## Capture and retention

`SmtpServerLifecycle` builds SubEtha SMTP from `SmtpServerProperties` and participates in Spring's
lifecycle. The listener accepts every SMTP envelope and stores the sender, exact recipient, raw
UTF-8 content, receipt time, and a monotonically increasing ID in `EmailStore`.

Storage is process-local and bounded globally. Under a write lock, each new message is added to the
global ID map and its recipient index. If the configured maximum is exceeded, the globally oldest
message is removed from both indexes. Recipient matching is exact and case-sensitive.

SMTP authentication and TLS are not enabled. Do not describe this service as secure or expose it to
untrusted networks without a separate, explicit security design.

## Shared query behavior

`EmailQueryService` validates recipient, page size, and cursor semantics for both REST and MCP
callers. Each transport owns and maps its own response payload instead of sharing published DTOs.

## HTTP contract

The canonical REST contract is [`resources/oas/openapi.yaml`](../../oas/openapi.yaml):

```text
GET /api/v1/emails?recipient=<exact-recipient>&limit=<1-100>&cursor=<opaque>
```

`recipient` is required and limited to 320 characters. `limit` defaults to 50. Results are ordered by
message ID descending, so newest messages come first. A successful response always uses the
collection envelope:

```json
{
  "items": [],
  "pagination": {
    "cursor": null,
    "hasNext": false
  }
}
```

Each item contains string `id`, UTC `receivedAt`, SMTP envelope `sender` and `recipient`, and raw
`content`. Unknown recipients return HTTP 200 with an empty collection.

## Cursor rules

The cursor is URL-safe Base64 encoding of a versioned token containing the last returned message ID.
Clients must treat it as opaque. The next query selects IDs lower than that value, fetches
`limit + 1`, returns at most `limit`, and derives `hasNext` and the next cursor from the last returned
item. New messages received during traversal do not shift later pages because they have higher IDs.
Retention eviction can remove older entries before a client reaches them.

Malformed, unsupported, or non-positive cursors return HTTP 400 with `application/problem+json` and
code `invalid_cursor`. Missing or invalid query parameters use code `invalid_request`.

## Compatibility classification

The Spring Boot migration replaces the historical unversioned `/email` array contract. The new
`/api/v1/emails` envelope, fields, ordering, validation, and cursor semantics are a deliberate
breaking API redesign and establish the first explicit major-version contract.

## Verification focus

Cover SMTP delivery, multiple recipients, newest-first ordering, page boundaries, malformed cursors,
empty results, retention eviction, error media types, shared REST/MCP query behavior, and
synchronization between implementation, OpenAPI, Swagger UI, MCP schemas, tests, and README examples.
