package org.example.web;

import io.muserver.*;
import io.muserver.rest.RestHandlerBuilder;
import org.example.App;
import org.example.web.resources.JaxRSResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.lang.String.join;

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
        myServer.withExceptionHandler((muRequest, muResponse, throwable) -> {
            log.error("Unhandled exception for request: " + muRequest.method() + " " + muRequest.uri(), throwable);
            return false;
        });

        server.set(myServer.start());
        running = true;

        log.info("Started server at {}", server.get().uri());
        log.info("Registered resources:");
        log.info("-------------------------------------------------");
        log.info(getPaths());
        log.info("-------------------------------------------------");
    }

    private String getPaths() {
        return resources.stream()
                .map(r -> "\n" + r.getResourceName() + "\n" +
                        r.paths().stream()
                                .map(p -> uri()+p+"\n")
                                .collect(Collectors.joining("\n")))
                .collect(Collectors.joining());
    }

    public void stop() {
        MuServer muServer = server.get();
        if (muServer != null) {
            muServer.stop();
            log.info("Stopped server at {}", muServer.uri());
        }
        running = false;
    }

    public URI uri() {
        MuServer muServer = server.get();
        if (muServer != null) {
            return muServer.uri();
        } else {
            throw new IllegalStateException("Server is not started");
        }
    }

    public int port() {
        return uri().getPort();
    }

    public boolean isRunning() {
        return running;
    }
}
