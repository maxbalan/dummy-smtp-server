package com.github.maxbalan.smtp.dummy.server.controller.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "smtp.server.port=0")
class McpStreamableHttpTest {

    private static final String ACCEPT = "application/json, text/event-stream";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Test
    void exposesRegisteredToolsOverStreamableHttp() throws Exception {
        var initializeResponse = post(null, """
                {"jsonrpc":"2.0","id":1,"method":"initialize","params":{
                  "protocolVersion":"2025-06-18",
                  "capabilities":{},
                  "clientInfo":{"name":"integration-test","version":"1.0"}
                }}
                """);
        var sessionId = initializeResponse.headers()
                .firstValue("Mcp-Session-Id")
                .orElseThrow();

        assertThat(initializeResponse.statusCode()).isEqualTo(200);
        assertThat(jsonData(initializeResponse.body()).at("/result/serverInfo/name").asText())
                .isEqualTo("dummy-smtp-server");

        var initializedResponse = post(sessionId, """
                {"jsonrpc":"2.0","method":"notifications/initialized"}
                """);
        var toolsResponse = post(sessionId, """
                {"jsonrpc":"2.0","id":2,"method":"tools/list","params":{}}
                """);
        var healthResponse = post(sessionId, """
                {"jsonrpc":"2.0","id":3,"method":"tools/call","params":{
                  "name":"get_service_health","arguments":{}
                }}
                """);
        var sendResponse = post(sessionId, """
                {"jsonrpc":"2.0","id":4,"method":"tools/call","params":{
                  "name":"send_test_email","arguments":{
                    "sender":"sender@example.test",
                    "recipient":"mcp@example.test",
                    "subject":"Protocol test",
                    "body":"Hello over Streamable HTTP"
                  }
                }}
                """);
        var listResponse = post(sessionId, """
                {"jsonrpc":"2.0","id":5,"method":"tools/call","params":{
                  "name":"list_captured_emails","arguments":{
                    "recipient":"mcp@example.test"
                  }
                }}
                """);

        assertThat(initializedResponse.statusCode()).isEqualTo(202);
        assertThat(toolNames(jsonData(toolsResponse.body())))
                .containsExactlyInAnyOrder(
                        "get_service_health",
                        "list_captured_emails",
                        "send_test_email");
        assertThat(jsonData(healthResponse.body())
                        .at("/result/structuredContent/status")
                        .asText())
                .isEqualTo("UP");
        assertThat(jsonData(sendResponse.body()).at("/result/isError").asBoolean()).isFalse();
        assertThat(jsonData(listResponse.body()).at("/result/isError").asBoolean()).isFalse();
        assertThat(jsonData(listResponse.body())
                        .at("/result/structuredContent/items/0/recipient")
                        .asText())
                .isEqualTo("mcp@example.test");
        assertThat(jsonData(listResponse.body())
                        .at("/result/structuredContent/pagination/cursor")
                        .asText())
                .isEmpty();
    }

    private HttpResponse<String> post(String sessionId, String body) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/mcp"))
                .header("Content-Type", "application/json")
                .header("Accept", ACCEPT);
        if (sessionId != null) {
            request.header("Mcp-Session-Id", sessionId);
        }

        return httpClient.send(
                request.POST(HttpRequest.BodyPublishers.ofString(body)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private JsonNode jsonData(String responseBody) throws Exception {
        var json = responseBody.lines()
                .filter(line -> line.startsWith("data: "))
                .map(line -> line.substring("data: ".length()))
                .findFirst()
                .orElse(responseBody);

        return objectMapper.readTree(json);
    }

    private static List<String> toolNames(JsonNode response) {
        return response.at("/result/tools").valueStream()
                .map(tool -> tool.get("name").asText())
                .toList();
    }
}
