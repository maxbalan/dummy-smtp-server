package com.github.maxbalan.smtp.dummy.server.integration.smtp;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("smtp.server")
public record SmtpServerProperties(@NotBlank String host,
                                   @Min(0) @Max(65_535) int port,
                                   @Min(1) int maxMessageSizeBytes,
                                   @Min(1) int maxConnections) {

    public SmtpServerProperties {
        host = host == null ? "0.0.0.0" : host;
        maxMessageSizeBytes = maxMessageSizeBytes == 0 ? 1_048_576 : maxMessageSizeBytes;
        maxConnections = maxConnections == 0 ? 20 : maxConnections;
    }
}
