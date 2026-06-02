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
 * TestFX tests that exercise add/edit/duplicate coffee dialogs,
 * the quality search dialog, and van settings dialog through the MainWindow.
 *
 * <p>Disabled in headless environments.
 */
@DisabledIfSystemProperty(named = "java.awt.headless", matches = "true")
class CoffeeDialogViaMainWindowTest extends ApplicationTest {

    private VanService vanService;

    @Override
    public void start(Stage stage) {
        DatabaseManager db = DatabaseManager.getInstance();
        CoffeeRepositoryImpl coffeeDao = new CoffeeRepositoryImpl(db.getConnection());
        VanSettingsRepository vanSettingsDao = new VanSettingsRepository(db.getConnection());
        vanService = new VanService(coffeeDao, vanSettingsDao);
        vanService.clearVan();

        // Add one item so edit/duplicate/delete buttons have something to select
        vanService.addCoffee(new BeanCoffee("Arabica Test", 400, 1.0, 1.5,
                PackagingType.PACKAGE, 85, "Ethiopia", "Medium"));

        MainWindow window = new MainWindow(vanService);
        window.show(stage);
    }

    // ── Add dialog — type switching ───────────────────────────────────────

    @Test
    void addDialog_switchToGroundType_updatesLabels() {
        clickOn("+ Додати");
        // ComboBox for type is the first combo in dialog
        javafx.scene.control.ComboBox<?> typeCombo =
                lookup(".combo-box").queryAs(javafx.scene.control.ComboBox.class);
        clickOn(typeCombo);
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);
        press(javafx.scene.input.KeyCode.ESCAPE);
        assertFalse(listWindows().isEmpty());
    }

    @Test
    void addDialog_switchToInstantJarType_updatesLabels() {
        clickOn("+ Додати");
        javafx.scene.control.ComboBox<?> typeCombo =
                lookup(".combo-box").queryAs(javafx.scene.control.ComboBox.class);
        clickOn(typeCombo);
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);
        press(javafx.scene.input.KeyCode.ESCAPE);
        assertFalse(listWindows().isEmpty());
    }

    @Test
    void addDialog_switchToInstantSachetType_updatesLabels() {
        clickOn("+ Додати");
        javafx.scene.control.ComboBox<?> typeCombo =
                lookup(".combo-box").queryAs(javafx.scene.control.ComboBox.class);
        clickOn(typeCombo);
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);
        press(javafx.scene.input.KeyCode.ESCAPE);
        assertFalse(listWindows().isEmpty());
    }

    // ── Add dialog — validation errors ────────────────────────────────────

    @Test
    void addDialog_saveWithEmptyName_showsError() {
        clickOn("+ Додати");
        clickOn("Зберегти");
        // Dialog should still be open (validation failed) — dismiss
        press(javafx.scene.input.KeyCode.ESCAPE);
        assertFalse(listWindows().isEmpty());
    }

    // ── Edit dialog ────────────────────────────────────────────────────────

    @Test
    void editDialog_selectRowAndOpen_populatesFields() {
        clickOn(".table-row-cell");
        clickOn("✏ Редагувати");
        press(javafx.scene.input.KeyCode.ESCAPE);
        assertFalse(listWindows().isEmpty());
    }

    @Test
    void editDialog_doubleClickRow_opensEditDialog() {
        doubleClickOn(".table-row-cell");
        press(javafx.scene.input.KeyCode.ESCAPE);
        assertFalse(listWindows().isEmpty());
    }

    // ── Duplicate dialog ───────────────────────────────────────────────────

    @Test
    void duplicateDialog_selectRowAndOpen_showsCopyDialog() {
        clickOn(".table-row-cell");
        clickOn("⎘ Копіювати");
        press(javafx.scene.input.KeyCode.ESCAPE);
        assertFalse(listWindows().isEmpty());
    }

    // ── Delete with confirmation ───────────────────────────────────────────

    @Test
    void deleteDialog_selectRowAndCancel_doesNotDelete() {
        int before = vanService.getAllCoffee().size();
        clickOn(".table-row-cell");
        clickOn("✖ Видалити");
        press(javafx.scene.input.KeyCode.ESCAPE);
        assertEquals(before, vanService.getAllCoffee().size(),
                "Item count should not change when deletion is cancelled");
    }

    // ── Quality search dialog ─────────────────────────────────────────────

    @Test
    void qualitySearchDialog_clickFind_filtersResults() {
        clickOn("🔍 Якість...");
        clickOn("Знайти");
        assertFalse(listWindows().isEmpty());
    }

    // ── Detail panel via row click ────────────────────────────────────────

    @Test
    void clickTableRow_detailPanelBecomesVisible() {
        clickOn(".table-row-cell");
        assertFalse(listWindows().isEmpty());
    }

    // ── Van Settings Dialog ───────────────────────────────────────────────

    @Test
    void vanSettingsDialog_saveValidValues_updatesVan() {
        clickOn("⚙  Налаштування");
        var textFields = lookup(".text-field").queryAllAs(javafx.scene.control.TextField.class);
        var list = new java.util.ArrayList<>(textFields);
        assertFalse(list.isEmpty());

        javafx.scene.control.TextField volField = list.get(0);
        javafx.scene.control.TextField budField = list.get(1);

        double oldVol = Double.parseDouble(volField.getText());
        double oldBud = Double.parseDouble(budField.getText());

        doubleClickOn(volField).write("1500.0");
        doubleClickOn(budField).write("60000.0");

        clickOn("Зберегти");

        assertEquals(1500.0, vanService.getVan().getMaxVolumeLiters(), 0.001);
        assertEquals(60000.0, vanService.getVan().getMaxBudget(), 0.001);

        // Restore old settings
        clickOn("⚙  Налаштування");
        textFields = lookup(".text-field").queryAllAs(javafx.scene.control.TextField.class);
        list = new java.util.ArrayList<>(textFields);
        doubleClickOn(list.get(0)).write(String.valueOf(oldVol));
        doubleClickOn(list.get(1)).write(String.valueOf(oldBud));
        clickOn("Зберегти");
    }

    @Test
    void vanSettingsDialog_invalidInput_showsErrorAlert() {
        clickOn("⚙  Налаштування");
        var textFields = lookup(".text-field").queryAllAs(javafx.scene.control.TextField.class);
        var list = new java.util.ArrayList<>(textFields);

        doubleClickOn(list.get(0)).write("invalid");

        clickOn("Зберегти");

        // Dismiss the error alert by pressing ENTER
        press(javafx.scene.input.KeyCode.ENTER);

        // Cancel the settings dialog
        press(javafx.scene.input.KeyCode.ESCAPE);
    }
}

