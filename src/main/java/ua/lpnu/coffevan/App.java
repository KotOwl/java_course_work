package ua.lpnu.coffevan;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.lpnu.coffevan.controller.CoffeeController;
import ua.lpnu.coffevan.controller.VanController;
import ua.lpnu.coffevan.repository.CoffeeRepositoryImpl;
import ua.lpnu.coffevan.repository.DatabaseManager;
import ua.lpnu.coffevan.repository.VanSettingsRepository;
import ua.lpnu.coffevan.service.VanService;
import ua.lpnu.coffevan.ui.MainWindow;

/**
 * Application entry point.
 */
public class App extends Application {

    private static final Logger logger = LogManager.getLogger(App.class);

    private static VanService vanService;
    private static CoffeeController coffeeController;
    private static VanController vanController;

    @Override
    public void start(Stage primaryStage) {
        logger.info("Coffee Van application starting");
        DatabaseManager db = DatabaseManager.getInstance();
        CoffeeRepositoryImpl coffeeRepository = new CoffeeRepositoryImpl(db.getConnection());
        VanSettingsRepository vanSettingsRepository = new VanSettingsRepository(db.getConnection());
        vanService = new VanService(coffeeRepository, vanSettingsRepository);

        coffeeController = new CoffeeController(vanService);
        vanController = new VanController(vanService);

        MainWindow mainWindow = new MainWindow(vanService);
        mainWindow.show(primaryStage);
    }

    @Override
    public void stop() {
        DatabaseManager.getInstance().close();
        logger.info("Coffee Van application stopped");
    }

    public static VanService getVanService() {
        return vanService;
    }

    public static CoffeeController getCoffeeController() {
        return coffeeController;
    }

    public static VanController getVanController() {
        return vanController;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
