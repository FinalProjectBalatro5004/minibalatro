package com.balatro.service;

import java.util.ArrayList;
import java.util.List;

import com.balatro.model.Player;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * GameStateManager coordinates the overall game flow, including round progression,
 * player management, and transitions between different game phases.
 */
public class GameStateManager {
    
    private final GameService gameService;
    private final List<Player> players;
    private final Player currentPlayer;
    
    // Game state properties
    private final IntegerProperty currentRound;
    private final IntegerProperty currentLevel;
    private final ObjectProperty<LevelStage> currentStage;
    private final IntegerProperty playerChips;
    private final IntegerProperty ante;
    private final StringProperty gamePhase;
    private final ObjectProperty<GamePhase> currentPhase;
    
    // Hand and discard limits
    private final IntegerProperty handsPlayedInStage;
    private final IntegerProperty discardsUsedInStage;
    private final IntegerProperty maxHandsPerStage;
    private final IntegerProperty maxDiscardsPerStage;
    private final BooleanProperty handLimitReached;
    private final BooleanProperty discardLimitReached;
    
    // Constants
    private static final int STARTING_CHIPS = 100;
    private static final int INITIAL_ANTE = 5;
    private static final int MAX_ROUNDS = 10;
    private static final int DEFAULT_MAX_HANDS = 4;  // Starting counts as 1 hand, maximum of 4 hands allowed
    private static final int DEFAULT_MAX_DISCARDS = 4;  // Maximum of 4 discards allowed
    
    /**
     * Enum representing the high-level phases of the game.
     */
    public enum GamePhase {
        GAME_START,        // Initial game setup
        ROUND_SETUP,       // Setting up a new round (ante, etc.)
        PLAYING_CARDS,     // Player is playing their hand
        ROUND_SCORING,     // Calculating score for the round
        ROUND_COMPLETE,    // End of round
        GAME_OVER          // Game is over (win or lose)
    }
    
    /**
     * Enum representing the different stages within each level.
     */
    public enum LevelStage {
        // Level 1 stages
        SMALL_BLIND(300, "Small Blind"),
        BIG_BLIND(450, "Big Blind"),
        THE_HOOK(600, "The Hook"),
        
        // Level 2 stages
        SMALL_BLIND_L2(800, "Small Blind L2"),
        BIG_BLIND_L2(1200, "Big Blind L2"),
        THE_HOOK_L2(1600, "The Hook L2"),
        
        // Level 3 stages
        SMALL_BLIND_L3(2000, "Small Blind L3"),
        BIG_BLIND_L3(3000, "Big Blind L3"),
        THE_HOOK_L3(4000, "The Hook L3");
        
        private final int targetScore;
        private final String displayName;
        
        LevelStage(int targetScore, String displayName) {
            this.targetScore = targetScore;
            this.displayName = displayName;
        }
        
        public int getTargetScore() {
            return targetScore;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        /**
         * Get the next stage after this one.
         * @return the next stage, or null if this is the last stage
         */
        public LevelStage getNextStage() {
            switch (this) {
                case SMALL_BLIND: return BIG_BLIND;
                case BIG_BLIND: return THE_HOOK;
                case THE_HOOK: return null; // End of level 1
                case SMALL_BLIND_L2: return BIG_BLIND_L2;
                case BIG_BLIND_L2: return THE_HOOK_L2;
                case THE_HOOK_L2: return SMALL_BLIND_L3; // Transition to Level 3
                case SMALL_BLIND_L3: return BIG_BLIND_L3;
                case BIG_BLIND_L3: return THE_HOOK_L3;
                default: return null; // THE_HOOK_L3 is the last stage
            }
        }
        
        /**
         * Get the first stage of the specified level.
         * @param level the level number (1-based)
         * @return the first stage of the specified level
         */
        public static LevelStage getFirstStageForLevel(int level) {
            if (level == 1) {
                return SMALL_BLIND;
            } else if (level == 2) {
                return SMALL_BLIND_L2;
            } else if (level == 3) {
                return SMALL_BLIND_L3;
            } else {
                return SMALL_BLIND; // Default to level 1
            }
        }
    }
    
    /**
     * Creates a new game state manager instance.
     */
    public GameStateManager() {
        this.gameService = new GameService();
        this.players = new ArrayList<>();
        this.currentPlayer = new Player("Player 1");
        this.players.add(currentPlayer);
        
        // Initialize properties
        this.currentRound = new SimpleIntegerProperty(1);
        this.currentLevel = new SimpleIntegerProperty(1);
        this.currentStage = new SimpleObjectProperty<>(LevelStage.SMALL_BLIND);
        this.playerChips = new SimpleIntegerProperty(STARTING_CHIPS);
        this.ante = new SimpleIntegerProperty(INITIAL_ANTE);
        this.gamePhase = new SimpleStringProperty(GamePhase.GAME_START.toString());
        this.currentPhase = new SimpleObjectProperty<>(GamePhase.GAME_START);
        
        // Initialize hand and discard limits
        this.handsPlayedInStage = new SimpleIntegerProperty(0);
        this.discardsUsedInStage = new SimpleIntegerProperty(0);
        this.maxHandsPerStage = new SimpleIntegerProperty(DEFAULT_MAX_HANDS);
        this.maxDiscardsPerStage = new SimpleIntegerProperty(DEFAULT_MAX_DISCARDS);
        this.handLimitReached = new SimpleBooleanProperty(false);
        this.discardLimitReached = new SimpleBooleanProperty(false);
        
        // Initialize the game
        initializeGame();
    }
    
    /**
     * Initializes the game state.
     */
    private void initializeGame() {
        // Set up players
        for (Player player : players) {
            player.setChips(STARTING_CHIPS);
        }
        
        // Reset round counter
        currentRound.set(1);
        
        // Reset level and stage
        currentLevel.set(1);
        currentStage.set(LevelStage.SMALL_BLIND);
        
        // Reset ante
        ante.set(INITIAL_ANTE);
        
        // Reset hand and discard limits
        resetLimitsForNewStage();
        
        // Set initial game phase
        setGamePhase(GamePhase.GAME_START);
    }
    
    /**
     * Resets the hand and discard limits for a new stage.
     */
    private void resetLimitsForNewStage() {
        handsPlayedInStage.set(0);
        discardsUsedInStage.set(0);
        handLimitReached.set(false);
        discardLimitReached.set(false);
    }
    
    /**
     * Starts a new game.
     */
    public void startNewGame() {
        initializeGame();
        gameService.newGame();
    }
    
    /**
     * Starts a new round.
     */
    public void startNewRound() {
        // Check if hand limit is reached
        if (handsPlayedInStage.get() >= maxHandsPerStage.get()) {
            handLimitReached.set(true);
            return;
        }
        
        // Only deduct ante under specific conditions:
        // 1. When the game first starts (by checking if currentRound is 1 and handsPlayedInStage is 0)
        // 2. Or when restarting from a failed level
        if ((currentRound.get() == 1 && handsPlayedInStage.get() == 0) || 
            gameService.getGameState() == GameService.GameState.GAME_OVER) {
            // Take ante from player chips
            int currentAnte = ante.get();
            int remainingChips = playerChips.get() - currentAnte;
            playerChips.set(remainingChips);
            System.out.println("Ante deducted: " + currentAnte + " chips");
        } else {
            System.out.println("No ante deducted - continuing to next stage");
        }
        
        // Reset the deck first to clear any ongoing round state
        gameService.getDeck().resetDeck();
        
        // Get the target score for the current level stage
        int targetScore = currentStage.get().getTargetScore();
        
        // Now mark it as a new round
        gameService.getDeck().startNewRound(0);
        
        // Pass the target score to game service
        gameService.setTargetScore(targetScore);
        
        // Reset score to 0 for the new round
        gameService.scoreProperty().set(0);
        
        // Complete any previous round
        gameService.roundCompletedProperty().set(true);
        
        // Start new round in the game service
        gameService.startNewRound();
        
        // Get the round number from the game service
        int currentRound = gameService.getRoundNumber();
        
        // Pass the updated game phase text to UI
        gamePhase.set("Round " + currentRound + " - " + currentStage.get().getDisplayName());
    }
    
    /**
     * Records a discard action and checks if the discard limit is reached.
     * @return true if discard was successful, false if limit reached
     */
    public boolean recordDiscard() {
        if (discardsUsedInStage.get() >= maxDiscardsPerStage.get()) {
            discardLimitReached.set(true);
            return false;
        }
        
        discardsUsedInStage.set(discardsUsedInStage.get() + 1);
        return true;
    }
    
    /**
     * Sets the ante amount for the game.
     * @param amount the new ante amount (10, 50, or 100)
     */
    public void setAnteAmount(int amount) {
        // Only allow values of 10, 50, or 100
        if (amount == 10 || amount == 50 || amount == 100) {
            ante.set(amount);
        }
    }
    
    /**
     * Resets the player's chips to the starting amount.
     */
    public void resetPlayerChips() {
        playerChips.set(STARTING_CHIPS);
    }
    
    /**
     * Completes the current round.
     */
    public void completeRound() {
        // Calculate round rewards
        int roundScore = gameService.getScore();
        int targetScore = currentStage.get().getTargetScore();
        
        // Progress to the next stage or level
        LevelStage nextStage = currentStage.get().getNextStage();
        LevelStage currentStageCopy = currentStage.get();
        
        if (nextStage != null) {
            // Progress to the next stage within the same level
            currentStage.set(nextStage);
            
            // Reset hand and discard limits for the new stage
            resetLimitsForNewStage();
            
            // Log the transition
            System.out.println("Advanced from " + currentStageCopy.getDisplayName() + 
                              " to " + nextStage.getDisplayName() + 
                              " (Score: " + roundScore + "/" + targetScore + ")");
        } else {
            // Completed all stages, move to the next level
            int newLevel = currentLevel.get() + 1;
            currentLevel.set(newLevel);
            
            // Set the first stage of the new level
            currentStage.set(LevelStage.getFirstStageForLevel(newLevel));
            
            // Reset hand and discard limits for the new stage
            resetLimitsForNewStage();
            
            // Increase ante for the next level (increase ante value in advance, but don't deduct it yet)
            int currentAnte = ante.get();
            ante.set(currentAnte + 5);
            
            // Award 3x the previous ante as reward for completing all stages
            int reward = currentAnte * 3;
            playerChips.set(playerChips.get() + reward);
            
            // Log the transition and reward
            System.out.println("Advanced to Level " + newLevel + 
                              " (Score: " + roundScore + "/" + targetScore + ")" +
                              " - Awarded " + reward + " chips!");
        }
        
        // Increment the round number
        currentRound.set(currentRound.get() + 1);
        
        // Move to round complete phase
        setGamePhase(GamePhase.ROUND_COMPLETE);
    }
    
    /**
     * Sets the current game phase.
     * 
     * @param phase the new game phase
     */
    public void setGamePhase(GamePhase phase) {
        currentPhase.set(phase);
        gamePhase.set(phase.toString());
        
        // Handle phase-specific logic
        switch (phase) {
            case ROUND_SETUP:
                // Prepare for a new round
                break;
            case PLAYING_CARDS:
                // Start card play phase
                break;
            case ROUND_SCORING:
                // Score the round
                break;
            case ROUND_COMPLETE:
                // End of round activities
                break;
            case GAME_OVER:
                // Game over handling
                break;
            default:
                break;
        }
    }
    
    /**
     * Calculates chips earned based on score.
     * 
     * @param score the round score
     * @return chips earned
     */
    private int calculateChipsEarned(int score) {
        // Basic calculation: score divided by 10, with minimum of ante
        int chips = Math.max(score / 10, ante.get());
        return chips;
    }
    
    /**
     * Advances to the next game phase.
     */
    public void advanceGamePhase() {
        switch (currentPhase.get()) {
            case GAME_START:
                setGamePhase(GamePhase.ROUND_SETUP);
                break;
            case ROUND_SETUP:
                setGamePhase(GamePhase.PLAYING_CARDS);
                break;
            case PLAYING_CARDS:
                setGamePhase(GamePhase.ROUND_SCORING);
                break;
            case ROUND_SCORING:
                setGamePhase(GamePhase.ROUND_COMPLETE);
                break;
            case ROUND_COMPLETE:
                setGamePhase(GamePhase.ROUND_SETUP);
                startNewRound();
                break;
            case GAME_OVER:
                // Reset and start new game
                startNewGame();
                break;
        }
    }
    
    // Getters for properties
    /**
     * Getter for the game service.
     * @return the game service
     */
    public GameService getGameService() {
        return gameService;
    }
    /**
     * Getter for the current player.
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    /**
     * Getter for the current round.
     * @return the current round
     */
    public int getCurrentRound() {
        return currentRound.get();
    }
    /**
     * Getter for the current round property.
     * @return the current round property
     */
    public IntegerProperty currentRoundProperty() {
        return currentRound;
    }
    /**
     * Getter for the current level.
     * @return the current level
     */
    public int getCurrentLevel() {
        return currentLevel.get();
    }
    /**
     * Getter for the current level property.
     * @return the current level property
     */
    public IntegerProperty currentLevelProperty() {
        return currentLevel;
    }
    /**
     * Getter for the current stage.
     * @return the current stage
     */
    public LevelStage getCurrentStage() {
        return currentStage.get();
    }
    /**
     * Getter for the current stage property.
     * @return the current stage property
     */
    public ObjectProperty<LevelStage> currentStageProperty() {
        return currentStage;
    }
    /**
     * Getter for the player chips.
     * @return the player chips
     */
    public int getPlayerChips() {
        return playerChips.get();
    }
    /**
     * Getter for the player chips property.
     * @return the player chips property
     */
    public IntegerProperty playerChipsProperty() {
        return playerChips;
    }
    /**
     * Getter for the ante.
     * @return the ante
     */
    public int getAnte() {
        return ante.get();
    }
    /**
     * Getter for the ante property.
     * @return the ante property
     */
    public IntegerProperty anteProperty() {
        return ante;
    }
    /**
     * Getter for the game phase.
     * @return the game phase
     */
    public String getGamePhase() {
        return gamePhase.get();
    }
    /**
     * Getter for the game phase property.
     * @return the game phase property
     */
    public StringProperty gamePhaseProperty() {
        return gamePhase;
    }
    /**
     * Getter for the current phase.
     * @return the current phase
     */
    public GamePhase getCurrentPhase() {
        return currentPhase.get();
    }
    /**
     * Getter for the current phase property.
     * @return the current phase property
     */
    public ObjectProperty<GamePhase> currentPhaseProperty() {
        return currentPhase;
    }
    /**
     * Checks if the game is over.
     * @return true if the game is over
     */
    public boolean isGameOver() {
        return currentPhase.get() == GamePhase.GAME_OVER;
    }
    
    /** 
     * Gets the number of hands played in the current stage.
     * @return the number of hands played
     */
    public int getHandsPlayedInStage() {
        return handsPlayedInStage.get();
    }
    /**
     * Getter for the hands played in stage property.
     * @return the hands played property
     */
    public IntegerProperty handsPlayedInStageProperty() {
        return handsPlayedInStage;
    }
        
    /**
     * Gets the number of discards used in the current stage.
     * @return the number of discards used
     */
    public int getDiscardsUsedInStage() {
        return discardsUsedInStage.get();
    }
    
    /**
     * Gets the discards used in stage property.
     * @return the discards used property
     */
    public IntegerProperty discardsUsedInStageProperty() {
        return discardsUsedInStage;
    }
    
    /**
     * Gets the maximum number of hands per stage.
     * @return the maximum number of hands
     */
    public int getMaxHandsPerStage() {
        return maxHandsPerStage.get();
    }
    
    /**
     * Gets the maximum hands per stage property.
     * @return the maximum hands property
     */
    public IntegerProperty maxHandsPerStageProperty() {
        return maxHandsPerStage;
    }
    
    /**
     * Gets the maximum number of discards per stage.
     * @return the maximum number of discards
     */
    public int getMaxDiscardsPerStage() {
        return maxDiscardsPerStage.get();
    }
    
    /**
     * Gets the maximum discards per stage property.
     * @return the maximum discards property
     */
    public IntegerProperty maxDiscardsPerStageProperty() {
        return maxDiscardsPerStage;
    }
    
    /**
     * Checks if the hand limit has been reached.
     * @return true if the hand limit is reached
     */
    public boolean isHandLimitReached() {
        return handLimitReached.get();
    }
    
    /**
     * Gets the hand limit reached property.
     * @return the hand limit reached property
     */
    public BooleanProperty handLimitReachedProperty() {
        return handLimitReached;
    }
    
    /**
     * Checks if the discard limit has been reached.
     * @return true if the discard limit is reached
     */
    public boolean isDiscardLimitReached() {
        return discardLimitReached.get();
    }
    
    /**
     * Gets the discard limit reached property.
     * @return the discard limit reached property
     */
    public BooleanProperty discardLimitReachedProperty() {
        return discardLimitReached;
    }
} 