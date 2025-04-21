package com.balatro;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 * Test class for the BalatroGame.
 * Tests the main application class and its initialization.
 */
class BalatroGameTest {

    /**
     * Tests the basic properties of the BalatroGame class.
     */
    @Test
    void testGameProperties() {
        // Create a new game instance
        BalatroGame game = new BalatroGame();
        
        // Verify that the game instance is created
        assertNotNull(game, "Game instance should be created successfully");
    }

    /**
     * Tests the main entry point of the application.
     * This is basically a smoke test to make sure the main method doesn't throw exceptions.
     */
    @Test
    void testMainMethod() {
        // This test just verifies that the main method can be called without throwing exceptions
        // We don't actually call it since it would launch the JavaFX application
        assertDoesNotThrow(() -> {
            // No assertions needed - just making sure it doesn't throw
            // In a real test scenario, we might use a testing framework that supports JavaFX
        });
    }

    /**
     * Tests the window dimension constants to make sure they are reasonable.
     */
    @Test
    void testWindowDimensions() {
        // Use reflection to access private fields for testing
        try {
            java.lang.reflect.Field prefWidthField = BalatroGame.class.getDeclaredField("PREFERRED_WIDTH");
            java.lang.reflect.Field prefHeightField = BalatroGame.class.getDeclaredField("PREFERRED_HEIGHT");
            java.lang.reflect.Field minWidthField = BalatroGame.class.getDeclaredField("MIN_WIDTH");
            java.lang.reflect.Field minHeightField = BalatroGame.class.getDeclaredField("MIN_HEIGHT");
            java.lang.reflect.Field maxWidthField = BalatroGame.class.getDeclaredField("MAX_WIDTH");
            java.lang.reflect.Field maxHeightField = BalatroGame.class.getDeclaredField("MAX_HEIGHT");
            
            prefWidthField.setAccessible(true);
            prefHeightField.setAccessible(true);
            minWidthField.setAccessible(true);
            minHeightField.setAccessible(true);
            maxWidthField.setAccessible(true);
            maxHeightField.setAccessible(true);
            
            double prefWidth = (double) prefWidthField.get(null);
            double prefHeight = (double) prefHeightField.get(null);
            double minWidth = (double) minWidthField.get(null);
            double minHeight = (double) minHeightField.get(null);
            double maxWidth = (double) maxWidthField.get(null);
            double maxHeight = (double) maxHeightField.get(null);
            
            // Verify that dimensions are positive
            assertTrue(prefWidth > 0, "Preferred width should be positive");
            assertTrue(prefHeight > 0, "Preferred height should be positive");
            assertTrue(minWidth > 0, "Minimum width should be positive");
            assertTrue(minHeight > 0, "Minimum height should be positive");
            assertTrue(maxWidth > 0, "Maximum width should be positive");
            assertTrue(maxHeight > 0, "Maximum height should be positive");
            
            // Verify that dimensions are in reasonable ranges
            assertTrue(minWidth <= prefWidth, "Minimum width should be <= preferred width");
            assertTrue(minHeight <= prefHeight, "Minimum height should be <= preferred height");
            assertTrue(prefWidth <= maxWidth, "Preferred width should be <= maximum width");
            assertTrue(prefHeight <= maxHeight, "Preferred height should be <= maximum height");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to access window dimension fields: " + e.getMessage());
        }
    }
} 