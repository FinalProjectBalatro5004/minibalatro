package com.balatro.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JokerTest {

    @Test
    void testJokerCreation() {
        Joker joker = new Joker(JokerType.STANDARD_JOKER, 2, ActivationType.INDEPENDENT, RarityType.COMMON);
        assertEquals(JokerType.STANDARD_JOKER, joker.getType());
        assertEquals(2, joker.getMultiplier());
        assertEquals(ActivationType.INDEPENDENT, joker.getActivationType());
        assertEquals(RarityType.COMMON, joker.getRarityType());
    }

    @Test
    void testJokerCreationWithNullType() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Joker(null, 2, ActivationType.INDEPENDENT, RarityType.COMMON);
        });
    }

    @Test
    void testJokerCreationWithNegativeMultiplier() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Joker(JokerType.STANDARD_JOKER, -1, ActivationType.INDEPENDENT, RarityType.COMMON);
        });
    }

    @Test
    void testJokerCreationWithNullActivationType() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Joker(JokerType.STANDARD_JOKER, 2, null, RarityType.COMMON);
        });
    }

    @Test
    void testJokerCreationWithNullRarityType() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Joker(JokerType.STANDARD_JOKER, 2, ActivationType.INDEPENDENT, null);
        });
    }

    @Test
    void testJokerEquals() {
        Joker joker1 = new Joker(JokerType.STANDARD_JOKER, 2, ActivationType.INDEPENDENT, RarityType.COMMON);
        Joker joker2 = new Joker(JokerType.STANDARD_JOKER, 2, ActivationType.INDEPENDENT, RarityType.COMMON);
        Joker joker3 = new Joker(JokerType.GREEDY_JOKER, 2, ActivationType.ON_SCORED, RarityType.COMMON);

        assertTrue(joker1.equals(joker2));
        assertFalse(joker1.equals(joker3));
        assertFalse(joker1.equals(null));
        assertTrue(joker1.equals(joker1));
    }

    @Test
    void testJokerHashCode() {
        Joker joker1 = new Joker(JokerType.STANDARD_JOKER, 2, ActivationType.INDEPENDENT, RarityType.COMMON);
        Joker joker2 = new Joker(JokerType.STANDARD_JOKER, 2, ActivationType.INDEPENDENT, RarityType.COMMON);
        Joker joker3 = new Joker(JokerType.GREEDY_JOKER, 2, ActivationType.ON_SCORED, RarityType.COMMON);

        assertEquals(joker1.hashCode(), joker2.hashCode());
        assertNotEquals(joker1.hashCode(), joker3.hashCode());
    }

    @Test
    void testJokerToString() {
        Joker joker = new Joker(JokerType.STANDARD_JOKER, 2, ActivationType.INDEPENDENT, RarityType.COMMON);
        String expected = "Joker[type=Joker, multiplier=2, activationType=INDEPENDENT, rarityType=COMMON]";
        assertEquals(expected, joker.toString());
    }

    @Test
    void testDifferentJokerTypes() {
        Joker standard = new Joker(JokerType.STANDARD_JOKER, 2, ActivationType.INDEPENDENT, RarityType.COMMON);
        Joker greedy = new Joker(JokerType.GREEDY_JOKER, 3, ActivationType.ON_SCORED, RarityType.RARE);
        Joker fibonacci = new Joker(JokerType.FIBONACCI, 4, ActivationType.INDEPENDENT, RarityType.LEGENDARY);

        assertEquals("Joker", standard.getType().getName());
        assertEquals("Greedy Joker", greedy.getType().getName());
        assertEquals("Fibonacci", fibonacci.getType().getName());
    }
} 