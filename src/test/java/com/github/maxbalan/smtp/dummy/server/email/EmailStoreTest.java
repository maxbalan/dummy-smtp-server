package com.github.maxbalan.smtp.dummy.server.email;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class EmailStoreTest {

    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void returnsNewestMessagesFirstWithCursorPagination() {
        var store = new EmailStore(CLOCK, new EmailStorageProperties(10));
        store.save("one@example.test", "user@example.test", "one");
        store.save("two@example.test", "user@example.test", "two");
        store.save("three@example.test", "user@example.test", "three");

        var firstPage = store.findByRecipient("user@example.test", null, 2);
        var secondPage = store.findByRecipient(
                "user@example.test", firstPage.nextId(), 2);

        assertThat(firstPage.items()).extracting(CapturedEmail::content)
                .containsExactly("three", "two");
        assertThat(firstPage.hasNext()).isTrue();
        assertThat(secondPage.items()).extracting(CapturedEmail::content)
                .containsExactly("one");
        assertThat(secondPage.hasNext()).isFalse();
    }

    @Test
    void evictsOldestMessagesAtTheConfiguredGlobalLimit() {
        var store = new EmailStore(CLOCK, new EmailStorageProperties(2));
        store.save("one@example.test", "first@example.test", "one");
        store.save("two@example.test", "second@example.test", "two");
        store.save("three@example.test", "second@example.test", "three");

        assertThat(store.findByRecipient("first@example.test", null, 10).items()).isEmpty();
        assertThat(store.findByRecipient("second@example.test", null, 10).items())
                .extracting(CapturedEmail::content)
                .containsExactly("three", "two");
    }
}
