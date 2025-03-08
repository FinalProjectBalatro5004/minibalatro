package com.balatro.model;

/**
 * Enum representing the rarity levels of joker cards.
 */
public enum RarityType {
    COMMON("Common"),
    UNCOMMON("Uncommon"),
    RARE("Rare"),
    LEGENDARY("Legendary");
    
    private final String displayName;
    
    RarityType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 