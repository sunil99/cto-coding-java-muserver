package org.example.web;

import io.muserver.MuServer;
import io.muserver.MuServerBuilder;
import io.muserver.rest.RestHandlerBuilder;
import org.example.App;
import org.example.web.resources.JaxRSResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class WebServer {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    final private AtomicReference<MuServer> server = new AtomicReference<>();

    final private Set<JaxRSResource> resources = new LinkedHashSet<>();

    private Boolean running = false;

    public WebServer addResource(JaxRSResource resource) {
        resources.add(resource);
        return this;
    }

    public void start() {
        MuServerBuilder myServer = MuServerBuilder.httpServer();
        for (JaxRSResource resource : resources) {
            myServer = myServer.addHandler(RestHandlerBuilder.restHandler(resource));
        }

        server.set(myServer.start());
        running = true;

        log.info("Started server at {}", server.get().uri());
    }

    public void stop() {
        MuServer muServer = server.get();
        if (muServer != null) {
            muServer.stop();
            log.info("Stopped server at {}", muServer.uri());
        }
        running = false;
    }

    public URI getUri() {
        MuServer muServer = server.get();
        if (muServer != null) {
            return muServer.uri();
        } else {
            throw new IllegalStateException("Server is not started");
        }
    }

    public int getPort() {
        return getUri().getPort();
    }

    public boolean isRunning() {
        return running;
    }
}
