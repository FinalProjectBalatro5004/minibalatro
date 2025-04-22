package com.balatro.view;

import java.util.List;

import com.balatro.model.Card;
import com.balatro.model.Joker;
import com.balatro.service.GameService;
import com.balatro.service.GameService.GameState;
import com.balatro.service.GameStateManager;
import com.balatro.service.GameStateManager.LevelStage;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Main view class for the Balatro game.
 * Handles the display of game elements and user interactions.
 */
public class GameView extends BorderPane {
    
    private final GameStateManager gameManager;
    private final GameService gameService;
    
    // UI components
    private Label gamePhaseLabel;
    private Label roundLabel;
    private Label chipsLabel;
    private Label stageValueLabel;
    private Label scoreLabel;
    private Label multiplierLabel;
    private Label handTypeLabel;
    private Label levelLabel;
    private Label stageLabel;
    private Label targetScoreLabel;
    private Label deckSizeLabel;
    private Label cardPoolLabel;
    
    private HBox playerHandArea;
    private HBox selectedCardsArea;
    private HBox jokerArea;
    
    private Button drawButton;
    private Button playButton;
    private Button discardButton;
    private Button nextRoundButton;
    
    // Add new fields for notification system
    private StackPane notificationOverlay;
    private Label notificationLabel;
    private StringProperty notificationText = new SimpleStringProperty("");
    private LevelStage lastStage = LevelStage.SMALL_BLIND;
    
    // Add new labels for displaying limits
    private Label handsPlayedLabel, discardsUsedLabel;
    private Label handLimitLabel, discardLimitLabel;
    
    // Add new field for deck viewer overlay
    private DeckViewerOverlay deckViewerOverlay;
    
    // Add new fields for joker
    private Label jokerEffectLabel;
    private Joker currentJoker;
    
    /**
     * Creates a new game view.
     */
    public GameView() {
        this.gameManager = new GameStateManager();
        this.gameService = gameManager.getGameService();
        
        // Set up the UI layout
        setPadding(new Insets(15));
        
        // Set responsive size constraints
        setMinSize(800, 650);
        setPrefSize(1080, 720);
        
        // Create game info panel
        VBox gameInfoPanel = createGameInfoPanel();
        setTop(gameInfoPanel);
        
        // Create player hand area with responsive layout
        playerHandArea = new HBox(6);
        playerHandArea.getStyleClass().addAll("card-area");
        playerHandArea.setAlignment(Pos.CENTER);
        playerHandArea.prefWidthProperty().bind(widthProperty().subtract(30));
        
        // Selected cards area
        selectedCardsArea = new HBox(6);
        selectedCardsArea.getStyleClass().addAll("card-area", "selected-cards-area");
        selectedCardsArea.setAlignment(Pos.CENTER);
        selectedCardsArea.prefWidthProperty().bind(widthProperty().subtract(30));
        selectedCardsArea.setVisible(false);
        selectedCardsArea.setManaged(false);
        
        // Create joker area with responsive layout
        jokerArea = new HBox(6);
        jokerArea.getStyleClass().addAll("card-area", "joker-area");
        jokerArea.setAlignment(Pos.CENTER);
        jokerArea.prefWidthProperty().bind(widthProperty().subtract(30));
        
        // Create joker effect label
        jokerEffectLabel = new Label();
        jokerEffectLabel.getStyleClass().add("joker-effect-label");
        jokerEffectLabel.setWrapText(true);
        jokerEffectLabel.setMaxWidth(300);
        
        // Add listener for game state changes to update joker display
        gameService.gameStateProperty().addListener((obs, oldState, newState) -> {
            updateJokerDisplay();
        });
        
        // Add listener for round completion to update joker display
        gameService.roundCompletedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                updateJokerDisplay();
            }
        });
        
        // Create notification overlay
        createNotificationOverlay();
        
        // Create deck viewer overlay
        deckViewerOverlay = new DeckViewerOverlay(gameService.getDeck());
        
        // Create buttons panel
        HBox buttonsPanel = createButtonsPanel();
        
        // Create card areas container
        VBox cardAreasContainer = new VBox(15);
        cardAreasContainer.getChildren().addAll(
                createAreaWithLabel("Your Hand", playerHandArea),
                createAreaWithLabel("Joker", jokerArea),
                buttonsPanel
        );
        
        // Add joker effect label to joker area
        jokerArea.getChildren().add(jokerEffectLabel);
        
        // Create a stack pane to handle overlay components
        StackPane gameContentWithOverlay = new StackPane();
        gameContentWithOverlay.getChildren().addAll(cardAreasContainer, notificationOverlay, deckViewerOverlay);
        
        // Set up the game data bindings
        setupBindings();
        
        // Initialize the game
        gameManager.startNewGame();
        
        // Create the start screen
        VBox startScreen = createStartScreen();
        
        // Initially display the start screen
        setCenter(startScreen);
        
        // Add listener for size changes to adjust layout
        widthProperty().addListener((obs, oldVal, newVal) -> {
            updateAllCardDisplays();
        });
    }
    
    /**
     * Creates the game information panel at the top of the screen.
     * 
     * @return the info panel
     */
    private VBox createGameInfoPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("game-info-panel");
        
        // Game title
        Label titleLabel = new Label("BALATRO");
        titleLabel.getStyleClass().add("game-title");
        
        // Game info grid
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(20);
        infoGrid.setVgap(10);
        infoGrid.setPadding(new Insets(10));
        
        // First row: Game Phase, Level, Stage
        // Game phase
        Label phaseTitle = new Label("Game Phase:");
        phaseTitle.getStyleClass().add("info-label");
        gamePhaseLabel = new Label();
        gamePhaseLabel.getStyleClass().add("info-label");
        infoGrid.add(phaseTitle, 0, 0);
        infoGrid.add(gamePhaseLabel, 1, 0);
        
        // Level
        Label levelTitle = new Label("Level:");
        levelTitle.getStyleClass().add("info-label");
        levelLabel = new Label();
        levelLabel.getStyleClass().add("info-label");
        infoGrid.add(levelTitle, 2, 0);
        infoGrid.add(levelLabel, 3, 0);
        
        // Stage
        Label stageTitle = new Label("Stage:");
        stageTitle.getStyleClass().add("info-label");
        stageLabel = new Label();
        stageLabel.getStyleClass().add("info-label");
        infoGrid.add(stageTitle, 4, 0);
        infoGrid.add(stageLabel, 5, 0);
        
        // Second row: Score, Target, Hands, Discards
        // Score
        Label scoreTitle = new Label("Score:");
        scoreTitle.getStyleClass().add("info-label");
        scoreLabel = new Label();
        scoreLabel.getStyleClass().add("info-label");
        infoGrid.add(scoreTitle, 0, 1);
        infoGrid.add(scoreLabel, 1, 1);
        
        // Target Score
        Label targetScoreTitle = new Label("Target:");
        targetScoreTitle.getStyleClass().add("info-label");
        targetScoreLabel = new Label();
        targetScoreLabel.getStyleClass().addAll("info-label", "target-score");
        infoGrid.add(targetScoreTitle, 2, 1);
        infoGrid.add(targetScoreLabel, 3, 1);
        
        // Hands Played/Limit
        Label handPlayedTitle = new Label("Hands:");
        handPlayedTitle.getStyleClass().add("info-label");
        handLimitLabel = new Label();
        handLimitLabel.getStyleClass().addAll("info-label", "limit-label");
        infoGrid.add(handPlayedTitle, 4, 1);
        infoGrid.add(handLimitLabel, 5, 1);
        
        // Discards Used/Limit
        Label discardUsedTitle = new Label("Discards:");
        discardUsedTitle.getStyleClass().add("info-label");
        discardLimitLabel = new Label();
        discardLimitLabel.getStyleClass().addAll("info-label", "limit-label");
        infoGrid.add(discardUsedTitle, 6, 1);
        infoGrid.add(discardLimitLabel, 7, 1);
        
        // Third row: Chips, Hand
        // Chips
        Label chipsTitle = new Label("Chips:");
        chipsTitle.getStyleClass().add("info-label");
        chipsLabel = new Label();
        chipsLabel.getStyleClass().addAll("info-label", "chips-label");
        infoGrid.add(chipsTitle, 0, 2);
        infoGrid.add(chipsLabel, 1, 2);
        
        // The Bet and Stage Value information will be updated when the player selects a bet amount
        // We'll create the Stage Value labels but don't add them to the grid yet
        stageValueLabel = new Label();
        stageValueLabel.getStyleClass().addAll("info-label", "ante-label");
        
        // Tooltip for Stage Value to explain what it means
        javafx.scene.control.Tooltip stageTip = new javafx.scene.control.Tooltip(
            "Stage Value increases as you progress through game stages.\n" +
            "It affects reward calculations and represents the current difficulty level."
        );
        javafx.scene.control.Tooltip.install(stageValueLabel, stageTip);
        
        // Hand Type
        Label handTypeTitle = new Label("Hand:");
        handTypeTitle.getStyleClass().add("info-label");
        handTypeLabel = new Label();
        handTypeLabel.getStyleClass().add("info-label");
        infoGrid.add(handTypeTitle, 6, 2);
        infoGrid.add(handTypeLabel, 7, 2);
        
        // Round - Added to UI but hidden to maintain compatibility
        roundLabel = new Label();
        
        // Deck size - Placed in the operation area below, not in the top information panel
        deckSizeLabel = createDeckSizeLabel("Deck", "0");
        
        panel.getChildren().addAll(titleLabel, infoGrid);
        return panel;
    }
    
    /**
     * Creates a styled label for displaying info values.
     * 
     * @param title the title for the label
     * @param value the initial value
     * @return the styled label
     */
    private Label createInfoValueLabel(String title, String value) {
        Label label = new Label(value);
        label.getStyleClass().addAll("info-label", "value-label");
        return label;
    }
    
    /**
     * Creates the buttons panel with game action buttons.
     * 
     * @return the buttons panel
     */
    private HBox createButtonsPanel() {
        HBox panel = new HBox(20);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(20, 0, 20, 0));
        
        // Create buttons
        drawButton = new Button("Draw Cards");
        drawButton.getStyleClass().addAll("action-button", "draw-button");
        
        playButton = new Button("Play");
        playButton.getStyleClass().addAll("action-button", "play-button");
        
        // Update discard button text to make it more clear
        discardButton = new Button("Discard Selected");
        discardButton.getStyleClass().addAll("action-button", "discard-button");
        
        // Create a deck visualization that can be hovered
        StackPane deckDisplay = new StackPane();
        deckDisplay.getStyleClass().add("deck-area");
        deckDisplay.setMinSize(80, 100);
        deckDisplay.setPrefSize(100, 120);
        
        // Add a background and border to make it look like a deck of cards using CSS
        deckDisplay.setStyle("-fx-background-color: #1E3250; -fx-background-radius: 10px; "
                          + "-fx-border-color: #4664A0; -fx-border-radius: 10px; "
                          + "-fx-border-width: 2px;");
        
        Label deckLabel = new Label("Cards in Deck");
        deckLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        Label deckCount = new Label(String.valueOf(gameService.getRemainingCards()));
        deckCount.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Ensure binding to real-time deck count
        deckCount.textProperty().bind(Bindings.convert(gameService.remainingCardsProperty()));
        
        VBox deckInfo = new VBox(5);
        deckInfo.setAlignment(Pos.CENTER);
        deckInfo.getChildren().addAll(deckLabel, deckCount);
        
        deckDisplay.getChildren().add(deckInfo);
        
        // Add hover effect to show the deck viewer
        deckDisplay.setOnMouseEntered(e -> {
            deckViewerOverlay.updateCardDistribution();
            deckViewerOverlay.show();
        });
        
        deckDisplay.setOnMouseExited(e -> {
            deckViewerOverlay.hide();
        });
        
        // Next Round button is no longer needed since we auto-advance to next round
        // But we'll keep it as a class field for compatibility
        nextRoundButton = new Button("Next Round");
        nextRoundButton.getStyleClass().addAll("action-button", "next-round-button");
        nextRoundButton.setVisible(false); // Hide the button
        nextRoundButton.setManaged(false); // Don't reserve space for it in layout
        
        // Add action handlers
        drawButton.setOnAction(e -> handleDrawCards());
        playButton.setOnAction(e -> handlePlayHand());
        discardButton.setOnAction(e -> handleDiscardCards());
        nextRoundButton.setOnAction(e -> handleNextRound());
        
        // Create a spacer to push deck to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        panel.getChildren().addAll(drawButton, playButton, discardButton, spacer, deckDisplay);
        return panel;
    }
    
    /**
     * Creates a titled area for displaying card groups.
     * 
     * @param title the area title
     * @param content the content node
     * @return the titled area
     */
    private VBox createAreaWithLabel(String title, HBox content) {
        VBox area = new VBox(5);
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("area-title");
        
        area.getChildren().addAll(titleLabel, content);
        return area;
    }
    
    /**
     * Creates a titled area for displaying card groups with scroll capability.
     * 
     * @param title the area title
     * @param content the content node
     * @return the titled area with scroll capability
     */
    private VBox createScrollableAreaWithLabel(String title, HBox content) {
        VBox area = new VBox(5);
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("area-title");
        
        // Create scroll pane
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToHeight(true); // Set to fit height automatically
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Show horizontal scrollbar when needed
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Don't show vertical scrollbar
        scrollPane.setPrefHeight(180); // Increase preferred height
        scrollPane.getStyleClass().add("card-scroll-pane"); // Add style class
        
        area.getChildren().addAll(titleLabel, scrollPane);
        return area;
    }
    
    /**
     * Creates the notification overlay for displaying stage advancement messages.
     */
    private void createNotificationOverlay() {
        notificationOverlay = new StackPane();
        notificationOverlay.setVisible(false);
        notificationOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        notificationOverlay.prefWidthProperty().bind(widthProperty());
        notificationOverlay.prefHeightProperty().bind(heightProperty());
        
        VBox notificationBox = new VBox(20);
        notificationBox.setAlignment(Pos.CENTER);
        notificationBox.setPadding(new Insets(20));
        notificationBox.setStyle("-fx-background-color: #222; -fx-background-radius: 10; -fx-border-color: #ffd700; -fx-border-width: 2; -fx-border-radius: 10;");
        notificationBox.setMaxWidth(500);
        notificationBox.setMaxHeight(300);
        
        notificationLabel = new Label();
        notificationLabel.textProperty().bind(notificationText);
        notificationLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");
        notificationLabel.setWrapText(true);
        
        notificationBox.getChildren().add(notificationLabel);
        notificationOverlay.getChildren().add(notificationBox);
        
        // Add listener for notification text
        notificationText.addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.isEmpty()) {
                showNotification();
            }
        });
    }
    
    /**
     * Shows the notification overlay with an animation.
     */
    private void showNotification() {
        notificationOverlay.setVisible(true);
        notificationOverlay.setOpacity(0);
        
        // Fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), notificationOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        // Pause animation (keep notification visible longer - 3 seconds)
        PauseTransition pause = new PauseTransition(Duration.millis(3000));
        
        // Fade out animation
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), notificationOverlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> notificationOverlay.setVisible(false));
        
        // Play animations in sequence
        fadeIn.setOnFinished(e -> pause.play());
        pause.setOnFinished(e -> fadeOut.play());
        fadeIn.play();
    }
    
    /**
     * Sets up data bindings between the UI and game models.
     */
    private void setupBindings() {
        // Bind game state labels
        gamePhaseLabel.textProperty().bind(gameManager.gamePhaseProperty());
        // Bind roundLabel but don't display in UI, maintain code compatibility
        roundLabel.textProperty().bind(Bindings.convert(gameManager.currentRoundProperty()));
        chipsLabel.textProperty().bind(Bindings.convert(gameManager.playerChipsProperty()));
        // We'll set stageValueLabel text manually in startGame() and then bind it
        //stageValueLabel.textProperty().bind(Bindings.convert(gameManager.stageValueProperty()));
        scoreLabel.textProperty().bind(Bindings.convert(gameService.scoreProperty()));
        handTypeLabel.textProperty().bind(gameService.currentHandTypeDisplayProperty());
        levelLabel.textProperty().bind(Bindings.convert(gameManager.currentLevelProperty()));
        stageLabel.textProperty().bind(Bindings.createStringBinding(
            () -> gameManager.currentStageProperty().get().getDisplayName(),
            gameManager.currentStageProperty()
        ));
        targetScoreLabel.textProperty().bind(Bindings.convert(gameService.targetScoreProperty()));
        
        // Bind limit labels
        handLimitLabel.textProperty().bind(Bindings.createStringBinding(
            () -> gameManager.getHandsPlayedInStage() + "/" + gameManager.getMaxHandsPerStage(),
            gameManager.handsPlayedInStageProperty(),
            gameManager.maxHandsPerStageProperty()
        ));
        
        discardLimitLabel.textProperty().bind(Bindings.createStringBinding(
            () -> gameManager.getDiscardsUsedInStage() + "/" + gameManager.getMaxDiscardsPerStage(),
            gameManager.discardsUsedInStageProperty(),
            gameManager.maxDiscardsPerStageProperty()
        ));
        
        // Set initial deck size
        deckSizeLabel.setText(String.valueOf(gameService.getRemainingCards()));
        
        // Add listener for stage changes to show notifications
        gameManager.currentStageProperty().addListener((obs, oldStage, newStage) -> {
            if (oldStage != null && newStage != null && oldStage != newStage) {
                // Only show notification for stage advancement
                if (oldStage == LevelStage.SMALL_BLIND && newStage == LevelStage.BIG_BLIND) {
                    notificationText.set("Stage Complete!\nAdvancing to Big Blind stage...\nDeck will be shuffled automatically.");
                } else if (oldStage == LevelStage.BIG_BLIND && newStage == LevelStage.THE_HOOK) {
                    notificationText.set("Stage Complete!\nAdvancing to The Hook stage...\nDeck will be shuffled automatically.");
                } else if (oldStage == LevelStage.THE_HOOK && newStage == LevelStage.SMALL_BLIND) {
                    int newLevel = gameManager.currentLevelProperty().get();
                    notificationText.set("Level " + (newLevel-1) + " Complete!\nAdvancing to Level " + newLevel + "...\nDeck will be shuffled automatically.");
                }
                
                // Set lastStage to the new stage for tracking
                lastStage = newStage;
            }
        });
        
        // Button disable properties
        drawButton.disableProperty().bind(gameService.canDrawCardsProperty().not());
        
        // Disable Play button if:
        // 1. Game state is not WAITING_FOR_SELECTION, or
        // 2. No cards are selected, or
        // 3. Hand limit is reached
        playButton.disableProperty().bind(
            Bindings.or(
                Bindings.or(
                    gameService.gameStateProperty().isNotEqualTo(GameState.WAITING_FOR_SELECTION),
                    Bindings.size(gameService.getSelectedCards()).lessThan(1)
                ),
                gameManager.handLimitReachedProperty()
            )
        );
        
        // Disable Discard button if:
        // 1. Game state is not WAITING_FOR_SELECTION, or
        // 2. No cards are selected, or
        // 3. Discard limit is reached
        discardButton.disableProperty().bind(
            Bindings.or(
                Bindings.or(
                    gameService.gameStateProperty().isNotEqualTo(GameState.WAITING_FOR_SELECTION),
                    Bindings.isEmpty(gameService.getSelectedCards())
                ),
                gameManager.discardLimitReachedProperty()
            )
        );
        
        // Next Round button is no longer needed/visible, but we'll keep the binding for compatibility
        // We've hidden the button in the UI
    }
    
    /**
     * Handles drawing cards action.
     */
    private void handleDrawCards() {
        // Get the number of cards that will be drawn
        int cardsToDraw = gameService.cardsToDrawCountProperty().get();
        
        // Draw cards
        gameService.drawCards();
        
        // Update display
        updateHandDisplay();
        updateSelectedCardsDisplay();
        
        // Update deck size - subtract the number of cards drawn
        deckSizeLabel.setText(String.valueOf(gameService.getRemainingCards()));
    }
    
    /**
     * Handles playing selected cards.
     */
    private void handlePlayHand() {
        // Get the number of cards being played
        int cardsPlayed = gameService.getSelectedCards().size();
        
        // Check if player needs to draw cards first
        if (gameService.getGameState() == GameService.GameState.WAITING_FOR_DRAW && 
            gameService.canDrawCardsProperty().get()) {
            // Show notification that player needs to draw cards first
            notificationText.set("Please draw cards before playing your hand!");
            return;
        }
        
        if (cardsPlayed > 0) {
            gameManager.handsPlayedInStageProperty().set(gameManager.getHandsPlayedInStage() + 1);
            
            // Evaluate the hand and get the score
            int score = gameService.evaluateHand();
            
            // Check if round is completed (score reached target)
            if (gameService.getScore() >= gameService.getTargetScore()) {
                // Ensure score must reach or exceed target to complete the level
                gameService.roundCompletedProperty().set(true);
                // Complete the round and automatically start a new one
                gameManager.completeRound();
                
                // Show notification about stage advancement first
                // After the notification fades, start the next round automatically
                // Wait for notification (500ms fade in + 3000ms pause + 500ms fade out)
                PauseTransition waitForNotification = new PauseTransition(Duration.millis(4000));
                waitForNotification.setOnFinished(e -> {
                    // Check if player has enough chips for the next round's stage value
                    if (gameManager.getPlayerChips() < gameManager.getStageValue()) {
                        notificationText.set("You don't have enough chips for the stage value!\nGame Over!");
                        
                        // Wait before restarting completely
                        PauseTransition waitBeforeRestart = new PauseTransition(Duration.millis(4000));
                        waitBeforeRestart.setOnFinished(ev -> {
                            restartGame();
                        });
                        waitBeforeRestart.play();
                    } else {
                        gameManager.startNewRound();
                        updateAllCardDisplays();
                    }
                });
                waitForNotification.play();
            } else if (gameService.getGameState() == GameService.GameState.WAITING_FOR_DRAW) {
                // Check if that was the last hand this stage (reached or will reach hand limit)
                if (gameManager.getHandsPlayedInStage() >= gameManager.getMaxHandsPerStage()) {
                    // Check if the player failed the stage (didn't reach target score)
                    if (gameService.getScore() < gameService.getTargetScore()) {
                        // Show notification about failing the stage
                        notificationText.set("Hand limit reached!\nFailed to reach the target score of " + 
                                            gameService.getTargetScore() + ".\nYou lost your stage value of " +
                                            gameManager.getStageValue() + " chips!\nGame Over!");
                        
                        // Wait before restarting the game
                        PauseTransition waitBeforeRestart = new PauseTransition(Duration.millis(4000));
                        waitBeforeRestart.setOnFinished(e -> {
                            // Restart the game completely
                            restartGame();
                        });
                        waitBeforeRestart.play();
                        return;
                    }
                }
                
                // Automatically draw cards to replace the ones that were played
                gameService.drawCards();
            }
            
            updateAllCardDisplays();
        }
    }
    
    /**
     * Restarts the game from the beginning.
     */
    private void restartGame() {
        // Reset game state completely
        gameManager.startNewGame();
        
        // Reset player chips to default
        gameManager.resetPlayerChips();
        
        // Show the start screen again
        VBox startScreen = createStartScreen();
        setCenter(startScreen);
        
        // Clear any existing displays
        playerHandArea.getChildren().clear();
        selectedCardsArea.getChildren().clear();
        jokerArea.getChildren().clear();
    }
    
    /**
     * Handles discarding cards action.
     */
    private void handleDiscardCards() {
        // Get the number of cards being discarded
        int cardsDiscarded = gameService.getSelectedCards().size();
        
        // Record discard with the game manager
        boolean canDiscard = gameManager.recordDiscard();
        
        // If discard limit reached, show notification
        if (!canDiscard) {
            notificationText.set("Discard limit reached!\nYou can only discard " + 
                               gameManager.getMaxDiscardsPerStage() + " times per stage.");
            return;
        }
        
        gameService.discardSelectedCards();
        updateAllCardDisplays();
    }
    
    /**
     * Handles proceeding to the next round.
     */
    private void handleNextRound() {
        gameManager.startNewRound();
        updateAllCardDisplays();
    }
    
    /**
     * Updates the player's hand display.
     */
    private void updateHandDisplay() {
        playerHandArea.getChildren().clear();
        
        // Get cards from the hand
        List<Card> playerCards = gameService.getPlayerHand().getCards();
        
        // If hand is empty, just return
        if (playerCards.isEmpty()) {
            return;
        }
        
        for (Card card : playerCards) {
            CardView cardView = new CardView(card);
            // Set the card as selected if it's in the selectedCards list
            if (gameService.getSelectedCards().contains(card)) {
                cardView.setSelected(true);
            }
            // Add hover effect to better indicate selectability - GREEN glow
            cardView.setOnMouseEntered(e -> {
                if (!gameService.getSelectedCards().contains(card)) {
                    cardView.setStyle("-fx-effect: dropshadow(gaussian, rgba(50,200,50,0.7), 10, 0, 0, 0);");
                }
            });
            cardView.setOnMouseExited(e -> {
                if (!gameService.getSelectedCards().contains(card)) {
                    cardView.setStyle("");
                }
            });
            cardView.setOnMouseClicked(e -> handleCardSelection(cardView, card));
            playerHandArea.getChildren().add(cardView);
        }
    }
    
    /**
     * Updates the selected cards display.
     */
    private void updateSelectedCardsDisplay() {
        selectedCardsArea.getChildren().clear();
        
        // Display a message about how many cards are selected
        List<Card> selectedCards = gameService.getSelectedCards();
        if (selectedCards.isEmpty()) {
            // Return early if no cards selected
            return;
        }
        
        // Display selected cards in the selected cards area
        for (Card card : selectedCards) {
            CardView cardView = new CardView(card);
            cardView.setSelected(true); // Always show as selected
            selectedCardsArea.getChildren().add(cardView);
        }
    }
    
    /**
     * Updates the joker display.
     */
    private void updateJokerDisplay() {
        jokerArea.getChildren().clear();
        
        // Get the current joker from game service
        Joker currentJoker = gameService.getCurrentJoker();
        
        if (currentJoker != null) {
            // Create a VBox to hold joker info
            VBox jokerInfo = new VBox(5);
            jokerInfo.setAlignment(Pos.CENTER);
            
            // Create labels for joker name and effect
            Label nameLabel = new Label(currentJoker.getType().getName());
            nameLabel.getStyleClass().add("joker-name");
            nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
            
            Label effectLabel = new Label(currentJoker.getType().getEffect());
            effectLabel.getStyleClass().add("joker-effect");
            effectLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #aaaaaa; -fx-wrap-text: true;");
            effectLabel.setMaxWidth(300);
            
            // Create multiplier label if applicable
            if (currentJoker.getMultiplier() > 0) {
                Label multiplierLabel = new Label("Multiplier: ×" + currentJoker.getMultiplier());
                multiplierLabel.getStyleClass().add("joker-multiplier");
                multiplierLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ffd700;"); // Gold color
                jokerInfo.getChildren().addAll(nameLabel, effectLabel, multiplierLabel);
            } else {
                jokerInfo.getChildren().addAll(nameLabel, effectLabel);
            }
            
            // Add a card visual representation
            CardView jokerCardView = new CardView(new Card("Joker", "J", 0));
            jokerCardView.setSelected(true);
            
            // Create an HBox to hold the card and info side by side
            HBox jokerDisplay = new HBox(10);
            jokerDisplay.setAlignment(Pos.CENTER);
            jokerDisplay.getChildren().addAll(jokerCardView, jokerInfo);
            
            jokerArea.getChildren().add(jokerDisplay);
        } else {
            Label noJokerLabel = new Label("No Joker");
            noJokerLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666;");
            jokerArea.getChildren().add(noJokerLabel);
        }
    }
    
    /**
     * Updates all card displays.
     */
    private void updateAllCardDisplays() {
        updateHandDisplay();
        // Still update selected cards internally, but don't display them
        updateSelectedCardsDisplay();
        updateJokerDisplay();
        updateDeckSizeDisplay();
    }
    
    /**
     * Handles card selection from the player's hand.
     * 
     * @param cardView the card view
     * @param card the card
     */
    private void handleCardSelection(CardView cardView, Card card) {
        GameState state = gameService.getGameState();
        
        if (state == GameState.WAITING_FOR_SELECTION) {
            // If card is already selected, deselect it
            if (gameService.getSelectedCards().contains(card)) {
                boolean deselected = gameService.deselectCard(card);
                if (deselected) {
                    cardView.setSelected(false);
                }
            } else {
                // Otherwise, try to select it
                boolean selected = gameService.selectCard(card);
                if (selected) {
                    cardView.setSelected(true);
                }
            }
            // Update displays to reflect changes
            updateHandDisplay();
        }
    }
    
    /**
     * Handles card deselection from the selected cards.
     * 
     * @param cardView the card view
     * @param card the card
     */
    private void handleCardDeselection(CardView cardView, Card card) {
        GameState state = gameService.getGameState();
        
        if (state == GameState.WAITING_FOR_SELECTION) {
            boolean deselected = gameService.deselectCard(card);
            if (deselected) {
                cardView.setSelected(false);
                updateSelectedCardsDisplay();
                updateHandDisplay();
            }
        }
    }
    
    /**
     * Creates the welcome/start screen.
     * 
     * @return the start screen
     */
    private VBox createStartScreen() {
        VBox startScreen = new VBox(20);
        startScreen.getStyleClass().add("welcome-screen");
        
        Label welcomeLabel = new Label("Welcome to Balatro!");
        welcomeLabel.getStyleClass().add("welcome-label");
        
        // Change "Ante" to "Bet" for clarity
        Label betLabel = new Label("Select your Bet amount:");
        betLabel.getStyleClass().add("info-label");
        
        HBox betOptions = new HBox(20);
        betOptions.setAlignment(Pos.CENTER);
        
        Button bet10Button = new Button("10 Chips");
        Button bet50Button = new Button("50 Chips");
        Button bet100Button = new Button("100 Chips");
        
        bet10Button.getStyleClass().add("ante-button");
        bet50Button.getStyleClass().add("ante-button");
        bet100Button.getStyleClass().add("ante-button");
        
        // Add color styling to match web version
        bet10Button.setStyle("-fx-background-color: linear-gradient(to right, #b91c1c, #7f1d1d);");
        bet50Button.setStyle("-fx-background-color: linear-gradient(to right, #15803d, #166534);");
        bet100Button.setStyle("-fx-background-color: linear-gradient(to right, #7e22ce, #581c87);");
        
        betOptions.getChildren().addAll(bet10Button, bet50Button, bet100Button);
        
        Label infoLabel = new Label("Note: Your bet amount will be deducted from your initial 100 chips.\nWhen you complete a level, you'll receive 10x your bet amount as a reward!\nStage Values are difficulty indicators that increase as you progress through the game.");
        infoLabel.getStyleClass().add("info-text");
        infoLabel.setWrapText(true);
        infoLabel.setAlignment(Pos.CENTER);

        Label referenceLabel = new Label("Reference: Balatro. (n.d.). Steam Store Page. Retrieved from https://store.steampowered.com/app/2379780/Balatro/");
        referenceLabel.getStyleClass().add("info-text");
        referenceLabel.setWrapText(true);
        referenceLabel.setAlignment(Pos.CENTER);
        
        // Set Bet actions
        bet10Button.setOnAction(e -> {
            // Set ante to 0 initially, it will be set to the proper value (5) when the game starts
            gameManager.setBetAmount(10);
            startGame();
        });
        
        bet50Button.setOnAction(e -> {
            gameManager.setBetAmount(50);
            startGame();
        });
        
        bet100Button.setOnAction(e -> {
            gameManager.setBetAmount(100);
            startGame();
        });
        
        // Add all components to the start screen, including the reference label
        startScreen.getChildren().addAll(welcomeLabel, betLabel, betOptions, infoLabel, referenceLabel);
        return startScreen;
    }
    
    /**
     * Starts the game with the selected bet amount.
     */
    private void startGame() {
        // Always have enough chips since the chips were adjusted in setBetAmount
        actuallyStartGame();
        
        // Now that the bet amount has been selected, add the Bet and Stage Value fields to the info grid
        // Get the GridPane from the top panel (first child is the title, second is the grid)
        VBox topPanel = (VBox) getTop();
        GridPane infoGrid = (GridPane) topPanel.getChildren().get(1);
        
        // Create and add the Bet labels now that we have a value
        Label betTitle = new Label("Bet:");
        betTitle.getStyleClass().add("info-label");
        Label betLabel = new Label();
        betLabel.getStyleClass().addAll("info-label", "bet-label");
        
        // Calculate the bet amount based on the difference between starting chips and current chips
        int betAmount = 100 - gameManager.getPlayerChips();
        betLabel.setText(String.valueOf(betAmount));
        
        // Add the bet fields to the grid at positions 2,2 and 3,2
        infoGrid.add(betTitle, 2, 2);
        infoGrid.add(betLabel, 3, 2);
        
        // Create and add the Stage Value labels
        Label stageValueTitle = new Label("Stage Value:");
        stageValueTitle.getStyleClass().add("info-label");
        
        // Add the Stage Value tooltip to the title
        javafx.scene.control.Tooltip stageTip = new javafx.scene.control.Tooltip(
            "Stage Value increases as you progress through game stages.\n" +
            "It affects reward calculations and represents the current difficulty level."
        );
        javafx.scene.control.Tooltip.install(stageValueTitle, stageTip);
        
        // Update the UI to reflect the correct stage value (which should be 5 for Small Blind)
        stageValueLabel.setText(String.valueOf(gameManager.getStageValue()));
        
        // Add the Stage Value fields to the grid at positions 4,2 and 5,2
        infoGrid.add(stageValueTitle, 4, 2);
        infoGrid.add(stageValueLabel, 5, 2);
        
        // Now set up binding for the stageValueLabel
        stageValueLabel.textProperty().bind(Bindings.convert(gameManager.stageValueProperty()));
        
        // Update the chips display to reflect the deducted bet amount
        chipsLabel.setText(String.valueOf(gameManager.getPlayerChips()));
    }
    
    /**
     * Actually starts the game after ante selection and validation.
     */
    private void actuallyStartGame() {
        gameManager.startNewRound();
        
        // Create card areas container - no Selected Cards area
        VBox cardAreasContainer = new VBox(20);
        cardAreasContainer.getChildren().addAll(
                createAreaWithLabel("Your Hand", playerHandArea),
                // Selected Cards area removed from container
                createScrollableAreaWithLabel("Joker", jokerArea), // Using scrollable area
                createButtonsPanel()
        );
        
        // Create a stack pane to hold both the game content and notification overlay
        StackPane gameContentWithOverlay = new StackPane();
        gameContentWithOverlay.getChildren().addAll(cardAreasContainer, notificationOverlay, deckViewerOverlay);
        
        // Update the display
        updateHandDisplay();
        
        // Update deck size label right after starting game - should be 44 (52-8)
        deckSizeLabel.setText(String.valueOf(gameService.getRemainingCards()));
        
        // Switch to the game view with overlay
        setCenter(gameContentWithOverlay);
    }
    
    /**
     * Updates the game interface after making a selection.
     */
    private void updateAfterSelection() {
        setCenter(createGameInterface());
    }
    
    /**
     * Creates the main game interface.
     * 
     * @return the game interface
     */
    private Node createGameInterface() {
        // Create card areas container - no Selected Cards area
        VBox cardAreasContainer = new VBox(15);
        cardAreasContainer.getChildren().addAll(
                createAreaWithLabel("Your Hand", playerHandArea),
                // Selected Cards area removed from container
                createScrollableAreaWithLabel("Joker", jokerArea), // Using scrollable area
                createButtonsPanel()
        );
        
        // Make sure the overlay is positioned in the bottom right corner
        StackPane.setAlignment(deckViewerOverlay, Pos.BOTTOM_RIGHT);
        
        // Create a stack pane to hold both the game content and notification overlay
        StackPane gameContentWithOverlay = new StackPane();
        gameContentWithOverlay.getChildren().addAll(cardAreasContainer, notificationOverlay, deckViewerOverlay);
        
        // Update the display
        updateAllCardDisplays();
        
        return gameContentWithOverlay;
    }
    
    /**
     * Updates the deck size display.
     */
    private void updateDeckSizeDisplay() {
        int remainingCards = gameService.getRemainingCards();
        deckSizeLabel.setText(String.valueOf(remainingCards));
        
        // Update the deck viewer overlay when the deck changes
        if (deckViewerOverlay != null) {
            deckViewerOverlay.updateCardDistribution();
        }
    }
    
    /**
     * Creates the deck size display label with hover interaction for deck viewer.
     * 
     * @param title the label text
     * @param value the initial value
     * @return the deck size label with hover functionality
     */
    private Label createDeckSizeLabel(String title, String value) {
        Label label = new Label(value);
        label.getStyleClass().addAll("info-label", "deck-size");
        
        // Add hover listeners to show/hide the deck viewer
        label.setOnMouseEntered(e -> {
            // Update the card distribution and show the overlay
            deckViewerOverlay.updateCardDistribution();
            deckViewerOverlay.show();
        });
        
        label.setOnMouseExited(e -> {
            // Hide the overlay when mouse leaves
            deckViewerOverlay.hide();
        });
        
        return label;
    }
    
    /**
     * Creates a stack pane representing a deck of cards that can be hovered to show the deck viewer.
     * 
     * @return the deck display stack pane
     */
    private StackPane createDeckDisplayArea() {
        StackPane deckArea = new StackPane();
        deckArea.getStyleClass().add("deck-area");
        deckArea.setMinSize(80, 100);
        deckArea.setPrefSize(100, 120);
        deckArea.setMaxSize(120, 150);
        
        // Style using CSS instead of using Background/Border classes
        deckArea.setStyle("-fx-background-color: #1E3250; -fx-background-radius: 10px; "
                     + "-fx-border-color: #4664A0; -fx-border-radius: 10px; "
                     + "-fx-border-width: 2px;");
        
        // Add deck size text
        Label deckCountLabel = new Label();
        deckCountLabel.textProperty().bind(Bindings.convert(gameService.remainingCardsProperty()));
        deckCountLabel.getStyleClass().add("deck-count-label");
        deckCountLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        // Add deck label
        Label deckLabel = new Label("Cards in Deck");
        deckLabel.getStyleClass().add("deck-label");
        deckLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        
        // Stack the labels
        VBox deckLabels = new VBox(5);
        deckLabels.setAlignment(Pos.CENTER);
        deckLabels.getChildren().addAll(deckLabel, deckCountLabel);
        
        deckArea.getChildren().add(deckLabels);
        
        // Add hover effect to show deck viewer
        deckArea.setOnMouseEntered(e -> {
            deckViewerOverlay.updateCardDistribution();
            deckViewerOverlay.show();
        });
        
        deckArea.setOnMouseExited(e -> {
            deckViewerOverlay.hide();
        });
        
        return deckArea;
    }
} 