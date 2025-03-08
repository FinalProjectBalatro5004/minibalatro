package com.balatro.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for Hand implementation.
 * Tests the functionality of a poker hand in the Balatro game.
 */
class HandTest {
    private Hand hand;
    private List<Card> testCards;

    /**
     * Sets up the test fixtures before each test method.
     * Creates:
     * 1. A new empty hand
     * 2. A list of test cards for various scenarios
     */
    @BeforeEach
    void setUp() {
        hand = new Hand();
        testCards = new ArrayList<>();
        // Create test cards for various hand combinations
        testCards.add(new Card("Hearts", "A", 11));    // Ace of Hearts
        testCards.add(new Card("Hearts", "K", 10));    // King of Hearts
        testCards.add(new Card("Hearts", "Q", 10));    // Queen of Hearts
        testCards.add(new Card("Hearts", "J", 10));    // Jack of Hearts
        testCards.add(new Card("Hearts", "10", 10));   // 10 of Hearts
    }

    /**
     * Tests constructor and initial state.
     */
    @Test
    void testConstructor() {
        // Verify initial state
        assertEquals(0, hand.getCardCount(), "New hand should be empty");
        assertEquals(HandType.HIGH_CARD, hand.getHandType(), "Initial hand type should be HIGH_CARD");
        assertEquals(0, hand.getBaseScore(), "Initial base score should be 0");
        assertEquals(1, hand.getMultiplier(), "Initial multiplier should be 1");
    }

    /**
     * Tests adding cards to hand.
     */
    @Test
    void testAddCard() {
        // Setup
        Card card = testCards.get(0);
        
        // Exercise
        hand.addCard(card);
        
        // Verify
        assertEquals(1, hand.getCardCount(), "Hand should have one card");
        assertTrue(hand.getCards().contains(card), "Hand should contain the added card");
        
        // Test adding card to full hand
        for (int i = 1; i < Hand.getMaxCards(); i++) {
            hand.addCard(testCards.get(i % testCards.size()));
        }
        assertThrows(IllegalStateException.class, () -> hand.addCard(card),
            "Should throw exception when adding to full hand");
    }

    /**
     * Tests discarding cards from hand.
     */
    @Test
    void testDiscardCards() {
        // Setup - add some cards first
        for (int i = 0; i < 3; i++) {
            hand.addCard(testCards.get(i));
        }
        
        // Exercise
        List<Card> cardsToDiscard = new ArrayList<>();
        cardsToDiscard.add(testCards.get(0));
        int discarded = hand.discardCards(cardsToDiscard);
        
        // Verify
        assertEquals(1, discarded, "Should discard one card");
        assertEquals(2, hand.getCardCount(), "Should have two cards remaining");
        assertFalse(hand.getCards().contains(testCards.get(0)), "Discarded card should not be in hand");
        
        // Test invalid discard - too many cards
        List<Card> tooManyCards = new ArrayList<>();
        for (int i = 0; i < Hand.getMaxCardsToDiscard() + 1; i++) {
            tooManyCards.add(new Card("Clubs", String.valueOf(i + 2), i + 2));
        }
        assertThrows(IllegalArgumentException.class, 
            () -> hand.discardCards(tooManyCards),
            "Should throw exception when discarding too many cards");
            
        // Test invalid discard - would leave hand empty
        List<Card> allRemainingCards = new ArrayList<>(hand.getCards());
        assertThrows(IllegalStateException.class, 
            () -> hand.discardCards(allRemainingCards),
            "Should throw exception when discarding would leave hand empty");
    }

    /**
     * Tests drawing new cards.
     */
    @Test
    void testDrawNewCards() {
        // Setup
        List<Card> newCards = new ArrayList<>();
        newCards.add(testCards.get(0));
        newCards.add(testCards.get(1));
        
        // Exercise
        hand.drawNewCards(newCards);
        
        // Verify
        assertEquals(2, hand.getCardCount(), "Should have drawn two cards");
        assertTrue(hand.getCards().containsAll(newCards), "Hand should contain all drawn cards");
        
        // Test invalid draw - too many cards
        List<Card> tooManyCards = new ArrayList<>();
        for (int i = 0; i < Hand.getMaxCardsToDiscard() + 1; i++) {
            tooManyCards.add(new Card("Clubs", String.valueOf(i + 2), i + 2));
        }
        assertThrows(IllegalArgumentException.class, 
            () -> hand.drawNewCards(tooManyCards),
            "Should throw exception when drawing too many cards");
    }

    /**
     * Tests hand validation methods.
     */
    @Test
    void testHandValidation() {
        // Test empty hand
        assertFalse(hand.isValidHand(), "Empty hand should not be valid");
        assertTrue(hand.canGetCards(), "Should be able to get cards when hand is empty");
        assertEquals(Hand.getMaxCards(), hand.getRemainingCardSlots(), "Should have max slots available");
        
        // Add one card and test
        hand.addCard(testCards.get(0));
        assertTrue(hand.isValidHand(), "Hand with one card should be valid");
        assertTrue(hand.meetsMinimumRequirements(), "Should meet minimum requirements");
        assertTrue(hand.meetsMaximumRequirements(), "Should meet maximum requirements");
        
        // Fill hand and test
        for (int i = 1; i < Hand.getMaxCards(); i++) {
            hand.addCard(testCards.get(i % testCards.size()));
        }
        assertFalse(hand.canGetCards(), "Should not be able to get more cards when hand is full");
        assertEquals(0, hand.getRemainingCardSlots(), "Should have no slots remaining");
    }

    /**
     * Tests evaluation of different poker hands.
     * Note: Hand.evaluateHand() only evaluates poker hands when there are at least 5 cards.
     * For hands with fewer cards, it always returns HIGH_CARD.
     */
    @Test
    void testPokerHandEvaluation() {
        // Create a hand with 5 cards for proper evaluation
        
        // Test High Card
        hand = new Hand();
        hand.addCard(new Card("Hearts", "A", 11));
        hand.addCard(new Card("Diamonds", "3", 3));
        hand.addCard(new Card("Clubs", "5", 5));
        hand.addCard(new Card("Spades", "7", 7));
        hand.addCard(new Card("Hearts", "9", 9));
        assertEquals(HandType.HIGH_CARD, hand.getHandType(), "Should be high card");
        
        // Test Pair
        hand = new Hand();
        hand.addCard(new Card("Hearts", "A", 11));
        hand.addCard(new Card("Diamonds", "A", 11));
        hand.addCard(new Card("Clubs", "5", 5));
        hand.addCard(new Card("Spades", "7", 7));
        hand.addCard(new Card("Hearts", "9", 9));
        assertEquals(HandType.PAIR, hand.getHandType(), "Should be a pair");
        assertEquals(10, hand.getBaseScore(), "Pair base score should be 10");

        // Test Two Pair
        hand = new Hand();
        hand.addCard(new Card("Hearts", "A", 11));
        hand.addCard(new Card("Diamonds", "A", 11));
        hand.addCard(new Card("Clubs", "K", 10));
        hand.addCard(new Card("Spades", "K", 10));
        hand.addCard(new Card("Hearts", "9", 9));
        assertEquals(HandType.TWO_PAIR, hand.getHandType(), "Should be two pair");
        assertEquals(20, hand.getBaseScore(), "Two pair base score should be 20");

        // Test Three of a Kind
        hand = new Hand();
        hand.addCard(new Card("Hearts", "Q", 10));
        hand.addCard(new Card("Diamonds", "Q", 10));
        hand.addCard(new Card("Spades", "Q", 10));
        hand.addCard(new Card("Hearts", "2", 2));
        hand.addCard(new Card("Diamonds", "3", 3));
        assertEquals(HandType.THREE_OF_A_KIND, hand.getHandType(), "Should be three of a kind");
        assertEquals(30, hand.getBaseScore(), "Three of a kind base score should be 30");

        // Test Straight
        hand = new Hand();
        hand.addCard(new Card("Hearts", "2", 2));
        hand.addCard(new Card("Diamonds", "3", 3));
        hand.addCard(new Card("Clubs", "4", 4));
        hand.addCard(new Card("Spades", "5", 5));
        hand.addCard(new Card("Hearts", "6", 6));
        assertEquals(HandType.STRAIGHT, hand.getHandType(), "Should be a straight");
        assertEquals(30, hand.getBaseScore(), "Straight base score should be 30");

        // Test Flush
        hand = new Hand();
        hand.addCard(new Card("Hearts", "2", 2));
        hand.addCard(new Card("Hearts", "4", 4));
        hand.addCard(new Card("Hearts", "6", 6));
        hand.addCard(new Card("Hearts", "8", 8));
        hand.addCard(new Card("Hearts", "10", 10));
        assertEquals(HandType.FLUSH, hand.getHandType(), "Should be a flush");
        assertEquals(35, hand.getBaseScore(), "Flush base score should be 35");

        // Test Full House
        hand = new Hand();
        hand.addCard(new Card("Hearts", "K", 10));
        hand.addCard(new Card("Diamonds", "K", 10));
        hand.addCard(new Card("Spades", "K", 10));
        hand.addCard(new Card("Hearts", "2", 2));
        hand.addCard(new Card("Diamonds", "2", 2));
        assertEquals(HandType.FULL_HOUSE, hand.getHandType(), "Should be a full house");
        assertEquals(40, hand.getBaseScore(), "Full house base score should be 40");

        // Test Four of a Kind
        hand = new Hand();
        hand.addCard(new Card("Hearts", "A", 11));
        hand.addCard(new Card("Diamonds", "A", 11));
        hand.addCard(new Card("Clubs", "A", 11));
        hand.addCard(new Card("Spades", "A", 11));
        hand.addCard(new Card("Hearts", "2", 2));
        assertEquals(HandType.FOUR_OF_A_KIND, hand.getHandType(), "Should be four of a kind");
        assertEquals(60, hand.getBaseScore(), "Four of a kind base score should be 60");

        // Test Straight Flush
        hand = new Hand();
        hand.addCard(new Card("Hearts", "2", 2));
        hand.addCard(new Card("Hearts", "3", 3));
        hand.addCard(new Card("Hearts", "4", 4));
        hand.addCard(new Card("Hearts", "5", 5));
        hand.addCard(new Card("Hearts", "6", 6));
        assertEquals(HandType.STRAIGHT_FLUSH, hand.getHandType(), "Should be a straight flush");
        assertEquals(100, hand.getBaseScore(), "Straight flush base score should be 100");
    }

    /**
     * Tests hand evaluation and scoring for straight flush.
     */
    @Test
    void testHandEvaluation() {
        // Create a proper straight flush
        hand.addCard(new Card("Hearts", "2", 2));
        hand.addCard(new Card("Hearts", "3", 3));
        hand.addCard(new Card("Hearts", "4", 4));
        hand.addCard(new Card("Hearts", "5", 5));
        hand.addCard(new Card("Hearts", "6", 6));
        
        assertEquals(HandType.STRAIGHT_FLUSH, hand.getHandType(), "Should be a straight flush");
        assertEquals(100, hand.getBaseScore(), "Should have correct base score");
        assertEquals(8, hand.getMultiplier(), "Should have correct multiplier");
        
        // Test total score calculation
        int expectedTotal = 2 + 3 + 4 + 5 + 6; // Sum of card values
        assertEquals(expectedTotal, hand.getTotalScore(), "Total score should be sum of card values");
    }

    /**
     * Tests static card limit getters.
     */
    @Test
    void testStaticGetters() {
        assertEquals(1, Hand.getMinCards(), "Minimum cards should be 1");
        assertEquals(8, Hand.getMaxCards(), "Maximum cards should be 8");
        assertEquals(1, Hand.getMinCardsToPlay(), "Minimum cards to play should be 1");
        assertEquals(5, Hand.getMaxCardsToPlay(), "Maximum cards to play should be 5");
        assertEquals(1, Hand.getMinCardsToDiscard(), "Minimum cards to discard should be 1");
        assertEquals(5, Hand.getMaxCardsToDiscard(), "Maximum cards to discard should be 5");
    }

    /**
     * Tests removing specific cards.
     */
    @Test
    void testRemoveCard() {
        // Setup
        Card cardToRemove = testCards.get(0);
        hand.addCard(cardToRemove);
        
        // Exercise & Verify
        assertTrue(hand.removeCard(cardToRemove), "Should successfully remove existing card");
        assertFalse(hand.removeCard(cardToRemove), "Should return false when removing non-existent card");
        assertEquals(0, hand.getCardCount(), "Hand should be empty after removal");
    }

    /**
     * Tests the string representation of hands.
     */
    @Test
    void testToString() {
        // Test empty hand
        hand = new Hand();
        String emptyHandString = hand.toString();
        assertTrue(emptyHandString.contains("High Card"), "Empty hand should be high card");
        assertTrue(emptyHandString.contains("Base Score: 0"), "Empty hand should have 0 base score");

        // Test hand with cards
        hand.addCard(new Card("Hearts", "A", 11));  // Add Ace of Hearts
        String handString = hand.toString();
        assertTrue(handString.contains("High Card"), "Should show hand type");
        assertTrue(handString.contains("A"), "Should show card rank");
        assertTrue(handString.contains("Hearts"), "Should show card suit");
        assertTrue(handString.contains("Base Score:"), "Should show base score");
    }
} 