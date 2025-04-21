package com.balatro.controller;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.balatro.service.GameStateManager;

/**
 * Test class for the GameController.
 * Tests the REST endpoints and their responses.
 */
@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    @Mock
    private GameStateManager gameStateManager;

    @InjectMocks
    private GameController gameController;

    /**
     * Sets up the test fixtures before each test method.
     */
    @BeforeEach
    void setUp() {
        // No additional setup needed as we're using Mockito annotations
    }

    /**
     * Tests the getGameStages endpoint.
     * Verifies that:
     * - The correct number of stages is returned
     * - Each stage has the required properties
     * - The stages are correctly organized by level
     */
    @Test
    void testGetGameStages() {
        // Execute the endpoint method
        List<Map<String, Object>> stages = gameController.getGameStages();
        
        // Verify the response
        assertNotNull(stages, "The stages list should not be null");
        assertEquals(9, stages.size(), "There should be 9 game stages");
        
        // Check a few specific stages
        Map<String, Object> smallBlind = stages.stream()
                .filter(stage -> stage.get("id").equals("SMALL_BLIND"))
                .findFirst()
                .orElse(null);
        
        assertNotNull(smallBlind, "SMALL_BLIND stage should exist");
        assertEquals("Small Blind", smallBlind.get("displayName"), "Display name should be correct");
        assertEquals(300, smallBlind.get("targetScore"), "Target score should be correct");
        assertEquals(1, smallBlind.get("level"), "Level should be correct");
        assertEquals(5, smallBlind.get("ante"), "Ante should be correct");
        
        // Check a level 2 stage
        Map<String, Object> bigBlindL2 = stages.stream()
                .filter(stage -> stage.get("id").equals("BIG_BLIND_L2"))
                .findFirst()
                .orElse(null);
        
        assertNotNull(bigBlindL2, "BIG_BLIND_L2 stage should exist");
        assertEquals("Big Blind L2", bigBlindL2.get("displayName"), "Display name should be correct");
        assertEquals(1200, bigBlindL2.get("targetScore"), "Target score should be correct");
        assertEquals(2, bigBlindL2.get("level"), "Level should be correct");
        assertEquals(25, bigBlindL2.get("ante"), "Ante should be correct");
        
        // Count stages by level
        long level1Stages = stages.stream()
                .filter(stage -> (int)stage.get("level") == 1)
                .count();
        long level2Stages = stages.stream()
                .filter(stage -> (int)stage.get("level") == 2)
                .count();
        long level3Stages = stages.stream()
                .filter(stage -> (int)stage.get("level") == 3)
                .count();
        
        assertEquals(3, level1Stages, "There should be 3 level 1 stages");
        assertEquals(3, level2Stages, "There should be 3 level 2 stages");
        assertEquals(3, level3Stages, "There should be 3 level 3 stages");
    }

    /**
     * Tests the getStageTransitionRules endpoint.
     * Verifies that:
     * - The response includes the correct reset behavior
     * - The transitions list contains the expected number of entries
     * - The level changes trigger score resets
     */
    @Test
    void testGetStageTransitionRules() {
        // Execute the endpoint method
        Map<String, Object> rules = gameController.getStageTransitionRules();
        
        // Verify the response
        assertNotNull(rules, "The rules map should not be null");
        assertTrue((boolean)rules.get("resetScoreOnLevelAdvance"), "Score should reset on level advance");
        
        // Check transitions
        List<Map<String, Object>> transitions = (List<Map<String, Object>>)rules.get("transitions");
        assertNotNull(transitions, "Transitions list should not be null");
        assertEquals(8, transitions.size(), "There should be 8 transitions");
        
        // Check a level change transition (should reset score)
        Map<String, Object> levelChangeTransition = transitions.stream()
                .filter(t -> t.get("fromStage").equals("THE_HOOK") && t.get("toStage").equals("SMALL_BLIND_L2"))
                .findFirst()
                .orElse(null);
        
        assertNotNull(levelChangeTransition, "Level change transition should exist");
        assertTrue((boolean)levelChangeTransition.get("resetScore"), "Score should reset when changing levels");
        
        // Check a within-level transition (should not reset score)
        Map<String, Object> withinLevelTransition = transitions.stream()
                .filter(t -> t.get("fromStage").equals("SMALL_BLIND") && t.get("toStage").equals("BIG_BLIND"))
                .findFirst()
                .orElse(null);
        
        assertNotNull(withinLevelTransition, "Within-level transition should exist");
        assertFalse((boolean)withinLevelTransition.get("resetScore"), "Score should not reset within same level");
    }
} 