package org.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MyMuServerTest {

    MyMuServer server;

    @Before
    public void before() {
        server = new MyMuServer();
        server.start();
    }

    @After
    public void after() {
        server.stop();
    }

    @Test
    public void getPort() {
        assertThat(server.getPort(), greaterThan(0));
    }

    @Test
    public void isRunning() {
        assertThat(server.isRunning(), is(true));
    }

    // api tests

    @Test
    public void testRootEndpoint() throws Exception {
        HttpResponse<String> response;
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(server.getUri())
                    .GET()
                    .build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.body(), equalTo("Hello, World!"));
    }

    @Test
    public void testHealthEndpoint() throws Exception {
        HttpResponse<String> response;
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(server.getUri().resolve("/health"))
                    .GET()
                    .build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.body(), equalTo("{\"isAvailable\":true}"));
    }
}