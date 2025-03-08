package com.balatro.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class for Joker implementation.
 * Tests the functionality of special Joker cards in the Balatro game.
 */
@DisplayName("Joker Tests")
class JokerTest {
    private Joker standardJoker;      // Basic joker with independent effect
    private Joker greedyJoker;        // Diamond suit joker
    private Joker lustyJoker;         // Heart suit joker
    private Joker standardJoker2;     // Identical to standardJoker for equality testing
    private Joker scaryFace;          // Face card joker
    private Joker fibonacci;          // Rare joker with special effect

    /**
     * Sets up the test fixtures before each test method.
     * Creates different types of Jokers with various attributes:
     * - Standard Joker: Basic joker with independent effect (Common)
     * - Greedy Joker: Diamond suit specific joker (Common)
     * - Lusty Joker: Heart suit specific joker (Common)
     * - Duplicate Standard Joker for equality testing (Common)
     * - Scary Face: Special joker for face cards (Common)
     * - Fibonacci: Special sequence joker (Rare)
     */
    @BeforeEach
    void setUp() {
        standardJoker = new Joker(JokerType.STANDARD_JOKER, 4, ActivationType.INDEPENDENT, RarityType.COMMON);
        greedyJoker = new Joker(JokerType.GREEDY_JOKER, 3, ActivationType.ON_SCORED, RarityType.COMMON);
        lustyJoker = new Joker(JokerType.LUSTY_JOKER, 3, ActivationType.ON_SCORED, RarityType.COMMON);
        standardJoker2 = new Joker(JokerType.STANDARD_JOKER, 4, ActivationType.INDEPENDENT, RarityType.COMMON);
        scaryFace = new Joker(JokerType.SCARY_FACE, 0, ActivationType.ON_SCORED, RarityType.COMMON);
        fibonacci = new Joker(JokerType.FIBONACCI, 0, ActivationType.INDEPENDENT, RarityType.RARE);
    }

    /**
     * Tests the getType method.
     * Verifies that jokers return their correct types.
     */
    @Test
    void getType() {
        assertEquals(JokerType.STANDARD_JOKER, standardJoker.getType(), "Should be Standard Joker type");
        assertEquals(JokerType.GREEDY_JOKER, greedyJoker.getType(), "Should be Greedy Joker type");
        assertEquals(JokerType.LUSTY_JOKER, lustyJoker.getType(), "Should be Lusty Joker type");
        assertEquals(JokerType.SCARY_FACE, scaryFace.getType(), "Should be Scary Face type");
        assertEquals(JokerType.FIBONACCI, fibonacci.getType(), "Should be Fibonacci type");
    }

    /**
     * Tests the getMultiplier method.
     * Verifies that jokers return their correct multiplier values.
     */
    @Test
    void getMultiplier() {
        assertEquals(4, standardJoker.getMultiplier(), "Standard Joker should have multiplier 4");
        assertEquals(3, greedyJoker.getMultiplier(), "Greedy Joker should have multiplier 3");
        assertEquals(3, lustyJoker.getMultiplier(), "Lusty Joker should have multiplier 3");
        assertEquals(0, scaryFace.getMultiplier(), "Scary Face should have multiplier 0");
        assertEquals(0, fibonacci.getMultiplier(), "Fibonacci should have base multiplier 0");
    }
    
    /**
     * Tests the getActivationType method.
     * Verifies that jokers return their correct activation types.
     */
    @Test
    void getActivationType() {
        assertEquals(ActivationType.INDEPENDENT, standardJoker.getActivationType(), 
            "Standard Joker should have independent activation");
        assertEquals(ActivationType.ON_SCORED, greedyJoker.getActivationType(), 
            "Greedy Joker should activate on scored");
        assertEquals(ActivationType.ON_SCORED, lustyJoker.getActivationType(), 
            "Lusty Joker should activate on scored");
        assertEquals(ActivationType.INDEPENDENT, fibonacci.getActivationType(), 
            "Fibonacci should have independent activation");
    }

    /**
     * Tests the getRarityType method.
     * Verifies that jokers return their correct rarity levels.
     */
    @Test
    void getRarityType() {
        assertEquals(RarityType.COMMON, standardJoker.getRarityType(), 
            "Standard Joker should be common rarity");
        assertEquals(RarityType.COMMON, greedyJoker.getRarityType(), 
            "Greedy Joker should be common rarity");
        assertEquals(RarityType.COMMON, scaryFace.getRarityType(), 
            "Scary Face should be common rarity");
        assertEquals(RarityType.RARE, fibonacci.getRarityType(), 
            "Fibonacci should be rare rarity");
    }

    /**
     * Tests invalid joker creation.
     * Verifies that appropriate exceptions are thrown for:
     * - Null type
     * - Negative multiplier
     * - Null activation type
     * - Null rarity type
     */
    @Test
    void testInvalidInputs() {
        // Test null type
        assertThrows(IllegalArgumentException.class,
            () -> new Joker(null, 4, ActivationType.INDEPENDENT, RarityType.COMMON),
            "Null type should throw IllegalArgumentException");

        // Test negative multiplier
        assertThrows(IllegalArgumentException.class,
            () -> new Joker(JokerType.STANDARD_JOKER, -1, ActivationType.INDEPENDENT, RarityType.COMMON),
            "Negative multiplier should throw IllegalArgumentException");

        // Test null activation type
        assertThrows(IllegalArgumentException.class,
            () -> new Joker(JokerType.STANDARD_JOKER, 4, null, RarityType.COMMON),
            "Null activation type should throw IllegalArgumentException");

        // Test null rarity type
        assertThrows(IllegalArgumentException.class,
            () -> new Joker(JokerType.STANDARD_JOKER, 4, ActivationType.INDEPENDENT, null),
            "Null rarity type should throw IllegalArgumentException");
    }

    /**
     * Tests the equals method.
     * Verifies that:
     * - Identical jokers are equal
     * - Different jokers are not equal
     * - A joker equals itself
     * - A joker does not equal null or objects of different types
     * - Jokers with different attributes are not equal
     */
    @Test
    void testEquals() {
        // Test identical jokers
        assertEquals(standardJoker, standardJoker2, "Identical jokers should be equal");
        assertEquals(standardJoker2, standardJoker, "Equality should be symmetric");

        // Test self equality
        assertEquals(standardJoker, standardJoker, "Joker should equal itself");

        // Test different jokers
        assertNotEquals(standardJoker, greedyJoker, "Different joker types should not be equal");
        assertNotEquals(standardJoker, lustyJoker, "Different joker types should not be equal");
        assertNotEquals(greedyJoker, lustyJoker, "Different joker types should not be equal");
        assertNotEquals(standardJoker, fibonacci, "Different rarity types should not be equal");

        // Test null and different types
        assertNotEquals(null, standardJoker, "Joker should not equal null");
        assertNotEquals("Not a joker", standardJoker, "Joker should not equal a string");

        // Test jokers with different attributes
        Joker differentActivation = new Joker(JokerType.STANDARD_JOKER, 4, ActivationType.ON_SCORED, RarityType.COMMON);
        assertNotEquals(standardJoker, differentActivation, "Different activation types should not be equal");

        Joker differentRarity = new Joker(JokerType.STANDARD_JOKER, 4, ActivationType.INDEPENDENT, RarityType.RARE);
        assertNotEquals(standardJoker, differentRarity, "Different rarity types should not be equal");
    }

    /**
     * Tests the hashCode method.
     * Verifies that:
     * - Equal jokers have equal hash codes
     * - Different jokers have different hash codes
     * - The same joker returns consistent hash codes
     */
    @Test
    void testHashCode() {
        // Equal jokers should have equal hash codes
        assertEquals(standardJoker.hashCode(), standardJoker2.hashCode(), 
            "Equal jokers should have equal hash codes");

        // Different jokers should have different hash codes
        assertNotEquals(standardJoker.hashCode(), greedyJoker.hashCode(), 
            "Different jokers should have different hash codes");
        assertNotEquals(greedyJoker.hashCode(), lustyJoker.hashCode(), 
            "Different jokers should have different hash codes");
        assertNotEquals(standardJoker.hashCode(), fibonacci.hashCode(), 
            "Jokers with different rarity should have different hash codes");

        // Same joker should always return same hash code
        int hash1 = standardJoker.hashCode();
        int hash2 = standardJoker.hashCode();
        assertEquals(hash1, hash2, "Same joker should have consistent hash code");
    }

    /**
     * Tests the toString method.
     * Verifies that the string representation includes all relevant joker information.
     */
    @Test
    void testToString() {
        assertEquals("Joker[type=Joker, multiplier=4, activationType=INDEPENDENT, rarityType=COMMON]", 
            standardJoker.toString(), "Should show type name, multiplier, activation type, and rarity");
        assertEquals("Joker[type=Greedy Joker, multiplier=3, activationType=ON_SCORED, rarityType=COMMON]", 
            greedyJoker.toString(), "Should show type name, multiplier, activation type, and rarity");
        assertEquals("Joker[type=Fibonacci, multiplier=0, activationType=INDEPENDENT, rarityType=RARE]", 
            fibonacci.toString(), "Should show type name, multiplier, activation type, and rarity");
    }
}