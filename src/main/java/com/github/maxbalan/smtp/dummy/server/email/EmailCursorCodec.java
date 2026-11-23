package com.github.maxbalan.smtp.dummy.server.email;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class EmailCursorCodec {

    private static final String VERSION_PREFIX = "v1:";

    public String encode(long lastId) {
        var value = VERSION_PREFIX + lastId;

        return Base64.getUrlEncoder()
                     .withoutPadding()
                     .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public long decode(String cursor) {
        try {
            var decoded = new String(
                    Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);

            if (!decoded.startsWith(VERSION_PREFIX)) {
                throw new EmailCursorException("Unsupported cursor version");
            }
            var id = Long.parseLong(decoded.substring(VERSION_PREFIX.length()));

            if (id < 1) {
                throw new EmailCursorException("Cursor identifier must be positive");
            }

            return id;
        } catch (IllegalArgumentException exception) {
            throw new EmailCursorException("Cursor is malformed", exception);
        }
    }
}
