package org.example;

import org.example.mock.MockInventoryService;
import org.example.mock.MockProductService;
import org.example.util.FileResourceReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppTest {

    App app;

    @BeforeEach
    public void before() throws URISyntaxException, IOException {
        // setup mock services
        MockProductService mockProductService = new MockProductService();
        mockProductService.start();

        MockInventoryService mockInventoryService = new MockInventoryService();
        mockInventoryService.start();

        // start main app
        app = new App(mockProductService.uri(), mockInventoryService.uri());
        app.start();
    }

    @AfterEach
    public void after() {
        app.stop();
    }

    @Test
    @DisplayName("getPort returns a valid port number")
    public void getPort() {
        assertThat(app.uri().getPort(), greaterThan(0));
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

        @Test
        @DisplayName("GET /ui/products?ids=1,2 returns merged results from product and inventory")
        public void testProductsEndpoint() throws IOException, InterruptedException, URISyntaxException {
            // expected response
            String expectedJson = FileResourceReader.readResourceToString("/products-response-no-errors.json");
            JSONObject expectedJsonObj = new JSONObject(expectedJson);

            HttpResponse<String> response;
            try (HttpClient httpClient = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(app.uri().resolve("/ui/products?ids=1,2"))
                        .GET()
                        .build();
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            }

            assertThat(response.statusCode(), equalTo(200));


            JSONObject responseJsonObj = new JSONObject(response.body());

            // verify results
            JSONArray resultsJSONArray = responseJsonObj.getJSONArray("results");
            JSONArray expectedResultsJSONArray = expectedJsonObj.getJSONArray("results");
            assertThat(resultsJSONArray.length(), equalTo(expectedResultsJSONArray.length()));
            for (int i = 0; i < resultsJSONArray.length(); i++) {
                JSONObject resultObj = resultsJSONArray.getJSONObject(i);
                JSONObject expectedResultObj = expectedResultsJSONArray.getJSONObject(i);
                assertThat(resultObj.toString(), equalTo(expectedResultObj.toString()));
            }
            // verify errors
            JSONArray errorsJSONArray = responseJsonObj.getJSONArray("errors");
            assertThat(errorsJSONArray.length(), equalTo(0));
        }

        @Test
        @DisplayName("GET /ui/products?ids=1,2,3 returns merged results from product and inventory with partial results and errors")
        public void testProductsEndpointWithErrors() throws IOException, InterruptedException, URISyntaxException {
            // expected response
            String expectedJson = FileResourceReader.readResourceToString("/products-response-with-errors.json");
            JSONObject expectedJsonObj = new JSONObject(expectedJson);

            HttpResponse<String> response;
            try (HttpClient httpClient = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(app.uri().resolve("/ui/products?ids=1,2,3"))
                        .GET()
                        .build();
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            }

            assertThat(response.statusCode(), equalTo(200));


            JSONObject responseJsonObj = new JSONObject(response.body());

            // verify results
            JSONArray resultsJSONArray = responseJsonObj.getJSONArray("results");
            JSONArray expectedResultsJSONArray = expectedJsonObj.getJSONArray("results");
            assertThat(resultsJSONArray.length(), equalTo(expectedResultsJSONArray.length()));
            for (int i = 0; i < resultsJSONArray.length(); i++) {
                JSONObject resultObj = resultsJSONArray.getJSONObject(i);
                JSONObject expectedResultObj = expectedResultsJSONArray.getJSONObject(i);
                assertThat(resultObj.toString(), equalTo(expectedResultObj.toString()));
            }
            // verify errors
            JSONArray errorsJSONArray = responseJsonObj.getJSONArray("errors");
            assertThat(errorsJSONArray.length(), equalTo(1));
            assertThat(errorsJSONArray.getJSONObject(0).toString(),
                    equalTo(expectedJsonObj.getJSONArray("errors").getJSONObject(0).toString()));
        }
    }

}