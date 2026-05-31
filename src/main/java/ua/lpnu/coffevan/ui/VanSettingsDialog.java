package ua.lpnu.coffevan.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ua.lpnu.coffevan.model.Van;

/**
 * Dialog for editing van capacity and budget settings.
 * Returns a double array [maxVolume, maxBudget].
 */
public class VanSettingsDialog extends Dialog<double[]> {

    public VanSettingsDialog(Van van) {
        setTitle("Налаштування фургону");
        setHeaderText("Змініть параметри фургону");

        ButtonType saveBtn = new ButtonType("Зберегти", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField volumeField = new TextField(String.valueOf(van.getMaxVolumeLiters()));
        TextField budgetField = new TextField(String.valueOf(van.getMaxBudget()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));
        grid.addRow(0, new Label("Максимальний об'єм (л):"), volumeField);
        grid.addRow(1, new Label("Максимальний бюджет (грн):"), budgetField);

        getDialogPane().setContent(grid);
        getDialogPane().setPrefWidth(400);

        setResultConverter(btn -> {
            if (btn == saveBtn) {
                try {
                    double volume = Double.parseDouble(volumeField.getText().trim());
                    double budget = Double.parseDouble(budgetField.getText().trim());
                    return new double[]{volume, budget};
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Введіть коректні числові значення", ButtonType.OK);
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });
    }
}
