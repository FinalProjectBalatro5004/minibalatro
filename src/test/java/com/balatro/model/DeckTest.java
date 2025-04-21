package com.balatro.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

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
        Deck newDeck = deck.drawCard(3, keptCards);
        assertNotNull(newDeck);
        assertEquals(3, newDeck.getCardCount());
        assertEquals(49, deck.getCardCount());
    }

    @Test
    void testDrawCardWithInvalidParameters() {
        List<Card> keptCards = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {
            deck.drawCard(0, keptCards);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            deck.drawCard(6, keptCards);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            deck.drawCard(3, null);
        });
    }

    @Test
    void testDrawCardWithInsufficientCards() {
        // Draw all cards first
        for (int i = 0; i < 10; i++) {
            List<Card> keptCards = new ArrayList<>();
            deck.drawCard(5, keptCards);
        }
        List<Card> keptCards = new ArrayList<>();
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