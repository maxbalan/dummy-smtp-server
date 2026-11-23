package com.github.maxbalan.smtp.dummy.server.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmailCursorCodecTest {

    private final EmailCursorCodec codec = new EmailCursorCodec();

    @Test
    void roundTripsOpaqueCursor() {
        assertThat(codec.decode(codec.encode(42))).isEqualTo(42);
    }

    @Test
    void rejectsMalformedCursor() {
        assertThatThrownBy(() -> codec.decode("%%%"))
                .isInstanceOf(EmailCursorException.class)
                .hasMessage("Cursor is malformed");
    }
}
