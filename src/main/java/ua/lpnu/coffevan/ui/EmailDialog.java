package ua.lpnu.coffevan.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.lpnu.coffevan.util.SmtpEmailNotifier;

/**
 * Dialog for sending a custom email message using SMTP.
 */
public class EmailDialog extends Dialog<Void> {

    private static final Logger logger = LogManager.getLogger(EmailDialog.class);

    public EmailDialog() {
        setTitle("Надіслати Email повідомлення");
        setHeaderText("Введіть адресу отримувача та текст листа");

        // We use our custom buttons instead of DialogPane's native button bar
        // to prevent event routing and closing bugs.

        // Trigger SmtpEmailNotifier static initializer to load .env variables
        SmtpEmailNotifier.getLastErrorMessage();

        TextField toField = new TextField();
        toField.setPromptText("Введіть email отримувача");
        toField.setPrefWidth(300);

        TextField subjectField = new TextField("Тестове сповіщення з Coffee Van");
        subjectField.setPrefWidth(300);

        TextArea bodyArea = new TextArea("Привіт!\n\nЦе тестове повідомлення з інформаційної системи управління товарами Coffee Van.\n\nСистема працює справно.");
        bodyArea.setPrefRowCount(6);
        bodyArea.setWrapText(true);

        Label lblTo = new Label("Кому:");
        lblTo.setMinWidth(100);
        Label lblSub = new Label("Тема:");
        lblSub.setMinWidth(100);
        Label lblBody = new Label("Повідомлення:");
        lblBody.setMinWidth(100);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));
        grid.addRow(0, lblTo, toField);
        grid.addRow(1, lblSub, subjectField);
        grid.addRow(2, lblBody, bodyArea);

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #5d4037; -fx-font-weight: bold;");

        // Custom Buttons
        Button btnSend = new Button("Надіслати");
        btnSend.getStyleClass().add("btn-add"); // Green button matching modern UI

        Button btnCancel = new Button("Скасувати");
        btnCancel.getStyleClass().add("btn-secondary"); // Neutral button

        btnCancel.setOnAction(e -> {
            if (getDialogPane().getScene() != null && getDialogPane().getScene().getWindow() != null) {
                getDialogPane().getScene().getWindow().hide();
            } else {
                close();
            }
        });

        // Use a boolean property to track sending state so we don't manually set a bound property
        javafx.beans.property.BooleanProperty isSending = new javafx.beans.property.SimpleBooleanProperty(false);

        btnSend.setOnAction(event -> {
            String to = toField.getText().trim();
            String subject = subjectField.getText().trim();
            String body = bodyArea.getText().trim();

            logger.info("SEND BUTTON CLICKED! To: {}, Subject: {}", to, subject);

            statusLabel.setText("⏳ Відправка листа...");
            statusLabel.setStyle("-fx-text-fill: #e65100; -fx-padding: 0 0 0 26;");
            isSending.set(true);
            btnCancel.setDisable(true);

            // Run in a background thread to prevent UI freezing
            new Thread(() -> {
                try {
                    logger.info("Thread started, calling SmtpEmailNotifier.sendEmail...");
                    boolean success = SmtpEmailNotifier.sendEmail(to, subject, body);
                    Platform.runLater(() -> {
                        isSending.set(false);
                        btnCancel.setDisable(false);
                        if (success) {
                            statusLabel.setText("✅ Лист успішно надіслано!");
                            statusLabel.setStyle("-fx-text-fill: #2e7d32; -fx-padding: 0 0 0 26;");
                            
                            // Close dialog after a short delay
                            new Thread(() -> {
                                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                                Platform.runLater(() -> {
                                    if (getDialogPane().getScene() != null && getDialogPane().getScene().getWindow() != null) {
                                        getDialogPane().getScene().getWindow().hide();
                                    } else {
                                        close();
                                    }
                                });
                            }).start();
                        } else {
                            String err = SmtpEmailNotifier.getLastErrorMessage();
                            statusLabel.setText("❌ Помилка: " + (err != null ? err : "Перевірте .env"));
                            statusLabel.setStyle("-fx-text-fill: #d32f2f; -fx-padding: 0 0 0 26;");
                        }
                    });
                } catch (Throwable t) {
                    logger.error("Error during email send thread execution", t);
                    Platform.runLater(() -> {
                        isSending.set(false);
                        btnCancel.setDisable(false);
                        statusLabel.setText("❌ Помилка: " + t.getClass().getSimpleName() + ": " + t.getMessage());
                        statusLabel.setStyle("-fx-text-fill: #d32f2f; -fx-padding: 0 0 0 26;");
                    });
                }
            }).start();
        });

        // Disable send button if fields are empty OR if currently sending
        btnSend.disableProperty().bind(
                toField.textProperty().isEmpty()
                        .or(subjectField.textProperty().isEmpty())
                        .or(bodyArea.textProperty().isEmpty())
                        .or(isSending)
        );

        HBox buttonBox = new HBox(12, btnCancel, btnSend);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 16, 16, 16));

        VBox content = new VBox(10, grid, statusLabel, buttonBox);
        content.setPadding(new Insets(0, 0, 10, 0));
        getDialogPane().setContent(content);
        getDialogPane().setPrefWidth(450);

        // Allow closing via ESCAPE key
        getDialogPane().setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                if (getDialogPane().getScene() != null && getDialogPane().getScene().getWindow() != null) {
                    getDialogPane().getScene().getWindow().hide();
                } else {
                    close();
                }
            }
        });
    }
}
