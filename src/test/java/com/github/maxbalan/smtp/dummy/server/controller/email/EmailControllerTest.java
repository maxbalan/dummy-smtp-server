package com.github.maxbalan.smtp.dummy.server.controller.email;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.maxbalan.smtp.dummy.server.controller.GlobalExceptionHandler;
import com.github.maxbalan.smtp.dummy.server.email.EmailCursorCodec;
import com.github.maxbalan.smtp.dummy.server.email.EmailQueryService;
import com.github.maxbalan.smtp.dummy.server.email.EmailStorageProperties;
import com.github.maxbalan.smtp.dummy.server.email.EmailStore;
import java.io.ByteArrayInputStream;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class EmailControllerTest {

    private MockMvc mockMvc;
    private EmailStore emailStore;

    @BeforeEach
    void setUp() {
        var clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        emailStore = new EmailStore(clock, new EmailStorageProperties(10));
        var queryService = new EmailQueryService(emailStore, new EmailCursorCodec());
        var service = new EmailService(queryService);
        mockMvc = MockMvcBuilders.standaloneSetup(new EmailController(service))
                .setControllerAdvice(new GlobalExceptionHandler(clock))
                .build();
    }

    @Test
    void listsCapturedEmailsUsingTheVersionedContract() throws Exception {
        emailStore.save(
                "sender@example.test",
                "user@example.test",
                new ByteArrayInputStream("Subject: Test".getBytes()));

        mockMvc.perform(get("/api/v1/emails").param("recipient", "user@example.test"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items[0].id").value("1"))
                .andExpect(jsonPath("$.items[0].sender").value("sender@example.test"))
                .andExpect(jsonPath("$.pagination.cursor").doesNotExist())
                .andExpect(jsonPath("$.pagination.hasNext").value(false));
    }

    @Test
    void returnsProblemDetailsForMalformedCursor() throws Exception {
        mockMvc.perform(get("/api/v1/emails")
                        .param("recipient", "user@example.test")
                        .param("cursor", "bad"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("invalid_cursor"));
    }
}
