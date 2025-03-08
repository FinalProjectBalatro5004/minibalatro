package com.balatro.view;

import com.balatro.model.Card;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Visual representation of a playing card in JavaFX.
 */
public class CardView extends StackPane {
    private static final double CARD_WIDTH = 100;
    private static final double CARD_HEIGHT = 140;
    private static final double CORNER_RADIUS = 10;
    private static final String FONT_FAMILY = "Arial";
    private static final double CORNER_PADDING = 10;
    private static final double CENTER_SUIT_SCALE = 2.0;
    
    private final Card card;
    private final Rectangle background;
    private boolean isSelected;

    /**
     * Creates a new card view for the specified card.
     *
     * @param card the card to display
     */
    public CardView(Card card) {
        this.card = card;
        this.isSelected = false;

        // Create card background with border
        background = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
        background.setArcWidth(CORNER_RADIUS);
        background.setArcHeight(CORNER_RADIUS);
        background.setFill(Color.WHITE);
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(1);

        setupRegularCard();

        // Add hover effect and click handling
        setupHoverEffect();
        setupClickHandling();
    }

    private void setupJokerCard() {
        // Center Joker text
        Label jokerLabel = new Label("JOKER");
        jokerLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 24));
        jokerLabel.setTextFill(Color.BLACK);
        
        // Yellow background for Joker
        background.setFill(Color.YELLOW);
        
        // Add components
        getChildren().addAll(background, jokerLabel);
        setAlignment(jokerLabel, Pos.CENTER);
    }

    private void setupRegularCard() {
        // Create top corner group
        VBox topCorner = new VBox(2);
        Label topRank = new Label(card.getRank());
        Label topSuit = new Label(getSuitSymbol());
        
        // Style top corner
        topRank.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 20));
        topSuit.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 20));
        topRank.setTextFill(getCardColor());
        topSuit.setTextFill(getCardColor());
        
        topCorner.getChildren().addAll(topRank, topSuit);
        topCorner.setPadding(new Insets(CORNER_PADDING, 0, 0, CORNER_PADDING));
        StackPane.setAlignment(topCorner, Pos.TOP_LEFT);

        // Create bottom corner group (rotated)
        VBox bottomCorner = new VBox(2);
        Label bottomRank = new Label(card.getRank());
        Label bottomSuit = new Label(getSuitSymbol());
        
        // Style bottom corner
        bottomRank.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 20));
        bottomSuit.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 20));
        bottomRank.setTextFill(getCardColor());
        bottomSuit.setTextFill(getCardColor());
        
        bottomCorner.getChildren().addAll(bottomRank, bottomSuit);
        bottomCorner.setPadding(new Insets(0, 5, 5, 0));
        bottomCorner.setRotate(180);
        
        // Create a container for the bottom corner to ensure proper positioning
        StackPane bottomContainer = new StackPane(bottomCorner);
        bottomContainer.setMaxWidth(CARD_WIDTH);
        bottomContainer.setMaxHeight(CARD_HEIGHT);
        StackPane.setAlignment(bottomContainer, Pos.BOTTOM_RIGHT);
        bottomContainer.setAlignment(Pos.BOTTOM_RIGHT);

        // Create center suit
        Label centerSuit = new Label(getSuitSymbol());
        centerSuit.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 48));
        centerSuit.setTextFill(getCardColor());
        StackPane.setAlignment(centerSuit, Pos.CENTER);
        
        // Add all components
        getChildren().addAll(background, centerSuit, topCorner, bottomContainer);
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
     * Sets whether this card is selected.
     *
     * @param selected true if selected, false otherwise
     */
    public void setSelected(boolean selected) {
        isSelected = selected;
        updateAppearance();
    }

    /**
     * Checks if this card is selected.
     *
     * @return true if selected, false otherwise
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Sets up the hover effect for the card.
     */
    private void setupHoverEffect() {
        setOnMouseEntered(e -> {
            if (!isSelected) {
                background.setStroke(Color.BLUE);
                setTranslateY(-5);
            }
        });

        setOnMouseExited(e -> {
            if (!isSelected) {
                background.setStroke(Color.BLACK);
                setTranslateY(0);
            }
        });
    }

    /**
     * Sets up click handling for the card.
     */
    private void setupClickHandling() {
        setOnMouseClicked(e -> {
            setSelected(!isSelected);
        });
    }

    /**
     * Updates the card's appearance based on its selected state.
     */
    private void updateAppearance() {
        if (isSelected) {
            background.setStroke(Color.BLUE);
            background.setStrokeWidth(3);
            setTranslateY(-10);
        } else {
            background.setStroke(Color.BLACK);
            background.setStrokeWidth(1);
            setTranslateY(0);
        }
    }

    /**
     * Gets the color for the card based on its suit.
     *
     * @return red for Hearts/Diamonds, black for Spades/Clubs
     */
    private Color getCardColor() {
        return switch (card.getSuit()) {
            case "Hearts", "Diamonds" -> Color.RED;
            default -> Color.BLACK;
        };
    }

    /**
     * Gets the Unicode symbol for the card's suit.
     *
     * @return the suit symbol
     */
    private String getSuitSymbol() {
        return switch (card.getSuit()) {
            case "Hearts" -> "♥";
            case "Diamonds" -> "♦";
            case "Clubs" -> "♣";
            case "Spades" -> "♠";
            default -> "?";
        };
    }
} 