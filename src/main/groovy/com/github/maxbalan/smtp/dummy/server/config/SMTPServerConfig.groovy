package com.github.maxbalan.smtp.dummy.server.config

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.MoreObjects

import javax.validation.constraints.Max
import javax.validation.constraints.Min

/**
 * 03/06/18
 *
 * @author maxbalan
 * */
class SMTPServerConfig {

    @JsonProperty("port")
    @Min(1L)
    @Max(65535L)
    private int port

    int getPort() {
        return port
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("port", this.port)
                          .toString()
    }

}
