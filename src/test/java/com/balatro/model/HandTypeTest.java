package com.balatro.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Test class for HandType enum.
 * Tests the functionality of poker hand types in the Balatro game.
 */
class HandTypeTest {

    /**
     * Tests that all hand types have the correct display names.
     */
    @Test
    void testDisplayNames() {
        assertEquals("High Card", HandType.HIGH_CARD.getDisplayName(), "HIGH_CARD should have correct display name");
        assertEquals("Pair", HandType.PAIR.getDisplayName(), "PAIR should have correct display name");
        assertEquals("Two Pair", HandType.TWO_PAIR.getDisplayName(), "TWO_PAIR should have correct display name");
        assertEquals("Three of a Kind", HandType.THREE_OF_A_KIND.getDisplayName(), "THREE_OF_A_KIND should have correct display name");
        assertEquals("Straight", HandType.STRAIGHT.getDisplayName(), "STRAIGHT should have correct display name");
        assertEquals("Flush", HandType.FLUSH.getDisplayName(), "FLUSH should have correct display name");
        assertEquals("Full House", HandType.FULL_HOUSE.getDisplayName(), "FULL_HOUSE should have correct display name");
        assertEquals("Four of a Kind", HandType.FOUR_OF_A_KIND.getDisplayName(), "FOUR_OF_A_KIND should have correct display name");
        assertEquals("Straight Flush", HandType.STRAIGHT_FLUSH.getDisplayName(), "STRAIGHT_FLUSH should have correct display name");
    }

    /**
     * Tests that all hand types have the correct base scores.
     */
    @Test
    void testBaseScores() {
        assertEquals(5, HandType.HIGH_CARD.getBaseScore(), "HIGH_CARD should have base score of 5");
        assertEquals(10, HandType.PAIR.getBaseScore(), "PAIR should have base score of 10");
        assertEquals(20, HandType.TWO_PAIR.getBaseScore(), "TWO_PAIR should have base score of 20");
        assertEquals(30, HandType.THREE_OF_A_KIND.getBaseScore(), "THREE_OF_A_KIND should have base score of 30");
        assertEquals(30, HandType.STRAIGHT.getBaseScore(), "STRAIGHT should have base score of 30");
        assertEquals(35, HandType.FLUSH.getBaseScore(), "FLUSH should have base score of 35");
        assertEquals(40, HandType.FULL_HOUSE.getBaseScore(), "FULL_HOUSE should have base score of 40");
        assertEquals(60, HandType.FOUR_OF_A_KIND.getBaseScore(), "FOUR_OF_A_KIND should have base score of 60");
        assertEquals(100, HandType.STRAIGHT_FLUSH.getBaseScore(), "STRAIGHT_FLUSH should have base score of 100");
    }

    /**
     * Tests that all hand types have the correct multipliers.
     */
    @Test
    void testMultipliers() {
        assertEquals(1, HandType.HIGH_CARD.getMultiplier(), "HIGH_CARD should have multiplier of 1");
        assertEquals(2, HandType.PAIR.getMultiplier(), "PAIR should have multiplier of 2");
        assertEquals(2, HandType.TWO_PAIR.getMultiplier(), "TWO_PAIR should have multiplier of 2");
        assertEquals(3, HandType.THREE_OF_A_KIND.getMultiplier(), "THREE_OF_A_KIND should have multiplier of 3");
        assertEquals(4, HandType.STRAIGHT.getMultiplier(), "STRAIGHT should have multiplier of 4");
        assertEquals(4, HandType.FLUSH.getMultiplier(), "FLUSH should have multiplier of 4");
        assertEquals(4, HandType.FULL_HOUSE.getMultiplier(), "FULL_HOUSE should have multiplier of 4");
        assertEquals(7, HandType.FOUR_OF_A_KIND.getMultiplier(), "FOUR_OF_A_KIND should have multiplier of 7");
        assertEquals(8, HandType.STRAIGHT_FLUSH.getMultiplier(), "STRAIGHT_FLUSH should have multiplier of 8");
    }

    /**
     * Tests the relative ordering of hand types.
     * In poker, hand types have a natural ordering from weakest to strongest.
     */
    @Test
    void testHandTypeOrdering() {
        // Verify that the enum constants are defined in ascending order of strength
        HandType[] values = HandType.values();
        assertEquals(HandType.HIGH_CARD, values[0], "HIGH_CARD should be the weakest hand");
        assertEquals(HandType.PAIR, values[1], "PAIR should be stronger than HIGH_CARD");
        assertEquals(HandType.TWO_PAIR, values[2], "TWO_PAIR should be stronger than PAIR");
        assertEquals(HandType.THREE_OF_A_KIND, values[3], "THREE_OF_A_KIND should be stronger than TWO_PAIR");
        assertEquals(HandType.STRAIGHT, values[4], "STRAIGHT should be stronger than THREE_OF_A_KIND");
        assertEquals(HandType.FLUSH, values[5], "FLUSH should be stronger than STRAIGHT");
        assertEquals(HandType.FULL_HOUSE, values[6], "FULL_HOUSE should be stronger than FLUSH");
        assertEquals(HandType.FOUR_OF_A_KIND, values[7], "FOUR_OF_A_KIND should be stronger than FULL_HOUSE");
        assertEquals(HandType.STRAIGHT_FLUSH, values[8], "STRAIGHT_FLUSH should be the strongest hand");
    }
} 