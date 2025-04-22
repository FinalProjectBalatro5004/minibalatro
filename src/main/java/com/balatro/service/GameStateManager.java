package com.balatro.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.balatro.model.Joker;
import com.balatro.model.JokerType;
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
    private final IntegerProperty stageValue;
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
    private static final int INITIAL_STAGE_VALUE = 0; // Start with 0 stage value, will be updated to 5 when game starts
    private static final int MAX_ROUNDS = 10;
    private static final int DEFAULT_MAX_HANDS = 4;  // Starting counts as 1 hand, maximum of 4 hands allowed
    private static final int DEFAULT_MAX_DISCARDS = 4;  // Maximum of 4 discards allowed
    
    // Add joker-related fields
    private Joker currentJoker;
    private final Random random = new Random();
    
    /**
     * Enum representing the high-level phases of the game.
     */
    public enum GamePhase {
        GAME_START,        // Initial game setup
        ROUND_SETUP,       // Setting up a new round (stage value, etc.)
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
        this.stageValue = new SimpleIntegerProperty(INITIAL_STAGE_VALUE);
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
        
        // Reset stage value
        stageValue.set(INITIAL_STAGE_VALUE);
        
        // Reset hand and discard limits
        resetLimitsForNewStage();
        
        // Set initial game phase
        setGamePhase(GamePhase.GAME_START);
        
        // Generate a random joker
        generateRandomJoker();
        
        // Start a new game in the game service
        gameService.startNewGame();
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
        gameService.startNewGame();
        
        // Reset player chips to the starting amount
        playerChips.set(STARTING_CHIPS);
        
        // Generate a random joker
        generateRandomJoker();
        
        // Update the game phase
        setGamePhase(GamePhase.GAME_START);
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
        
        // Only deduct stage value under specific conditions:
        // 1. When restarting from a failed level
        // (No longer deducting when game first starts as it's handled in setBetAmount)
        if (gameService.getGameState() == GameService.GameState.GAME_OVER) {
            // Take stage value from player chips
            int currentStageValue = stageValue.get();
            int remainingChips = playerChips.get() - currentStageValue;
            playerChips.set(remainingChips);
            System.out.println("Stage value deducted: " + currentStageValue + " chips");
        } else {
            System.out.println("No stage value deducted - continuing to next stage");
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
     * Records a discard and returns whether it was successful.
     * @return true if the discard was recorded, false if limit reached
     */
    public boolean recordDiscard() {
        if (discardsUsedInStage.get() >= maxDiscardsPerStage.get()) {
            discardLimitReached.set(true);
            return false;
        }
        
        discardsUsedInStage.set(discardsUsedInStage.get() + 1);
        
        // Check if we've reached the limit after incrementing
        if (discardsUsedInStage.get() >= maxDiscardsPerStage.get()) {
            discardLimitReached.set(true);
        }
        
        return true;
    }
    
    /**
     * Sets the bet amount for the game.
     * @param amount the bet amount (10, 50, or 100)
     */
    public void setBetAmount(int amount) {
        // Only allow values of 10, 50, or 100 for the bite/bet amount
        if (amount == 10 || amount == 50 || amount == 100) {
            // This value is the bite/bet amount and will get deducted from chips
            // The actual stage value for the stage will be set based on the stage when the game starts
            
            // Store the bet amount
            int betAmount = amount;
            // But set the stage value to the appropriate value for the Small Blind stage
            stageValue.set(5); // Small Blind stage always has stage value of 5
            
            // Deduct the bet amount from player chips
            int remainingChips = STARTING_CHIPS - betAmount;
            playerChips.set(remainingChips);
            
            System.out.println("Bet amount set to: " + betAmount + " chips, Stage value set to: 5 for Small Blind stage");
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
        
        // Award chips based on the score
        int chipsEarned = calculateChipsEarned(roundScore);
        playerChips.set(playerChips.get() + chipsEarned);
        
        // Progress to the next stage or level
        LevelStage nextStage = currentStage.get().getNextStage();
        LevelStage currentStageCopy = currentStage.get();
        
        if (nextStage != null) {
            // Progress to the next stage within the same level
            currentStage.set(nextStage);
            
            // Reset hand and discard limits for the new stage
            resetLimitsForNewStage();
            
            // Generate a new Joker for the next stage
            generateRandomJoker();
            
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
            
            // Reset the score for the new level
            gameService.scoreProperty().set(0);
            
            // Generate a new Joker for the next level
            generateRandomJoker();
            
            // Increase stage value for the next level (increase stage value in advance, but don't deduct it yet)
            int currentStageValue = stageValue.get();
            stageValue.set(currentStageValue + 5);
            
            // Award 3x the previous stage value as reward for completing all stages
            int reward = currentStageValue * 3;
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
        // Basic calculation: score divided by 10, with minimum of stage value
        int chips = Math.max(score / 10, stageValue.get());
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
     * Getter for the stage value.
     * @return the stage value
     */
    public int getStageValue() {
        return stageValue.get();
    }
    /**
     * Getter for the stage value property.
     * @return the stage value property
     */
    public IntegerProperty stageValueProperty() {
        return stageValue;
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
        // Game is over if either the phase is GAME_OVER or player has no chips
        return currentPhase.get() == GamePhase.GAME_OVER || playerChips.get() <= 0;
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
    
    /**
     * Generates a random joker for the current round.
     */
    private void generateRandomJoker() {
        // Get all available joker types
        JokerType[] jokerTypes = JokerType.values();
        
        // Randomly select a joker type
        JokerType selectedType = jokerTypes[random.nextInt(jokerTypes.length)];
        
        // Create a new joker with the selected type
        currentJoker = new Joker(
            selectedType,
            selectedType.getMultiplier(),
            selectedType.getActivationType(),
            selectedType.getRarity()
        );
        
        // Set the joker in the game service
        gameService.setCurrentJoker(currentJoker);
        
        // Log the joker effect
        System.out.println("Generated new Joker: " + selectedType.getName() + 
                         " - Effect: " + selectedType.getEffect() +
                         " - Multiplier: " + selectedType.getMultiplier() +
                         " - Activation: " + selectedType.getActivationType().getDisplayName());
    }
    
    /**
     * Gets the current joker.
     * @return the current joker
     */
    public Joker getCurrentJoker() {
        return currentJoker;
    }
} 