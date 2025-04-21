package com.balatro.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RarityTypeTest {

    @Test
    void testCommon() {
        assertEquals("Common", RarityType.COMMON.getDisplayName());
    }

    @Test
    void testUncommon() {
        assertEquals("Uncommon", RarityType.UNCOMMON.getDisplayName());
    }

    @Test
    void testRare() {
        assertEquals("Rare", RarityType.RARE.getDisplayName());
    }

    @Test
    void testLegendary() {
        assertEquals("Legendary", RarityType.LEGENDARY.getDisplayName());
    }

    @Test
    void testRarityTypeValues() {
        RarityType[] values = RarityType.values();
        assertEquals(4, values.length);
        assertEquals(RarityType.COMMON, values[0]);
        assertEquals(RarityType.LEGENDARY, values[values.length - 1]);
    }

    @Test
    void testRarityTypeValueOf() {
        assertEquals(RarityType.COMMON, RarityType.valueOf("COMMON"));
        assertEquals(RarityType.LEGENDARY, RarityType.valueOf("LEGENDARY"));
    }

    @Test
    void testRarityTypeOrder() {
        RarityType[] values = RarityType.values();
        assertEquals(RarityType.COMMON, values[0]);
        assertEquals(RarityType.UNCOMMON, values[1]);
        assertEquals(RarityType.RARE, values[2]);
        assertEquals(RarityType.LEGENDARY, values[3]);
    }
} 