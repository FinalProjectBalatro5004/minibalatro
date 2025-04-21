package com.balatro.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ActivationTypeTest {

    @Test
    void testIndependent() {
        assertEquals("Indep.", ActivationType.INDEPENDENT.getDisplayName());
    }

    @Test
    void testOnScored() {
        assertEquals("On Scored", ActivationType.ON_SCORED.getDisplayName());
    }

    @Test
    void testActivationTypeValues() {
        ActivationType[] values = ActivationType.values();
        assertEquals(2, values.length);
        assertEquals(ActivationType.INDEPENDENT, values[0]);
        assertEquals(ActivationType.ON_SCORED, values[1]);
    }

    @Test
    void testActivationTypeValueOf() {
        assertEquals(ActivationType.INDEPENDENT, ActivationType.valueOf("INDEPENDENT"));
        assertEquals(ActivationType.ON_SCORED, ActivationType.valueOf("ON_SCORED"));
    }

    @Test
    void testActivationTypeOrder() {
        ActivationType[] values = ActivationType.values();
        assertEquals(ActivationType.INDEPENDENT, values[0]);
        assertEquals(ActivationType.ON_SCORED, values[1]);
    }
} 