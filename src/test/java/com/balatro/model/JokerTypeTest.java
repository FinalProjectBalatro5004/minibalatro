package com.balatro.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

/**
 * Test class for JokerType enum.
 * Tests the functionality of joker card types in the Balatro game.
 */
class JokerTypeTest {

    /**
     * Tests that all joker types have the correct names.
     */
    @Test
    void testNames() {
        assertEquals("Joker", JokerType.STANDARD_JOKER.getName(), 
            "STANDARD_JOKER should have correct name");
        assertEquals("Greedy Joker", JokerType.GREEDY_JOKER.getName(), 
            "GREEDY_JOKER should have correct name");
        assertEquals("Lusty Joker", JokerType.LUSTY_JOKER.getName(), 
            "LUSTY_JOKER should have correct name");
        assertEquals("Wrathful Joker", JokerType.WRATHFUL_JOKER.getName(), 
            "WRATHFUL_JOKER should have correct name");
        assertEquals("Gluttonous Joker", JokerType.GLUTTONOUS_JOKER.getName(), 
            "GLUTTONOUS_JOKER should have correct name");
        assertEquals("Scary Face", JokerType.SCARY_FACE.getName(), 
            "SCARY_FACE should have correct name");
        assertEquals("Fibonacci", JokerType.FIBONACCI.getName(), 
            "FIBONACCI should have correct name");
    }

    /**
     * Tests that all joker types have the correct effects.
     */
    @Test
    void testEffects() {
        assertEquals("Basic joker that adds +4 multiplier", JokerType.STANDARD_JOKER.getEffect(), 
            "STANDARD_JOKER should have correct effect");
        assertEquals("Played cards with Diamond suit give +3 Mult when scored", JokerType.GREEDY_JOKER.getEffect(), 
            "GREEDY_JOKER should have correct effect");
        assertEquals("Played cards with Heart suit give +3 Mult when scored", JokerType.LUSTY_JOKER.getEffect(), 
            "LUSTY_JOKER should have correct effect");
        assertEquals("Played cards with Spade suit give +3 Mult when scored", JokerType.WRATHFUL_JOKER.getEffect(), 
            "WRATHFUL_JOKER should have correct effect");
        assertEquals("Played cards with Club suit give +3 Mult when scored", JokerType.GLUTTONOUS_JOKER.getEffect(), 
            "GLUTTONOUS_JOKER should have correct effect");
        assertEquals("Played face cards give +30 Chips when scored", JokerType.SCARY_FACE.getEffect(), 
            "SCARY_FACE should have correct effect");
        assertEquals("Adds Fibonacci sequence multiplier (1,1,2,3,5,8,13,21)", JokerType.FIBONACCI.getEffect(), 
            "FIBONACCI should have correct effect");
    }

    /**
     * Tests that all joker types have the correct multipliers.
     */
    @Test
    void testMultipliers() {
        assertEquals(4, JokerType.STANDARD_JOKER.getMultiplier(), 
            "STANDARD_JOKER should have multiplier of 4");
        assertEquals(3, JokerType.GREEDY_JOKER.getMultiplier(), 
            "GREEDY_JOKER should have multiplier of 3");
        assertEquals(3, JokerType.LUSTY_JOKER.getMultiplier(), 
            "LUSTY_JOKER should have multiplier of 3");
        assertEquals(3, JokerType.WRATHFUL_JOKER.getMultiplier(), 
            "WRATHFUL_JOKER should have multiplier of 3");
        assertEquals(3, JokerType.GLUTTONOUS_JOKER.getMultiplier(), 
            "GLUTTONOUS_JOKER should have multiplier of 3");
        assertEquals(0, JokerType.SCARY_FACE.getMultiplier(), 
            "SCARY_FACE should have multiplier of 0");
        assertEquals(0, JokerType.FIBONACCI.getMultiplier(), 
            "FIBONACCI should have multiplier of 0");
    }

    /**
     * Tests that all joker types have the correct costs.
     */
    @Test
    void testCosts() {
        assertEquals(2, JokerType.STANDARD_JOKER.getCost(), 
            "STANDARD_JOKER should have cost of 2");
        assertEquals(5, JokerType.GREEDY_JOKER.getCost(), 
            "GREEDY_JOKER should have cost of 5");
        assertEquals(5, JokerType.LUSTY_JOKER.getCost(), 
            "LUSTY_JOKER should have cost of 5");
        assertEquals(5, JokerType.WRATHFUL_JOKER.getCost(), 
            "WRATHFUL_JOKER should have cost of 5");
        assertEquals(5, JokerType.GLUTTONOUS_JOKER.getCost(), 
            "GLUTTONOUS_JOKER should have cost of 5");
        assertEquals(4, JokerType.SCARY_FACE.getCost(), 
            "SCARY_FACE should have cost of 4");
        assertEquals(8, JokerType.FIBONACCI.getCost(), 
            "FIBONACCI should have cost of 8");
    }

    /**
     * Tests that all joker types have the correct active suits.
     */
    @Test
    void testActiveSuits() {
        assertNull(JokerType.STANDARD_JOKER.getActiveSuit(), 
            "STANDARD_JOKER should have null active suit");
        assertEquals("Diamonds", JokerType.GREEDY_JOKER.getActiveSuit(), 
            "GREEDY_JOKER should have Diamonds active suit");
        assertEquals("Hearts", JokerType.LUSTY_JOKER.getActiveSuit(), 
            "LUSTY_JOKER should have Hearts active suit");
        assertEquals("Spades", JokerType.WRATHFUL_JOKER.getActiveSuit(), 
            "WRATHFUL_JOKER should have Spades active suit");
        assertEquals("Clubs", JokerType.GLUTTONOUS_JOKER.getActiveSuit(), 
            "GLUTTONOUS_JOKER should have Clubs active suit");
        assertNull(JokerType.SCARY_FACE.getActiveSuit(), 
            "SCARY_FACE should have null active suit");
        assertNull(JokerType.FIBONACCI.getActiveSuit(), 
            "FIBONACCI should have null active suit");
    }

    /**
     * Tests that all joker types have the correct rarity types.
     */
    @Test
    void testRarityTypes() {
        assertEquals(RarityType.COMMON, JokerType.STANDARD_JOKER.getRarity(), 
            "STANDARD_JOKER should have COMMON rarity");
        assertEquals(RarityType.COMMON, JokerType.GREEDY_JOKER.getRarity(), 
            "GREEDY_JOKER should have COMMON rarity");
        assertEquals(RarityType.COMMON, JokerType.LUSTY_JOKER.getRarity(), 
            "LUSTY_JOKER should have COMMON rarity");
        assertEquals(RarityType.COMMON, JokerType.WRATHFUL_JOKER.getRarity(), 
            "WRATHFUL_JOKER should have COMMON rarity");
        assertEquals(RarityType.COMMON, JokerType.GLUTTONOUS_JOKER.getRarity(), 
            "GLUTTONOUS_JOKER should have COMMON rarity");
        assertEquals(RarityType.COMMON, JokerType.SCARY_FACE.getRarity(), 
            "SCARY_FACE should have COMMON rarity");
        assertEquals(RarityType.RARE, JokerType.FIBONACCI.getRarity(), 
            "FIBONACCI should have RARE rarity");
    }

    /**
     * Tests that all joker types have the correct activation types.
     */
    @Test
    void testActivationTypes() {
        assertEquals(ActivationType.INDEPENDENT, JokerType.STANDARD_JOKER.getActivationType(), 
            "STANDARD_JOKER should have INDEPENDENT activation type");
        assertEquals(ActivationType.ON_SCORED, JokerType.GREEDY_JOKER.getActivationType(), 
            "GREEDY_JOKER should have ON_SCORED activation type");
        assertEquals(ActivationType.ON_SCORED, JokerType.LUSTY_JOKER.getActivationType(), 
            "LUSTY_JOKER should have ON_SCORED activation type");
        assertEquals(ActivationType.ON_SCORED, JokerType.WRATHFUL_JOKER.getActivationType(), 
            "WRATHFUL_JOKER should have ON_SCORED activation type");
        assertEquals(ActivationType.ON_SCORED, JokerType.GLUTTONOUS_JOKER.getActivationType(), 
            "GLUTTONOUS_JOKER should have ON_SCORED activation type");
        assertEquals(ActivationType.ON_SCORED, JokerType.SCARY_FACE.getActivationType(), 
            "SCARY_FACE should have ON_SCORED activation type");
        assertEquals(ActivationType.INDEPENDENT, JokerType.FIBONACCI.getActivationType(), 
            "FIBONACCI should have INDEPENDENT activation type");
    }

    /**
     * Tests that all joker types have the correct unlock requirements.
     * In Balatro, all Joker cards are available for purchase after completing the first round,
     * and players need to use chips earned from winning rounds to buy them.
     */
    @Test
    void testUnlockRequirements() {
        // Basic jokers are available after first round
        assertEquals("Available after first round with sufficient chips.", JokerType.STANDARD_JOKER.getUnlockRequirement(), 
            "STANDARD_JOKER should be available after first round");
        assertEquals("Available after first round with sufficient chips.", JokerType.GREEDY_JOKER.getUnlockRequirement(), 
            "GREEDY_JOKER should be available after first round");
        assertEquals("Available after first round with sufficient chips.", JokerType.LUSTY_JOKER.getUnlockRequirement(), 
            "LUSTY_JOKER should be available after first round");
        assertEquals("Available after first round with sufficient chips.", JokerType.WRATHFUL_JOKER.getUnlockRequirement(), 
            "WRATHFUL_JOKER should be available after first round");
        assertEquals("Available after first round with sufficient chips.", JokerType.GLUTTONOUS_JOKER.getUnlockRequirement(), 
            "GLUTTONOUS_JOKER should be available after first round");
        assertEquals("Available after first round with sufficient chips.", JokerType.SCARY_FACE.getUnlockRequirement(), 
            "SCARY_FACE should be available after first round");
        
        // Special jokers have additional requirements
        assertEquals("Win with a Straight Flush and have sufficient chips.", JokerType.FIBONACCI.getUnlockRequirement(), 
            "FIBONACCI should require winning with a Straight Flush and having chips");
    }

    /**
     * Tests that all enum values can be retrieved.
     */
    @Test
    void testEnumValues() {
        JokerType[] values = JokerType.values();
        assertEquals(7, values.length, "There should be 7 joker types");
        assertEquals(JokerType.STANDARD_JOKER, values[0], "First value should be STANDARD_JOKER");
        assertEquals(JokerType.GREEDY_JOKER, values[1], "Second value should be GREEDY_JOKER");
        assertEquals(JokerType.LUSTY_JOKER, values[2], "Third value should be LUSTY_JOKER");
        assertEquals(JokerType.WRATHFUL_JOKER, values[3], "Fourth value should be WRATHFUL_JOKER");
        assertEquals(JokerType.GLUTTONOUS_JOKER, values[4], "Fifth value should be GLUTTONOUS_JOKER");
        assertEquals(JokerType.SCARY_FACE, values[5], "Sixth value should be SCARY_FACE");
        assertEquals(JokerType.FIBONACCI, values[6], "Seventh value should be FIBONACCI");
    }

    /**
     * Tests valueOf method for retrieving enum constants by name.
     */
    @Test
    void testValueOf() {
        assertEquals(JokerType.STANDARD_JOKER, JokerType.valueOf("STANDARD_JOKER"), 
            "Should retrieve STANDARD_JOKER by name");
        assertEquals(JokerType.GREEDY_JOKER, JokerType.valueOf("GREEDY_JOKER"), 
            "Should retrieve GREEDY_JOKER by name");
        assertEquals(JokerType.LUSTY_JOKER, JokerType.valueOf("LUSTY_JOKER"), 
            "Should retrieve LUSTY_JOKER by name");
        assertEquals(JokerType.WRATHFUL_JOKER, JokerType.valueOf("WRATHFUL_JOKER"), 
            "Should retrieve WRATHFUL_JOKER by name");
        assertEquals(JokerType.GLUTTONOUS_JOKER, JokerType.valueOf("GLUTTONOUS_JOKER"), 
            "Should retrieve GLUTTONOUS_JOKER by name");
        assertEquals(JokerType.SCARY_FACE, JokerType.valueOf("SCARY_FACE"), 
            "Should retrieve SCARY_FACE by name");
        assertEquals(JokerType.FIBONACCI, JokerType.valueOf("FIBONACCI"), 
            "Should retrieve FIBONACCI by name");
    }

    /**
     * Tests that all properties are not null.
     */
    @Test
    void testPropertiesNotNull() {
        for (JokerType type : JokerType.values()) {
            assertNotNull(type.getName(), "Name should not be null");
            assertNotNull(type.getEffect(), "Effect should not be null");
            assertNotNull(type.getRarity(), "Rarity should not be null");
            assertNotNull(type.getActivationType(), "Activation type should not be null");
            assertNotNull(type.getUnlockRequirement(), "Unlock requirement should not be null");
            // Note: activeSuit can be null for some jokers
        }
    }
} 