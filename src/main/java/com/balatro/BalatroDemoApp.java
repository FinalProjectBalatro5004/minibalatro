package com.balatro;

import com.balatro.model.ActivationType;
import com.balatro.model.Card;
import com.balatro.model.Joker;
import com.balatro.model.JokerType;
import com.balatro.model.RarityType;
import com.balatro.view.CardView;
import com.balatro.view.JokerView;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main application class for Balatro demo.
 */
public class BalatroDemoApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        
        // Title
        Label title = new Label("Balatro Card Demo");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        
        // Create a row of cards (one of each suit)
        HBox regularCards = new HBox(20);
        CardView heartCard = new CardView(new Card("Hearts", "A", 11));
        CardView diamondCard = new CardView(new Card("Diamonds", "K", 10));
        CardView clubCard = new CardView(new Card("Clubs", "Q", 10));
        CardView spadeCard = new CardView(new Card("Spades", "J", 10));
        
        regularCards.getChildren().addAll(heartCard, diamondCard, clubCard, spadeCard);
        
        // Create a row of number cards
        HBox numberCards = new HBox(20);
        for (int i = 2; i <= 10; i += 2) {
            CardView card = new CardView(new Card("Hearts", String.valueOf(i), i));
            numberCards.getChildren().add(card);
        }
        
        // Add jokers
        HBox specialCards = new HBox(20);
        JokerView steelJoker = new JokerView(new Joker(JokerType.STANDARD_JOKER, 4, ActivationType.INDEPENDENT, RarityType.COMMON));
        JokerView glassJoker = new JokerView(new Joker(JokerType.FIBONACCI, 8, ActivationType.ON_SCORED, RarityType.RARE));
        specialCards.getChildren().addAll(steelJoker, glassJoker);
        
        // Add instructions
        Label instructions = new Label("Click on a card to select/deselect it. Hover over cards to see the hover effect.");
        instructions.setStyle("-fx-font-style: italic;");
        
        // Add all components to the root
        root.getChildren().addAll(
            title, 
            new Label("Face Cards (Different Suits):"), 
            regularCards,
            new Label("Number Cards:"),
            numberCards,
            new Label("Special Cards:"),
            specialCards,
            instructions
        );
        
        // Create the scene and show the stage
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setTitle("Balatro Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main method to launch the application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
} 