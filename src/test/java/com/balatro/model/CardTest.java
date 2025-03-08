package com.balatro.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class for Card implementation.
 * Tests the functionality of playing cards in the Balatro game.
 */
@DisplayName("Card Tests")
class CardTest {
    private Card aceHearts;
    private Card kingSpades;
    private Card queenClubs;
    private Card jackDiamonds;
    private Card sevenHearts;
    private Card aceHearts2;  // Identical to aceHearts for equality testing

    /**
     * Sets up the test fixtures before each test method.
     * Creates different types of cards (face cards, number cards) with various suits.
     */
    @BeforeEach
    void setUp() {
        aceHearts = new Card("Hearts", "A", 11);
        kingSpades = new Card("Spades", "K", 10);
        queenClubs = new Card("Clubs", "Q", 10);
        jackDiamonds = new Card("Diamonds", "J", 10);
        sevenHearts = new Card("Hearts", "7", 7);
        aceHearts2 = new Card("Hearts", "A", 11);
    }

    /**
     * Tests the getSuit method.
     * Verifies that cards return their correct suits.
     */
    @Test
    void getSuit() {
        assertEquals("Hearts", aceHearts.getSuit(), "Ace of Hearts should have Hearts suit");
        assertEquals("Spades", kingSpades.getSuit(), "King of Spades should have Spades suit");
        assertEquals("Clubs", queenClubs.getSuit(), "Queen of Clubs should have Clubs suit");
        assertEquals("Diamonds", jackDiamonds.getSuit(), "Jack of Diamonds should have Diamonds suit");
    }

    /**
     * Tests the getRank method.
     * Verifies that cards return their correct ranks.
     */
    @Test
    void getRank() {
        assertEquals("A", aceHearts.getRank(), "Ace should have rank A");
        assertEquals("K", kingSpades.getRank(), "King should have rank K");
        assertEquals("Q", queenClubs.getRank(), "Queen should have rank Q");
        assertEquals("J", jackDiamonds.getRank(), "Jack should have rank J");
        assertEquals("7", sevenHearts.getRank(), "Seven should have rank 7");
    }

    /**
     * Tests the getValue method.
     * Verifies that cards return their correct numerical values.
     */
    @Test
    void getValue() {
        assertEquals(11, aceHearts.getValue(), "Ace should have value 11");
        assertEquals(10, kingSpades.getValue(), "King should have value 10");
        assertEquals(10, queenClubs.getValue(), "Queen should have value 10");
        assertEquals(10, jackDiamonds.getValue(), "Jack should have value 10");
        assertEquals(7, sevenHearts.getValue(), "Seven should have value 7");
    }

    /**
     * Tests the isFaceCard method.
     * Verifies that:
     * - Jack, Queen, and King are identified as face cards
     * - Ace and number cards are not identified as face cards
     */
    @Test
    void isFaceCard() {
        assertTrue(kingSpades.isFaceCard(), "King should be a face card");
        assertTrue(queenClubs.isFaceCard(), "Queen should be a face card");
        assertTrue(jackDiamonds.isFaceCard(), "Jack should be a face card");
        assertFalse(aceHearts.isFaceCard(), "Ace should not be a face card");
        assertFalse(sevenHearts.isFaceCard(), "Seven should not be a face card");
    }

    /**
     * Tests the getColor method.
     * Verifies that:
     * - Hearts and Diamonds are red
     * - Clubs and Spades are black
     */
    @Test
    void getColor() {
        assertEquals("Red", aceHearts.getColor(), "Hearts should be red");
        assertEquals("Red", jackDiamonds.getColor(), "Diamonds should be red");
        assertEquals("Black", kingSpades.getColor(), "Spades should be black");
        assertEquals("Black", queenClubs.getColor(), "Clubs should be black");
    }

    /**
     * Tests invalid card creation.
     * Verifies that appropriate exceptions are thrown for:
     * - Null or empty suit/rank
     * - Invalid suit/rank values
     * - Negative card values
     */
    @Test
    void testInvalidInputs() {
        // Test null and empty inputs
        assertThrows(IllegalArgumentException.class, () -> new Card(null, "A", 11),
            "Null suit should throw IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> new Card("", "A", 11),
            "Empty suit should throw IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> new Card("Hearts", null, 11),
            "Null rank should throw IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> new Card("Hearts", "", 11),
            "Empty rank should throw IllegalArgumentException");

        // Test invalid values
        assertThrows(IllegalArgumentException.class, () -> new Card("InvalidSuit", "A", 11),
            "Invalid suit should throw IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> new Card("Hearts", "InvalidRank", 11),
            "Invalid rank should throw IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> new Card("Hearts", "A", -1),
            "Negative value should throw IllegalArgumentException");
    }

    /**
     * Tests the equals method.
     * Verifies that:
     * - Identical cards are equal
     * - Different cards are not equal
     * - A card equals itself
     * - A card does not equal null or objects of different types
     * - Cards with different attributes (suit, rank, value) are not equal
     */
    @Test
    void testEquals() {
        // Test identical cards
        assertEquals(aceHearts, aceHearts2, "Identical cards should be equal");
        assertEquals(aceHearts2, aceHearts, "Equality should be symmetric");

        // Test different cards
        assertNotEquals(aceHearts, kingSpades, "Different cards should not be equal");

        // Test self equality
        assertEquals(aceHearts, aceHearts, "Card should equal itself");

        // Test null and different types
        assertNotEquals(null, aceHearts, "Card should not equal null");
        assertNotEquals("Not a card", aceHearts, "Card should not equal a string");

        // Test cards with different attributes
        Card differentSuit = new Card("Spades", "A", 11);
        Card differentRank = new Card("Hearts", "K", 11);
        Card differentValue = new Card("Hearts", "A", 10);

        assertNotEquals(aceHearts, differentSuit, "Cards with different suits should not be equal");
        assertNotEquals(aceHearts, differentRank, "Cards with different ranks should not be equal");
        assertNotEquals(aceHearts, differentValue, "Cards with different values should not be equal");
    }

    /**
     * Tests the hashCode method.
     * Verifies that:
     * - Equal cards have equal hash codes
     * - Different cards have different hash codes
     * - The same card returns consistent hash codes
     */
    @Test
    void testHashCode() {
        // Equal cards should have equal hash codes
        assertEquals(aceHearts.hashCode(), aceHearts2.hashCode(), "Equal cards should have equal hash codes");

        // Different cards should have different hash codes
        assertNotEquals(aceHearts.hashCode(), kingSpades.hashCode(), "Different cards should have different hash codes");

        // Same card should always return same hash code
        int hash1 = aceHearts.hashCode();
        int hash2 = aceHearts.hashCode();
        assertEquals(hash1, hash2, "Same card should have consistent hash code");
    }

    /**
     * Tests the toString method.
     * Verifies that the string representation includes all relevant card information.
     */
    @Test
    void testToString() {
        assertEquals("A of Hearts (11)", aceHearts.toString(), "Should show rank, suit, and value");
        assertEquals("K of Spades (10)", kingSpades.toString(), "Should show rank, suit, and value");
        assertEquals("7 of Hearts (7)", sevenHearts.toString(), "Should show rank, suit, and value");
    }
} 