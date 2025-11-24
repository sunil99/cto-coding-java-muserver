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

public class MockInventoryService {

    private static Logger log = LoggerFactory.getLogger(MockInventoryService.class);

    private final WebServer service;
    private final Map<Integer, JSONObject> inventory;

    public MockInventoryService() throws URISyntaxException, IOException {
        inventory = new HashMap<>();
        service = new WebServer()
                .addResource(new InventoryResource(inventory));

        loadMockData();
    }

    private void loadMockData() throws URISyntaxException, IOException {
        String inventoryData = FileResourceReader.readResourceToString("/inventory.json");

        JSONArray inventoryJSON = new JSONArray(inventoryData);
        Map<Integer, JSONObject> result = StreamSupport.stream(inventoryJSON.spliterator(), false)
                .map(o -> (JSONObject) o)
                .collect(Collectors.toMap(json -> json.getInt("productId"), json -> json));

        this.inventory.putAll(result);
    }


    public URI uri() {
        return service.uri();
    }

    public void stop() {
        service.stop();
        log.info("MockInventoryService stopped.");
    }

    public void start() {
        service.start();
        log.info("MockInventoryService started at {}", service.uri());
    }

    public boolean isRunning() {
        return service.isRunning();
    }

    @Path("/api/inventory")
    public static class InventoryResource implements JaxRSResource {

        private final Map<Integer, JSONObject> inventory;

        public InventoryResource(Map<Integer, JSONObject> inventory) {
            this.inventory = inventory;
        }

        @GET
        @Path("/{id}")
        @Produces("application/json")
        public String get(@PathParam("id") int id) {
            if (id <= 0) {
                throw new BadRequestException("ID must be a positive integer");
            }

            JSONObject json = this.inventory.get(id);
            if (json == null) {
                throw new NotFoundException("No inventory with that ID found");
            }
            return json.toString(4);
        }
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        var service = new MockInventoryService();
        service.start();
    }

}
