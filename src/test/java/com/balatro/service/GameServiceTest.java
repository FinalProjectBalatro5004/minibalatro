package com.balatro.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.balatro.model.Card;

/**
 * Test class for GameService implementation.
 * Tests the functionality of the game service in the Balatro game.
 */
class GameServiceTest {
    private GameService gameService;

    /**
     * Sets up the test fixtures before each test method.
     * Creates a new GameService instance for testing.
     */
    @BeforeEach
    void setUp() {
        gameService = new GameService();
    }

    /**
     * Tests that the game initializes with the correct state.
     * Verifies initial game state, hand size, deck size, and score.
     */
    @Test
    void testInitialGameState() {
        // Exercise & Verify
        assertEquals(GameService.GameState.WAITING_FOR_SELECTION, gameService.getGameState(),
                "Initial game state should be WAITING_FOR_SELECTION");
        assertEquals(8, gameService.getPlayerHand().getCardCount(),
                "Player should start with 8 cards");
        assertEquals(44, gameService.getRemainingCards(),
                "Deck should have 44 cards after dealing initial hand");
        assertEquals(0, gameService.getScore(),
                "Initial score should be 0");
        assertEquals(1, gameService.getRound(),
                "Initial round should be 1");
        assertEquals(1000, gameService.getTargetScore(),
                "Initial target score should be 1000");
        assertTrue(gameService.getDiscardPile().isEmpty(),
                "Discard pile should be empty at start");
        assertTrue(gameService.getSelectedCards().isEmpty(),
                "Selected cards should be empty at start");
        assertFalse(gameService.getCanDrawCards(),
                "Player should not be able to draw cards at start");
    }

    /**
     * Tests the card selection functionality.
     * Verifies that cards can be selected from the player's hand.
     */
    @Test
    void testSelectCard() {
        // Setup
        Card cardToSelect = gameService.getPlayerHand().getCards().get(0);
        
        // Exercise
        boolean result = gameService.selectCard(cardToSelect);
        
        // Verify
        assertTrue(result, "Card selection should succeed");
        assertEquals(1, gameService.getSelectedCards().size(),
                "Selected cards should contain 1 card");
        assertTrue(gameService.getSelectedCards().contains(cardToSelect),
                "Selected cards should contain the selected card");
    }

    /**
     * Tests the card deselection functionality.
     * Verifies that selected cards can be deselected.
     */
    @Test
    void testDeselectCard() {
        // Setup
        Card cardToSelect = gameService.getPlayerHand().getCards().get(0);
        gameService.selectCard(cardToSelect);
        
        // Exercise
        boolean result = gameService.deselectCard(cardToSelect);
        
        // Verify
        assertTrue(result, "Card deselection should succeed");
        assertTrue(gameService.getSelectedCards().isEmpty(),
                "Selected cards should be empty after deselection");
    }

    /**
     * Tests the discard functionality.
     * Verifies that selected cards can be discarded and moved to the discard pile.
     */
    @Test
    void testDiscardSelectedCards() {
        // Setup
        for (int i = 0; i < 3; i++) {
            Card card = gameService.getPlayerHand().getCards().get(i);
            gameService.selectCard(card);
        }
        int initialHandSize = gameService.getPlayerHand().getCardCount();
        int initialDiscardSize = gameService.getDiscardPile().size();
        
        // Exercise
        boolean result = gameService.discardSelectedCards();
        
        // Verify
        assertTrue(result, "Discard operation should succeed");
        assertEquals(initialHandSize - 3, gameService.getPlayerHand().getCardCount(),
                "Hand size should decrease by 3");
        assertEquals(initialDiscardSize + 3, gameService.getDiscardPile().size(),
                "Discard pile size should increase by 3");
        assertTrue(gameService.getCanDrawCards(),
                "Player should be able to draw cards after discard");
        assertEquals(3, gameService.getCardsToDrawCount(),
                "Cards to draw count should be 3 after discarding 3 cards");
    }

    /**
     * Tests the draw functionality.
     * Verifies that cards can be drawn after discarding.
     */
    @Test
    void testDrawCards() {
        // Setup
        for (int i = 0; i < 3; i++) {
            Card card = gameService.getPlayerHand().getCards().get(i);
            gameService.selectCard(card);
        }
        gameService.discardSelectedCards();
        int initialDeckSize = gameService.getRemainingCards();
        
        // Exercise
        List<Card> drawnCards = gameService.drawCards();
        
        // Verify
        assertEquals(3, drawnCards.size(), "Should draw 3 cards");
        assertEquals(8, gameService.getPlayerHand().getCardCount(),
                "Hand size should be 8 after draw");
        assertEquals(initialDeckSize - 3, gameService.getRemainingCards(),
                "Deck size should decrease by 3");
    }

    /**
     * Tests that drawing is not allowed without discarding first.
     * Verifies the restriction that players must discard before drawing.
     */
    @Test
    void testDrawWithoutDiscard() {
        // Exercise
        List<Card> drawnCards = gameService.drawCards();
        
        // Verify
        assertTrue(drawnCards.isEmpty(), "Should not draw any cards without discarding first");
        assertFalse(gameService.getCanDrawCards(),
                "Player should not be able to draw cards without discarding first");
    }

    /**
     * Tests the hand evaluation functionality.
     * Verifies that selected cards can be evaluated and scored.
     */
    @Test
    void testEvaluateHand() {
        // Setup
        for (int i = 0; i < 5; i++) {
            Card card = gameService.getPlayerHand().getCards().get(i);
            gameService.selectCard(card);
        }
        int initialScore = gameService.getScore();
        
        // Exercise
        int handScore = gameService.evaluateHand();
        
        // Verify
        assertTrue(handScore > 0, "Hand score should be positive");
        assertEquals(initialScore + handScore, gameService.getScore(),
                "Score should increase by hand score");
        assertTrue(gameService.getSelectedCards().isEmpty(),
                "Selected cards should be empty after evaluation");
    }

    /**
     * Tests that empty selection is not evaluated.
     * Verifies that attempting to evaluate with no selected cards has no effect.
     */
    @Test
    void testEvaluateEmptySelection() {
        // Setup
        int initialScore = gameService.getScore();
        
        // Exercise
        int handScore = gameService.evaluateHand();
        
        // Verify
        assertEquals(0, handScore, "Hand score should be 0 for empty selection");
        assertEquals(initialScore, gameService.getScore(),
                "Score should not change for empty selection");
    }

    /**
     * Tests the hand type display functionality.
     * Verifies that the hand type display updates when cards are selected.
     */
    @Test
    void testHandTypeDisplay() {
        // Setup
        for (int i = 0; i < 5; i++) {
            Card card = gameService.getPlayerHand().getCards().get(i);
            gameService.selectCard(card);
        }
        
        // Exercise & Verify
        assertFalse(gameService.getCurrentHandTypeDisplay().equals("No cards selected"),
                "Hand type display should be updated when cards are selected");
        assertTrue(gameService.getCurrentHandTypeDisplay().contains("Base:"),
                "Hand type display should contain base score");
        assertTrue(gameService.getCurrentHandTypeDisplay().contains("Mult:"),
                "Hand type display should contain multiplier");
    }

    /**
     * Tests that hand type display resets when deselecting all cards.
     * Verifies that the hand type display is reset when no cards are selected.
     */
    @Test
    void testResetHandTypeDisplay() {
        // Setup
        Card card = gameService.getPlayerHand().getCards().get(0);
        gameService.selectCard(card);
        
        // Exercise
        gameService.deselectCard(card);
        
        // Verify
        assertEquals("No cards selected", gameService.getCurrentHandTypeDisplay(),
                "Hand type display should be reset when no cards are selected");
    }

    /**
     * Tests the game over condition.
     * Verifies that the game ends when the deck is empty and player has too few cards.
     */
    @Test
    void testGameOver() {
        // Setup - Empty the deck and reduce player's hand
        gameService.scoreProperty().set(0); // Reset score to avoid completing round
        
        // Discard all but one card
        while (gameService.getPlayerHand().getCardCount() > 1) {
            gameService.getSelectedCards().clear();
            Card card = gameService.getPlayerHand().getCards().get(0);
            gameService.selectCard(card);
            gameService.discardSelectedCards();
            gameService.drawCards();
        }
        
        // Empty the deck
        while (gameService.getRemainingCards() > 0) {
            gameService.getSelectedCards().clear();
            List<Card> cards = gameService.getPlayerHand().getCards();
            for (int i = 0; i < cards.size() - 1; i++) {
                gameService.selectCard(cards.get(i));
            }
            
            if (!gameService.getSelectedCards().isEmpty()) {
                gameService.discardSelectedCards();
                gameService.drawCards();
            } else {
                break;
            }
        }
        
        // Exercise
        gameService.getSelectedCards().clear();
        for (Card card : gameService.getPlayerHand().getCards()) {
            gameService.selectCard(card);
        }
        gameService.evaluateHand();
        
        // Verify
        if (gameService.getPlayerHand().getCardCount() < 1 && gameService.getRemainingCards() == 0) {
            assertTrue(gameService.isGameOver(),
                    "Game should be over when deck is empty and player has too few cards");
        }
    }

    /**
     * Tests the round completion functionality.
     * Verifies that the round is completed when the score reaches the target.
     */
    @Test
    void testRoundCompletion() {
        // Setup
        gameService.scoreProperty().set(1000);
        
        // Exercise
        gameService.evaluateHand();
        
        // Verify
        assertTrue(gameService.isRoundCompleted(),
                "Round should be completed when score reaches target");
        assertEquals(GameService.GameState.ROUND_COMPLETE, gameService.getGameState(),
                "Game state should be ROUND_COMPLETE when round is completed");
    }

    /**
     * Tests starting a new round.
     * Verifies that a new round can be started when the current round is completed.
     */
    @Test
    void testStartNewRound() {
        // Setup
        gameService.scoreProperty().set(1000);
        gameService.evaluateHand();
        int initialRound = gameService.getRound();
        int initialTargetScore = gameService.getTargetScore();
        
        // Exercise
        boolean result = gameService.startNewRound();
        
        // Verify
        assertTrue(result, "Should start new round when round is completed");
        assertEquals(initialRound + 1, gameService.getRound(),
                "Round should increase by 1");
        assertEquals(initialTargetScore * 2, gameService.getTargetScore(),
                "Target score should double");
        assertEquals(8, gameService.getPlayerHand().getCardCount(),
                "Player should have 8 cards in new round");
    }

    /**
     * Tests that new round cannot be started if current round is not completed.
     * Verifies the restriction that a round must be completed before starting a new one.
     */
    @Test
    void testStartNewRoundWithoutCompletion() {
        // Exercise
        boolean result = gameService.startNewRound();
        
        // Verify
        assertFalse(result, "Should not start new round if current round is not completed");
        assertEquals(1, gameService.getRound(),
                "Round should still be 1");
    }

    /**
     * Tests starting a new game.
     * Verifies that a new game resets all game state.
     */
    @Test
    void testNewGame() {
        // Setup - Modify game state
        for (int i = 0; i < 3; i++) {
            Card card = gameService.getPlayerHand().getCards().get(i);
            gameService.selectCard(card);
        }
        gameService.discardSelectedCards();
        gameService.drawCards();
        
        // Exercise
        gameService.newGame();
        
        // Verify
        assertEquals(GameService.GameState.WAITING_FOR_SELECTION, gameService.getGameState(),
                "New game should start in WAITING_FOR_SELECTION state");
        assertEquals(8, gameService.getPlayerHand().getCardCount(),
                "New game should start with 8 cards");
        assertEquals(44, gameService.getRemainingCards(),
                "New game should have 44 cards in deck");
        assertEquals(0, gameService.getScore(),
                "New game should start with 0 score");
        assertEquals(1, gameService.getRound(),
                "New game should start with round 1");
        assertEquals(1000, gameService.getTargetScore(),
                "New game should start with target score 1000");
        assertTrue(gameService.getDiscardPile().isEmpty(),
                "New game should start with empty discard pile");
        assertTrue(gameService.getSelectedCards().isEmpty(),
                "New game should start with empty selected cards");
    }
} 