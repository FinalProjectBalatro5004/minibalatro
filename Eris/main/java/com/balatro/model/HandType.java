package com.balatro.model;

/**
 * Enum representing different types of poker hands
 * The enum is used to store the type of hand, display name, base score and multiplier.
 */
public enum HandType {
    HIGH_CARD("High Card", 5, 1),
    PAIR("Pair", 10, 2),
    TWO_PAIR("Two Pair", 20, 2),
    THREE_OF_A_KIND("Three of a Kind", 30, 3),
    STRAIGHT("Straight", 30, 4),
    FLUSH("Flush", 35, 4),
    FULL_HOUSE("Full House", 40, 4),
    FOUR_OF_A_KIND("Four of a Kind", 60, 7),
    STRAIGHT_FLUSH("Straight Flush", 100, 8);
    /*
     * Creating a private final string to store the display name of the hand.
     */
    private final String displayName;
    private final int baseScore;
    private final int multiplier;
    
    /**
     * Constructor for the HandType enum.
     * @param displayName the display name of the hand
     * @param baseScore the base score for this hand type
     * @param multiplier the multiplier for this hand type
     */
    HandType(String displayName, int baseScore, int multiplier) {
        this.displayName = displayName;
        this.baseScore = baseScore;
        this.multiplier = multiplier;
    }
    /**
     * Getter for the display name of the hand.
     * @return the display name of the hand.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the base score for this hand type.
     * @return the base score
     */
    public int getBaseScore() {
        return baseScore;
    }

    /**
     * Gets the multiplier for this hand type.
     * @return the multiplier
     */
    public int getMultiplier() {
        return multiplier;
    }
} 