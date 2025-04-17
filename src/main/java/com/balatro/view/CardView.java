package com.balatro.view;

import com.balatro.model.Card;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Visual representation of a playing card in the game UI.
 * Supports responsive resizing based on parent container.
 */
public class CardView extends StackPane {
    
    // Default card dimensions
    private static final double DEFAULT_CARD_WIDTH = 90;
    private static final double DEFAULT_CARD_HEIGHT = 126;
    
    // Minimum and maximum card dimensions for responsive scaling
    private static final double MIN_CARD_WIDTH = 70;
    private static final double MAX_CARD_WIDTH = 110;
    
    private final Card card;
    private boolean isSelected = false;
    
    // UI components that need to be accessed for resizing
    private Rectangle background;
    private BorderPane cardContent;
    private Label topLabel;
    private Label centerLabel;
    private Label bottomLabel;
    private Timeline hoverAnimation;
    private Timeline selectAnimation;
    private DropShadow dropShadow;
    
    /**
     * Creates a new card view for the given card.
     * 
     * @param card the card to display
     */
    public CardView(Card card) {
        this.card = card;
        
        // Set up the card appearance
        setPadding(new Insets(5));
        
        // Force consistent card size
        setPrefSize(DEFAULT_CARD_WIDTH, DEFAULT_CARD_HEIGHT);
        setMinSize(DEFAULT_CARD_WIDTH, DEFAULT_CARD_HEIGHT); 
        setMaxSize(DEFAULT_CARD_WIDTH, DEFAULT_CARD_HEIGHT);
        
        // Create drop shadow effect
        dropShadow = new DropShadow();
        dropShadow.setRadius(8.0);
        dropShadow.setOffsetX(0.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.4));
        
        // Create subtle lighting effect for a glossy look
        Light.Distant light = new Light.Distant();
        light.setAzimuth(-135.0);
        light.setElevation(30.0);
        
        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(0.5);
        lighting.setSpecularConstant(0.7);
        lighting.setSpecularExponent(20.0);
        lighting.setDiffuseConstant(1.0);
        
        // Create card background
        background = new Rectangle(DEFAULT_CARD_WIDTH - 10, DEFAULT_CARD_HEIGHT - 10);
        background.setArcWidth(15);
        background.setArcHeight(15);
        background.setFill(Color.WHITE);
        background.setEffect(dropShadow);
        
        // Apply lighting for glossy look
        if (card.getSuit().equalsIgnoreCase("diamonds") || card.getSuit().equalsIgnoreCase("hearts")) {
            // Slightly different lighting for red cards
            lighting.setSpecularConstant(0.8);
        }
        
        // Create a border pane for better layout control
        cardContent = new BorderPane();
        cardContent.setPrefSize(DEFAULT_CARD_WIDTH - 10, DEFAULT_CARD_HEIGHT - 10);
        cardContent.setEffect(lighting);
        
        // Color based on suit
        Color suitColor = getSuitColor(card.getSuit());
        String suitSymbol = getSuitSymbol(card.getSuit());
        
        // Create top-left rank and suit labels
        topLabel = new Label(card.getRank() + " " + suitSymbol);
        topLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        topLabel.setTextFill(suitColor);
        topLabel.setAlignment(Pos.TOP_LEFT);
        BorderPane.setAlignment(topLabel, Pos.TOP_LEFT);
        BorderPane.setMargin(topLabel, new Insets(5, 0, 0, 5));
        
        // Create center suit label (larger)
        centerLabel = new Label(suitSymbol);
        centerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        centerLabel.setTextFill(suitColor);
        centerLabel.setAlignment(Pos.CENTER);
        
        // Create bottom-right rank and suit labels (inverted)
        bottomLabel = new Label(card.getRank() + " " + suitSymbol);
        bottomLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        bottomLabel.setTextFill(suitColor);
        bottomLabel.setAlignment(Pos.BOTTOM_RIGHT);
        bottomLabel.setRotate(180);
        BorderPane.setAlignment(bottomLabel, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(bottomLabel, new Insets(0, 5, 5, 0));
        
        // Set the labels in the border pane
        cardContent.setTop(topLabel);
        cardContent.setCenter(centerLabel);
        cardContent.setBottom(bottomLabel);
        
        // Add all elements to the card
        getChildren().addAll(background, cardContent);
        
        // Apply CSS styling
        getStyleClass().add("card");
        
        // Set up hover animation timeline
        setupHoverAnimation();
        
        // Add hover effect
        setOnMouseEntered(e -> {
            if (!isSelected) {
                hoverAnimation.playFromStart();
            }
        });
        
        setOnMouseExited(e -> {
            if (!isSelected) {
                hoverAnimation.setRate(-1.0);  // Reverse the animation
                hoverAnimation.play();
            }
        });
        
        // Listen for parent container size changes and resize accordingly
        parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent != null) {
                // Get parent width when it's available
                newParent.layoutBoundsProperty().addListener((obs2, oldBounds, newBounds) -> {
                    resizeCard(newBounds.getWidth());
                });
            }
        });
    }
    
    /**
     * Sets up the hover animation timeline.
     */
    private void setupHoverAnimation() {
        hoverAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(scaleXProperty(), 1.0),
                new KeyValue(scaleYProperty(), 1.0),
                new KeyValue(translateYProperty(), 0),
                new KeyValue(dropShadow.radiusProperty(), 8.0),
                new KeyValue(dropShadow.colorProperty(), Color.color(0, 0, 0, 0.4))
            ),
            new KeyFrame(Duration.millis(150), 
                new KeyValue(scaleXProperty(), 1.05),
                new KeyValue(scaleYProperty(), 1.05),
                new KeyValue(translateYProperty(), -5),
                new KeyValue(dropShadow.radiusProperty(), 12.0),
                new KeyValue(dropShadow.colorProperty(), Color.color(0.8, 0.7, 0, 0.5))
            )
        );
        
        // Setup selection animation - keep the card size the same when selected
        selectAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(scaleXProperty(), 1.0),
                new KeyValue(scaleYProperty(), 1.0),
                new KeyValue(translateYProperty(), 0),
                new KeyValue(dropShadow.radiusProperty(), 8.0),
                new KeyValue(dropShadow.colorProperty(), Color.color(0, 0, 0, 0.4))
            ),
            new KeyFrame(Duration.millis(200), 
                new KeyValue(scaleXProperty(), 1.0),  // Keep the same scale
                new KeyValue(scaleYProperty(), 1.0),  // Keep the same scale
                new KeyValue(translateYProperty(), -12), // Move up by 12px
                new KeyValue(dropShadow.radiusProperty(), 12.0),
                new KeyValue(dropShadow.colorProperty(), Color.color(0, 0.8, 0, 0.6))
            )
        );
    }
    
    /**
     * Resizes the card based on the parent container width.
     * Note: This method now maintains consistent card sizing.
     * 
     * @param containerWidth the width of the parent container
     */
    private void resizeCard(double containerWidth) {
        // No longer resize cards based on container width
        // Instead use fixed dimensions for consistency
        
        // Update card components with fixed dimensions
        background.setWidth(DEFAULT_CARD_WIDTH - 10);
        background.setHeight(DEFAULT_CARD_HEIGHT - 10);
        
        // Update content container
        cardContent.setPrefSize(DEFAULT_CARD_WIDTH - 10, DEFAULT_CARD_HEIGHT - 10);
        
        // Set consistent font sizes
        double topFontSize = DEFAULT_CARD_WIDTH * 0.18;
        double centerFontSize = DEFAULT_CARD_WIDTH * 0.36;
        
        topLabel.setFont(Font.font("Arial", FontWeight.BOLD, topFontSize));
        centerLabel.setFont(Font.font("Arial", FontWeight.BOLD, centerFontSize));
        bottomLabel.setFont(Font.font("Arial", FontWeight.BOLD, topFontSize));
        
        // Set consistent shadow
        dropShadow.setRadius(DEFAULT_CARD_WIDTH * 0.08);
        dropShadow.setOffsetY(DEFAULT_CARD_WIDTH * 0.02);
    }
    
    /**
     * Sets whether this card is selected.
     * 
     * @param selected whether the card is selected
     */
    public void setSelected(boolean selected) {
        if (this.isSelected == selected) {
            return; // No change
        }
        
        this.isSelected = selected;
        
        if (selected) {
            // Apply selection effect
            if (selectAnimation != null) {
                selectAnimation.stop();
            }
            if (hoverAnimation != null) {
                hoverAnimation.stop();
            }
            
            // Create a more pronounced selection effect - GREEN glow
            DropShadow selectionGlow = new DropShadow();
            selectionGlow.setColor(Color.rgb(50, 255, 50, 0.9)); // Brighter green with increased opacity
            selectionGlow.setRadius(25);
            selectionGlow.setSpread(0.6);
            background.setEffect(selectionGlow); // Apply to background instead of the whole card
            
            // Add a visual indicator at the top of the card to make selection obvious - GREEN indicator
            Rectangle selectionIndicator = new Rectangle(getWidth(), 8);
            selectionIndicator.setFill(Color.rgb(50, 255, 50, 0.9)); // Brighter green
            selectionIndicator.setTranslateY(-getHeight()/2 + 4);
            selectionIndicator.setArcWidth(8);
            selectionIndicator.setArcHeight(8);
            getChildren().add(selectionIndicator);
            
            // Scale up the card slightly for better visibility
            setScaleX(1.05);
            setScaleY(1.05);
            setTranslateY(-12); // Move up by 12px
            
            // Add a style class for CSS styling
            getStyleClass().add("selected-card");
        } else {
            // Remove selection effect
            background.setEffect(dropShadow); // Restore original shadow effect
            setScaleX(1.0);
            setScaleY(1.0);
            setTranslateY(0);
            
            // Remove the selection indicator (if present)
            getChildren().removeIf(node -> node instanceof Rectangle && node != background);
            
            // Remove the style class
            getStyleClass().remove("selected-card");
        }
    }
    
    /**
     * Gets the card represented by this view.
     * 
     * @return the card
     */
    public Card getCard() {
        return card;
    }
    
    /**
     * Gets a visual symbol for the given suit.
     * 
     * @param suit the card suit
     * @return the suit symbol
     */
    private String getSuitSymbol(String suit) {
        switch (suit.toLowerCase()) {
            case "hearts": return "♥";
            case "diamonds": return "♦";
            case "clubs": return "♣";
            case "spades": return "♠";
            default: return suit;
        }
    }
    
    /**
     * Gets the color for the given suit.
     * 
     * @param suit the card suit
     * @return the suit color
     */
    private Color getSuitColor(String suit) {
        switch (suit.toLowerCase()) {
            case "hearts":
                return Color.rgb(220, 20, 60); // Crimson red for hearts
            case "diamonds":
                return Color.rgb(255, 0, 0);   // Bright red for diamonds
            case "clubs":
                return Color.rgb(20, 20, 20);  // Near black for clubs
            case "spades":
                return Color.rgb(0, 0, 0);     // Pure black for spades
            default:
                return Color.BLACK;
        }
    }
} 