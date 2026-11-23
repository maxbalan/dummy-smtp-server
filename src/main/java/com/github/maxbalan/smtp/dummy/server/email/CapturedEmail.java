package com.github.maxbalan.smtp.dummy.server.email;

import java.time.Instant;

public record CapturedEmail(long id,
                            Instant receivedAt,
                            String sender,
                            String recipient,
                            String content) {}
