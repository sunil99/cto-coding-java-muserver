package org.example;

import org.example.web.WebServer;

import java.net.URI;

public abstract class BaseApp {

    protected WebServer webServer;

    public Boolean isRunning() {
        return webServer.isRunning();
    }

    public int getPort() {
        return webServer.getPort();
    }

    public URI uri() {
        return webServer.uri();
    }
}
