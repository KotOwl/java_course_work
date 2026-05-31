package ua.lpnu.coffevan.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * Dialog for specifying a quality score search range.
 * Returns an int array [minQuality, maxQuality].
 */
public class SearchDialog extends Dialog<int[]> {

    public SearchDialog() {
        setTitle("Пошук за якістю");
        setHeaderText("Введіть діапазон оцінки якості (1–100)");

        ButtonType searchBtn = new ButtonType("Знайти", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(searchBtn, ButtonType.CANCEL);

        Spinner<Integer> minSpinner = new Spinner<>(1, 100, 1);
        minSpinner.setEditable(true);
        Spinner<Integer> maxSpinner = new Spinner<>(1, 100, 100);
        maxSpinner.setEditable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));
        grid.addRow(0, new Label("Мінімальна якість:"), minSpinner);
        grid.addRow(1, new Label("Максимальна якість:"), maxSpinner);

        getDialogPane().setContent(grid);

        setResultConverter(btn -> {
            if (btn == searchBtn) {
                return new int[]{minSpinner.getValue(), maxSpinner.getValue()};
            }
            return null;
        });
    }
}
