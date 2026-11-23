package com.github.maxbalan.smtp.dummy.server;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OpenApiContractTest {

    @Test
    void documentsTheVersionedEmailCollectionEndpoint() throws Exception {
        var mapper = new ObjectMapper(new YAMLFactory());
        Map<String, Object> contract = mapper.readValue(
                Files.readString(Path.of("resources/oas/openapi.yaml")),
                new TypeReference<>() {});

        assertThat(contract.get("openapi")).isEqualTo("3.1.0");

        var info = (Map<?, ?>) contract.get("info");

        assertThat(info.get("version")).isEqualTo(Files.readString(Path.of("VERSION")).trim());

        var paths = (Map<?, ?>) contract.get("paths");

        assertThat(paths.containsKey("/api/v1/emails")).isTrue();
    }
}
