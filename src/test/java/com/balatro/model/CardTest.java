package com.balatro.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testCardCreation() {
        Card card = new Card("Hearts", "A", 1);
        assertEquals("Hearts", card.getSuit());
        assertEquals("A", card.getRank());
        assertEquals(1, card.getValue());
    }

    @Test
    void testInvalidSuit() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Card("Invalid", "A", 1);
        });
    }

    @Test
    void testInvalidRank() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Card("Hearts", "Invalid", 1);
        });
    }

    @Test
    void testInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Card("Hearts", "A", -1);
        });
    }

    @Test
    void testIsFaceCard() {
        Card jack = new Card("Hearts", "J", 11);
        Card queen = new Card("Hearts", "Q", 12);
        Card king = new Card("Hearts", "K", 13);
        Card ace = new Card("Hearts", "A", 1);

        assertTrue(jack.isFaceCard());
        assertTrue(queen.isFaceCard());
        assertTrue(king.isFaceCard());
        assertFalse(ace.isFaceCard());
    }

    @Test
    void testGetColor() {
        Card hearts = new Card("Hearts", "A", 1);
        Card diamonds = new Card("Diamonds", "A", 1);
        Card clubs = new Card("Clubs", "A", 1);
        Card spades = new Card("Spades", "A", 1);
        Card joker = new Card("Joker", "A", 1);

        assertEquals("Red", hearts.getColor());
        assertEquals("Red", diamonds.getColor());
        assertEquals("Black", clubs.getColor());
        assertEquals("Black", spades.getColor());
        assertEquals("Joker", joker.getColor());
    }

    @Test
    void testEquals() {
        Card card1 = new Card("Hearts", "A", 1);
        Card card2 = new Card("Hearts", "A", 1);
        Card card3 = new Card("Diamonds", "A", 1);

        assertTrue(card1.equals(card2));
        assertFalse(card1.equals(card3));
        assertFalse(card1.equals(null));
        assertTrue(card1.equals(card1));
    }

    @Test
    void testHashCode() {
        Card card1 = new Card("Hearts", "A", 1);
        Card card2 = new Card("Hearts", "A", 1);
        Card card3 = new Card("Diamonds", "A", 1);

        assertEquals(card1.hashCode(), card2.hashCode());
        assertNotEquals(card1.hashCode(), card3.hashCode());
    }

    @Test
    void testToString() {
        Card card = new Card("Hearts", "A", 1);
        assertEquals("A of Hearts (1)", card.toString());
    }
} 