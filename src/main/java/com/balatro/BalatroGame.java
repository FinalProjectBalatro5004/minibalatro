package com.balatro;

import com.balatro.view.GameView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Main application class for the Balatro game.
 */
public class BalatroGame extends Application {

    // Constants for window size
    private static final double PREFERRED_WIDTH = 1080;
    private static final double PREFERRED_HEIGHT = 1000;
    private static final double MIN_WIDTH = 850;
    private static final double MIN_HEIGHT = 650;
    private static final double MAX_WIDTH = 1600;
    private static final double MAX_HEIGHT = 1000;

    /**
     * The entry point for the JavaFX application.
     * 
     * @param stage the primary stage for this application
     */
    @Override
    public void start(Stage stage) {
        // Create the main game view
        GameView gameView = new GameView();
        
        // Create a scene with the game view using preferred size
        Scene scene = new Scene(gameView, PREFERRED_WIDTH, PREFERRED_HEIGHT, Color.web("#1a1a2e"));
        
        // Load the CSS style sheet
        scene.getStylesheets().add(getClass().getResource("/styles/game.css").toExternalForm());
        
        // Configure the stage with size constraints
        stage.setTitle("Balatro");
        stage.setScene(scene);
        
        // Set window size constraints
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMaxWidth(MAX_WIDTH);
        stage.setMaxHeight(MAX_HEIGHT);
        
        // Enable resizing
        stage.setResizable(true);
        
        stage.show();
    }

    /**
     * The main entry point for the application.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
} 