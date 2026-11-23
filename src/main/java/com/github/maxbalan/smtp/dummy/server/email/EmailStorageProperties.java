package com.github.maxbalan.smtp.dummy.server.email;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("email.storage")
public record EmailStorageProperties(@Min(1) int maxTotalMessages) {

    public EmailStorageProperties {
        if (maxTotalMessages == 0) {
            maxTotalMessages = 100;
        }
    }
}
