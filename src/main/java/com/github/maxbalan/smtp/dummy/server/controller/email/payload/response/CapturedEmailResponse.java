package com.github.maxbalan.smtp.dummy.server.controller.email.payload.response;

import java.time.Instant;

public record CapturedEmailResponse(String id,
                                    Instant receivedAt,
                                    String sender,
                                    String recipient,
                                    String content) {}
