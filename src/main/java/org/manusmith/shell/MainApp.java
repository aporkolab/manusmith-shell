package org.manusmith.shell;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.manusmith.shell.service.EngineBridge;
import org.manusmith.shell.service.PreferencesService;
import org.manusmith.shell.util.Fx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    private static volatile MainApp instance;
    private Stage primaryStage;

    public static void reload() {
        MainApp currentInstance = getInstance();
        if (currentInstance != null && currentInstance.primaryStage != null) {
            try {
                currentInstance.primaryStage.setScene(loadScene());
            } catch (IOException e) {
                logger.error("Failed to reload the UI", e);
                Fx.error("Error", "Failed to reload the UI.");
            }
        }
    }
    
    public static MainApp getInstance() {
        return instance;
    }
    
    private static synchronized void setInstance(MainApp app) {
        instance = app;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        logger.debug("JavaFX start() method called");
        setInstance(this);
        this.primaryStage = primaryStage;
        
        try {
            logger.debug("Loading scene...");
            Scene scene = loadScene();
            primaryStage.setScene(scene);
            logger.debug("Scene loaded and set");
            
            primaryStage.show();
            logger.debug("Primary stage shown");
        } catch (Exception e) {
            logger.error("Error in start method", e);
            throw e;
        }

        // Setup tray icon after stage is shown
        boolean trayAvailable = false;
        try {
            logger.debug("Setting up tray integration...");
            EngineBridge engineBridge = new EngineBridge();
            TrayIntegration tray = new TrayIntegration(primaryStage, engineBridge);
            trayAvailable = tray.setupTray();
            logger.info("Tray integration available: {}", trayAvailable);
        } catch (Exception e) {
            logger.warn("Could not setup tray integration: {}", e.getMessage());
            // Continue without tray - this is not critical for basic functionality
        }

        // Hide to tray on close only when the tray icon exists; otherwise the window
        // would disappear with no way to bring it back and the JVM would keep running.
        if (trayAvailable) {
            primaryStage.setOnCloseRequest(event -> {
                primaryStage.hide();
                event.consume(); // Consume the event to prevent the window from closing
            });
        }
    }

    private static Scene loadScene() throws IOException {
        // Load saved language or use default
        PreferencesService preferencesService = new PreferencesService();
        String langCode = preferencesService.getLanguage();
        Locale locale = (langCode != null) ? Locale.forLanguageTag(langCode) : Locale.getDefault();

        // Load the resource bundle
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);

        URL fxmlUrl = MainApp.class.getResource("/fxml/main.fxml");
        if (fxmlUrl == null) {
            logger.error("Cannot find main.fxml in resources. Check classpath.");
            throw new IOException("Cannot find FXML file.");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl, bundle);
        Scene scene = new Scene(loader.load(), 800, 600);

        if (instance != null && instance.primaryStage != null) {
            instance.primaryStage.setTitle(bundle.getString("app.title"));
        }

        // Apply the default theme
        org.manusmith.shell.service.ThemeService.getInstance().applyCurrentTheme(scene);

        return scene;
    }

    public static void main(String[] args) {
        // Set macOS specific properties before JavaFX launch
        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac")) {
            System.setProperty("apple.awt.UIElement", "false");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("java.awt.headless", "false");
        }
        
        // Debug information
        logger.info("Starting ManuSmith Shell (OS: {}, Java: {}, JavaFX available: {})",
                System.getProperty("os.name"), System.getProperty("java.version"), isJavaFXAvailable());
        
        launch(args);
    }
    
    private static boolean isJavaFXAvailable() {
        try {
            Class.forName("javafx.application.Application");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
