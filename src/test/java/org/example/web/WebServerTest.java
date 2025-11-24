package org.example.web;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.example.web.resources.JaxRSResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class WebServerTest {

    WebServer server;

    @BeforeEach
    void before() {
        server = new WebServer();
        server.start();
    }

    @AfterEach
    void after() {
        server.stop();
    }


    @Test
    @DisplayName("can add a resource and respond to requests")
    void addResource() throws IOException, InterruptedException {
        WebServer server = new WebServer();

        @Path("/test")
        class TestResource implements JaxRSResource {
            @GET
            @Produces(MediaType.TEXT_PLAIN)
            public String test() {
                return "test";
            }
        }

        server.addResource(new TestResource());
        server.start();

        try {
            HttpResponse<String> response;
            try (var httpClient = java.net.http.HttpClient.newHttpClient()) {
                var request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(server.uri() + "/test"))
                        .GET()
                        .build();
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            }
            assertThat(response.statusCode(), is(200));
            assertThat(response.body(), is("test"));
        } finally {
            server.stop();
        }
    }

    @Test
    @DisplayName("can stop and start the webserver")
    void stopStart() {
        int previousPort = server.port();
        server.stop();
        assertThat(server.isRunning(), is(false));
        server.start();
        assertThat(server.isRunning(), is(true));
        assertThat(server.port(), is(not(previousPort)));
    }

    @Test
    @DisplayName("can stop the webserver")
    void stop() {
        server.stop();
        assertThat(server.isRunning(), is(false));
    }

    @Test
    @DisplayName("webserver has a valid URI after start")
    void uri() {
        assertThat(server.uri().toString(), equalTo("http://localhost:" + server.port()));
    }

    @Test
    @DisplayName("webserver has a valid port after start")
    void port() {
        assertThat(server.port(), greaterThan(0));
    }

    @Test
    @DisplayName("webserver is running after start")
    void isRunning() {
        assertThat(server.isRunning(), is(true));
    }
}