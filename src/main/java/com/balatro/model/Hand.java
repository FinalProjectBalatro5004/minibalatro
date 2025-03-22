package com.balatro.model;
// Importing java.util for the ArrayList, HashMap, and Collections classes.
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a poker hand in the game.
 * This class handles card collection and poker hand evaluation.
 */
public class Hand {
    // Constants for hand limits
    private static final int MIN_CARDS = 1;
    private static final int MAX_CARDS = 8;
    // Constants for the number of cards to play and discard
    private static final int MIN_CARDS_TO_PLAY = 1;
    private static final int MAX_CARDS_TO_PLAY = 5;
    private static final int MIN_CARDS_TO_DISCARD = 1;
    private static final int MAX_CARDS_TO_DISCARD = 5;

    // Creating a list of cards to store the cards in the hand.
    private final List<Card> cards;
    // Creating a hand type to store the type of hand.
    private HandType handType;
    // Creating a base score to store the base score of the hand.
    private int baseScore;
    // Creating a multiplier to store the multiplier of the hand.
    private int multiplier;
    
    /**
     * Constructor for the Hand class.
     * @param cards: the cards in the hand.
     * @param handType: the type of hand. default is HIGH_CARD
     * @param baseScore: the base score of the hand. default is 0
     * @param multiplier: the multiplier of the hand. default is 1
     */
    public Hand() {
        this.cards = new ArrayList<>();
        this.handType = HandType.HIGH_CARD;
        this.baseScore = 0;
        this.multiplier = 1;
    }
    
    /**
     * Adds a card to this hand
     * 
     * @param card the card to add
     * @throws IllegalStateException if the hand already has maximum number of cards
     */
    public void addCard(Card card) {
        if (cards.size() >= MAX_CARDS) {
            throw new IllegalStateException("Cannot add more cards. Maximum hand size is " + MAX_CARDS);
        }
        cards.add(card);
        evaluateHand();
    }

    /**
     * Discards cards from the hand
     * 
     * @param cardsToDiscard the list of cards to discard
     * @throws IllegalArgumentException if number of cards to discard is invalid
     * @throws IllegalStateException if discarding would leave hand with less than minimum cards
     * @return the number of cards discarded
     */
    public int discardCards(List<Card> cardsToDiscard) {
        if (cardsToDiscard.size() < MIN_CARDS_TO_DISCARD || cardsToDiscard.size() > MAX_CARDS_TO_DISCARD) {
            throw new IllegalArgumentException("Must discard between " + MIN_CARDS_TO_DISCARD + 
                " and " + MAX_CARDS_TO_DISCARD + " cards");
        }
        
        if (cards.size() - cardsToDiscard.size() < MIN_CARDS) {
            throw new IllegalStateException("Cannot discard cards: would leave hand with less than minimum required cards");
        }
        
        int discardedCount = 0;
        for (Card card : cardsToDiscard) {
            if (cards.remove(card)) {
                discardedCount++;
            }
        }
        
        if (discardedCount > 0) {
            evaluateHand();
        }
        return discardedCount;
    }

    /**
     * Draws new cards to replace discarded cards
     * 
     * @param newCards the list of new cards to add
     * @throws IllegalArgumentException if number of new cards doesn't match discarded count
     */
    public void drawNewCards(List<Card> newCards) {
        if (newCards.size() < MIN_CARDS_TO_DISCARD || newCards.size() > MAX_CARDS_TO_DISCARD) {
            throw new IllegalArgumentException("Must draw between " + MIN_CARDS_TO_DISCARD + 
                " and " + MAX_CARDS_TO_DISCARD + " cards");
        }
        
        for (Card card : newCards) {
            addCard(card);
        }
    }
    
    /**
     * Initializes the hand with a set of cards at the beginning of the game.
     * This method bypasses the discard restrictions since it's used for initial setup.
     * 
     * @param initialCards the list of initial cards to add to the hand
     * @throws IllegalArgumentException if the number of cards exceeds MAX_CARDS
     */
    public void initializeHand(List<Card> initialCards) {
        if (initialCards.size() > MAX_CARDS) {
            throw new IllegalArgumentException("Cannot add more than " + MAX_CARDS + " cards to hand");
        }
        
        for (Card card : initialCards) {
            cards.add(card);
        }
        evaluateHand();
    }

    /**
     * Gets the minimum number of cards that must be played
     * @return the minimum number of cards to play
     */
    public static int getMinCardsToPlay() {
        return MIN_CARDS_TO_PLAY;
    }

    /**
     * Gets the maximum number of cards that can be played
     * @return the maximum number of cards to play
     */
    public static int getMaxCardsToPlay() {
        return MAX_CARDS_TO_PLAY;
    }

    /**
     * Gets the minimum number of cards that must be discarded
     * @return the minimum number of cards to discard
     */
    public static int getMinCardsToDiscard() {
        return MIN_CARDS_TO_DISCARD;
    }

    /**
     * Gets the maximum number of cards that can be discarded
     * @return the maximum number of cards to discard
     */
    public static int getMaxCardsToDiscard() {
        return MAX_CARDS_TO_DISCARD;
    }

    /**
     * Gets the maximum number of cards allowed in a hand
     * @return the maximum number of cards
     */
    public static int getMaxCards() {
        return MAX_CARDS;
    }

    /**
     * Gets the minimum number of cards allowed in a hand
     * @return the minimum number of cards
     */
    public static int getMinCards() {
        return MIN_CARDS;
    }
    
    /**
     * Checks if the player can get cards based on current hand size
     * 
     * @return true if player can get more cards, false otherwise
     */
    public boolean canGetCards() {
        return cards.size() < MAX_CARDS;
    }

    /**
     * Checks if the current hand is valid for playing
     * 
     * @return true if hand is valid (has at least one card), false otherwise
     */
    public boolean isValidHand() {
        return cards.size() >= MIN_CARDS;
    }

    /**
     * Gets the number of cards the player can still get
     * 
     * @return number of cards that can be added to hand
     */
    public int getRemainingCardSlots() {
        return MAX_CARDS - cards.size();
    }

    /**
     * Validates if the hand meets the minimum requirements for playing
     * 
     * @return true if hand meets minimum requirements, false otherwise
     */
    public boolean meetsMinimumRequirements() {
        return cards.size() >= MIN_CARDS_TO_PLAY;
    }

    /**
     * Validates if the hand meets the maximum requirements for playing
     * 
     * @return true if hand meets maximum requirements, false otherwise
     */
    public boolean meetsMaximumRequirements() {
        return cards.size() <= MAX_CARDS_TO_PLAY;
    }

    /**
     * Gets all cards in this hand
     * unmodifiableList is used to return an unmodifiable view of the cards to prevent modification of the cards list.
     * @return an unmodifiable view of the cards
     */
    public List<Card> getCards() {
        // Allow access to the cards even if empty
        return Collections.unmodifiableList(cards);
    }
    
    /**
     * Gets the mutable list of cards (for initialization purposes only)
     * @return the mutable list of cards
     */
    public List<Card> getMutableCards() {
        return cards;
    }
    
    /**
     * Gets the number of cards in this hand
     * 
     * @return the card count
     */
    public int getCardCount() {
        return cards.size();
    }
    
    /**
     * Gets the current hand type
     * 
     * @return the hand type
     */
    public HandType getHandType() {
        return handType;
    }
    
    /**
     * Gets the base score for this hand (before multipliers)
     * 
     * @return the base score
     */
    public int getBaseScore() {
        return baseScore;
    }

    /**
     * Gets the multiplier for this hand
     * 
     * @return the multiplier
     */
    public int getMultiplier() {
        return multiplier;
    }

    /**
     * Gets the total score for this hand based on Balatro scoring rules:
     * 1. Add the hand type base score (e.g., 35 for Flush)
     * 2. Add the sum of all card values (A=11, K/Q/J=10, others=face value)
     * 3. Multiply the combined total by the hand type multiplier
     * 
     * @return the total score
     */
    public int getTotalScore() {
        // Calculate the sum of all card values using point values (not rank values)
        int cardValuesSum = cards.stream()
                   .mapToInt(card -> getPointValue(card.getRank()))
                   .sum();
        
        // Add the hand type base score to the card values sum
        int combinedScore = baseScore + cardValuesSum;
        
        // Multiply by the hand type multiplier
        return combinedScore * multiplier;
    }
    
    /**
     * Evaluates the current hand to determine its type and base score
     * the type of hand is determined by the highest value of highest HandType 
     */
    public void evaluateHand() {
        // Modified logic to allow special combinations even with fewer than 5 cards
        if (cards.isEmpty()) {
            handType = HandType.HIGH_CARD;
            baseScore = calculateHighCardScore();
            multiplier = handType.getMultiplier();
            return;
        }
        
        if (cards.size() >= 5 && isStraightFlush()) {
            handType = HandType.STRAIGHT_FLUSH;
        } else if (isFourOfAKind()) {
            handType = HandType.FOUR_OF_A_KIND;
        } else if (cards.size() >= 5 && isFullHouse()) {
            handType = HandType.FULL_HOUSE;
        } else if (cards.size() >= 5 && isFlush()) {
            handType = HandType.FLUSH;
        } else if (cards.size() >= 5 && isStraight()) {
            handType = HandType.STRAIGHT;
        } else if (isThreeOfAKind()) {
            handType = HandType.THREE_OF_A_KIND;
        } else if (isTwoPair()) {
            handType = HandType.TWO_PAIR;
        } else if (isPair()) {
            handType = HandType.PAIR;
        } else {
            handType = HandType.HIGH_CARD;
        }
        
        baseScore = handType.getBaseScore();
        multiplier = handType.getMultiplier();
    }
    
    /**
     * Checks if the hand contains a straight flush
     * @return true if the hand contains a straight flush, false otherwise
     */
    private boolean isStraightFlush() {
        return isFlush() && isStraight();
    }
    
    /**
     * Checks if the hand contains four of a kind
     * @return true if the hand contains four of a kind, false otherwise.
     */
    private boolean isFourOfAKind() {
        Map<String, Integer> rankCounts = getRankCounts();
        return rankCounts.values().contains(4);
    }
    
    /**
     * Checks if the hand contains a full house
     * @return true if the hand contains a full house, false otherwise.
     */
    private boolean isFullHouse() {
        Map<String, Integer> rankCounts = getRankCounts();
        return rankCounts.values().contains(3) && rankCounts.values().contains(2);
    }
    
    /**
     * Checks if the hand contains a flush
     */
    private boolean isFlush() {
        if (cards.isEmpty()) return false;
        String suit = cards.get(0).getSuit();
        return cards.stream().allMatch(card -> card.getSuit().equals(suit));
    }
    
    /**
     * Checks if the hand contains a straight
     * if the hand has less than 5 cards, it cannot be a straight
     * the cards are sorted by rank and then checked for consecutive ranks
     * if the cards are not consecutive, the hand is not a straight
     * @return true if the hand contains a straight, false otherwise.
     */
    private boolean isStraight() {
        if (cards.size() < 5) return false;
        
        // Sort cards by rank
        List<Card> sortedCards = new ArrayList<>(cards);
        sortedCards.sort(Comparator.comparingInt(card -> getRankValue(card.getRank())));
        
        // Check for consecutive ranks
        for (int i = 0; i < sortedCards.size() - 1; i++) {
            int current = getRankValue(sortedCards.get(i).getRank());
            int next = getRankValue(sortedCards.get(i + 1).getRank());
            
            // Check if ranks are sequential
            // For a 5-card straight, we're checking 4 pairs of consecutive cards
            if (next - current != 1) {
                // Special case for A-10-J-Q-K straight where A can be low (1) or high (14)
                // But this would require rethinking our ranking system, so we'll leave it for now
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Checks if the hand contains three of a kind
     */
    private boolean isThreeOfAKind() {
        Map<String, Integer> rankCounts = getRankCounts();
        return rankCounts.values().contains(3);
    }
    
    /**
     * Checks if the hand contains two pair
     */
    private boolean isTwoPair() {
        Map<String, Integer> rankCounts = getRankCounts();
        int pairCount = 0;
        for (int count : rankCounts.values()) {
            if (count == 2) {
                pairCount++;
            }
        }
        return pairCount >= 2;
    }
    
    /**
     * Checks if the hand contains a pair
     * @return true if the hand contains a pair, false otherwise.
     */
    private boolean isPair() {
        Map<String, Integer> rankCounts = getRankCounts();
        return rankCounts.values().contains(2);
    }
    
    /**
     * Calculates the score for a high card hand
     * Find the highest point value card in the hand
     * @return the score for a high card hand
     */
    private int calculateHighCardScore() {
        return cards.stream()
                .mapToInt(card -> getPointValue(card.getRank()))
                .max()
                .orElse(0);
    }
    
    /**
     * Gets the numerical value of a card rank for straight evaluation
     * Returns the proper sequence values to detect straights:
     * 2=2, 3=3, 4=4, 5=5, 6=6, 7=7, 8=8, 9=9, 10=10, J=11, Q=12, K=13, A=14
     * @param rank the card rank
     * @return the sequential value for straight detection
     */
    private int getRankValue(String rank) {
        return switch (rank) {
            case "A" -> 14;  // Ace is highest (could be 1 or 14 but we use 14 for now)
            case "K" -> 13;
            case "Q" -> 12;
            case "J" -> 11;
            case "10" -> 10;
            default -> Integer.parseInt(rank);  // Number cards are worth their number
        };
    }
    
    /**
     * Gets the point value of a card for scoring purposes
     * A=11, K/Q/J=10, others=face value
     * @param rank the card rank
     * @return the point value
     */
    private int getPointValue(String rank) {
        return switch (rank) {
            case "A" -> 11;  // Ace is worth 11 points
            case "K", "Q", "J" -> 10;  // Face cards are worth 10 points
            default -> Integer.parseInt(rank);  // Number cards are worth their number
        };
    }
    
    /**
     * Counts occurrences of each rank in the hand.
     * Using a map to store the rank and the number of occurrences.
     * Entering a for loop to iterate through the cards in the hand.
     * Using the getRank() method to get the rank of the card.
     * Using the getOrDefault() method to get the number of occurrences of the rank.
     * Adding 1 to the number of occurrences.
     * Returning the map.
     * @return a map of the rank and the number of occurrences
     */
    private Map<String, Integer> getRankCounts() {
        Map<String, Integer> counts = new HashMap<>();
        for (Card card : cards) {
            counts.put(card.getRank(), counts.getOrDefault(card.getRank(), 0) + 1);
        }
        return counts;
    }
    
    /**
     * String representation of the hand
     * Using a StringBuilder to build the string representation of the hand.
     * Entering a for loop to iterate through the cards in the hand.
     * Using the toString() method to get the string representation of the card.
     * Adding the string representation of the card to the StringBuilder.
     * Adding a comma and space to the StringBuilder.
     * Returning the StringBuilder.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(handType.getDisplayName()).append(": ");
        
        for (Card card : cards) {
            sb.append(card).append(", ");
        }
        
        if (!cards.isEmpty()) {
            sb.setLength(sb.length() - 2); // Remove trailing comma and space
        }
        
        sb.append(" (Base Score: ").append(baseScore)
          .append(" × ").append(multiplier)
          .append(" = ").append(getTotalScore())
          .append(")");
        return sb.toString();
    }

    /**
     * Removes a specific card from the hand.
     *
     * @param card the card to remove
     * @return true if the card was successfully removed, false otherwise
     */
    public boolean removeCard(Card card) {
        return cards.remove(card);
    }
}