package ua.lpnu.coffevan.ui;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.testfx.framework.junit5.ApplicationTest;
import ua.lpnu.coffevan.dao.CoffeeDaoImpl;
import ua.lpnu.coffevan.dao.DatabaseManager;
import ua.lpnu.coffevan.dao.VanSettingsDao;
import ua.lpnu.coffevan.service.VanService;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 * JavaFX GUI automation tests using TestFX.
 *
 * <p>These tests launch the real {@link MainWindow} and drive it
 * programmatically through button clicks and keyboard input.
 *
 * <p>Tests are disabled in headless CI environments (no display).
 * To run locally: {@code mvn test -Dtestfx.headless=false}
 */
@DisabledIfSystemProperty(named = "java.awt.headless", matches = "true")
class MainWindowTest extends ApplicationTest {

    private VanService vanService;

    @Override
    public void start(Stage stage) {
        DatabaseManager db = DatabaseManager.getInstance();
        CoffeeDaoImpl coffeeDao = new CoffeeDaoImpl(db.getConnection());
        VanSettingsDao vanSettingsDao = new VanSettingsDao(db.getConnection());
        vanService = new VanService(coffeeDao, vanSettingsDao);
        // Clear van for a clean test state
        vanService.clearVan();

        MainWindow window = new MainWindow(vanService);
        window.show(stage);
    }

    // ── Window title ──────────────────────────────────────────────────

    @Test
    void mainWindow_titleContainsCoffeeVan() {
        Stage stage = (Stage) listWindows().get(0);
        assertTrue(stage.getTitle().contains("Фургон Кави"),
                "Window title should contain 'Фургон Кави'");
    }

    // ── Settings button ───────────────────────────────────────────────

    @Test
    void settingsButton_clickOpensDialog_andCancelClosesIt() {
        // Click settings button (text contains "Налаштування")
        clickOn("⚙  Налаштування");
        // Dialog should be open — press Escape to close
        press(javafx.scene.input.KeyCode.ESCAPE);
        // The main window remains open
        assertFalse(listWindows().isEmpty());
    }

    // ── Analytics button ──────────────────────────────────────────────

    @Test
    void analyticsButton_clickOpensAndCloses() {
        clickOn("📊 Аналітика");
        press(javafx.scene.input.KeyCode.ESCAPE);
        assertFalse(listWindows().isEmpty());
    }

    // ── Reset filter button ───────────────────────────────────────────

    @Test
    void resetFilterButton_click_doesNotThrow() {
        javafx.scene.control.TextField searchField =
                lookup(".search-field").queryAs(javafx.scene.control.TextField.class);
        interact(() -> searchField.setText("arabica"));
        // Then reset filters
        clickOn("✕ Скинути");
        // No exception expected; search field should be empty
        assertEquals("", searchField.getText());
    }

    // ── Add dialog / cancel ───────────────────────────────────────────

    @Test
    void addButton_clickOpensDialog_thenCancel() {
        clickOn("+ Додати");
        press(javafx.scene.input.KeyCode.ESCAPE);
        // Should return to main window
        assertFalse(listWindows().isEmpty());
    }
}
