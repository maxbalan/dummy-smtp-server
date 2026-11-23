package com.github.maxbalan.smtp.dummy.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "smtp.server.port=0")
class DummySmtpServerApplicationTest {

    @Test
    void contextLoads() {}
}
