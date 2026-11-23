package com.github.maxbalan.smtp.dummy.server.email;

import java.util.List;

public record EmailQueryPage(List<CapturedEmail> items, String cursor, boolean hasNext) {

    public EmailQueryPage {
        items = List.copyOf(items);
    }
}
