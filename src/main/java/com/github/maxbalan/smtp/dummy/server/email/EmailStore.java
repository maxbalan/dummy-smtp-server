package com.github.maxbalan.smtp.dummy.server.email;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.springframework.stereotype.Component;

@Component
public class EmailStore {

    private final AtomicLong sequence = new AtomicLong();
    private final NavigableMap<Long, CapturedEmail> allMessages = new TreeMap<>();
    private final HashMap<String, NavigableMap<Long, CapturedEmail>> messagesByRecipient =
            new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Clock clock;
    private final int maxTotalMessages;

    public EmailStore(Clock clock, EmailStorageProperties properties) {
        this.clock = clock;
        this.maxTotalMessages = properties.maxTotalMessages();
    }

    public CapturedEmail save(String sender, String recipient, InputStream messageData)
            throws IOException {
        var content = new String(messageData.readAllBytes(), StandardCharsets.UTF_8);
        return save(sender, recipient, content);
    }

    CapturedEmail save(String sender, String recipient, String content) {
        lock.writeLock().lock();
        try {
            var id = sequence.incrementAndGet();
            var email = new CapturedEmail(id,
                                          clock.instant(),
                                          sender,
                                          recipient,
                                          content);
            allMessages.put(id, email);
            messagesByRecipient
                    .computeIfAbsent(recipient, ignored -> new TreeMap<>())
                    .put(id, email);
            evictOldestMessages();
            return email;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public EmailPage findByRecipient(String recipient, Long beforeId, int limit) {
        lock.readLock().lock();
        try {
            var recipientMessages = messagesByRecipient.get(recipient);
            if (recipientMessages == null) {
                return new EmailPage(List.of(), null, false);
            }

            NavigableMap<Long, CapturedEmail> candidates = beforeId == null
                    ? recipientMessages.descendingMap()
                    : recipientMessages.headMap(beforeId, false).descendingMap();
            var fetched = new ArrayList<CapturedEmail>(limit + 1);
            for (var email : candidates.values()) {
                fetched.add(email);
                if (fetched.size() == limit + 1) {
                    break;
                }
            }

            var hasNext = fetched.size() > limit;
            if (hasNext) {
                fetched.removeLast();
            }
            var nextId = hasNext ? fetched.getLast().id() : null;

            return new EmailPage(fetched, nextId, hasNext);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void evictOldestMessages() {
        while (allMessages.size() > maxTotalMessages) {
            var oldest = allMessages.pollFirstEntry();
            if (oldest == null) {
                return;
            }
            var email = oldest.getValue();
            var recipientMessages = messagesByRecipient.get(email.recipient());
            if (recipientMessages != null) {
                recipientMessages.remove(email.id());
                if (recipientMessages.isEmpty()) {
                    messagesByRecipient.remove(email.recipient());
                }
            }
        }
    }
}
