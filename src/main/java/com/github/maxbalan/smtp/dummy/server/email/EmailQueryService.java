package com.github.maxbalan.smtp.dummy.server.email;

import org.springframework.stereotype.Service;

@Service
public class EmailQueryService {

    private static final int MAX_RECIPIENT_LENGTH = 320;
    private static final int MAX_PAGE_SIZE = 100;

    private final EmailStore emailStore;
    private final EmailCursorCodec cursorCodec;

    public EmailQueryService(EmailStore emailStore, EmailCursorCodec cursorCodec) {
        this.emailStore = emailStore;
        this.cursorCodec = cursorCodec;
    }

    public EmailQueryPage findByRecipient(String recipient, int limit, String cursor) {
        validate(recipient, limit);

        try {
            var beforeId = cursor == null ? null : cursorCodec.decode(cursor);
            var page = emailStore.findByRecipient(recipient, beforeId, limit);
            var nextCursor = page.nextId() == null ? null : cursorCodec.encode(page.nextId());

            return new EmailQueryPage(page.items(), nextCursor, page.hasNext());
        } catch (EmailCursorException exception) {
            throw new EmailQueryException("invalid_cursor", exception.getMessage(), exception);
        }
    }

    private static void validate(String recipient, int limit) {
        if (recipient == null || recipient.isBlank()) {
            throw new EmailQueryException("invalid_recipient", "Recipient must not be blank");
        }

        if (recipient.length() > MAX_RECIPIENT_LENGTH) {
            throw new EmailQueryException(
                    "invalid_recipient", "Recipient must not exceed 320 characters");
        }

        if (limit < 1 || limit > MAX_PAGE_SIZE) {
            throw new EmailQueryException("invalid_limit", "Limit must be between 1 and 100");
        }
    }
}
