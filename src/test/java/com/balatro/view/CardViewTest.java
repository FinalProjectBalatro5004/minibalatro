package com.balatro.view;

import com.balatro.model.Card;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * A simple test application to display CardView components.
 */
public class CardViewTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        
        // Title
        Label title = new Label("Card View Test");
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
        
        // Add a joker if supported
        HBox specialCards = new HBox(20);
        try {
            CardView jokerCard = new CardView(new Card("Joker", "Joker", 0));
            specialCards.getChildren().add(jokerCard);
            
            Label jokerLabel = new Label("Joker Card");
            specialCards.getChildren().add(jokerLabel);
        } catch (Exception e) {
            Label noJoker = new Label("Joker cards not supported in current implementation");
            specialCards.getChildren().add(noJoker);
        }
        
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
        primaryStage.setTitle("CardView Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} 