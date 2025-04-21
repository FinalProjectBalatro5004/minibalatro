package com.balatro.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeckTest {
    private Deck deck;

    @BeforeEach
    void setUp() {
        deck = new Deck();
    }

    @Test
    void testDeckInitialization() {
        assertEquals(52, deck.getCardCount());
        assertFalse(deck.isEmpty());
    }

    @Test
    void testResetDeck() {
        // Draw some cards first
        List<Card> keptCards = new ArrayList<>();
        // Add 3 cards to keptCards to make total of 8 (5 discarded + 3 kept)
        String[] validRanks = {"A", "2", "3"};
        for (int i = 0; i < 3; i++) {
            keptCards.add(new Card("Hearts", validRanks[i], i+1));
        }
        deck.drawCard(5, keptCards);
        assertEquals(47, deck.getCardCount());

        // Reset the deck
        deck.resetDeck();
        assertEquals(52, deck.getCardCount());
    }

    @Test
    void testShuffle() {
        List<Card> originalOrder = new ArrayList<>(deck.getCards());
        deck.shuffle();
        List<Card> shuffledOrder = deck.getCards();

        assertEquals(52, shuffledOrder.size());
        assertNotEquals(originalOrder, shuffledOrder);
    }

    @Test
    void testStartNewRound() {
        deck.updateScore(100);
        deck.startNewRound(50);
        assertTrue(deck.isNewRound());
        assertEquals(52, deck.getCardCount());
    }

    @Test
    void testStartNewRoundWithInsufficientScore() {
        deck.updateScore(30);
        assertThrows(IllegalStateException.class, () -> {
            deck.startNewRound(50);
        });
    }

    @Test
    void testCanStartNewRound() {
        deck.updateScore(60);
        assertTrue(deck.canStartNewRound(50));
        assertFalse(deck.canStartNewRound(70));
    }

    @Test
    void testDrawCard() {
        List<Card> keptCards = new ArrayList<>();
        // Add 5 cards to keptCards to make total of 8 (3 discarded + 5 kept)
        String[] validRanks = {"A", "2", "3", "4", "5"};
        for (int i = 0; i < 5; i++) {
            keptCards.add(new Card("Hearts", validRanks[i], i+1));
        }
        Deck newDeck = deck.drawCard(3, keptCards);
        assertNotNull(newDeck);
        assertEquals(8, newDeck.getCardCount()); // Should have 8 cards total
        assertEquals(49, deck.getCardCount());
    }

    @Test
    void testDrawCardWithInvalidParameters() {
        List<Card> keptCards = new ArrayList<>();
        // Add 7 cards for testing discard 1
        String[] validRanks = {"A", "2", "3", "4", "5", "6", "7"};
        for (int i = 0; i < 7; i++) {
            keptCards.add(new Card("Hearts", validRanks[i], i+1));
        }
        
        List<Card> keptCards2 = new ArrayList<>();
        // Add 3 cards for testing discard 5
        for (int i = 0; i < 3; i++) {
            keptCards2.add(new Card("Hearts", validRanks[i], i+1));
        }
        
        // Invalid discard amount
        assertThrows(IllegalArgumentException.class, () -> {
            deck.drawCard(0, keptCards);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            deck.drawCard(6, keptCards2);
        });
        // Null kept cards
        assertThrows(IllegalArgumentException.class, () -> {
            deck.drawCard(3, null);
        });
        // Not right total (kept + discarded ≠ 8)
        List<Card> wrongNumberKept = new ArrayList<>();
        wrongNumberKept.add(new Card("Hearts", "A", 11));
        assertThrows(IllegalArgumentException.class, () -> {
            deck.drawCard(3, wrongNumberKept);
        });
    }

    @Test
    void testDrawCardWithInsufficientCards() {
        // First, reduce the deck to less than 3 cards
        String[] validRanks = {"A", "2", "3", "4", "5"};
        for (int i = 0; i < 10; i++) {
            List<Card> keptCards = new ArrayList<>();
            // Add the right number of kept cards for each draw
            for (int j = 0; j < 3; j++) {
                keptCards.add(new Card("Hearts", validRanks[j], j+1));
            }
            Deck result = deck.drawCard(5, keptCards);
            if (result == null) break; // Stop if we can't draw anymore
        }
        
        // Now try to draw 3 more when deck has insufficient cards
        List<Card> keptCards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            keptCards.add(new Card("Hearts", validRanks[i], i+1));
        }
        assertNull(deck.drawCard(3, keptCards));
    }

    @Test
    void testGetCards() {
        List<Card> cards = deck.getCards();
        assertEquals(52, cards.size());
        assertNotSame(deck.getMutableCards(), cards); // Should return a copy
    }

    @Test
    void testIsEmpty() {
        assertFalse(deck.isEmpty());
        deck.resetDeck();
        assertFalse(deck.isEmpty());
    }
} 