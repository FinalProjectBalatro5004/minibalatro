package com.balatro.model;

/**
 * Enum representing when a joker's effect activates.
 * INDEPENDENT: The joker's effect activates independently of the score.
 * ON_SCORED: The joker's effect activates when a score is scored.
 */
public enum ActivationType {
    INDEPENDENT("Indep."),
    ON_SCORED("On Scored");
    
    private final String displayName;
    
    /**
     * Constructor for the ActivationType enum.
     * @param displayName The display name of the activation type.
     */
    ActivationType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Get the display name of the activation type.
     * @return The display name of the activation type.
     */
    public String getDisplayName() {
        return displayName;
    }
} 