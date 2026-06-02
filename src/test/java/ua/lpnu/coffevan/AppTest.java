package ua.lpnu.coffevan;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

@DisabledIfSystemProperty(named = "java.awt.headless", matches = "true")
class AppTest extends ApplicationTest {

    private App app;

    @Override
    public void start(Stage stage) throws Exception {
        app = new App();
        app.start(stage);
    }

    @Override
    public void stop() throws Exception {
        if (app != null) {
            app.stop();
        }
    }

    @Test
    void testAppGettersAndLifecycle() {
        assertNotNull(App.getVanService(), "VanService should be initialized");
        assertNotNull(App.getCoffeeController(), "CoffeeController should be initialized");
        assertNotNull(App.getVanController(), "VanController should be initialized");
    }
}
