package com.balatro.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 * Test class for RarityType enum.
 * Tests the functionality of joker rarity levels in the Balatro game.
 */
class RarityTypeTest {

    /**
     * Tests that all rarity types have the correct display names.
     */
    @Test
    void testDisplayNames() {
        assertEquals("Common", RarityType.COMMON.getDisplayName(), 
            "COMMON should have display name 'Common'");
        assertEquals("Uncommon", RarityType.UNCOMMON.getDisplayName(), 
            "UNCOMMON should have display name 'Uncommon'");
        assertEquals("Rare", RarityType.RARE.getDisplayName(), 
            "RARE should have display name 'Rare'");
        assertEquals("Legendary", RarityType.LEGENDARY.getDisplayName(), 
            "LEGENDARY should have display name 'Legendary'");
    }

    /**
     * Tests that all enum values can be retrieved.
     */
    @Test
    void testEnumValues() {
        RarityType[] values = RarityType.values();
        assertEquals(4, values.length, "There should be 4 rarity types");
        assertEquals(RarityType.COMMON, values[0], "First value should be COMMON");
        assertEquals(RarityType.UNCOMMON, values[1], "Second value should be UNCOMMON");
        assertEquals(RarityType.RARE, values[2], "Third value should be RARE");
        assertEquals(RarityType.LEGENDARY, values[3], "Fourth value should be LEGENDARY");
    }

    /**
     * Tests valueOf method for retrieving enum constants by name.
     */
    @Test
    void testValueOf() {
        assertEquals(RarityType.COMMON, RarityType.valueOf("COMMON"), 
            "Should retrieve COMMON by name");
        assertEquals(RarityType.UNCOMMON, RarityType.valueOf("UNCOMMON"), 
            "Should retrieve UNCOMMON by name");
        assertEquals(RarityType.RARE, RarityType.valueOf("RARE"), 
            "Should retrieve RARE by name");
        assertEquals(RarityType.LEGENDARY, RarityType.valueOf("LEGENDARY"), 
            "Should retrieve LEGENDARY by name");
    }

    /**
     * Tests that display names are not null or empty.
     */
    @Test
    void testDisplayNamesNotEmpty() {
        for (RarityType type : RarityType.values()) {
            assertNotNull(type.getDisplayName(), "Display name should not be null");
            assert !type.getDisplayName().isEmpty() : "Display name should not be empty";
        }
    }

    /**
     * Tests the relative ordering of rarity types.
     * In games, rarity types typically have a natural ordering from common to legendary.
     */
    @Test
    void testRarityOrdering() {
        // Verify that the enum constants are defined in ascending order of rarity
        RarityType[] values = RarityType.values();
        assertEquals(RarityType.COMMON, values[0], "COMMON should be the least rare");
        assertEquals(RarityType.UNCOMMON, values[1], "UNCOMMON should be rarer than COMMON");
        assertEquals(RarityType.RARE, values[2], "RARE should be rarer than UNCOMMON");
        assertEquals(RarityType.LEGENDARY, values[3], "LEGENDARY should be the rarest");
    }
} 