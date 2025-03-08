package com.balatro.model;
import java.util.Objects;
/**
 * Represents a Joker card in Balatro, which is a special card with unique effects.
 * Each Joker has a type, multiplier value, activation type, and rarity level.
 */
public class Joker {
    private final JokerType type;
    private final int multiplier;
    private final ActivationType activationType;
    private final RarityType rarityType;

    /**
     * Creates a new Joker with specified properties.
     *
     * @param type the type of Joker (e.g., STANDARD_JOKER, GREEDY_JOKER, etc.)
     * @param multiplier the multiplier value of this Joker
     * @param activationType when this Joker's effect activates
     * @param rarityType the rarity level of this Joker
     */
    public Joker(JokerType type, int multiplier, ActivationType activationType, RarityType rarityType) {
        if (type == null) {
            throw new IllegalArgumentException("Joker type cannot be null");
        }
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier cannot be negative");
        }
        if (activationType == null) {
            throw new IllegalArgumentException("Activation type cannot be null");
        }
        if (rarityType == null) {
            throw new IllegalArgumentException("Rarity type cannot be null");
        }

        this.type = type;
        this.multiplier = multiplier;
        this.activationType = activationType;
        this.rarityType = rarityType;
    }

    /**
     * Gets the type of this Joker.
     *
     * @return the Joker type
     */
    public JokerType getType() {
        return type;
    }

    /**
     * Gets the multiplier value of this Joker.
     *
     * @return the multiplier value
     */
    public int getMultiplier() {
        return multiplier;
    }

    /**
     * Gets when this Joker's effect activates.
     *
     * @return the activation type
     */
    public ActivationType getActivationType() {
        return activationType;
    }

    /**
     * Gets the rarity level of this Joker.
     *
     * @return the rarity type
     */
    public RarityType getRarityType() {
        return rarityType;
    }

    /**
     * This equals method checks if two Jokers are equal by comparing their type, multiplier, activation type, and rarity type.
     * @param obj the object to compare to
     * @return true if the Jokers are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Joker other = (Joker) obj;
        return multiplier == other.multiplier &&
               type == other.type &&
               activationType == other.activationType &&
               rarityType == other.rarityType;
    }

    /**
     * This hashCode method returns a hash code for the Joker object based on its type, multiplier, activation type, and rarity type.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, multiplier, activationType, rarityType);
    }

    /**
     * This toString method returns a string representation of the Joker object.
     * @return the string representation
     */
    @Override
    public String toString() {
        return String.format("Joker[type=%s, multiplier=%d, activationType=%s, rarityType=%s]",
                           type.getName(), multiplier, activationType, rarityType);
    }
} 