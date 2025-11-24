package org.example.mock;

import jakarta.ws.rs.*;
import org.example.util.FileResourceReader;
import org.example.web.WebServer;
import org.example.web.resources.JaxRSResource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MockProductService {

    private static final Logger log = LoggerFactory.getLogger(MockProductService.class);

    private final WebServer service;
    private final Map<Integer, JSONObject> products;

    public MockProductService() throws URISyntaxException, IOException {
        products = new HashMap<>();
        service = new WebServer()
                .addResource(new ProductResource(products));

        loadMockData();
    }

    private void loadMockData() throws URISyntaxException, IOException {
        String data = FileResourceReader.readResourceToString("/products.json");

        JSONArray jsonArray = new JSONArray(data);
        Map<Integer, JSONObject> result = StreamSupport.stream(jsonArray.spliterator(), false)
                .map(o -> (JSONObject) o)
                .collect(Collectors.toMap(json -> json.getInt("id"), json -> json));

        this.products.putAll(result);
    }


    public URI uri() {
        return service.uri();
    }

    public void stop() {
        service.stop();
        log.info("MockProductService stopped.");
    }

    public void start() {
        service.start();
        log.info("MockProductService started at {}", service.uri());
    }

    public boolean isRunning() {
        return service.isRunning();
    }

    @Path("/api/products")
    public static class ProductResource implements JaxRSResource {

        private final Map<Integer, JSONObject> products;

        public ProductResource(Map<Integer, JSONObject> products) {
            this.products = products;
        }

        @GET
        @Path("/{id}")
        @Produces("application/json")
        public String get(@PathParam("id") int id) {
            if (id <= 0) {
                throw new BadRequestException("ID must be a positive integer");
            }

            JSONObject json = this.products.get(id);
            if (json == null) {
                throw new NotFoundException("No product with that ID found");
            }
            return json.toString(4);
        }

    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        var service = new MockProductService();
        service.start();
    }

}


