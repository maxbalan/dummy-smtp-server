package com.github.maxbalan.smtp.dummy.server.controller;

import java.time.Instant;

public record ApiError(String type,
                       String title,
                       int status,
                       String detail,
                       String instance,
                       String code,
                       Instant timestamp) {}
