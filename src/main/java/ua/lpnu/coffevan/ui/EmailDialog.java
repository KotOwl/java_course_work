package ua.lpnu.coffevan.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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

        ButtonType sendBtnType = new ButtonType("Надіслати", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(sendBtnType, ButtonType.CANCEL);

        TextField toField = new TextField(System.getProperty("mail.to", "admin@example.com"));
        toField.setPrefWidth(300);

        TextField subjectField = new TextField("Тестове сповіщення з Coffee Van");
        subjectField.setPrefWidth(300);

        TextArea bodyArea = new TextArea("Привіт!\n\nЦе тестове повідомлення з інформаційної системи управління товарами Coffee Van.\n\nСистема працює справно.");
        bodyArea.setPrefRowCount(6);
        bodyArea.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));
        grid.addRow(0, new Label("Кому:"), toField);
        grid.addRow(1, new Label("Тема:"), subjectField);
        grid.addRow(2, new Label("Повідомлення:"), bodyArea);

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #5d4037; -fx-font-weight: bold;");

        VBox content = new VBox(10, grid, statusLabel);
        content.setPadding(new Insets(0, 0, 10, 0));
        getDialogPane().setContent(content);
        getDialogPane().setPrefWidth(450);

        // Disable send button if fields are empty
        javafx.scene.Node sendButton = getDialogPane().lookupButton(sendBtnType);
        sendButton.disableProperty().bind(
                toField.textProperty().isEmpty()
                        .or(subjectField.textProperty().isEmpty())
                        .or(bodyArea.textProperty().isEmpty())
        );

        // Prevent the dialog from closing immediately when clicking Send
        // so we can execute the task and show result status
        sendButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            event.consume(); // Prevent default close

            String to = toField.getText().trim();
            String subject = subjectField.getText().trim();
            String body = bodyArea.getText().trim();

            statusLabel.setText("⏳ Відправка листа...");
            statusLabel.setStyle("-fx-text-fill: #e65100; -fx-padding: 0 0 0 26;");
            sendButton.setDisable(true);

            // Run in a background thread to prevent UI freezing
            new Thread(() -> {
                boolean success = SmtpEmailNotifier.sendEmail(to, subject, body);
                Platform.runLater(() -> {
                    sendButton.setDisable(false);
                    if (success) {
                        statusLabel.setText("✅ Лист успішно надіслано!");
                        statusLabel.setStyle("-fx-text-fill: #2e7d32; -fx-padding: 0 0 0 26;");
                        
                        // Close dialog after a short delay
                        new Thread(() -> {
                            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                            Platform.runLater(this::close);
                        }).start();
                    } else {
                        statusLabel.setText("❌ Помилка відправки! Перевірте налаштування .env");
                        statusLabel.setStyle("-fx-text-fill: #d32f2f; -fx-padding: 0 0 0 26;");
                    }
                });
            }).start();
        });
    }
}
