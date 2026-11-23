package com.github.maxbalan.smtp.dummy.server.email;

import java.util.List;

public record EmailPage(List<CapturedEmail> items, Long nextId, boolean hasNext) {

    public EmailPage {
        items = List.copyOf(items);
    }
}
