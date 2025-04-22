package com.balatro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.balatro.service.GameStateManager.GamePhase;
import com.balatro.service.GameStateManager.LevelStage;

/**
 * Test class for the GameStateManager.
 * Tests game phase transitions, level progression, and game state management.
 */
class GameStateManagerTest {

    private GameStateManager gameStateManager;
    
    @Mock
    private GameService mockGameService;

    /**
     * Sets up the test fixtures before each test method.
     * Creates a new GameStateManager instance.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameStateManager = new GameStateManager();
    }

    /**
     * Tests the initial state of the game state manager after creation.
     */
    @Test
    void testInitialState() {
        // Check initial round and level
        assertEquals(1, gameStateManager.getCurrentRound(), "Initial round should be 1");
        assertEquals(1, gameStateManager.getCurrentLevel(), "Initial level should be 1");
        
        // Check initial stage
        assertEquals(LevelStage.SMALL_BLIND, gameStateManager.getCurrentStage(), 
                "Initial stage should be SMALL_BLIND");
        
        // Check initial phase
        assertEquals(GamePhase.GAME_START, gameStateManager.getCurrentPhase(), 
                "Initial phase should be GAME_START");
        
        // Check initial player chips and ante
        assertEquals(100, gameStateManager.getPlayerChips(), "Initial player chips should be 100");
        assertEquals(0, gameStateManager.getStageValue(), "Initial stage value should be 0");
        
        // Check initial limits
        assertEquals(0, gameStateManager.getHandsPlayedInStage(), 
                "Initial hands played should be 0");
        assertEquals(0, gameStateManager.getDiscardsUsedInStage(), 
                "Initial discards used should be 0");
        assertEquals(4, gameStateManager.getMaxHandsPerStage(), 
                "Initial max hands per stage should be 4");
        assertEquals(4, gameStateManager.getMaxDiscardsPerStage(), 
                "Initial max discards per stage should be 4");
        assertFalse(gameStateManager.isHandLimitReached(), 
                "Hand limit should not be reached initially");
        assertFalse(gameStateManager.isDiscardLimitReached(), 
                "Discard limit should not be reached initially");
    }

    /**
     * Tests starting a new game.
     */
    @Test
    void testStartNewGame() {
        // Start with some non-initial state
        gameStateManager.currentRoundProperty().set(3);
        gameStateManager.currentLevelProperty().set(2);
        gameStateManager.currentStageProperty().set(LevelStage.BIG_BLIND_L2);
        gameStateManager.playerChipsProperty().set(50);
        gameStateManager.handsPlayedInStageProperty().set(2);
        
        // Start a new game
        gameStateManager.startNewGame();
        
        // Verify the state is reset
        assertEquals(1, gameStateManager.getCurrentRound(), "Round should be reset to 1");
        assertEquals(1, gameStateManager.getCurrentLevel(), "Level should be reset to 1");
        assertEquals(LevelStage.SMALL_BLIND, gameStateManager.getCurrentStage(), 
                "Stage should be reset to SMALL_BLIND");
        assertEquals(100, gameStateManager.getPlayerChips(), 
                "Player chips should be reset to starting value");
        assertEquals(0, gameStateManager.getHandsPlayedInStage(), 
                "Hands played should be reset to 0");
        assertEquals(GamePhase.GAME_START, gameStateManager.getCurrentPhase(), 
                "Phase should be reset to GAME_START");
    }

    /**
     * Tests recording a discard and reaching the discard limit.
     */
    @Test
    void testRecordDiscard() {
        // Initially no discards used
        assertEquals(0, gameStateManager.getDiscardsUsedInStage(), 
                "Initial discards used should be 0");
        assertFalse(gameStateManager.isDiscardLimitReached(), 
                "Discard limit should not be reached initially");
        
        // Record some discards
        for (int i = 0; i < 3; i++) {
            assertTrue(gameStateManager.recordDiscard(), 
                    "Should be able to record a discard");
            assertEquals(i + 1, gameStateManager.getDiscardsUsedInStage(), 
                    "Discards used should be incremented");
            assertFalse(gameStateManager.isDiscardLimitReached(), 
                    "Discard limit should not be reached yet");
        }
        
        // Record one more discard to reach the limit
        assertTrue(gameStateManager.recordDiscard(), 
                "Should be able to record the last discard");
        assertEquals(4, gameStateManager.getDiscardsUsedInStage(), 
                "Discards used should be at maximum");
        assertTrue(gameStateManager.isDiscardLimitReached(), 
                "Discard limit should now be reached");
        
        // Try to record another discard (should fail)
        assertFalse(gameStateManager.recordDiscard(), 
                "Should not be able to record more discards once limit is reached");
        assertEquals(4, gameStateManager.getDiscardsUsedInStage(), 
                "Discards used should remain at maximum");
    }

    /**
     * Tests completing a round and advancing to the next stage.
     */
    @Test
    void testCompleteRound() {
        // Set up a score that exceeds the target for the current stage
        gameStateManager.getGameService().scoreProperty().set(400);
        
        // Complete the round
        gameStateManager.completeRound();
        
        // Verify that we've advanced to the next stage
        assertEquals(LevelStage.BIG_BLIND, gameStateManager.getCurrentStage(), 
                "Should advance to BIG_BLIND after completing SMALL_BLIND");
        
        // Verify that the round counter was incremented
        assertEquals(2, gameStateManager.getCurrentRound(), 
                "Round counter should be incremented");
        
        // Verify that hands played was reset for the new stage
        assertEquals(0, gameStateManager.getHandsPlayedInStage(), 
                "Hands played should be reset for new stage");
        
        // Verify that discards used was reset for the new stage
        assertEquals(0, gameStateManager.getDiscardsUsedInStage(), 
                "Discards used should be reset for new stage");
    }

    /**
     * Tests level transition when completing the final stage of a level.
     */
    @Test
    void testLevelTransition() {
        // Set the current stage to THE_HOOK (final stage of level 1)
        gameStateManager.currentStageProperty().set(LevelStage.THE_HOOK);
        
        // Set up a score that exceeds the target for the current stage
        gameStateManager.getGameService().scoreProperty().set(700);
        
        // Complete the round
        gameStateManager.completeRound();
        
        // Verify that we've advanced to the next level
        assertEquals(2, gameStateManager.getCurrentLevel(), 
                "Should advance to level 2");
        
        // Verify that we've advanced to the first stage of level 2
        assertEquals(LevelStage.SMALL_BLIND_L2, gameStateManager.getCurrentStage(), 
                "Should advance to SMALL_BLIND_L2");
        
        // Verify that the score was reset for the new level
        assertEquals(0, gameStateManager.getGameService().scoreProperty().get(), 
                "Score should be reset for new level");
    }

    /**
     * Tests advancing game phases.
     */
    @Test
    void testAdvanceGamePhase() {
        // Initial phase should be GAME_START
        assertEquals(GamePhase.GAME_START, gameStateManager.getCurrentPhase(), 
                "Initial phase should be GAME_START");
        
        // Advance to the next phase
        gameStateManager.advanceGamePhase();
        assertEquals(GamePhase.ROUND_SETUP, gameStateManager.getCurrentPhase(), 
                "Should advance to ROUND_SETUP");
        
        // Advance to the next phase
        gameStateManager.advanceGamePhase();
        assertEquals(GamePhase.PLAYING_CARDS, gameStateManager.getCurrentPhase(), 
                "Should advance to PLAYING_CARDS");
        
        // Advance to the next phase
        gameStateManager.advanceGamePhase();
        assertEquals(GamePhase.ROUND_SCORING, gameStateManager.getCurrentPhase(), 
                "Should advance to ROUND_SCORING");
        
        // Advance to the next phase
        gameStateManager.advanceGamePhase();
        assertEquals(GamePhase.ROUND_COMPLETE, gameStateManager.getCurrentPhase(), 
                "Should advance to ROUND_COMPLETE");
        
        // Advance to the next phase (should cycle back to ROUND_SETUP)
        gameStateManager.advanceGamePhase();
        assertEquals(GamePhase.ROUND_SETUP, gameStateManager.getCurrentPhase(), 
                "Should cycle back to ROUND_SETUP");
    }

    /**
     * Tests calculating chips earned based on score.
     */
    @Test
    void testChipsEarned() {
        // Start a new round to setup the game
        gameStateManager.startNewRound();
        
        // Initial player chips
        int initialChips = gameStateManager.getPlayerChips();
        
        // Set up score for testing (the actual calculation is private, so we test indirectly)
        gameStateManager.getGameService().scoreProperty().set(300);
        
        // Complete the round which should award chips
        gameStateManager.completeRound();
        
        // Player should have earned some chips
        assertTrue(gameStateManager.getPlayerChips() > initialChips, 
                "Player should earn chips for completing a round");
    }

    /**
     * Tests game over detection.
     */
    @Test
    void testGameOver() {
        // Initially the game should not be over
        assertFalse(gameStateManager.isGameOver(), "Game should not be over initially");
        
        // Set player chips to 0
        gameStateManager.playerChipsProperty().set(0);
        
        // Now the game should be over
        assertTrue(gameStateManager.isGameOver(), "Game should be over when player has 0 chips");
        
        // Reset chips and check again
        gameStateManager.playerChipsProperty().set(100);
        assertFalse(gameStateManager.isGameOver(), "Game should not be over when player has chips");
    }
} 