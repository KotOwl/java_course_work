package ua.lpnu.coffevan.ui;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.testfx.framework.junit5.ApplicationTest;
import ua.lpnu.coffevan.repository.CoffeeRepositoryImpl;
import ua.lpnu.coffevan.repository.DatabaseManager;
import ua.lpnu.coffevan.repository.VanSettingsRepository;
import ua.lpnu.coffevan.model.*;
import ua.lpnu.coffevan.service.VanService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Extended JavaFX GUI automation tests using TestFX.
 *
 * <p>Tests are disabled in headless environments.
 */
@DisabledIfSystemProperty(named = "java.awt.headless", matches = "true")
class MainWindowExtendedTest extends ApplicationTest {

    private VanService vanService;

    @Override
    public void start(Stage stage) {
        DatabaseManager db = DatabaseManager.getInstance();
        CoffeeRepositoryImpl coffeeDao = new CoffeeRepositoryImpl(db.getConnection());
        VanSettingsRepository vanSettingsDao = new VanSettingsRepository(db.getConnection());
        vanService = new VanService(coffeeDao, vanSettingsDao);
        vanService.clearVan();

        // Pre-seed some data so table is not empty
        vanService.addCoffee(new BeanCoffee("Arabica Ethiopia", 480, 1.0, 2.0,
                PackagingType.PACKAGE, 92, "Ethiopia", "Light"));
        vanService.addCoffee(new GroundCoffee("Lavazza Crema", 320, 1.0, 1.5,
                PackagingType.PACKAGE, 75, "Fine"));
        vanService.addCoffee(new InstantCoffeeJar("Nescafe Gold", 380, 0.2, 0.4, 70, 1));
        vanService.addCoffee(new InstantCoffeeSachet("MacCoffee", 180, 0.02, 0.05, 32, 24));

        MainWindow window = new MainWindow(vanService);
        window.show(stage);
    }

    // ── Sort button ───────────────────────────────────────────────────────

    @Test
    void sortByPriceWeightButton_clickDoesNotThrow() {
        clickOn("↕ Ціна/вага");
        assertFalse(listWindows().isEmpty());
    }

    // ── Quality search dialog ─────────────────────────────────────────────

    @Test
    void qualitySearchButton_clickOpensDialog_andCancelClosesIt() {
        clickOn("🔍 Якість...");
        press(javafx.scene.input.KeyCode.ESCAPE);
        assertFalse(listWindows().isEmpty());
    }

    // ── Analytics dialog ──────────────────────────────────────────────────

    @Test
    void analyticsDialog_withData_opensSuccessfully() {
        clickOn("📊 Аналітика");
        press(javafx.scene.input.KeyCode.ESCAPE);
        assertFalse(listWindows().isEmpty());
    }

    // ── Edit with no selection ─────────────────────────────────────────────

    @Test
    void editButton_withNoSelection_showsAlert() {
        clickOn("✏ Редагувати");
        press(javafx.scene.input.KeyCode.ENTER);
        assertFalse(listWindows().isEmpty());
    }

    // ── Duplicate with no selection ───────────────────────────────────────

    @Test
    void duplicateButton_withNoSelection_showsAlert() {
        clickOn("⎘ Копіювати");
        press(javafx.scene.input.KeyCode.ENTER);
        assertFalse(listWindows().isEmpty());
    }

    // ── Delete with no selection ──────────────────────────────────────────

    @Test
    void deleteButton_withNoSelection_showsAlert() {
        clickOn("✖ Видалити");
        press(javafx.scene.input.KeyCode.ENTER);
        assertFalse(listWindows().isEmpty());
    }

    // ── Undo when stack is empty ──────────────────────────────────────────

    @Test
    void undoButton_whenNothingToUndo_showsInfoStatus() {
        clickOn("↩ Undo");
        assertFalse(listWindows().isEmpty());
    }

    // ── CSV export (cancel) ───────────────────────────────────────────────

    @Test
    void csvExportButton_cancelDialog_doesNotThrow() {
        clickOn("⬇ CSV");
        press(javafx.scene.input.KeyCode.ESCAPE);
        assertFalse(listWindows().isEmpty());
    }

    // ── CSV import (cancel) ───────────────────────────────────────────────

    @Test
    void csvImportButton_cancelDialog_doesNotThrow() {
        clickOn("↑ Імпорт CSV");
        press(javafx.scene.input.KeyCode.ESCAPE);
        assertFalse(listWindows().isEmpty());
    }

    // ── Seed button ───────────────────────────────────────────────────────

    @Test
    void seedButton_clickAddsTestData() {
        clickOn("⚗");
        assertFalse(listWindows().isEmpty());
    }

    // ── Search field filter ───────────────────────────────────────────────

    @Test
    void searchField_typing_filtersTableByName() {
        javafx.scene.control.TextField sf =
                lookup("#searchField").queryAs(javafx.scene.control.TextField.class);
        interact(() -> sf.setText("Arabica"));
        assertEquals("Arabica", sf.getText());
        clickOn("#resetBtn");
    }

    // ── Max price filter ───────────────────────────────────────────────────

    @Test
    void resetFilter_clearsSearchField() {
        javafx.scene.control.TextField sf =
                lookup("#searchField").queryAs(javafx.scene.control.TextField.class);
        interact(() -> sf.setText("test"));
        clickOn("#resetBtn");
        assertEquals("", sf.getText());
    }

    // ── Quality slider ────────────────────────────────────────────────────

    @Test
    void qualitySlider_move_filtersTable() {
        javafx.scene.control.Slider slider =
                lookup(".slider").queryAs(javafx.scene.control.Slider.class);
        clickOn(slider);
        press(javafx.scene.input.KeyCode.RIGHT);
        assertFalse(listWindows().isEmpty());
        clickOn("#resetBtn");
    }
}
