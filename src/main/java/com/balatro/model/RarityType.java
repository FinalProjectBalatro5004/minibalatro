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
    /**
     * Constructor for the RarityType enum.
     * @param displayName the display name of the rarity type
     */
    RarityType(String displayName) {
        this.displayName = displayName;
    }
    /** 
     * Getter for the display name of the rarity type.
     * @return the display name of the rarity type
     */
    public String getDisplayName() {
        return displayName;
    }
} 