package com.github.maxbalan.smtp.dummy.server.controller.email;

import com.github.maxbalan.smtp.dummy.server.controller.email.payload.response.CapturedEmailResponse;
import com.github.maxbalan.smtp.dummy.server.controller.email.payload.response.EmailCollectionResponse;
import com.github.maxbalan.smtp.dummy.server.controller.email.payload.response.PaginationResponse;
import com.github.maxbalan.smtp.dummy.server.email.CapturedEmail;
import com.github.maxbalan.smtp.dummy.server.email.EmailQueryException;
import com.github.maxbalan.smtp.dummy.server.email.EmailQueryService;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final EmailQueryService emailQueryService;

    public EmailService(EmailQueryService emailQueryService) {
        this.emailQueryService = emailQueryService;
    }

    public EmailCollectionResponse findByRecipient(String recipient, int limit, String cursor) {
        try {
            var page = emailQueryService.findByRecipient(recipient, limit, cursor);
            var items = page.items().stream().map(EmailService::toResponse).toList();

            return new EmailCollectionResponse(
                    items, new PaginationResponse(page.cursor(), page.hasNext()));
        } catch (EmailQueryException exception) {
            throw new EmailException(exception.code(), exception.getMessage(), exception);
        }
    }

    private static CapturedEmailResponse toResponse(CapturedEmail email) {
        return new CapturedEmailResponse(Long.toString(email.id()),
                                         email.receivedAt(),
                                         email.sender(),
                                         email.recipient(),
                                         email.content());
    }
}
