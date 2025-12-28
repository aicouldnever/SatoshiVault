package com.wallet.app.util;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * Utility class for displaying toast notifications with retry options.
 * Uses ControlsFX Notifications API.
 */
public class NotificationUtil {
    
    /**
     * Show success toast notification
     */
    public static void showSuccess(String title, String message) {
        Notifications.create()
            .title(title)
            .text(message)
            .position(Pos.TOP_RIGHT)
            .hideAfter(Duration.seconds(4))
            .darkStyle()
            .showInformation();
    }
    
    /**
     * Show error toast notification
     */
    public static void showError(String title, String message) {
        Notifications.create()
            .title(title)
            .text(message)
            .position(Pos.TOP_RIGHT)
            .hideAfter(Duration.seconds(7))
            .darkStyle()
            .showError();
    }
    
    /**
     * Show error toast notification with retry button
     */
    public static void showErrorWithRetry(String title, String message, Runnable retryAction) {
        // Create custom graphic with retry button
        VBox content = new VBox(5);
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button retryButton = new Button("Retry");
        retryButton.getStyleClass().add("notification-retry-button");
        retryButton.setOnAction(e -> {
            retryAction.run();
        });
        
        buttonBox.getChildren().add(retryButton);
        content.getChildren().addAll(messageLabel, buttonBox);
        
        Notifications.create()
            .title(title)
            .graphic(content)
            .position(Pos.TOP_RIGHT)
            .hideAfter(Duration.seconds(10))
            .darkStyle()
            .showError();
    }
    
    /**
     * Show warning toast notification
     */
    public static void showWarning(String title, String message) {
        Notifications.create()
            .title(title)
            .text(message)
            .position(Pos.TOP_RIGHT)
            .hideAfter(Duration.seconds(5))
            .darkStyle()
            .showWarning();
    }
    
    /**
     * Show info toast notification
     */
    public static void showInfo(String title, String message) {
        Notifications.create()
            .title(title)
            .text(message)
            .position(Pos.TOP_RIGHT)
            .hideAfter(Duration.seconds(4))
            .darkStyle()
            .show();
    }
    
    /**
     * Show loading/progress notification
     */
    public static void showLoading(String title, String message) {
        Notifications.create()
            .title(title)
            .text(message)
            .position(Pos.TOP_RIGHT)
            .hideAfter(Duration.seconds(2))
            .darkStyle()
            .show();
    }
}
