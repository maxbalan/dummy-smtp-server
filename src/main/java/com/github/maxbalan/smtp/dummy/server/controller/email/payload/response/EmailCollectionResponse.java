package com.github.maxbalan.smtp.dummy.server.controller.email.payload.response;

import java.util.List;

public record EmailCollectionResponse(List<CapturedEmailResponse> items,
                                      PaginationResponse pagination) {

    public EmailCollectionResponse {
        items = List.copyOf(items);
    }
}
