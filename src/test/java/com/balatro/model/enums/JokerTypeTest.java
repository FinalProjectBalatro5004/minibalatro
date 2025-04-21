package com.balatro.model.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.balatro.model.JokerType;
import com.balatro.model.RarityType;
import com.balatro.model.ActivationType;

class JokerTypeTest {

    @Test
    void testStandardJoker() {
        assertEquals("Joker", JokerType.STANDARD_JOKER.getName());
        assertEquals("Basic joker that adds +2 multiplier", JokerType.STANDARD_JOKER.getEffect());
        assertEquals(2, JokerType.STANDARD_JOKER.getMultiplier());
        assertEquals(2, JokerType.STANDARD_JOKER.getCost());
        assertNull(JokerType.STANDARD_JOKER.getActiveSuit());
        assertEquals(RarityType.COMMON, JokerType.STANDARD_JOKER.getRarity());
        assertEquals(ActivationType.INDEPENDENT, JokerType.STANDARD_JOKER.getActivationType());
        assertEquals("Available after first round with sufficient chips.", JokerType.STANDARD_JOKER.getUnlockRequirement());
    }

    @Test
    void testGreedyJoker() {
        assertEquals("Greedy Joker", JokerType.GREEDY_JOKER.getName());
        assertEquals("Played cards with Diamond suit give +3 Mult when scored", JokerType.GREEDY_JOKER.getEffect());
        assertEquals(3, JokerType.GREEDY_JOKER.getMultiplier());
        assertEquals(5, JokerType.GREEDY_JOKER.getCost());
        assertEquals("Diamonds", JokerType.GREEDY_JOKER.getActiveSuit());
        assertEquals(RarityType.COMMON, JokerType.GREEDY_JOKER.getRarity());
        assertEquals(ActivationType.ON_SCORED, JokerType.GREEDY_JOKER.getActivationType());
        assertEquals("Available after first round with sufficient chips.", JokerType.GREEDY_JOKER.getUnlockRequirement());
    }

    @Test
    void testFibonacciJoker() {
        assertEquals("Fibonacci", JokerType.FIBONACCI.getName());
        assertEquals("Adds Fibonacci sequence multiplier (1,1,2,3,5,8,13,21)", JokerType.FIBONACCI.getEffect());
        assertEquals(8, JokerType.FIBONACCI.getMultiplier());
        assertEquals(8, JokerType.FIBONACCI.getCost());
        assertNull(JokerType.FIBONACCI.getActiveSuit());
        assertEquals(RarityType.RARE, JokerType.FIBONACCI.getRarity());
        assertEquals(ActivationType.INDEPENDENT, JokerType.FIBONACCI.getActivationType());
        assertEquals("Win with a Straight Flush and have sufficient chips.", JokerType.FIBONACCI.getUnlockRequirement());
    }

    @Test
    void testJokerTypeValues() {
        JokerType[] values = JokerType.values();
        assertEquals(8, values.length);
        assertEquals(JokerType.STANDARD_JOKER, values[0]);
        assertEquals(JokerType.FIBONACCI, values[values.length - 1]);
    }

    @Test
    void testJokerTypeValueOf() {
        assertEquals(JokerType.STANDARD_JOKER, JokerType.valueOf("STANDARD_JOKER"));
        assertEquals(JokerType.FIBONACCI, JokerType.valueOf("FIBONACCI"));
    }

    @Test
    void testSuitSpecificJokers() {
        assertEquals("Diamonds", JokerType.GREEDY_JOKER.getActiveSuit());
        assertEquals("Hearts", JokerType.LUSTY_JOKER.getActiveSuit());
        assertEquals("Spades", JokerType.WRATHFUL_JOKER.getActiveSuit());
        assertEquals("Clubs", JokerType.GLUTTONOUS_JOKER.getActiveSuit());
    }

    @Test
    void testScaryFaceJoker() {
        assertEquals("Scary Face", JokerType.SCARY_FACE.getName());
        assertEquals("Played face cards give +30 Chips when scored", JokerType.SCARY_FACE.getEffect());
        assertEquals(0, JokerType.SCARY_FACE.getMultiplier());
        assertEquals(4, JokerType.SCARY_FACE.getCost());
        assertNull(JokerType.SCARY_FACE.getActiveSuit());
        assertEquals(RarityType.COMMON, JokerType.SCARY_FACE.getRarity());
        assertEquals(ActivationType.ON_SCORED, JokerType.SCARY_FACE.getActivationType());
    }
} 