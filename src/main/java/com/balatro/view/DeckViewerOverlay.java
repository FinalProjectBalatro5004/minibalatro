package com.balatro.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.balatro.model.Card;
import com.balatro.model.Deck;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * An overlay that shows the distribution of cards in the deck.
 * This appears when hovering over the deck size label.
 */
public class DeckViewerOverlay extends StackPane {
    
    private final Deck deck;
    private final GridPane cardGrid;
    private final Map<String, Map<String, Label>> cardCells = new HashMap<>();
    private final String[] suits = {"Spades", "Hearts", "Clubs", "Diamonds"};
    private final String[] ranks = {"A", "K", "Q", "J", "10", "9", "8", "7", "6", "5", "4", "3", "2"};
    
    private final FadeTransition fadeIn;
    private final FadeTransition fadeOut;
    private Label totalCardsLabel;
    
    /**
     * Creates a new deck viewer overlay.
     * 
     * @param deck the deck to display
     */
    public DeckViewerOverlay(Deck deck) {
        this.deck = deck;
        
        // Set up the overlay
        setVisible(false);
        setOpacity(0);
        setMaxSize(500, 350);
        setAlignment(Pos.BOTTOM_RIGHT);
        setPickOnBounds(false); // Allow mouse events to pass through when not on content
        
        // Set margins to position at the right bottom corner with some padding
        setMargin(this, new Insets(0, 20, 20, 0)); // top, right, bottom, left
        
        // Create the main container
        VBox container = new VBox(10);
        container.setPadding(new Insets(18));
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(480);
        
        // Add a drop shadow to the container for depth
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.6));
        shadow.setOffsetX(0);
        shadow.setOffsetY(4);
        shadow.setRadius(15);
        shadow.setSpread(0.1);
        container.setEffect(shadow);
        
        // Style with a dark gradient background
        container.setBackground(new Background(new BackgroundFill(
                Color.rgb(15, 25, 40, 0.95), new CornerRadii(12), Insets.EMPTY)));
        container.setBorder(new Border(new BorderStroke(
                Color.rgb(100, 150, 220, 0.6), BorderStrokeStyle.SOLID, new CornerRadii(12), new BorderWidths(1.5))));
        
        // Create title
        Label title = new Label("Cards Remaining In Deck");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);
        
        // Create the card grid
        cardGrid = createCardGrid();
        
        // Create total cards label
        totalCardsLabel = new Label("Total: 0 cards");
        totalCardsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        totalCardsLabel.setTextFill(Color.WHITE);
        totalCardsLabel.setPadding(new Insets(5, 0, 0, 0));
        
        // Add components to the container
        container.getChildren().addAll(title, cardGrid, totalCardsLabel);
        
        // Add container to the overlay - position in bottom right
        StackPane.setAlignment(container, Pos.BOTTOM_RIGHT);
        getChildren().add(container);
        
        // Set up animations
        fadeIn = new FadeTransition(Duration.millis(200), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        fadeOut = new FadeTransition(Duration.millis(200), this);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> setVisible(false));
    }
    
    /**
     * Creates the grid for displaying card distribution.
     * 
     * @return the card grid
     */
    private GridPane createCardGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(6);
        grid.setVgap(6);
        grid.setPadding(new Insets(15));
        grid.setAlignment(Pos.CENTER);
        
        // Add header row title
        Label headerLabel = new Label("Cards");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        headerLabel.setTextFill(Color.rgb(180, 180, 180));
        headerLabel.setMinWidth(40);
        headerLabel.setAlignment(Pos.CENTER);
        grid.add(headerLabel, 0, 0);
        
        // Add column headers (ranks)
        for (int i = 0; i < ranks.length; i++) {
            Label rankLabel = new Label(ranks[i]);
            rankLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            rankLabel.setTextFill(Color.rgb(220, 220, 220));
            rankLabel.setMinWidth(30);
            rankLabel.setAlignment(Pos.CENTER);
            grid.add(rankLabel, i + 1, 0);
        }
        
        // Add row headers (suits) and cells
        for (int i = 0; i < suits.length; i++) {
            String suit = suits[i];
            
            // Create suit map if not exists
            cardCells.putIfAbsent(suit, new HashMap<>());
            
            // Add suit label
            Label suitLabel = new Label(getSuitSymbol(suit));
            suitLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            suitLabel.setTextFill(getSuitColor(suit));
            suitLabel.setMinWidth(35);
            suitLabel.setAlignment(Pos.CENTER);
            grid.add(suitLabel, 0, i + 1);
            
            // Add card cells for each rank
            for (int j = 0; j < ranks.length; j++) {
                String rank = ranks[j];
                
                Label cell = new Label("0");
                cell.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
                cell.setTextFill(getSuitColor(suit));
                cell.setMinSize(30, 28);
                cell.setAlignment(Pos.CENTER);
                
                // Style the cell
                cell.setBackground(new Background(new BackgroundFill(
                        Color.rgb(30, 40, 60, 0.7), new CornerRadii(5), Insets.EMPTY)));
                cell.setBorder(new Border(new BorderStroke(
                        Color.rgb(50, 70, 100, 0.4), BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(1))));
                
                // Add hover effects for cells
                cell.setOnMouseEntered(e -> handleCellHover(e, cell, true));
                cell.setOnMouseExited(e -> handleCellHover(e, cell, false));
                
                // Store the cell in the map
                cardCells.get(suit).put(rank, cell);
                
                // Add to grid
                grid.add(cell, j + 1, i + 1);
            }
        }
        
        return grid;
    }
    
    /**
     * Handles hover effects for cells.
     * 
     * @param event the mouse event
     * @param cell the cell being hovered
     * @param isEntering whether the mouse is entering or exiting
     */
    private void handleCellHover(MouseEvent event, Label cell, boolean isEntering) {
        if (isEntering) {
            // Highlight the cell on hover
            cell.setBackground(new Background(new BackgroundFill(
                    Color.rgb(50, 70, 100, 0.8), new CornerRadii(5), Insets.EMPTY)));
            cell.setBorder(new Border(new BorderStroke(
                    Color.rgb(100, 150, 220, 0.7), BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(1.5))));
            
            // Add a subtle glow effect
            DropShadow glow = new DropShadow();
            glow.setColor(Color.rgb(100, 150, 250, 0.5));
            glow.setRadius(10);
            glow.setSpread(0.2);
            cell.setEffect(glow);
        } else {
            // Remove highlight on exit
            cell.setBackground(new Background(new BackgroundFill(
                    Color.rgb(30, 40, 60, 0.7), new CornerRadii(5), Insets.EMPTY)));
            cell.setBorder(new Border(new BorderStroke(
                    Color.rgb(50, 70, 100, 0.4), BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(1))));
            cell.setEffect(null);
        }
    }
    
    /**
     * Updates the card distribution display based on the current deck.
     */
    public void updateCardDistribution() {
        // Reset all counts to 0
        for (Map<String, Label> rankMap : cardCells.values()) {
            for (Label cell : rankMap.values()) {
                cell.setText("0");
                cell.setStyle("-fx-opacity: 0.5;");
            }
        }
        
        // Count cards in the deck
        List<Card> cards = deck.getCards();
        Map<String, Map<String, Integer>> cardCounts = new HashMap<>();
        
        // Initialize counts map
        for (String suit : suits) {
            cardCounts.put(suit, new HashMap<>());
            for (String rank : ranks) {
                cardCounts.get(suit).put(rank, 0);
            }
        }
        
        // Count cards
        for (Card card : cards) {
            String suit = card.getSuit();
            String rank = card.getRank();
            
            // Increment the count
            Map<String, Integer> rankMap = cardCounts.get(suit);
            if (rankMap != null) {
                Integer count = rankMap.get(rank);
                if (count != null) {
                    rankMap.put(rank, count + 1);
                }
            }
        }
        
        // Update the UI
        for (String suit : suits) {
            Map<String, Integer> rankMap = cardCounts.get(suit);
            if (rankMap != null) {
                for (String rank : ranks) {
                    Integer count = rankMap.get(rank);
                    if (count != null) {
                        Label cell = cardCells.get(suit).get(rank);
                        if (cell != null) {
                            cell.setText(String.valueOf(count));
                            if (count > 0) {
                                cell.setStyle("-fx-opacity: 1.0;");
                            }
                        }
                    }
                }
            }
        }
        
        // Update total cards label
        totalCardsLabel.setText("Total: " + cards.size() + " cards");
    }
    
    /**
     * Shows the overlay with an animation.
     */
    public void show() {
        updateCardDistribution();
        setVisible(true);
        fadeIn.play();
    }
    
    /**
     * Hides the overlay with an animation.
     */
    public void hide() {
        fadeOut.play();
    }
    
    /**
     * Gets a symbol for the given suit.
     * 
     * @param suit the card suit
     * @return the suit symbol
     */
    private String getSuitSymbol(String suit) {
        return switch (suit) {
            case "Hearts" -> "♥";
            case "Diamonds" -> "♦";
            case "Clubs" -> "♣";
            case "Spades" -> "♠";
            default -> suit;
        };
    }
    
    /**
     * Gets the color for the given suit.
     * 
     * @param suit the card suit
     * @return the suit color
     */
    private Color getSuitColor(String suit) {
        return switch (suit) {
            case "Hearts" -> Color.rgb(220, 20, 60);  // Crimson red
            case "Diamonds" -> Color.rgb(255, 0, 0);  // Bright red
            case "Clubs" -> Color.rgb(180, 180, 180); // Light gray
            case "Spades" -> Color.rgb(230, 230, 230); // White
            default -> Color.WHITE;
        };
    }
} 