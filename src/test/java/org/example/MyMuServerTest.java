package org.example;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MyMuServerTest {

    MyMuServer server;

    @Before
    public void before() {
        server = new MyMuServer();
        server.start();
    }

    @After
    public void after() {
        server.stop();
    }

    @Test
    public void getPort() {
        assertThat(server.getPort(), greaterThan(0));
    }

    @Test
    public void isRunning() {
        assertThat(server.isRunning(), is(true));
    }

    // api tests

    @Test
    public void testRootEndpoint() throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(server.getUri().toURL())
                .build();
        var response = client.newCall(request).execute();
        assertThat(response.code(), equalTo(200));
        assertThat(response.body(), notNullValue());
        assertThat(response.body().string(), equalTo("Hello, World!"));
    }

    @Test
    public void testHealthEndpoint() throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(server.getUri().resolve("/health").toURL())
                .build();
        var response = client.newCall(request).execute();
        assertThat(response.code(), equalTo(200));
        assertThat(response.body(), notNullValue());
        assertThat(response.body().string(), equalTo("{\"isAvailable\":true}"));
    }
}