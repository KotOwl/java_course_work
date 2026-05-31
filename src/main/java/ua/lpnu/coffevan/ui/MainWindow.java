package ua.lpnu.coffevan.ui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.lpnu.coffevan.model.*;
import ua.lpnu.coffevan.service.VanService;

import java.io.*;
import java.util.*;

/**
 * Main JavaFX window for the Coffee Van application.
 */
public class MainWindow {

    private static final Logger logger = LogManager.getLogger(MainWindow.class);

    private static final int MAX_ITEMS_SOFT = 100;

    private final VanService vanService;
    private ObservableList<Coffee> masterList;
    private FilteredList<Coffee> filteredList;
    private TableView<Coffee> tableView;
    private Label statusLabel;
    private ProgressBar volumeBar;
    private ProgressBar budgetBar;
    private Label volumeCardValue;
    private Label budgetCardValue;
    private Label countCardValue;
    private ProgressBar countBar;
    private TextField searchField;
    private ComboBox<String> typeFilter;
    private ComboBox<String> packFilter;
    private Slider qualitySlider;
    private Label qualitySliderLabel;
    private TextField maxPriceField;
    private Label avgQualCardValue;
    private ProgressBar avgQualBar;
    private Label summaryLabel;
    private final Deque<Coffee> undoStack = new ArrayDeque<>();
    // detail panel
    private VBox detailPanel;
    private Label detailName;
    private Label detailType;
    private Label detailPrice;
    private Label detailWeight;
    private Label detailVolume;
    private Label detailPack;
    private Label detailQual;
    private ProgressBar detailQualBar;
    private Label detailTotal;
    private Label detailRatio;
    private Label detailExtra;
    private Stage primaryStage;

    public MainWindow(VanService vanService) {
        this.vanService = vanService;
    }

    public void show(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("☕ Фургон Кави — Управління товарами");

        BorderPane root = new BorderPane();

        root.setTop(buildHeader());
        root.setCenter(buildCenter());
        root.setBottom(buildStatusBar());

        Scene scene = new Scene(root, 1200, 760);
        scene.getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(640);
        stage.centerOnScreen();
        stage.show();
        stage.toFront();
        stage.requestFocus();

        refreshData();
        logger.info("Main window opened");
    }

    // ── Header ────────────────────────────────────────────────────────

    private HBox buildHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("header-bar");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("☕  Фургон Кави");
        title.getStyleClass().add("header-title");

        Label sub = new Label("система управління товарами");
        sub.setStyle("-fx-font-size: 11px; -fx-text-fill: #a08060;");

        VBox titleBox = new VBox(2, title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button settingsBtn = new Button("⚙  Налаштування");
        settingsBtn.getStyleClass().add("btn-settings");
        settingsBtn.setOnAction(e -> openVanSettingsDialog());

        header.getChildren().addAll(titleBox, spacer, settingsBtn);
        return header;
    }

    // ── Center (cards + toolbar + table) ──────────────────────────────

    private VBox buildCenter() {
        VBox center = new VBox(12);
        center.setPadding(new Insets(16, 20, 8, 20));

        HBox cards   = buildStatCards();
        VBox toolbar = buildToolbar();
        tableView    = buildTable();
        detailPanel  = buildDetailPanel();

        HBox tableArea = new HBox(10, tableView, detailPanel);
        HBox.setHgrow(tableView, Priority.ALWAYS);
        VBox.setVgrow(tableArea, Priority.ALWAYS);

        summaryLabel = new Label();
        summaryLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#6d4c41; -fx-padding: 2 0 0 0;");

        center.getChildren().addAll(cards, toolbar, tableArea, summaryLabel);
        return center;
    }

    // ── Stat Cards ────────────────────────────────────────────────────

    private HBox buildStatCards() {
        HBox row = new HBox(12);
        row.setFillHeight(true);

        VBox countCard   = makeCard("📦 Товарів",      "0",    "з " + MAX_ITEMS_SOFT + " максимум", "#bbdefb", "#1565c0");
        VBox volumeCard  = makeCard("📐 Об'єм (л)",    "0.0",  "завантажено",                       "#ffe0b2", "#e65100");
        VBox budgetCard  = makeCard("💰 Бюджет (грн)", "0.00", "витрачено",                         "#c8e6c9", "#2e7d32");
        VBox avgQualCard = makeCard("⭐ Сер. якість",  "—",    "по всіх товарах",                   "#ede7f6", "#6a1b9a");

        countCardValue   = (Label) ((VBox) countCard.getChildren().get(1)).getChildren().get(0);
        volumeCardValue  = (Label) ((VBox) volumeCard.getChildren().get(1)).getChildren().get(0);
        budgetCardValue  = (Label) ((VBox) budgetCard.getChildren().get(1)).getChildren().get(0);
        avgQualCardValue = (Label) ((VBox) avgQualCard.getChildren().get(1)).getChildren().get(0);

        countBar   = (ProgressBar) countCard.getChildren().get(2);
        volumeBar  = (ProgressBar) volumeCard.getChildren().get(2);
        budgetBar  = (ProgressBar) budgetCard.getChildren().get(2);
        avgQualBar = (ProgressBar) avgQualCard.getChildren().get(2);

        attachBarStyle(countBar,   "#bbdefb", "#1565c0");
        attachBarStyle(volumeBar,  "#ffe0b2", "#e65100");
        attachBarStyle(budgetBar,  "#c8e6c9", "#2e7d32");
        attachBarStyle(avgQualBar, "#ede7f6", "#6a1b9a");

        for (VBox card : new VBox[]{countCard, volumeCard, budgetCard, avgQualCard}) {
            HBox.setHgrow(card, Priority.ALWAYS);
        }
        row.getChildren().addAll(countCard, volumeCard, budgetCard, avgQualCard);
        return row;
    }

    private VBox makeCard(String title, String value, String sub, String trackColor, String barColor) {
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("stat-card-title");

        Label valueLbl = new Label(value);
        valueLbl.getStyleClass().add("stat-card-value");

        Label subLbl = new Label(sub);
        subLbl.getStyleClass().add("stat-card-sub");

        VBox textBox = new VBox(2, valueLbl, subLbl);

        ProgressBar bar = new ProgressBar(0);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(8);

        VBox card = new VBox(6, titleLbl, textBox, bar);
        card.getStyleClass().add("stat-card");
        card.setStyle("-fx-border-color: " + barColor + " #e0d5c8 #e0d5c8 #e0d5c8; -fx-border-width: 3 1 1 1;");
        return card;
    }

    private void attachBarStyle(ProgressBar bar, String trackColor, String fillColor) {
        bar.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin == null) return;
            Platform.runLater(() -> applyBarColors(bar, trackColor, fillColor));
        });
        if (bar.getSkin() != null) {
            Platform.runLater(() -> applyBarColors(bar, trackColor, fillColor));
        }
    }

    private void applyBarColors(ProgressBar bar, String trackColor, String fillColor) {
        javafx.scene.Node trackNode = bar.lookup(".track");
        javafx.scene.Node barNode   = bar.lookup(".bar");
        if (trackNode != null) trackNode.setStyle(
                "-fx-background-color: " + trackColor + "; -fx-background-radius: 3;");
        if (barNode != null) barNode.setStyle(
                "-fx-background-color: " + fillColor + "; -fx-background-radius: 3;");
    }

    // ── Toolbar ─────────────────────────────────────────────────────

    private VBox buildToolbar() {
        // ── Row 1: action buttons ─────────────────────────────
        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button addBtn    = btn("+ Додати",      "btn-add");
        Button editBtn   = btn("✏ Редагувати", "btn-edit");
        Button dupBtn    = btn("⎘ Копіювати",  "btn-purple");
        Button deleteBtn = btn("✖ Видалити",    "btn-delete");
        Button undoBtn   = btn("↩ Undo",         "btn-secondary");
        Button clearBtn  = btn("🗑 Очистити",   "btn-secondary");
        Button sortBtn   = btn("↕ Ціна/вага",  "btn-teal");
        Button searchBtn = btn("🔍 Якість...",  "btn-teal");
        Button exportBtn = btn("⬇ CSV",         "btn-export");
        Button importBtn = btn("↑ Імпорт CSV",   "btn-export");
        Button chartBtn  = btn("📊 Аналітика", "btn-add");

        addBtn.setOnAction(e    -> openAddDialog());
        editBtn.setOnAction(e   -> openEditDialog());
        dupBtn.setOnAction(e    -> duplicateSelected());
        deleteBtn.setOnAction(e -> deleteSelected());
        undoBtn.setOnAction(e   -> undoLastDelete());
        clearBtn.setOnAction(e  -> clearVan());
        sortBtn.setOnAction(e   -> {
            masterList.setAll(vanService.sortByPriceToWeightRatio());
            resetFilters();
            setStatus("Відсортовано за ціна/вага");
        });
        searchBtn.setOnAction(e -> openSearchDialog());
        exportBtn.setOnAction(e -> exportCsv());
        importBtn.setOnAction(e -> importCsv());
        chartBtn.setOnAction(e  -> openAnalytics());

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Button seedBtn = btn("⚗", "btn-seed");
        seedBtn.setTooltip(new Tooltip("Заповнити тестовими даними"));
        seedBtn.setOnAction(e -> seedTestData());

        actions.getChildren().addAll(addBtn, editBtn, dupBtn,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                deleteBtn, undoBtn, clearBtn,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                sortBtn, searchBtn, chartBtn,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                exportBtn, importBtn, spacer1, seedBtn);

        // ── Row 2: filter bar ─────────────────────────────
        HBox filters = buildFilterBar();

        VBox toolbar = new VBox(8, actions, filters);
        toolbar.getStyleClass().add("toolbar-area");
        return toolbar;
    }

    private HBox buildFilterBar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-padding: 6 0 2 0;");

        // ─ search field ──
        searchField = new TextField();
        searchField.setPromptText("🔎 Назва / тип...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(180);
        searchField.textProperty().addListener((obs, o, n) -> applyActiveFilters());

        // ─ type combo ──
        typeFilter = new ComboBox<>();
        typeFilter.getItems().addAll("— усі типи —", "Зернова", "Мелена",
                "Розчинна (банка)", "Розчинна (пакетик)");
        typeFilter.setValue("— усі типи —");
        typeFilter.setPrefWidth(155);
        typeFilter.getStyleClass().add("filter-combo");
        typeFilter.valueProperty().addListener((obs, o, n) -> applyActiveFilters());

        // ─ packaging combo ──
        packFilter = new ComboBox<>();
        packFilter.getItems().add("— усі упаковки —");
        for (PackagingType p : PackagingType.values()) packFilter.getItems().add(p.getDisplayName());
        packFilter.setValue("— усі упаковки —");
        packFilter.setPrefWidth(160);
        packFilter.getStyleClass().add("filter-combo");
        packFilter.valueProperty().addListener((obs, o, n) -> applyActiveFilters());

        // ─ quality slider ──
        qualitySlider = new Slider(1, 100, 1);
        qualitySlider.setPrefWidth(120);
        qualitySlider.setMajorTickUnit(25);
        qualitySlider.setSnapToTicks(false);
        qualitySlider.setShowTickMarks(false);
        qualitySliderLabel = new Label("якість ≥ 1");
        qualitySliderLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#5d4037; -fx-min-width:72;");
        qualitySlider.valueProperty().addListener((obs, o, n) -> {
            qualitySliderLabel.setText("якість ≥ " + n.intValue());
            applyActiveFilters();
        });

        // ─ max price field ──
        maxPriceField = new TextField();
        maxPriceField.setPromptText("макс ціна");
        maxPriceField.getStyleClass().add("search-field");
        maxPriceField.setPrefWidth(100);
        maxPriceField.textProperty().addListener((obs, o, n) -> applyActiveFilters());

        // ─ reset button ──
        Button resetBtn = new Button("✕ Скинути");
        resetBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #a08060; " +
                "-fx-font-size: 11px; -fx-cursor: hand; -fx-border-color: #d5c9bc; " +
                "-fx-border-radius: 4; -fx-padding: 4 8 4 8;");
        resetBtn.setOnAction(e -> resetFilters());

        Label typeLbl  = filterLabel("Тип:");
        Label packLbl  = filterLabel("Упаковка:");
        Label priceLbl = filterLabel("Макс ціна (грн/кг):");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        bar.getChildren().addAll(
                searchField,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                typeLbl, typeFilter,
                packLbl, packFilter,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                qualitySliderLabel, qualitySlider,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                priceLbl, maxPriceField,
                sp, resetBtn);
        return bar;
    }

    private Label filterLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:11px; -fx-text-fill:#8d6e63; -fx-font-weight:bold;");
        return l;
    }

    // ── Table ─────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private TableView<Coffee> buildTable() {
        TableView<Coffee> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("Фургон порожній. Додайте перший товар!"));

        TableColumn<Coffee, String> typeCol = new TableColumn<>("Тип");
        typeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCoffeeType()));
        typeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                badge.getStyleClass().add(badgeClass(item));
                setGraphic(badge);
                setText(null);
            }
        });
        typeCol.setPrefWidth(130);

        TableColumn<Coffee, String> nameCol = new TableColumn<>("Назва");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        nameCol.setPrefWidth(180);

        TableColumn<Coffee, Double> priceCol = new TableColumn<>("Ціна (грн/кг)");
        priceCol.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getPricePerKg()).asObject());
        priceCol.setCellFactory(col -> fmtCell("%.2f"));
        priceCol.setPrefWidth(110);

        TableColumn<Coffee, Double> weightCol = new TableColumn<>("Вага (кг)");
        weightCol.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getWeightKg()).asObject());
        weightCol.setCellFactory(col -> fmtCell("%.3f"));
        weightCol.setPrefWidth(90);

        TableColumn<Coffee, Double> volumeCol = new TableColumn<>("Об'єм (л)");
        volumeCol.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getVolumeLiters()).asObject());
        volumeCol.setCellFactory(col -> fmtCell("%.3f"));
        volumeCol.setPrefWidth(90);

        TableColumn<Coffee, String> packCol = new TableColumn<>("Упаковка");
        packCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPackagingType().getDisplayName()));
        packCol.setPrefWidth(90);

        TableColumn<Coffee, Integer> qualCol = new TableColumn<>("Якість");
        qualCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getQualityScore()).asObject());
        qualCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item.toString());
                String color = item >= 80 ? "#2e7d32" : item >= 50 ? "#e65100" : "#c62828";
                setStyle("-fx-font-weight: bold; -fx-text-fill: " + color + ";");
            }
        });
        qualCol.setPrefWidth(70);

        TableColumn<Coffee, Double> totalCol = new TableColumn<>("Сума (грн)");
        totalCol.setCellValueFactory(d -> new SimpleDoubleProperty(
                Math.round(d.getValue().getTotalPrice() * 100.0) / 100.0).asObject());
        totalCol.setCellFactory(col -> fmtCell("%.2f"));
        totalCol.setPrefWidth(110);

        TableColumn<Coffee, Double> ratioCol = new TableColumn<>("Ціна/Вага");
        ratioCol.setCellValueFactory(d -> new SimpleDoubleProperty(
                Math.round(d.getValue().getPriceToWeightRatio() * 100.0) / 100.0).asObject());
        ratioCol.setCellFactory(col -> fmtCell("%.2f"));
        ratioCol.setPrefWidth(90);

        masterList    = FXCollections.observableArrayList();
        filteredList  = new FilteredList<>(masterList, p -> true);
        table.setItems(filteredList);
        table.getColumns().addAll(typeCol, nameCol, priceCol, weightCol,
                volumeCol, packCol, qualCol, totalCol, ratioCol);

        table.setRowFactory(tv -> {
            TableRow<Coffee> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) openEditDialog();
            });
            return row;
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> updateDetailPanel(sel));

        return table;
    }

    private VBox buildDetailPanel() {
        detailName    = dpVal(""); detailName.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#3b1f08;");
        detailType    = dpVal("");
        detailPrice   = dpVal("");
        detailWeight  = dpVal("");
        detailVolume  = dpVal("");
        detailPack    = dpVal("");
        detailQual    = dpVal("");
        detailTotal   = dpVal("");
        detailRatio   = dpVal("");
        detailExtra   = dpVal("");

        detailQualBar = new ProgressBar(0);
        detailQualBar.setMaxWidth(Double.MAX_VALUE);
        detailQualBar.setPrefHeight(8);
        detailQualBar.setStyle("-fx-accent: #7b1fa2;");

        Label header = new Label("Деталі товару");
        header.setStyle("-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:#8d6e63;");

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(6);
        grid.setPadding(new Insets(8, 0, 0, 0));
        ColumnConstraints lc = new ColumnConstraints(80);
        ColumnConstraints vc = new ColumnConstraints(); vc.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(lc, vc);

        int r = 0;
        grid.addRow(r++, dpLbl("Тип:"),     detailType);
        grid.addRow(r++, dpLbl("Ціна:"),    detailPrice);
        grid.addRow(r++, dpLbl("Вага:"),    detailWeight);
        grid.addRow(r++, dpLbl("Об'єм:"),   detailVolume);
        grid.addRow(r++, dpLbl("Упак.:"),   detailPack);
        grid.addRow(r++, dpLbl("Якість:"),  detailQual);
        grid.add(detailQualBar, 0, r++, 2, 1);
        grid.addRow(r++, dpLbl("Сума:"),    detailTotal);
        grid.addRow(r++, dpLbl("Ц/В:"),     detailRatio);
        grid.addRow(r++, dpLbl("Додатково:"), detailExtra);

        VBox panel = new VBox(6, header, detailName, new Separator(), grid);
        panel.getStyleClass().add("detail-panel");
        panel.setPrefWidth(200);
        panel.setMinWidth(190);
        panel.setMaxWidth(220);
        panel.setVisible(false);
        return panel;
    }

    private Label dpLbl(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-size:10px;-fx-text-fill:#a08060;-fx-font-weight:bold;");
        return l;
    }
    private Label dpVal(String v) {
        Label l = new Label(v);
        l.setStyle("-fx-font-size:12px;-fx-text-fill:#3b1f08;");
        l.setWrapText(true);
        return l;
    }

    private void updateDetailPanel(Coffee c) {
        if (c == null) { detailPanel.setVisible(false); return; }
        detailPanel.setVisible(true);
        detailName.setText(c.getName());
        detailType.setText(c.getCoffeeType());
        detailPrice.setText(String.format("%.2f грн/кг", c.getPricePerKg()));
        detailWeight.setText(String.format("%.3f кг", c.getWeightKg()));
        detailVolume.setText(String.format("%.3f л", c.getVolumeLiters()));
        detailPack.setText(c.getPackagingType().getDisplayName());
        detailQual.setText(c.getQualityScore() + " / 100");
        detailQualBar.setProgress(c.getQualityScore() / 100.0);
        detailTotal.setText(String.format("%.2f грн", c.getTotalPrice()));
        detailRatio.setText(String.format("%.2f", c.getPriceToWeightRatio()));
        String extra;
        if      (c instanceof BeanCoffee bc)        extra = "Пох.: " + bc.getOrigin() + "\nОбсм.: " + bc.getRoastLevel();
        else if (c instanceof GroundCoffee gc)      extra = "Помол: " + gc.getGrindSize();
        else if (c instanceof InstantCoffeeJar ij)  extra = "Гранули: " + ij.getGranules();
        else if (c instanceof InstantCoffeeSachet is) extra = "Пакетики: " + is.getSachetsCount();
        else                                        extra = "";
        detailExtra.setText(extra);
    }

    // ── Status bar ────────────────────────────────────────────────────

    private HBox buildStatusBar() {
        HBox bar = new HBox();
        bar.getStyleClass().add("status-bar");
        bar.setAlignment(Pos.CENTER_LEFT);
        statusLabel = new Label("Готово");
        statusLabel.getStyleClass().add("status-label");
        bar.getChildren().add(statusLabel);
        return bar;
    }

    // ── Data refresh ──────────────────────────────────────────────────

    private void refreshData() {
        masterList.setAll(vanService.getAllCoffee());
        applyActiveFilters();
        updateCards();
    }

    private void applyActiveFilters() {
        String text     = searchField  == null ? "" : searchField.getText();
        String type     = typeFilter   == null ? null : typeFilter.getValue();
        String pack     = packFilter   == null ? null : packFilter.getValue();
        int    minQual  = qualitySlider == null ? 1 : (int) qualitySlider.getValue();
        double maxPrice = parseMaxPrice();

        boolean noType = type  == null || type.startsWith("—");
        boolean noPack = pack  == null || pack.startsWith("—");
        String lowerText = text == null ? "" : text.toLowerCase();

        filteredList.setPredicate(c ->
            (lowerText.isBlank() || c.getName().toLowerCase().contains(lowerText)
                    || c.getCoffeeType().toLowerCase().contains(lowerText))
            && (noType || c.getCoffeeType().equals(type))
            && (noPack || c.getPackagingType().getDisplayName().equals(pack))
            && c.getQualityScore() >= minQual
            && (maxPrice <= 0 || c.getPricePerKg() <= maxPrice)
        );
        updateCards();
    }

    private double parseMaxPrice() {
        if (maxPriceField == null || maxPriceField.getText().isBlank()) return 0;
        try { return Double.parseDouble(maxPriceField.getText().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private void resetFilters() {
        if (searchField   != null) searchField.clear();
        if (typeFilter    != null) typeFilter.setValue("— усі типи —");
        if (packFilter    != null) packFilter.setValue("— усі упаковки —");
        if (qualitySlider != null) qualitySlider.setValue(1);
        if (maxPriceField != null) maxPriceField.clear();
    }

    private void updateCards() {
        double usedVol    = vanService.getTotalVolume();
        double maxVol     = vanService.getVan().getMaxVolumeLiters();
        double usedBudget = vanService.getTotalBudget();
        double maxBudget  = vanService.getVan().getMaxBudget();
        int    count      = masterList.size();

        double avgQual = masterList.stream()
                .mapToInt(Coffee::getQualityScore).average().orElse(0);

        volumeCardValue.setText(String.format("%.1f / %.1f", usedVol, maxVol));
        budgetCardValue.setText(String.format("%.2f / %.2f", usedBudget, maxBudget));
        countCardValue.setText(count + " од.");
        avgQualCardValue.setText(count == 0 ? "—" : String.format("%.1f", avgQual));

        volumeBar.setProgress(maxVol > 0 ? Math.min(usedVol / maxVol, 1.0) : 0);
        budgetBar.setProgress(maxBudget > 0 ? Math.min(usedBudget / maxBudget, 1.0) : 0);
        countBar.setProgress(Math.min((double) count / MAX_ITEMS_SOFT, 1.0));
        avgQualBar.setProgress(avgQual / 100.0);

        // summary per type
        if (summaryLabel != null && count > 0) {
            long bean    = masterList.stream().filter(c -> c.getCoffeeType().equals("Зернова")).count();
            long ground  = masterList.stream().filter(c -> c.getCoffeeType().equals("Мелена")).count();
            long jar     = masterList.stream().filter(c -> c.getCoffeeType().equals("Розчинна (банка)")).count();
            long sachet  = masterList.stream().filter(c -> c.getCoffeeType().equals("Розчинна (пакетик)")).count();
            double totalW = masterList.stream().mapToDouble(Coffee::getWeightKg).sum();
            double totalS = masterList.stream().mapToDouble(Coffee::getTotalPrice).sum();
            summaryLabel.setText(String.format(
                    "Зернова: %d  ·  Мелена: %d  ·  Банка: %d  ·  Пакетик: %d    " +
                    "│  Загальна вага: %.2f кг  │  Загальна сума: %.2f грн",
                    bean, ground, jar, sachet, totalW, totalS));
        } else if (summaryLabel != null) {
            summaryLabel.setText("");
        }

        statusLabel.setText(String.format(
                "Показано: %d з %d | Об'єм: %.1f л (%.0f%%) | Бюджет: %.2f грн (%.0f%%) | Сер. якість: %.0f",
                filteredList.size(), count,
                usedVol, maxVol > 0 ? usedVol / maxVol * 100 : 0,
                usedBudget, maxBudget > 0 ? usedBudget / maxBudget * 100 : 0,
                avgQual));
    }

    // ── Actions ───────────────────────────────────────────────────────

    private void openAddDialog() {
        CoffeeDialog dialog = new CoffeeDialog(null, vanService);
        dialog.initOwner(primaryStage);
        dialog.showAndWait().filter(c -> c != null).ifPresent(coffee -> {
            boolean added = vanService.addCoffee(coffee);
            if (added) {
                refreshData();
                setStatus("✅ Товар «" + coffee.getName() + "» додано до фургону");
            } else {
                showAlert("Помилка завантаження",
                        "Неможливо додати товар: перевищено об'єм або бюджет фургону.");
            }
        });
    }

    private void openEditDialog() {
        Coffee selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Нічого не вибрано", "Виберіть товар для редагування."); return; }
        CoffeeDialog dialog = new CoffeeDialog(selected, vanService);
        dialog.initOwner(primaryStage);
        dialog.showAndWait().filter(c -> c != null).ifPresent(updated -> {
            vanService.updateCoffee(selected, updated);
            refreshData();
            setStatus("✏ Товар «" + updated.getName() + "» оновлено");
        });
    }

    private void deleteSelected() {
        Coffee selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Нічого не вибрано", "Виберіть товар для видалення."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Видалити «" + selected.getName() + "»?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Підтвердження");
        confirm.initOwner(primaryStage);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                undoStack.push(selected);
                vanService.removeCoffee(selected);
                refreshData();
                setStatus("🗑 Товар видалено (← Undo для відновлення)");
            }
        });
    }

    private void clearVan() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Очистити весь фургон? Всі товари будуть видалені.",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Підтвердження");
        confirm.initOwner(primaryStage);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                vanService.clearVan();
                refreshData();
                setStatus("🗑 Фургон очищено");
            }
        });
    }

    private void openSearchDialog() {
        SearchDialog dialog = new SearchDialog();
        dialog.initOwner(primaryStage);
        dialog.showAndWait().ifPresent(range -> {
            List<Coffee> results = vanService.findByQualityRange(range[0], range[1]);
            masterList.setAll(results);
            applyActiveFilters();
            setStatus(String.format("🔍 Знайдено %d товарів з якістю від %d до %d",
                    results.size(), range[0], range[1]));
        });
    }

    private void openVanSettingsDialog() {
        VanSettingsDialog dialog = new VanSettingsDialog(vanService.getVan());
        dialog.initOwner(primaryStage);
        dialog.showAndWait().ifPresent(settings -> {
            vanService.updateVanSettings(settings[0], settings[1]);
            updateCards();
            setStatus("⚙ Налаштування фургону оновлено");
        });
    }

    /** Exports the current (filtered) table view to a CSV file. */
    private void exportCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Зберегти як CSV");
        chooser.setInitialFileName("coffee_van_export.csv");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV файли", "*.csv"));
        File file = chooser.showSaveDialog(primaryStage);
        if (file == null) return;

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("Тип,Назва,Ціна (грн/кг),Вага (кг),Об'єм (л),Упаковка,Якість,Сума (грн),Ціна/Вага");
            for (Coffee c : filteredList) {
                pw.printf("%s,%s,%.2f,%.3f,%.3f,%s,%d,%.2f,%.2f%n",
                        c.getCoffeeType(), c.getName(),
                        c.getPricePerKg(), c.getWeightKg(), c.getVolumeLiters(),
                        c.getPackagingType().getDisplayName(), c.getQualityScore(),
                        c.getTotalPrice(), c.getPriceToWeightRatio());
            }
            logger.info("Exported {} items to {}", filteredList.size(), file.getAbsolutePath());
            setStatus("⬇ Експортовано " + filteredList.size() + " рядків → " + file.getName());
        } catch (IOException ex) {
            logger.error("CSV export failed", ex);
            showAlert("Помилка експорту", "Не вдалося зберегти файл:\n" + ex.getMessage());
        }
    }

    private void duplicateSelected() {
        Coffee sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { showAlert("Нічого не вибрано", "Виберіть товар для копіювання."); return; }
        CoffeeDialog dlg = new CoffeeDialog(sel, null, true);
        dlg.initOwner(primaryStage);
        dlg.showAndWait().filter(c -> c != null).ifPresent(copy -> {
            if (vanService.addCoffee(copy)) {
                refreshData();
                setStatus("⎘ Скопійовано «" + copy.getName() + "»");
            } else {
                showAlert("Не вміщується", "Перевищено об'єм або бюджет фургону.");
            }
        });
    }

    private void undoLastDelete() {
        if (undoStack.isEmpty()) { setStatus("ℹ Нічого скасовувати"); return; }
        Coffee c = undoStack.pop();
        if (vanService.addCoffee(c)) {
            refreshData();
            setStatus("↩ Відновлено «" + c.getName() + "» (залишилось: " + undoStack.size() + ")");
        } else {
            showAlert("Не вміщується", "Неможливо відновити: перевищено об'єм або бюджет.");
        }
    }

    private void importCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Імпорт CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV файли", "*.csv"));
        File file = chooser.showOpenDialog(primaryStage);
        if (file == null) return;

        int added = 0, skipped = 0, errors = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                try {
                    String[] p = line.split(",", -1);
                    if (p.length < 7) { errors++; continue; }
                    String type    = p[0].trim();
                    String name    = p[1].trim();
                    double price   = Double.parseDouble(p[2].trim());
                    double weight  = Double.parseDouble(p[3].trim());
                    double volume  = Double.parseDouble(p[4].trim());
                    String pack    = p[5].trim();
                    int quality    = Integer.parseInt(p[6].trim());
                    PackagingType pt = Arrays.stream(PackagingType.values())
                            .filter(v -> v.getDisplayName().equals(pack))
                            .findFirst().orElse(PackagingType.PACKAGE);

                    Coffee c = switch (type) {
                        case "Зернова"           -> new BeanCoffee(name, price, weight, volume, pt, quality, "", "");
                        case "Мелена"            -> new GroundCoffee(name, price, weight, volume, pt, quality, "");
                        case "Розчинна (банка)"  -> new InstantCoffeeJar(name, price, weight, volume, quality, 0);
                        case "Розчинна (пакетик)" -> new InstantCoffeeSachet(name, price, weight, volume, quality, 1);
                        default -> null;
                    };
                    if (c == null) { errors++; continue; }
                    if (vanService.addCoffee(c)) added++; else skipped++;
                } catch (Exception e) { errors++; }
            }
        } catch (IOException ex) {
            showAlert("Помилка читання", ex.getMessage()); return;
        }
        refreshData();
        setStatus(String.format("↑ CSV імпорт: +%d додано, %d пропущено, %d помилок", added, skipped, errors));
    }

    private void openAnalytics() {
        AnalyticsDialog dlg = new AnalyticsDialog(new ArrayList<>(masterList));
        dlg.initOwner(primaryStage);
        dlg.showAndWait();
    }

    private void seedTestData() {
        List<Coffee> seeds = List.of(
            new BeanCoffee("Arabica Ethiopia",   480, 1.0,  2.0,  PackagingType.PACKAGE, 92, "Ethiopia",   "Light"),
            new BeanCoffee("Robusta Vietnam",    210, 2.0,  3.5,  PackagingType.BAG,     61, "Vietnam",    "Dark"),
            new BeanCoffee("Colombia Supremo",   560, 0.5,  1.0,  PackagingType.PACKAGE, 88, "Colombia",   "Medium"),
            new GroundCoffee("Lavazza Crema",    320, 1.0,  1.5,  PackagingType.PACKAGE, 75, "Fine"),
            new GroundCoffee("Tchibo Gold",      290, 0.25, 0.5,  PackagingType.PACKAGE, 55, "Medium"),
            new GroundCoffee("Illy Classico",    640, 0.25, 0.6,  PackagingType.PACKAGE, 84, "Fine"),
            new InstantCoffeeJar("Nescafe Gold", 380, 0.2,  0.4,  70, 1),
            new InstantCoffeeJar("Jacobs Velvet",340, 0.1,  0.25, 48, 0),
            new InstantCoffeeSachet("MacCoffee", 180, 0.02, 0.05, 32, 24),
            new InstantCoffeeSachet("Nescafe 3в1",150,0.02, 0.05, 29, 30)
        );
        int added = 0;
        for (Coffee c : seeds) {
            if (vanService.addCoffee(c)) added++;
        }
        refreshData();
        setStatus("⚗ Додано " + added + " тестових товарів (" + (seeds.size() - added) + " не помістилось)");
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private String badgeClass(String type) {
        return switch (type) {
            case "Зернова"            -> "badge-bean";
            case "Мелена"             -> "badge-ground";
            case "Розчинна (банка)"   -> "badge-jar";
            case "Розчинна (пакетик)" -> "badge-sachet";
            default                    -> "badge-bean";
        };
    }

    private TableCell<Coffee, Double> fmtCell(String fmt) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format(fmt, item));
            }
        };
    }

    private Button btn(String text, String styleClass) {
        Button b = new Button(text);
        b.getStyleClass().add(styleClass);
        return b;
    }

    private void setStatus(String msg) { statusLabel.setText(msg); }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }
}
