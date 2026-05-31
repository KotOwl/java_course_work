package ua.lpnu.coffevan.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import ua.lpnu.coffevan.model.Coffee;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Analytics dialog showing PieChart distribution and Top-3 lists.
 */
public class AnalyticsDialog extends Dialog<Void> {

    public AnalyticsDialog(List<Coffee> items) {
        setTitle("📊 Аналітика фургону");
        initModality(Modality.APPLICATION_MODAL);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        getDialogPane().setPrefSize(720, 520);
        getDialogPane().setStyle("-fx-background-color: #faf7f4;");

        if (items.isEmpty()) {
            getDialogPane().setContent(new Label("Немає даних для аналізу."));
            return;
        }

        // ── Pie chart by type ──────────────────────────────────────────
        Map<String, Long> byType = items.stream()
                .collect(Collectors.groupingBy(Coffee::getCoffeeType, Collectors.counting()));

        PieChart pie = new PieChart(FXCollections.observableArrayList(
                byType.entrySet().stream()
                        .map(e -> new PieChart.Data(e.getKey() + " (" + e.getValue() + ")", e.getValue()))
                        .toList()));
        pie.setTitle("Розподіл за типом");
        pie.setLegendVisible(true);
        pie.setPrefSize(340, 340);
        pie.setLabelsVisible(true);

        // ── Top-3 panels ───────────────────────────────────────────────
        VBox topPrice   = topPanel("💰 Топ-3 найдорожчих (грн/кг)",
                items.stream().sorted(Comparator.comparingDouble(Coffee::getPricePerKg).reversed())
                        .limit(3).toList(),
                c -> String.format("%.0f грн/кг", c.getPricePerKg()));

        VBox topQuality = topPanel("⭐ Топ-3 найякісніших",
                items.stream().sorted(Comparator.comparingInt(Coffee::getQualityScore).reversed())
                        .limit(3).toList(),
                c -> "якість " + c.getQualityScore());

        VBox topVolume  = topPanel("📐 Топ-3 за об'ємом (л)",
                items.stream().sorted(Comparator.comparingDouble(Coffee::getVolumeLiters).reversed())
                        .limit(3).toList(),
                c -> String.format("%.3f л", c.getVolumeLiters()));

        VBox tops = new VBox(16, topPrice, topQuality, topVolume);
        tops.setPadding(new Insets(0, 0, 0, 16));

        // ── Summary stats ──────────────────────────────────────────────
        double avgPrice  = items.stream().mapToDouble(Coffee::getPricePerKg).average().orElse(0);
        double avgQual   = items.stream().mapToInt(Coffee::getQualityScore).average().orElse(0);
        double totalW    = items.stream().mapToDouble(Coffee::getWeightKg).sum();
        double totalS    = items.stream().mapToDouble(Coffee::getTotalPrice).sum();

        Label statsLbl = new Label(String.format(
                "Всього: %d товарів  |  Сер. ціна: %.0f грн/кг  |  Сер. якість: %.1f  |  " +
                "Загальна вага: %.2f кг  |  Загальна сума: %.2f грн",
                items.size(), avgPrice, avgQual, totalW, totalS));
        statsLbl.setStyle("-fx-font-size:11px; -fx-text-fill:#5d4037; -fx-wrap-text:true;");
        statsLbl.setWrapText(true);

        HBox charts = new HBox(0, pie, tops);
        charts.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(pie, Priority.NEVER);
        HBox.setHgrow(tops, Priority.ALWAYS);

        VBox root = new VBox(16, charts, statsLbl);
        root.setPadding(new Insets(20));

        getDialogPane().setContent(root);
    }

    private VBox topPanel(String title, List<Coffee> list, java.util.function.Function<Coffee, String> valueF) {
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-weight:bold; -fx-font-size:12px; -fx-text-fill:#4e342e;");

        VBox box = new VBox(4, titleLbl);
        box.setStyle("-fx-background-color:white; -fx-border-color:#e0d5c8; -fx-border-width:1;" +
                     "-fx-border-radius:6; -fx-background-radius:6; -fx-padding:10;");

        int rank = 1;
        for (Coffee c : list) {
            Label row = new Label(rank++ + ". " + c.getName() + "  —  " + valueF.apply(c));
            row.setStyle("-fx-font-size:11px; -fx-text-fill:#3b1f08;");
            box.getChildren().add(row);
        }
        return box;
    }
}
