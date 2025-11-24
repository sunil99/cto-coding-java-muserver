package org.example;

import org.junit.jupiter.api.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppTest {

    App app;

    @BeforeEach
    public void before() {
        app = new App();
        app.start();
    }

    @AfterEach
    public void after() {
        app.stop();
    }

    @Test
    @DisplayName("getPort returns a valid port number")
    public void getPort() {
        assertThat(app.port(), greaterThan(0));
    }

    @Test
    @DisplayName("isRunning returns true when the app is started")
    public void isRunning() {
        assertThat(app.isRunning(), is(true));
    }

    @Test
    @DisplayName("isRunning returns false when the app is stopped")
    public void isNotRunning() {
        app.stop();
        assertThat(app.isRunning(), is(false));
    }


    @Nested
    @DisplayName("API Endpoint Tests")
    class ApiEndpointTests {
        @Test
        @DisplayName("GET / returns 'Hello, World!'")
        public void testRootEndpoint() throws Exception {
            HttpResponse<String> response;
            try (HttpClient httpClient = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(app.uri())
                        .GET()
                        .build();
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            }

            assertThat(response.statusCode(), equalTo(200));
            assertThat(response.body(), equalTo("Hello, World!"));
        }

        @Test
        @DisplayName("GET /health returns isAvailable true")
        public void testHealthEndpoint() throws Exception {
            HttpResponse<String> response;
            try (HttpClient httpClient = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(app.uri().resolve("/health"))
                        .GET()
                        .build();
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            }

            assertThat(response.statusCode(), equalTo(200));
            assertThat(response.body(), equalTo("{\"isAvailable\":true}"));
        }
    }

}