package com.balatro.model;

/**
 * Enum representing different types of joker cards in the game.
 * Each joker type has its own properties and effects.
 */
public enum JokerType {
    // Basic Jokers
    STANDARD_JOKER("Joker", "Basic joker that adds +2 multiplier", 2, 2, null, 
            RarityType.COMMON, ActivationType.INDEPENDENT, "Available after first round with sufficient chips."),
    
    // Suit-specific Jokers 
    GREEDY_JOKER("Greedy Joker", "Played cards with Diamond suit give +3 Mult when scored", 3, 5,
            "Diamonds", RarityType.COMMON, ActivationType.ON_SCORED, "Available after first round with sufficient chips."),
    LUSTY_JOKER("Lusty Joker", "Played cards with Heart suit give +3 Mult when scored", 3, 5,
            "Hearts", RarityType.COMMON, ActivationType.ON_SCORED, "Available after first round with sufficient chips."),
    WRATHFUL_JOKER("Wrathful Joker", "Played cards with Spade suit give +3 Mult when scored", 3, 5,
            "Spades", RarityType.COMMON, ActivationType.ON_SCORED, "Available after first round with sufficient chips."),
    GLUTTONOUS_JOKER("Gluttonous Joker", "Played cards with Club suit give +3 Mult when scored", 3, 5,
            "Clubs", RarityType.COMMON, ActivationType.ON_SCORED, "Available after first round with sufficient chips."),
            
    // Advanced Jokers
    SCARY_FACE("Scary Face", "Played face cards give +30 Chips when scored", 0, 4, null,
            RarityType.COMMON, ActivationType.ON_SCORED, "Available after first round with sufficient chips."),
    FIBONACCI("Fibonacci", "Adds Fibonacci sequence multiplier (1,1,2,3,5,8,13,21)", 8, 8, null,
            RarityType.RARE, ActivationType.INDEPENDENT, "Win with a Straight Flush and have sufficient chips."),
    LUCKY_JOKER("Lucky Joker", "Adds +4 Mult if hand contains at least two 7s", 4, 6, null,
            RarityType.UNCOMMON, ActivationType.ON_SCORED, "Win with a hand containing at least two 7s.");

    // Properties
    private final String name;
    private final String effect;
    private final int multiplier;
    private final int cost;
    private final String activeSuit;
    private final RarityType rarity;
    private final ActivationType activationType;
    private final String unlockRequirement;

    // Constructor
    JokerType(String name, String effect, int multiplier, int cost,
              String activeSuit, RarityType rarity,
              ActivationType activationType, String unlockRequirement) {
        this.name = name;
        this.effect = effect;
        this.multiplier = multiplier;
        this.cost = cost;
        this.activeSuit = activeSuit;
        this.rarity = rarity;
        this.activationType = activationType;
        this.unlockRequirement = unlockRequirement;
    }

    // Getters
    public String getName() { return name; }
    public String getEffect() { return effect; }
    public int getMultiplier() { return multiplier; }
    public int getCost() { return cost; }
    public String getActiveSuit() { return activeSuit; }
    public RarityType getRarity() { return rarity; }
    public ActivationType getActivationType() { return activationType; }
    public String getUnlockRequirement() { return unlockRequirement; }
}
