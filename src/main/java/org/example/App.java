package org.example;

import org.example.web.WebServer;
import org.example.web.resources.HealthResource;
import org.example.web.resources.RootResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private WebServer webServer;

    public App(URI productServiceUri, URI inventoryServiceUri) {
        // You can use the provided URIs to configure your application

    }

    public void start() {
        log.info("Application starting...");
        // TODO Add required api resource (same as a controller in Spring) here for your application.
        //  Example RootResource and HealthResource have been added.

        webServer = new WebServer()
                .addResource(new RootResource())
                .addResource(new HealthResource());
        webServer.start();

        log.info("Application started at {}", webServer.uri());
    }

    public void stop() {
        log.info("Application stopping...");
        if (webServer != null) {
            webServer.stop();
        }
        log.info("Application stopped successfully.");
    }

    // helper methods
    public Boolean isRunning() {
        return webServer.isRunning();
    }

    public URI uri() {
        return webServer.uri();
    }

    public static void main(String[] args) {
        // You can use configuration to pass actual base URIs. The ones below are just examples and won't work.
        // Focus on the tests to see how the application interacts with mock services.
        URI productServiceUri = URI.create("http://host:8081");
        URI inventoryServiceUri = URI.create("http://host:8082");
        new App(productServiceUri, inventoryServiceUri).start();
    }
}
