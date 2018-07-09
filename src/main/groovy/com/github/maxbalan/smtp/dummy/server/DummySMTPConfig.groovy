package com.github.maxbalan.smtp.dummy.server

import com.github.maxbalan.smtp.dummy.server.config.SMTPServerConfig
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * 02/06/18
 *
 * @author maxbalan
 * */
class DummySMTPConfig extends Configuration {

    @Valid
    @NotNull
    private SMTPServerConfig smtpServerConfig = new SMTPServerConfig()

    @JsonProperty("smtpServer")
    public SMTPServerConfig getSmtpServerConfig() {
        smtpServerConfig
    }

}
