package com.github.maxbalan.smtp.dummy.server.controller.email;

import com.github.maxbalan.smtp.dummy.server.controller.ApiError;
import com.github.maxbalan.smtp.dummy.server.controller.email.payload.response.EmailCollectionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(path = "/api/v1/emails", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Captured emails", description = "Read messages accepted by the SMTP listener")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    @Operation(
            summary = "List captured emails for a recipient",
            description = "Returns newest messages first using an opaque cursor for pagination.")
    @ApiResponse(responseCode = "200", description = "Captured messages, possibly empty")
    @ApiResponse(
            responseCode = "400",
            description = "A query parameter or cursor is invalid",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "https://dummy-smtp-server.local/problems/invalid_cursor",
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Cursor is malformed",
                              "instance": "/api/v1/emails",
                              "code": "invalid_cursor",
                              "timestamp": "2026-01-01T00:00:00Z"
                            }
                            """)))
    @ApiResponse(
            responseCode = "500",
            description = "An unexpected server error occurred",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    public EmailCollectionResponse findByRecipient(
            @Parameter(description = "Exact SMTP envelope recipient", example = "user@example.test")
                    @RequestParam
                    @NotBlank
                    @Size(max = 320)
                    String recipient,
            @Parameter(description = "Maximum messages to return", example = "50")
                    @RequestParam(defaultValue = "50")
                    @Min(1)
                    @Max(100)
                    int limit,
            @Parameter(description = "Opaque cursor returned by the previous page")
                    @RequestParam(required = false)
                    @Size(max = 256)
                    String cursor) {
        return emailService.findByRecipient(recipient, limit, cursor);
    }
}
