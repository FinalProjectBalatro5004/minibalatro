package com.balatro.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class HandTest {
    private Hand hand;
    private List<Card> testCards;

    @BeforeEach
    void setUp() {
        hand = new Hand();
        testCards = new ArrayList<>();
        // Create some test cards
        testCards.add(new Card("Hearts", "A", 11));
        testCards.add(new Card("Diamonds", "K", 10));
        testCards.add(new Card("Clubs", "Q", 10));
        testCards.add(new Card("Spades", "J", 10));
        testCards.add(new Card("Hearts", "10", 10));
    }

    @Test
    void testHandInitialization() {
        assertEquals(0, hand.getCardCount());
        assertEquals(HandType.HIGH_CARD, hand.getHandType());
        assertEquals(0, hand.getBaseScore());
        assertEquals(1, hand.getMultiplier());
    }

    @Test
    void testAddCard() {
        Card card = new Card("Hearts", "A", 11);
        hand.addCard(card);
        assertEquals(1, hand.getCardCount());
        assertTrue(hand.getCards().contains(card));
    }

    @Test
    void testAddCardExceedsMaximum() {
        // Add 8 cards
        for (int i = 0; i < 8; i++) {
            hand.addCard(new Card("Hearts", "A", 11));
        }
        assertThrows(IllegalStateException.class, () -> {
            hand.addCard(new Card("Hearts", "2", 2));
        });
    }

    @Test
    void testDiscardCards() {
        hand.initializeHand(testCards);
        List<Card> cardsToDiscard = new ArrayList<>();
        cardsToDiscard.add(testCards.get(0));
        cardsToDiscard.add(testCards.get(1));
        
        int discarded = hand.discardCards(cardsToDiscard);
        assertEquals(2, discarded);
        assertEquals(3, hand.getCardCount());
    }

    @Test
    void testDiscardCardsInvalidCount() {
        hand.initializeHand(testCards);
        List<Card> cardsToDiscard = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {
            hand.discardCards(cardsToDiscard);
        });
    }

    @Test
    void testDrawNewCards() {
        hand.initializeHand(testCards);
        List<Card> newCards = new ArrayList<>();
        newCards.add(new Card("Hearts", "9", 9));
        newCards.add(new Card("Diamonds", "8", 8));
        
        hand.drawNewCards(newCards);
        assertEquals(7, hand.getCardCount());
    }

    @Test
    void testInitializeHand() {
        hand.initializeHand(testCards);
        assertEquals(5, hand.getCardCount());
        assertEquals(testCards, hand.getCards());
    }

    @Test
    void testCanGetCards() {
        assertTrue(hand.canGetCards());
        for (int i = 0; i < 8; i++) {
            hand.addCard(new Card("Hearts", "A", 11));
        }
        assertFalse(hand.canGetCards());
    }

    @Test
    void testIsValidHand() {
        assertFalse(hand.isValidHand());
        hand.addCard(new Card("Hearts", "A", 11));
        assertTrue(hand.isValidHand());
    }

    @Test
    void testGetRemainingCardSlots() {
        assertEquals(8, hand.getRemainingCardSlots());
        hand.addCard(new Card("Hearts", "A", 11));
        assertEquals(7, hand.getRemainingCardSlots());
    }

    @Test
    void testMeetsMinimumRequirements() {
        assertFalse(hand.meetsMinimumRequirements());
        hand.addCard(new Card("Hearts", "A", 11));
        assertTrue(hand.meetsMinimumRequirements());
    }

    @Test
    void testMeetsMaximumRequirements() {
        assertTrue(hand.meetsMaximumRequirements());
        for (int i = 0; i < 6; i++) {
            hand.addCard(new Card("Hearts", "A", 11));
        }
        assertFalse(hand.meetsMaximumRequirements());
    }

    @Test
    void testGetCardsUnmodifiable() {
        hand.initializeHand(testCards);
        List<Card> cards = hand.getCards();
        assertThrows(UnsupportedOperationException.class, () -> {
            cards.add(new Card("Hearts", "A", 11));
        });
    }

    @Test
    void testRemoveCard() {
        hand.initializeHand(testCards);
        Card cardToRemove = testCards.get(0);
        assertTrue(hand.removeCard(cardToRemove));
        assertFalse(hand.getCards().contains(cardToRemove));
        assertEquals(4, hand.getCardCount());
    }

    @Test
    void testEvaluateHand() {
        // Test a straight flush
        List<Card> straightFlush = new ArrayList<>();
        straightFlush.add(new Card("Hearts", "A", 11));
        straightFlush.add(new Card("Hearts", "K", 10));
        straightFlush.add(new Card("Hearts", "Q", 10));
        straightFlush.add(new Card("Hearts", "J", 10));
        straightFlush.add(new Card("Hearts", "10", 10));
        hand.initializeHand(straightFlush);
        assertEquals(HandType.STRAIGHT_FLUSH, hand.getHandType());
    }
} 