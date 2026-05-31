package ua.lpnu.coffevan;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.lpnu.coffevan.dao.CoffeeDaoImpl;
import ua.lpnu.coffevan.dao.DatabaseManager;
import ua.lpnu.coffevan.dao.VanSettingsDao;
import ua.lpnu.coffevan.service.VanService;
import ua.lpnu.coffevan.ui.MainWindow;

/**
 * Application entry point.
 */
public class App extends Application {

    private static final Logger logger = LogManager.getLogger(App.class);

    private static VanService vanService;

    @Override
    public void start(Stage primaryStage) {
        logger.info("Coffee Van application starting");
        DatabaseManager db = DatabaseManager.getInstance();
        CoffeeDaoImpl coffeeDao = new CoffeeDaoImpl(db.getConnection());
        VanSettingsDao vanSettingsDao = new VanSettingsDao(db.getConnection());
        vanService = new VanService(coffeeDao, vanSettingsDao);

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

    public static void main(String[] args) {
        launch(args);
    }
}
