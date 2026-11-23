package com.github.maxbalan.smtp.dummy.server;

import com.github.maxbalan.smtp.dummy.server.email.EmailStorageProperties;
import com.github.maxbalan.smtp.dummy.server.integration.smtp.SmtpServerProperties;
import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties({EmailStorageProperties.class, SmtpServerProperties.class})
public class DummySmtpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DummySmtpServerApplication.class, args);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
