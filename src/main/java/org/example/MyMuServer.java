package org.example;

import io.muserver.Method;
import io.muserver.MuServer;
import io.muserver.MuServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

public class MyMuServer {

    private static final Logger log = LoggerFactory.getLogger(MyMuServer.class);

    final private AtomicReference<MuServer> server = new AtomicReference<>();



    public void start() {
        MuServer myServer = MuServerBuilder.httpServer()
                .addHandler(Method.GET, "/", (_, res, _) -> res.write("Hello, World!"))
                .addHandler(Method.GET, "/health", (_, res, _) -> res.write("{\"isAvailable\":true}"))
                .start();

        server.set(myServer);

        log.info("Started server at {}", server.get().uri());
    }

    public void stop() {
        MuServer muServer = server.get();
        if (muServer != null) {
            muServer.stop();
            log.info("Stopped server at {}", muServer.uri());
        }
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
        MuServer muServer = server.get();
        return muServer != null && getUri() != null;
    }


    public static void main(String[] args) {
        log.info("Hello and welcome!");

        new MyMuServer().start();
    }

}
