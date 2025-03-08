package com.balatro.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 * Test class for ActivationType enum.
 * Tests the functionality of joker activation types in the Balatro game.
 */
class ActivationTypeTest {

    /**
     * Tests that all activation types have the correct display names.
     */
    @Test
    void testDisplayNames() {
        assertEquals("Indep.", ActivationType.INDEPENDENT.getDisplayName(), 
            "INDEPENDENT should have display name 'Indep.'");
        assertEquals("On Scored", ActivationType.ON_SCORED.getDisplayName(), 
            "ON_SCORED should have display name 'On Scored'");
    }

    /**
     * Tests that all enum values can be retrieved.
     */
    @Test
    void testEnumValues() {
        ActivationType[] values = ActivationType.values();
        assertEquals(2, values.length, "There should be 2 activation types");
        assertEquals(ActivationType.INDEPENDENT, values[0], "First value should be INDEPENDENT");
        assertEquals(ActivationType.ON_SCORED, values[1], "Second value should be ON_SCORED");
    }

    /**
     * Tests valueOf method for retrieving enum constants by name.
     */
    @Test
    void testValueOf() {
        assertEquals(ActivationType.INDEPENDENT, ActivationType.valueOf("INDEPENDENT"), 
            "Should retrieve INDEPENDENT by name");
        assertEquals(ActivationType.ON_SCORED, ActivationType.valueOf("ON_SCORED"), 
            "Should retrieve ON_SCORED by name");
    }

    /**
     * Tests that display names are not null or empty.
     */
    @Test
    void testDisplayNamesNotEmpty() {
        for (ActivationType type : ActivationType.values()) {
            assertNotNull(type.getDisplayName(), "Display name should not be null");
            assert !type.getDisplayName().isEmpty() : "Display name should not be empty";
        }
    }
} 