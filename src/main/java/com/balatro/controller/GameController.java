package com.balatro.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.balatro.service.GameStateManager;

/**
 * REST Controller for game operations
 */
@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*") // For development - restrict in production
public class GameController {

    /**
     * Returns all game stages information from GameStateManager
     * 
     * @return List of game stages with their properties
     */
    @GetMapping("/stages")
    public List<Map<String, Object>> getGameStages() {
        List<Map<String, Object>> stages = new ArrayList<>();
        
        // Add all game stages from the enum
        for (GameStateManager.LevelStage stage : GameStateManager.LevelStage.values()) {
            Map<String, Object> stageInfo = new HashMap<>();
            stageInfo.put("id", stage.name());
            stageInfo.put("displayName", stage.getDisplayName());
            stageInfo.put("targetScore", stage.getTargetScore());
            
            // Determine level based on stage name
            int level = 1;
            if (stage.name().endsWith("L2")) {
                level = 2;
            } else if (stage.name().endsWith("L3")) {
                level = 3;
            }
            stageInfo.put("level", level);
            
            // Calculate ante (simplified approach - in production would come from GameStateManager)
            int ante = 5;
            if (stage.name().equals("BIG_BLIND")) ante = 10;
            if (stage.name().equals("THE_HOOK")) ante = 15;
            if (stage.name().equals("SMALL_BLIND_L2")) ante = 20;
            if (stage.name().equals("BIG_BLIND_L2")) ante = 25;
            if (stage.name().equals("THE_HOOK_L2")) ante = 30;
            if (stage.name().equals("SMALL_BLIND_L3")) ante = 40;
            if (stage.name().equals("BIG_BLIND_L3")) ante = 50;
            if (stage.name().equals("THE_HOOK_L3")) ante = 60;
            stageInfo.put("ante", ante);
            
            stages.add(stageInfo);
        }
        
        return stages;
    }
    
    /**
     * Returns game stage transition rules, including score reset behavior
     * 
     * @return Rules for game stage transitions
     */
    @GetMapping("/stage-transitions")
    public Map<String, Object> getStageTransitionRules() {
        Map<String, Object> rules = new HashMap<>();
        
        // Based on GameStateManager behavior, scores reset when advancing to a new level
        rules.put("resetScoreOnLevelAdvance", true);
        
        // Define transition points where score should reset
        // In our current GameStateManager, score resets when advancing to a new level
        List<Map<String, Object>> transitions = new ArrayList<>();
        
        // Add transitions that reset score (level changes)
        transitions.add(Map.of(
            "fromStage", "THE_HOOK",
            "toStage", "SMALL_BLIND_L2",
            "resetScore", true
        ));
        
        transitions.add(Map.of(
            "fromStage", "THE_HOOK_L2",
            "toStage", "SMALL_BLIND_L3",
            "resetScore", true
        ));
        
        // Add transitions within the same level that don't reset score
        transitions.add(Map.of(
            "fromStage", "SMALL_BLIND",
            "toStage", "BIG_BLIND",
            "resetScore", false
        ));
        
        transitions.add(Map.of(
            "fromStage", "BIG_BLIND",
            "toStage", "THE_HOOK",
            "resetScore", false
        ));
        
        transitions.add(Map.of(
            "fromStage", "SMALL_BLIND_L2",
            "toStage", "BIG_BLIND_L2",
            "resetScore", false
        ));
        
        transitions.add(Map.of(
            "fromStage", "BIG_BLIND_L2",
            "toStage", "THE_HOOK_L2",
            "resetScore", false
        ));
        
        transitions.add(Map.of(
            "fromStage", "SMALL_BLIND_L3",
            "toStage", "BIG_BLIND_L3",
            "resetScore", false
        ));
        
        transitions.add(Map.of(
            "fromStage", "BIG_BLIND_L3",
            "toStage", "THE_HOOK_L3",
            "resetScore", false
        ));
        
        rules.put("transitions", transitions);
        
        return rules;
    }
} 