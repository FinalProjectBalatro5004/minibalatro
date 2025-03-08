package com.balatro.model;

import java.util.Objects;

/**
 * Represents a standard playing card in the game.
 */
public class Card {
    private static final String[] VALID_SUITS = {"Hearts", "Diamonds", "Clubs", "Spades"};
    private static final String[] VALID_RANKS = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    
    private final String suit;
    private final String rank;
    private final int value;

    /**
     * Creates a new card with the specified suit, rank, and value.
     *
     * @param suit the suit of the card (Hearts, Diamonds, Clubs, Spades)
     * @param rank the rank of the card (A, 2-10, J, Q, K)
     * @param value the numerical value of the card
     * @throws IllegalArgumentException if suit or rank is invalid
     */
    public Card(String suit, String rank, int value) {
        validateSuit(suit);
        validateRank(rank);
        validateValue(value);
        
        this.suit = suit;
        this.rank = rank;
        this.value = value;
    }

    /**
     * Gets the suit of this card.
     *
     * @return the suit
     */
    public String getSuit() {
        return suit;
    }

    /**
     * Gets the rank of this card.
     *
     * @return the rank
     */
    public String getRank() {
        return rank;
    }

    /**
     * Gets the numerical value of this card.
     *
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * Checks if this card is a face card (J, Q, K).
     *
     * @return true if this is a face card, false otherwise
     */
    public boolean isFaceCard() {
        return rank.equals("J") || rank.equals("Q") || rank.equals("K");
    }

    /**
     * Gets the color of this card (Red for Hearts/Diamonds, Black for Clubs/Spades).
     * switch statement is used to check the suit and return the color
     * @return "Red" or "Black"
     * @throws IllegalStateException if the suit is invalid
     */
    public String getColor() {
        return switch (suit) {
            case "Hearts", "Diamonds" -> "Red";
            case "Clubs", "Spades" -> "Black";
            default -> throw new IllegalStateException("Invalid suit: " + suit);
        };
    }

    /**
     * Validates the suit of the card.
     * suit.trim() is used to remove any whitespace from the suit, isEmpty() is used to check if the suit is empty
     * @param suit the suit to validate
     * @throws IllegalArgumentException if the suit is invalid
     */
    private void validateSuit(String suit) {
        if (suit == null || suit.trim().isEmpty()) {
            throw new IllegalArgumentException("Suit cannot be null or empty");
        }
        //set default value of validSuit to false
        boolean validSuit = false;
        //loop through the valid suits and check if the suit is valid, if it is, set validSuit to true and break
        for (String validSuitValue : VALID_SUITS) {
            if (validSuitValue.equals(suit)) {
                validSuit = true;
                break;
            }
        }
        //if the suit is not valid, throw an exception
        if (!validSuit) {
            throw new IllegalArgumentException("Invalid suit: " + suit);
        }
    }
    /**
     * Validates the rank of the card.
     * rank.trim() is used to remove any whitespace from the rank, isEmpty() is used to check if the rank is empty
     * @param rank the rank to validate
     * @throws IllegalArgumentException if the rank is invalid
     */
    private void validateRank(String rank) {
        if (rank == null || rank.trim().isEmpty()) {
            throw new IllegalArgumentException("Rank cannot be null or empty");
        }
        boolean validRank = false;
        for (String validRankValue : VALID_RANKS) {
            if (validRankValue.equals(rank)) {
                validRank = true;
                break;
            }
        }
        if (!validRank) {
            throw new IllegalArgumentException("Invalid rank: " + rank);
        }
    }

    private void validateValue(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }
    }
    /**
     * Checks if this card is equal to another object.
     * @param obj the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Card other = (Card) obj;
        return value == other.value &&
               Objects.equals(suit, other.suit) &&
               Objects.equals(rank, other.rank);
    }
    /**
     * Returns the hash code of this card.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(suit, rank, value);
    }
    /**
     * Returns a string representation of this card.    
     * @return the string representation
     */
    @Override
    public String toString() {
        return String.format("%s of %s (%d)", rank, suit, value);
    }
}
