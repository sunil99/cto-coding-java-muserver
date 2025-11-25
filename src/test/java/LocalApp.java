import org.example.App;
import org.example.mock.MockInventoryService;
import org.example.mock.MockProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.net.URISyntaxException;

public class LocalApp {

    static final Logger log = LoggerFactory.getLogger(LocalApp.class);
    App app;

    public LocalApp() throws URISyntaxException, IOException {
        // setup mock services
        MockProductService mockProductService = new MockProductService();
        mockProductService.start();

        MockInventoryService mockInventoryService = new MockInventoryService();
        mockInventoryService.start();

        // start main app
        app = new App(mockProductService.uri(), mockInventoryService.uri());
    }

    public void start() {
        app.start();
    }

    public void stop() {
        app.stop();
    }

    public static void main(String[] args) {
        try {
            final LocalApp localApp = new LocalApp();
            localApp.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shutting down application...");
                localApp.stop();
                log.info("Application stopped.");
            }));
            log.info("Application started. Press Ctrl+C to stop.");
        } catch (Throwable t) {
            log.error("Failed to start application", t);
        }
    }

}
