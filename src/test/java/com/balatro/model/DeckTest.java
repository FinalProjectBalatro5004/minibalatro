package com.balatro.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for Deck implementation.
 * Tests the functionality of a deck of cards in the Balatro game.
 */
class DeckTest {
    private Deck deck;
    private List<Card> keptCards;

    /**
     * Sets up the test fixtures before each test method.
     * Creates:
     * 1. A new standard deck of 52 cards
     * 2. A list of 5 kept cards (Hearts 2-6) to simulate player's kept cards
     */
    @BeforeEach
    void setUp() {
        // Create a new standard deck
        deck = new Deck();
        
        // Create a list to represent cards that player keeps (doesn't discard)
        keptCards = new ArrayList<>();
        
        // Add 5 hearts cards (2♥,3♥,4♥,5♥,6♥) as kept cards
        for (int rank = 2; rank <= 6; rank++) {
            String rankStr = String.valueOf(rank);
            int value = rank;  // In Balatro, number cards are worth their face value
            keptCards.add(new Card("Hearts", rankStr, value));
        }
    }

    /**
     * Test 1st Constructor: Tests that a new deck is created with 52 cards.
     * Verifies the composition of suits and special cards.
     */
    @Test
    void testNewDeckHas52Cards() {
        // Setup - done in setUp()

        // Exercise & Verify
        assertEquals(52, deck.getCardCount(), "New deck should have 52 cards");
        
        // Verify deck composition
        List<Card> cards = deck.getCards();
        int hearts = 0, diamonds = 0, clubs = 0, spades = 0;
        int aces = 0, faceCards = 0;
        //Enter the for loop to check the composition of the deck
        
        for (Card card : cards) {
            assertNotNull(card, "Card should not be null");
            //switch case to check the suit of the card
            //If the suit is hearts, diamonds, clubs, or spades, increment the corresponding counter
            switch (card.getSuit()) {
                case "Hearts" -> hearts++;
                case "Diamonds" -> diamonds++;
                case "Clubs" -> clubs++;
                case "Spades" -> spades++;
            }
            //if the rank is an ace, increment the ace counter
            if (card.getRank().equals("A")) {
                aces++;
            } else if (card.isFaceCard()) {
                faceCards++;
            }
        }
        
        // Verify correct number of each suit and special cards
        assertEquals(13, hearts, "Should have 13 hearts");
        assertEquals(13, diamonds, "Should have 13 diamonds");
        assertEquals(13, clubs, "Should have 13 clubs");
        assertEquals(13, spades, "Should have 13 spades");
        assertEquals(4, aces, "Should have 4 aces");
        assertEquals(12, faceCards, "Should have 12 face cards");
    }

    /**
     * Test 2nd Constructor: Tests creating a custom deck with specific cards.
     */
    @Test
    void testCreateCustomDeck() {
        // Setup
        List<Card> customCards = new ArrayList<>();
        customCards.add(new Card("Hearts", "A", 11));
        customCards.add(new Card("Spades", "K", 10));
        
        // Exercise
        Deck customDeck = new Deck(customCards);
        
        // Verify
        assertEquals(2, customDeck.getCardCount(), "Custom deck should have specified number of cards");
        assertEquals(customCards, customDeck.getCards(), "Custom deck should contain specified cards");
    }

    /**
     * Test DrawCard
     * Tests drawing cards maintains exactly 8 cards in hand.
     */
    @Test
    void testDrawingMaintains8Cards() {
        
        // Exercise
        Deck newHand = deck.drawCard(3, keptCards);  // Draw 3 to make total of 8
        
        // Verify
        assertNotNull(newHand, "New hand should not be null");
        assertEquals(8, newHand.getCardCount(), "New hand should have exactly 8 cards");
        assertEquals(49, deck.getCardCount(), "Original deck should have 49 cards remaining");
        assertTrue(newHand.getCards().containsAll(keptCards), "New hand should contain all kept cards");
    }

    /**
     * Test DrawCard 2: Tests invalid inputs when drawing cards.
     * Tests invalid inputs when drawing cards.
     */
    @Test
    void testInvalidDrawInputs() {
        // Setup
        List<Card> tooFewCards = new ArrayList<>(keptCards.subList(0, 4));  // 4 cards
        List<Card> tooManyCards = new ArrayList<>(keptCards);
        tooManyCards.add(new Card("Hearts", "7", 7));  // 6 cards
        
        // Exercise & Verify
        Exception e1 = assertThrows(IllegalArgumentException.class, () -> deck.drawCard(3, tooFewCards),
            "Should throw exception when total cards would be less than 8");
        assertEquals("Total cards (kept + discarded) must be 8", e1.getMessage());

        Exception e2 = assertThrows(IllegalArgumentException.class, () -> deck.drawCard(3, tooManyCards),
            "Should throw exception when total cards would be more than 8");
        assertEquals("Total cards (kept + discarded) must be 8", e2.getMessage());

        Exception e3 = assertThrows(IllegalArgumentException.class, () -> deck.drawCard(0, keptCards),
            "Should throw exception for 0 cards");
        assertEquals("Must discard between 1 and 5 cards", e3.getMessage());

        Exception e4 = assertThrows(IllegalArgumentException.class, () -> deck.drawCard(6, keptCards),
            "Should throw exception for more than 5 cards");
        assertEquals("Must discard between 1 and 5 cards", e4.getMessage());

        Exception e5 = assertThrows(IllegalArgumentException.class, () -> deck.drawCard(3, null),
            "Should throw exception for null kept cards");
        assertEquals("Kept cards list cannot be null", e5.getMessage());
    }

    /**
     * Tests drawing cards when deck has insufficient cards.
     * Verifies what happens when there aren't enough cards left in the deck to satisfy a draw request
     */
    @Test
    void testDrawWithInsufficientCards() {
        // Setup - Draw most cards from deck
        List<Card> sevenKeptCards = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            sevenKeptCards.add(new Card("Hearts", String.valueOf(i + 2), i + 2));
        }
        while (deck.getCardCount() > 3) {
            deck.drawCard(1, sevenKeptCards);
        }
        
        // Exercise
        Deck insufficientHand = deck.drawCard(5, keptCards);
        
        // Verify
        assertNull(insufficientHand, "Should return null when not enough cards");
    }

    /**
     * Tests resetting the deck to initial state.
     */
    @Test
    void testResetDeck() {
        // Setup
        List<Card> sevenCards = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            sevenCards.add(new Card("Hearts", String.valueOf(i + 2), i + 2));
        }
        deck.drawCard(1, sevenCards);  // Remove some cards
        
        // Exercise
        deck.resetDeck();
        
        // Verify
        assertEquals(52, deck.getCardCount(), "Reset deck should have 52 cards");
        assertFalse(deck.isEmpty(), "Reset deck should not be empty");
    }

    /**
     * Tests shuffling the deck according to Balatro rules:
     * 1. Deck is shuffled at the start of a new round
     * 2. Cannot shuffle during an ongoing round
     * 3. Can only shuffle again when starting a new round after reaching required score
     */
    @Test
    void testShuffle() {
        // Setup - New deck is already shuffled in constructor
        List<Card> firstRoundOrder = new ArrayList<>(deck.getCards());
        
        // Verify cannot shuffle during round
        Exception e = assertThrows(IllegalStateException.class, () -> deck.shuffle(),
            "Should not be able to shuffle during a round");
        assertEquals("Cannot shuffle deck during an ongoing round", e.getMessage());
        
        // Verify deck order remains unchanged
        assertEquals(firstRoundOrder, deck.getCards(), "Deck order should not change during round");
        
        // Setup for new round
        deck.updateScore(1000);  // Assume 1000 is required score
        deck.startNewRound(1000);
        
        // Verify deck is different after new round shuffle
        List<Card> secondRoundOrder = deck.getCards();
        int differentPositions = 0;
        //This for loop checks if the cards are in different positions
       // if the firstRoundOrder and secondRoundOrder are not the same, increment the differentPositions counter
       // because the cards are in different positions
        for (int i = 0; i < firstRoundOrder.size(); i++) {
            if (!firstRoundOrder.get(i).equals(secondRoundOrder.get(i))) {
                differentPositions++;
            }
        }
        // We expect at least 70% of the 52 cards to be in different positions after shuffling
        // 52 * 0.7 ≈ 36 cards should be in different positions
        assertTrue(differentPositions > 36, "At least 70% of cards should be in different positions in new round");
        
        // Verify cannot start new round without required score
        deck.updateScore(500);  // Reset score below requirement
        assertThrows(IllegalStateException.class, () -> deck.startNewRound(1000),
            "Should not be able to start new round without required score");
    }

    /**
     * Tests score management and round transitions.
     * Verifies:
     * 1. Score updates correctly
     * 2. Can start new round only when score requirement is met
     * 3. Cannot start new round with insufficient score
     */
    @Test
    void testScoreAndRoundManagement() {
        // Test initial state
        assertFalse(deck.canStartNewRound(1000), "Should not be able to start new round with initial score of 0");
        
        // Test partial score
        deck.updateScore(500);
        assertFalse(deck.canStartNewRound(1000), "Should not be able to start new round with insufficient score");
        
        // Test meeting score requirement
        deck.updateScore(1000);
        assertTrue(deck.canStartNewRound(1000), "Should be able to start new round when score requirement is met");
        
        // Test exceeding score requirement
        deck.updateScore(1500);
        assertTrue(deck.canStartNewRound(1000), "Should be able to start new round when score requirement is exceeded");
        
        // Test score reset doesn't affect current round
        deck.updateScore(0);
        assertFalse(deck.canStartNewRound(1000), "Should not be able to start new round after score reset");
    }

    /**
     * Tests the complete round transition process.
     * Verifies:
     * 1. Round can only start with sufficient score
     * 2. Deck is reset and shuffled on new round
     * 3. Score requirements are properly enforced
     */
    @Test
    void testStartNewRound() {
        // Setup - try to start new round without sufficient score
        assertThrows(IllegalStateException.class, () -> deck.startNewRound(1000),
            "Should not be able to start new round without required score");
            
        // Setup for valid new round
        deck.updateScore(1000);
        List<Card> originalOrder = new ArrayList<>(deck.getCards());
        
        // Exercise - start new round
        deck.startNewRound(1000);
        
        // Verify
        assertEquals(52, deck.getCardCount(), "Deck should be reset to 52 cards in new round");
        List<Card> newOrder = deck.getCards();
        
        // Verify deck is shuffled in new round
        int differentPositions = 0;
        for (int i = 0; i < originalOrder.size(); i++) {
            if (!originalOrder.get(i).equals(newOrder.get(i))) {
                differentPositions++;
            }
        }
        assertTrue(differentPositions > 36, "Deck should be shuffled in new round");
        
        // Verify multiple round transitions
        deck.updateScore(2000);
        deck.startNewRound(1000);  // Should work with higher score
        assertEquals(52, deck.getCardCount(), "Deck should be reset to 52 cards in subsequent round");
    }
} 