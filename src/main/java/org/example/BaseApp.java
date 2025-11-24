package org.example;

import org.example.web.WebServer;

import java.net.URI;

public abstract class BaseApp {

    protected WebServer webServer;

    public Boolean isRunning() {
        return webServer.isRunning();
    }

    public int port() {
        return webServer.port();
    }

    public URI uri() {
        return webServer.uri();
    }
}
