package com.balatro.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class represents a deck of playing cards in the game.
 * It contains a list of cards and a random number generator.
 * It also contains methods to reset the deck, shuffle the deck, draw a card, peek at the top card, add a card, get the number of cards remaining in the deck, and get all cards in the deck.
 */
public class Deck {
    private List<Card> cards;
    private final Random random;
    private boolean isNewRound;  // Track if this is a new round
    private int currentScore;    // Track current score

    /**
     * This constructor creates a new standard deck of 52 cards.
     * In Balatro, deck is only shuffled at the start of each round.
     */
    public Deck() {
        this.cards = new ArrayList<>();
        this.random = new Random();
        this.isNewRound = true;  // First round starts with shuffle
        this.currentScore = 0;
        resetDeck();
        shuffle();  // Initial shuffle for first round
    }

    /**
     * This constructor creates a deck with the specified cards.
     * @new ArrayList<>(cards) is used to create a new ArrayList of the cards passed in
     * @new Random() is used to create a new Random object
     * @param cards the initial cards for the deck
     * @param random the random number generator to use
     * 
     * The purpose of this constructor is to create a deck with the specified cards.
     */
    public Deck(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
        this.random = new Random();
    }

    /**
     * This method creates a standard 52-card deck (without Jokers)
     * Define a suits and ranks array,with Hearts, Diamonds, Clubs, Spades and A, 2, 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K respectively.
     * Setting the value of the card according to the Balatro rules.
     *
     * @return an array of 52 cards
     */
    private Card[] createStandardDeck() {
        Card[] deck = new Card[52];
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        
        int index = 0;
        for (String suit : suits) {
            for (String rank : ranks) {
                int value;
                // Set card value according to Balatro rules
                value = switch (rank) {
                    case "A" -> 11;  // Ace is worth 11
                    case "J", "Q", "K" -> 10;  // Face cards are worth 10
                    default -> Integer.parseInt(rank);  // Number cards are worth their number
                };
                
                deck[index] = new Card(suit, rank, value);
                index++;
            }
        }
        
        return deck;
    }

    /**
     * Resets the deck to a standard 52-card deck.
     * The purpose of this method is to reset the deck to a standard 52-card deck.
     */
    public void resetDeck() {
        cards.clear();
        Card[] standardDeck = createStandardDeck();
        for (Card card : standardDeck) {
            cards.add(card);
        }
    }

    /**
     * Shuffles all cards in the deck.
     * In Balatro, shuffling is only allowed at the start of a new round.
     * @throws IllegalStateException if attempting to shuffle during an ongoing round
     */
    public void shuffle() {
        if (!isNewRound) {
            throw new IllegalStateException("Cannot shuffle deck during an ongoing round");
        }
        
        for (int i = cards.size() - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            Card temp = cards.get(index);
            cards.set(index, cards.get(i));
            cards.set(i, temp);
        }
        isNewRound = false;  // Mark round as started after shuffle
    }

    /**
     * Starts a new round, allowing the deck to be shuffled again.
     * @param requiredScore the score required to start a new round
     * @throws IllegalStateException if the required score hasn't been reached
     */
    public void startNewRound(int requiredScore) {
        if (currentScore < requiredScore) {
            throw new IllegalStateException("Cannot start new round: required score not reached");
        }
        resetDeck();
        isNewRound = true;
        shuffle();
    }

    /**
     * Updates the current score.
     * @param score the new score to set
     */
    public void updateScore(int score) {
        this.currentScore = score;
    }

    /**
     * Gets whether a new round can be started.
     * @param requiredScore the score required to start a new round
     * @return true if the current score meets or exceeds the required score
     */
    public boolean canStartNewRound(int requiredScore) {
        return currentScore >= requiredScore;
    }

    /**
     * Draws cards from the deck based on the number of cards discarded.
     * In Balatro, players always have 8 cards in hand.
     * When discarding 1-5 cards, they draw the same number to maintain 8 cards total.
     *
     * @param numCardsDiscarded the number of cards discarded (1-5)
     * @param keptCards the list of cards the player kept (didn't discard)
     * @return a new deck containing exactly 8 cards (kept cards + newly drawn cards), or null if not enough cards
     * @throws IllegalArgumentException if numCardsDiscarded is not between 1 and 5
     * @throws IllegalArgumentException if keptCards is null or if keptCards.size() + numCardsDiscarded != 8
     */
    public Deck drawCard(int numCardsDiscarded, List<Card> keptCards) {
        if (numCardsDiscarded < 1 || numCardsDiscarded > 5) {
            throw new IllegalArgumentException("Must discard between 1 and 5 cards");
        }
        if (keptCards == null) {
            throw new IllegalArgumentException("Kept cards list cannot be null");
        }
        if (keptCards.size() + numCardsDiscarded != 8) {
            throw new IllegalArgumentException("Total cards (kept + discarded) must be 8");
        }
        
        // Check if we have enough cards to draw
        if (cards.size() < numCardsDiscarded) {
            return null;
        }

        // Create list for new cards
        List<Card> newCards = new ArrayList<>();
        
        // Draw the requested number of cards
        for (int i = 0; i < numCardsDiscarded; i++) {
            newCards.add(cards.remove(cards.size() - 1));
        }
        
        // Combine kept cards and new cards
        List<Card> combinedCards = new ArrayList<>(keptCards);
        combinedCards.addAll(newCards);
        
        // Verify we have exactly 8 cards
        assert combinedCards.size() == 8 : "Hand must contain exactly 8 cards";
        
        // Return new deck with combined cards
        return new Deck(combinedCards);
    }

    /**
     * Gets the number of cards remaining in the deck.
     *
     * @return the number of cards
     */
    public int getCardCount() {
        return cards.size();
    }

    /**
     * Checks if the deck is empty.
     *
     * @return true if the deck is empty, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Gets all cards in the deck.
     *
     * @return a list of all cards
     */
    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }
} 