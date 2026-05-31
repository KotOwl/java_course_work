package ua.lpnu.coffevan.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import ua.lpnu.coffevan.model.*;
import ua.lpnu.coffevan.service.VanService;

/**
 * Dialog for adding or editing a Coffee item.
 */
public class CoffeeDialog extends Dialog<Coffee> {

    private final Coffee existing;
    private final boolean isDuplicate;

    private ComboBox<String> typeCombo;
    private TextField nameField;
    private TextField priceField;
    private TextField weightField;
    private TextField volumeField;
    private ComboBox<PackagingType> packagingCombo;
    private Spinner<Integer> qualitySpinner;
    private TextField extra1Field;
    private TextField extra2Field;
    private Label extra1Label;
    private Label extra2Label;
    private Label errorLabel;

    public CoffeeDialog(Coffee existing, VanService vanService) {
        this(existing, vanService, false);
    }

    public CoffeeDialog(Coffee existing, VanService vanService, boolean isDuplicate) {
        this.existing    = isDuplicate ? null : existing;
        this.isDuplicate = isDuplicate;

        setTitle(isDuplicate ? "Копія товару" : existing == null ? "Додати каву" : "Редагувати каву");
        initModality(Modality.APPLICATION_MODAL);

        ButtonType saveBtn = new ButtonType("Зберегти", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        VBox content = buildContent(isDuplicate ? existing : this.existing);
        getDialogPane().setContent(content);
        getDialogPane().setPrefWidth(560);
        getDialogPane().setStyle("-fx-background-color: #faf7f4;");

        Coffee toPopulate = isDuplicate ? existing : this.existing;
        if (toPopulate != null) {
            populateFields(toPopulate);
        }

        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateExtraLabels(newVal));
        updateExtraLabels(typeCombo.getValue());

        setResultConverter(button -> {
            if (button == saveBtn) {
                return buildCoffee();
            }
            return null;
        });
    }

    private VBox buildContent(Coffee c) {
        String headerText = isDuplicate ? "Копія: " + (c != null ? c.getName() : "")
                : c == null ? "Введіть дані нового товару" : "Редагування: " + c.getName();
        Label header = new Label(headerText);
        header.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        header.setTextFill(Color.web("#4a2c0a"));
        header.setPadding(new Insets(0, 0, 8, 0));

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #c62828; -fx-font-size: 12px; -fx-font-weight: bold;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        GridPane grid = buildGrid();

        VBox box = new VBox(10, header, errorLabel, grid);
        box.setPadding(new Insets(20));
        return box;
    }

    private GridPane buildGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        ColumnConstraints labelCol = new ColumnConstraints(170);
        ColumnConstraints fieldCol = new ColumnConstraints();
        fieldCol.setHgrow(Priority.ALWAYS);
        fieldCol.setFillWidth(true);
        grid.getColumnConstraints().addAll(labelCol, fieldCol);

        typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Зернова", "Мелена", "Розчинна (банка)", "Розчинна (пакетик)");
        typeCombo.setValue("Зернова");
        typeCombo.setMaxWidth(Double.MAX_VALUE);

        nameField = new TextField();
        nameField.setPromptText("Наприклад: Arabica Premium");
        priceField = new TextField();
        priceField.setPromptText("Наприклад: 350.00");
        weightField = new TextField();
        weightField.setPromptText("Наприклад: 1.5");
        volumeField = new TextField();
        volumeField.setPromptText("Наприклад: 2.0");

        packagingCombo = new ComboBox<>();
        packagingCombo.getItems().addAll(PackagingType.values());
        packagingCombo.setValue(PackagingType.PACKAGE);
        packagingCombo.setMaxWidth(Double.MAX_VALUE);

        qualitySpinner = new Spinner<>(1, 100, 50);
        qualitySpinner.setEditable(true);
        qualitySpinner.setMaxWidth(Double.MAX_VALUE);

        extra1Label = new Label("Походження:");
        extra1Field = new TextField();
        extra1Field.setPromptText("Наприклад: Ethiopia");
        extra2Label = new Label("Обсмажування:");
        extra2Field = new TextField();
        extra2Field.setPromptText("Наприклад: Medium");

        grid.addRow(0, styledLabel("Тип кави:"), typeCombo);
        grid.addRow(1, styledLabel("Назва:"), nameField);
        grid.addRow(2, styledLabel("Ціна (грн/кг):"), priceField);
        grid.addRow(3, styledLabel("Вага (кг):"), weightField);
        grid.addRow(4, styledLabel("Об'єм з упаковкою (л):"), volumeField);
        grid.addRow(5, styledLabel("Упаковка:"), packagingCombo);
        grid.addRow(6, styledLabel("Якість (1-100):"), qualitySpinner);
        grid.addRow(7, extra1Label, extra1Field);
        grid.addRow(8, extra2Label, extra2Field);

        styleLabel(extra1Label);
        styleLabel(extra2Label);

        return grid;
    }

    private Label styledLabel(String text) {
        Label l = new Label(text);
        styleLabel(l);
        return l;
    }

    private void styleLabel(Label l) {
        l.setFont(Font.font("Arial", 13));
        l.setTextFill(Color.web("#3e2723"));
        l.setAlignment(Pos.CENTER_RIGHT);
        l.setMaxWidth(Double.MAX_VALUE);
    }

    private void updateExtraLabels(String type) {
        if (type == null) return;
        switch (type) {
            case "Зернова" -> {
                extra1Label.setText("Походження:");
                extra1Label.setVisible(true);
                extra1Field.setVisible(true);
                extra2Label.setText("Обсмажування:");
                extra2Label.setVisible(true);
                extra2Field.setVisible(true);
                packagingCombo.setDisable(false);
            }
            case "Мелена" -> {
                extra1Label.setText("Розмір помолу:");
                extra1Label.setVisible(true);
                extra1Field.setVisible(true);
                extra2Label.setVisible(false);
                extra2Field.setVisible(false);
                packagingCombo.setDisable(false);
            }
            case "Розчинна (банка)" -> {
                extra1Label.setText("Гранул (0=порошок):");
                extra1Label.setVisible(true);
                extra1Field.setVisible(true);
                extra2Label.setVisible(false);
                extra2Field.setVisible(false);
                packagingCombo.setValue(PackagingType.JAR);
                packagingCombo.setDisable(true);
            }
            case "Розчинна (пакетик)" -> {
                extra1Label.setText("Кількість пакетиків:");
                extra1Label.setVisible(true);
                extra1Field.setVisible(true);
                extra2Label.setVisible(false);
                extra2Field.setVisible(false);
                packagingCombo.setValue(PackagingType.BAG);
                packagingCombo.setDisable(true);
            }
        }
    }

    private void populateFields(Coffee c) {
        typeCombo.setValue(c.getCoffeeType());
        nameField.setText(c.getName());
        priceField.setText(String.valueOf(c.getPricePerKg()));
        weightField.setText(String.valueOf(c.getWeightKg()));
        volumeField.setText(String.valueOf(c.getVolumeLiters()));
        packagingCombo.setValue(c.getPackagingType());
        qualitySpinner.getValueFactory().setValue(c.getQualityScore());

        if (c instanceof BeanCoffee bc) {
            extra1Field.setText(bc.getOrigin());
            extra2Field.setText(bc.getRoastLevel());
        } else if (c instanceof GroundCoffee gc) {
            extra1Field.setText(gc.getGrindSize());
        } else if (c instanceof InstantCoffeeJar ij) {
            extra1Field.setText(String.valueOf(ij.getGranules()));
        } else if (c instanceof InstantCoffeeSachet is) {
            extra1Field.setText(String.valueOf(is.getSachetsCount()));
        }
    }

    private Coffee buildCoffee() {
        clearErrors();
        boolean valid = true;

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            markError(nameField, "Назва не може бути порожньою");
            valid = false;
        }

        double price = 0, weight = 0, volume = 0;
        try { price = Double.parseDouble(priceField.getText().trim()); if (price <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { markError(priceField, "Ціна має бути числом > 0"); valid = false; }

        try { weight = Double.parseDouble(weightField.getText().trim()); if (weight <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { markError(weightField, "Вага має бути числом > 0"); valid = false; }

        try { volume = Double.parseDouble(volumeField.getText().trim()); if (volume <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { markError(volumeField, "Об'єм має бути числом > 0"); valid = false; }

        if (!valid) return null;

        String type = typeCombo.getValue();
        PackagingType packaging = packagingCombo.getValue();
        int quality = qualitySpinner.getValue();
        String e1 = extra1Field.getText().trim();
        String e2 = extra2Field.getText().trim();

        try {
            Coffee coffee = switch (type) {
                case "Зернова"           -> new BeanCoffee(name, price, weight, volume, packaging, quality, e1, e2);
                case "Мелена"            -> new GroundCoffee(name, price, weight, volume, packaging, quality, e1);
                case "Розчинна (банка)"  -> {
                    int granules = e1.isEmpty() ? 0 : Integer.parseInt(e1);
                    yield new InstantCoffeeJar(name, price, weight, volume, quality, granules);
                }
                case "Розчинна (пакетик)" -> {
                    int sachets = e1.isEmpty() ? 1 : Integer.parseInt(e1);
                    yield new InstantCoffeeSachet(name, price, weight, volume, quality, sachets);
                }
                default -> null;
            };
            if (coffee != null && existing != null) coffee.setId(existing.getId());
            return coffee;
        } catch (NumberFormatException ex) {
            markError(extra1Field, "Введіть ціле число");
            return null;
        }
    }

    private void markError(TextField field, String msg) {
        field.setStyle("-fx-border-color: #c62828; -fx-border-width: 2;");
        if (errorLabel != null) {
            errorLabel.setText("⚠ " + msg);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    private void clearErrors() {
        for (TextField f : new TextField[]{nameField, priceField, weightField, volumeField, extra1Field}) {
            f.setStyle("");
        }
        if (errorLabel != null) { errorLabel.setVisible(false); errorLabel.setManaged(false); }
    }

}
