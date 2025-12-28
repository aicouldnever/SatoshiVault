package com.wallet.app;

import com.wallet.app.util.ThemeManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load welcome view (Layer 1)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/wallet/app/view/auth/WelcomeView.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root, 1100, 700);
        
        // Apply theme based on user preference
        ThemeManager.applyTheme(scene);
        
        primaryStage.setTitle("Satoshi Vault - Welcome");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
