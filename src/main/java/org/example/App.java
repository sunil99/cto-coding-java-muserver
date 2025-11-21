package org.example;

import org.example.web.WebServer;
import org.example.web.resources.HealthResource;
import org.example.web.resources.RootResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App extends Boilerplate {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public void start() {
        log.info("Application starting...");
        // You can add bootstrap initialisation here

        webServer = new WebServer()
                .addResource(new RootResource())
                .addResource(new HealthResource());
        webServer.start();

        log.info("Application started at {}", webServer.getUri());
    }

    public void stop() {
        log.info("Application stopping...");
        if (webServer != null) {
            webServer.stop();
        }
        log.info("Application stopped successfully.");
    }

    public static void main(String[] args) {
        new App().start();
    }
}
