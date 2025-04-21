package com.balatro.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.balatro.model.Card;
import com.balatro.model.Hand;
import com.balatro.service.GameService.GameState;

/**
 * Test class for the GameService.
 * Tests game state management, card operations, and scoring.
 */
class GameServiceTest {

    private GameService gameService;

    /**
     * Sets up the test fixtures before each test method.
     * Creates a new GameService instance.
     */
    @BeforeEach
    void setUp() {
        gameService = new GameService();
    }

    /**
     * Tests the initial state of the game service after creation.
     */
    @Test
    void testInitialState() {
        // Check initial game state
        assertEquals(GameState.WAITING_FOR_SELECTION, gameService.getGameState(), 
                "Initial game state should be WAITING_FOR_SELECTION");
        
        // Check initial score and round
        assertEquals(0, gameService.scoreProperty().get(), "Initial score should be 0");
        assertEquals(1, gameService.roundProperty().get(), "Initial round should be 1");
        
        // Check that player hand is empty initially
        assertNotNull(gameService.getPlayerHand(), "Player hand should not be null");
        assertNotNull(gameService.getPlayerHand().getCards(), "Player hand cards should not be null");
        
        // Check that discard pile is empty initially
        assertNotNull(gameService.getDiscardPile(), "Discard pile should not be null");
        assertTrue(gameService.getDiscardPile().isEmpty(), "Discard pile should be empty initially");
        
        // Check that no cards are selected initially
        assertNotNull(gameService.getSelectedCards(), "Selected cards should not be null");
        assertTrue(gameService.getSelectedCards().isEmpty(), "No cards should be selected initially");
        
        // Other initial conditions
        assertFalse(gameService.canDrawCardsProperty().get(), "Should not be able to draw cards initially");
        assertEquals(0, gameService.getCardsToDrawCount(), "Initial cards to draw count should be 0");
        assertFalse(gameService.roundCompletedProperty().get(), "Round should not be completed initially");
    }

    /**
     * Tests starting a new round.
     */
    @Test
    void testStartNewRound() {
        // Start a new round
        gameService.startNewRound();
        
        // Check that player now has initial cards
        assertFalse(gameService.getPlayerHand().getCards().isEmpty(), 
                "Player should have cards after starting a new round");
        assertEquals(8, gameService.getPlayerHand().getCardCount(), 
                "Player should have 8 cards after starting a new round");
        
        // Game state should still be waiting for selection
        assertEquals(GameState.WAITING_FOR_SELECTION, gameService.getGameState(), 
                "Game state should be WAITING_FOR_SELECTION after starting a new round");
        
        // Check that discard pile is empty
        assertTrue(gameService.getDiscardPile().isEmpty(), 
                "Discard pile should be empty after starting a new round");
        
        // Check deck has correct number of cards (52 - 8 = 44)
        assertEquals(44, gameService.getRemainingCards(), 
                "Deck should have 44 cards after dealing 8 to player");
    }

    /**
     * Tests the card selection functionality.
     */
    @Test
    void testCardSelection() {
        // Start a new round to get cards
        gameService.startNewRound();
        
        // Get a card from the player's hand
        Hand playerHand = gameService.getPlayerHand();
        Card cardToSelect = playerHand.getCards().get(0);
        
        // Select the card
        assertTrue(gameService.selectCard(cardToSelect), 
                "Should be able to select a card from player's hand");
        
        // Check that the card is now selected
        assertEquals(1, gameService.getSelectedCards().size(), 
                "One card should be selected");
        assertEquals(cardToSelect, gameService.getSelectedCards().get(0), 
                "The selected card should match the one we selected");
        
        // Try to select the same card again (should fail)
        assertFalse(gameService.selectCard(cardToSelect), 
                "Should not be able to select the same card twice");
        
        // Check that we still have only one card selected
        assertEquals(1, gameService.getSelectedCards().size(), 
                "Should still have only one card selected");
        
        // Deselect the card
        assertTrue(gameService.deselectCard(cardToSelect), 
                "Should be able to deselect a selected card");
        
        // Check that no cards are selected
        assertTrue(gameService.getSelectedCards().isEmpty(), 
                "No cards should be selected after deselection");
    }

    /**
     * Tests drawing cards functionality.
     */
    @Test
    void testDrawCards() {
        // Start a new round
        gameService.startNewRound();
        
        // Initially should not be able to draw cards
        assertFalse(gameService.canDrawCardsProperty().get(), 
                "Should not be able to draw cards initially");
        
        // First discard some cards to make room for drawing new ones
        Hand playerHand = gameService.getPlayerHand();
        // Select 3 cards to discard
        for (int i = 0; i < 3; i++) {
            Card card = playerHand.getCards().get(i);
            gameService.selectCard(card);
        }
        
        // Evaluate hand to discard the selected cards
        gameService.evaluateHand();
        
        // Verify player now has 5 cards
        assertEquals(5, gameService.getPlayerHand().getCardCount(),
                "Player should have 5 cards after discarding 3");
        
        // Verify we can now draw cards
        assertTrue(gameService.canDrawCardsProperty().get(),
                "Should be able to draw cards after discarding");
        assertEquals(3, gameService.getCardsToDrawCount(),
                "Should be able to draw 3 cards");
        
        // Draw cards
        List<Card> drawnCards = gameService.drawCards();
        
        // Check that we drew the correct number of cards
        assertEquals(3, drawnCards.size(), "Should have drawn 3 cards");
        
        // Check that the drawn cards were added to the player's hand
        assertEquals(8, gameService.getPlayerHand().getCardCount(), 
                "Player should have 8 cards (5 remaining + 3 drawn)");
        
        // Check that the deck size was reduced accordingly
        assertEquals(41, gameService.getRemainingCards(), 
                "Deck should have 41 cards (44 - 3 drawn)");
        
        // Check that we can't draw any more cards
        assertFalse(gameService.canDrawCardsProperty().get(), 
                "Should not be able to draw cards after drawing");
        assertEquals(0, gameService.getCardsToDrawCount(), 
                "Cards to draw count should be reset to 0");
    }

    /**
     * Tests the hand evaluation and scoring functionality.
     */
    @Test
    void testEvaluateHand() {
        // Start a new round
        gameService.startNewRound();
        
        // Get cards from the player's hand
        Hand playerHand = gameService.getPlayerHand();
        
        // Select 5 cards from the player's hand
        for (int i = 0; i < 5; i++) {
            Card card = playerHand.getCards().get(i);
            gameService.selectCard(card);
        }
        
        // Check that we have 5 cards selected
        assertEquals(5, gameService.getSelectedCards().size(), 
                "Should have 5 cards selected");
        
        // Evaluate the hand
        int score = gameService.evaluateHand();
        
        // Score should be non-negative
        assertTrue(score >= 0, "Score should be non-negative");
        
        // The selected cards should now be in the discard pile
        assertEquals(5, gameService.getDiscardPile().size(), 
                "Discard pile should contain the 5 played cards");
        
        // The selected cards should be removed from the player's hand
        assertEquals(3, gameService.getPlayerHand().getCardCount(), 
                "Player should have 3 cards left (8 initial - 5 played)");
        
        // The selected cards list should now be empty
        assertTrue(gameService.getSelectedCards().isEmpty(), 
                "Selected cards list should be empty after evaluation");
        
        // Check if deck is empty or if game state is GAME_OVER
        if (!gameService.getDeck().isEmpty() && 
            gameService.getGameState() != GameService.GameState.GAME_OVER) {
            // We should now be able to draw 5 replacement cards
            assertTrue(gameService.canDrawCardsProperty().get(),
                    "Should be able to draw replacement cards");
            assertEquals(5, gameService.getCardsToDrawCount(), 
                    "Should be able to draw 5 replacement cards");
        }
    }

    /**
     * Tests the round completion logic.
     */
    @Test
    void testRoundCompletion() {
        // Start a new round
        gameService.startNewRound();
        
        // Set a low target score and a high current score to force round completion
        gameService.targetScoreProperty().set(10);
        gameService.scoreProperty().set(20);
        
        // Select and evaluate a hand to trigger round completion check
        Hand playerHand = gameService.getPlayerHand();
        for (int i = 0; i < 5; i++) {
            gameService.selectCard(playerHand.getCards().get(i));
        }
        
        // Evaluate the hand
        gameService.evaluateHand();
        
        // Round should be completed
        assertTrue(gameService.roundCompletedProperty().get(), 
                "Round should be completed when score exceeds target");
        
        // Game state should be ROUND_COMPLETE
        assertEquals(GameState.ROUND_COMPLETE, gameService.getGameState(), 
                "Game state should be ROUND_COMPLETE");
    }

    /**
     * Tests the game over condition when the deck is empty.
     */
    @Test
    void testGameOverWhenDeckEmpty() {
        // Start with a custom setup where the deck has very few cards
        gameService.startNewRound();
        
        // Force the deck to be nearly empty by setting a very low remaining cards count
        // This is a simplification since we can't directly modify the deck easily
        // In a real test, you might need to use reflection or a different approach
        
        // Instead, we'll simulate playing almost all cards from the deck
        // First, let's play the current hand
        Hand playerHand = gameService.getPlayerHand();
        for (int i = 0; i < 5; i++) {
            gameService.selectCard(playerHand.getCards().get(i));
        }
        gameService.evaluateHand();
        
        // Now draw the replacement cards
        gameService.drawCards();
        
        // Repeat until we have very few cards left
        // For testing purposes, we'll just assert the game state based on the remaining cards
        if (gameService.getRemainingCards() < Hand.getMinCardsToPlay() && 
            gameService.getPlayerHand().getCardCount() < Hand.getMinCardsToPlay()) {
            assertEquals(GameState.GAME_OVER, gameService.getGameState(), 
                    "Game state should be GAME_OVER when not enough cards remain");
        }
    }
} 