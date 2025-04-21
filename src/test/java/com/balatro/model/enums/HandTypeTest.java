package com.balatro.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HandTypeTest {

    @Test
    void testHighCard() {
        assertEquals("High Card", HandType.HIGH_CARD.getDisplayName());
        assertEquals(5, HandType.HIGH_CARD.getBaseScore());
        assertEquals(1, HandType.HIGH_CARD.getMultiplier());
    }

    @Test
    void testPair() {
        assertEquals("Pair", HandType.PAIR.getDisplayName());
        assertEquals(10, HandType.PAIR.getBaseScore());
        assertEquals(2, HandType.PAIR.getMultiplier());
    }

    @Test
    void testTwoPair() {
        assertEquals("Two Pair", HandType.TWO_PAIR.getDisplayName());
        assertEquals(20, HandType.TWO_PAIR.getBaseScore());
        assertEquals(2, HandType.TWO_PAIR.getMultiplier());
    }

    @Test
    void testThreeOfAKind() {
        assertEquals("Three of a Kind", HandType.THREE_OF_A_KIND.getDisplayName());
        assertEquals(30, HandType.THREE_OF_A_KIND.getBaseScore());
        assertEquals(3, HandType.THREE_OF_A_KIND.getMultiplier());
    }

    @Test
    void testStraight() {
        assertEquals("Straight", HandType.STRAIGHT.getDisplayName());
        assertEquals(30, HandType.STRAIGHT.getBaseScore());
        assertEquals(4, HandType.STRAIGHT.getMultiplier());
    }

    @Test
    void testFlush() {
        assertEquals("Flush", HandType.FLUSH.getDisplayName());
        assertEquals(35, HandType.FLUSH.getBaseScore());
        assertEquals(4, HandType.FLUSH.getMultiplier());
    }

    @Test
    void testFullHouse() {
        assertEquals("Full House", HandType.FULL_HOUSE.getDisplayName());
        assertEquals(40, HandType.FULL_HOUSE.getBaseScore());
        assertEquals(4, HandType.FULL_HOUSE.getMultiplier());
    }

    @Test
    void testFourOfAKind() {
        assertEquals("Four of a Kind", HandType.FOUR_OF_A_KIND.getDisplayName());
        assertEquals(60, HandType.FOUR_OF_A_KIND.getBaseScore());
        assertEquals(7, HandType.FOUR_OF_A_KIND.getMultiplier());
    }

    @Test
    void testStraightFlush() {
        assertEquals("Straight Flush", HandType.STRAIGHT_FLUSH.getDisplayName());
        assertEquals(100, HandType.STRAIGHT_FLUSH.getBaseScore());
        assertEquals(8, HandType.STRAIGHT_FLUSH.getMultiplier());
    }

    @Test
    void testHandTypeValues() {
        HandType[] values = HandType.values();
        assertEquals(9, values.length);
        assertEquals(HandType.HIGH_CARD, values[0]);
        assertEquals(HandType.STRAIGHT_FLUSH, values[values.length - 1]);
    }

    @Test
    void testHandTypeValueOf() {
        assertEquals(HandType.HIGH_CARD, HandType.valueOf("HIGH_CARD"));
        assertEquals(HandType.STRAIGHT_FLUSH, HandType.valueOf("STRAIGHT_FLUSH"));
    }
} 