package com.balatro.view;

import com.balatro.model.Joker;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Visual representation of a Joker card in JavaFX.
 */
public class JokerView extends StackPane {
    private static final double CARD_WIDTH = 100;
    private static final double CARD_HEIGHT = 140;
    private static final double CORNER_RADIUS = 10;
    private static final String FONT_FAMILY = "Arial";
    
    private final Joker joker;
    private final Rectangle background;
    private boolean isSelected;

    /**
     * Creates a new joker view for the specified joker.
     *
     * @param joker the joker to display
     */
    public JokerView(Joker joker) {
        this.joker = joker;
        this.isSelected = false;

        // Create card background
        background = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
        background.setArcWidth(CORNER_RADIUS);
        background.setArcHeight(CORNER_RADIUS);
        background.setFill(Color.GOLD);
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(1);

        setupJokerCard();
        setupHoverEffect();
        setupClickHandling();
    }

    private void setupJokerCard() {
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);

        // Joker type
        Label typeLabel = new Label(joker.getType().toString());
        typeLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 16));
        typeLabel.setTextFill(Color.BLACK);

        // Multiplier
        Label multiplierLabel = new Label("x" + joker.getMultiplier());
        multiplierLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 24));
        multiplierLabel.setTextFill(Color.RED);

        // Activation type
        Label activationLabel = new Label(joker.getActivationType().toString());
        activationLabel.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, 12));
        activationLabel.setTextFill(Color.BLACK);
        activationLabel.setWrapText(true);
        activationLabel.setMaxWidth(CARD_WIDTH - 20);
        activationLabel.setAlignment(Pos.CENTER);

        content.getChildren().addAll(typeLabel, multiplierLabel, activationLabel);
        
        getChildren().addAll(background, content);
    }

    /**
     * Gets the joker represented by this view.
     *
     * @return the joker
     */
    public Joker getJoker() {
        return joker;
    }

    /**
     * Sets whether this joker is selected.
     *
     * @param selected true if selected, false otherwise
     */
    public void setSelected(boolean selected) {
        isSelected = selected;
        updateAppearance();
    }

    /**
     * Checks if this joker is selected.
     *
     * @return true if selected, false otherwise
     */
    public boolean isSelected() {
        return isSelected;
    }

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

    private void setupClickHandling() {
        setOnMouseClicked(e -> {
            setSelected(!isSelected);
        });
    }

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
} 