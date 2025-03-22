package com.balatro.service;

import java.util.ArrayList;
import java.util.List;

import com.balatro.model.Card;
import com.balatro.model.Deck;
import com.balatro.model.Hand;
import com.balatro.model.HandType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Service class to manage game state and logic.
 * This class acts as a bridge between the UI and the game model.
 */
public class GameService {
    private final Deck deck;
    private final Hand playerHand;
    private final ObservableList<Card> discardPile;
    private final ObservableList<Card> selectedCards;
    private final ObjectProperty<GameState> gameState;
    private final IntegerProperty score;
    private final IntegerProperty round;
    private final IntegerProperty targetScore;
    private final StringProperty currentHandTypeDisplay;
    private final BooleanProperty canDrawCards;
    private final IntegerProperty cardsToDrawCount;
    private final BooleanProperty roundCompleted;
    private final IntegerProperty remainingCards;

    /**
     * Represents the current state of the game.
     */
    public enum GameState {
        WAITING_FOR_SELECTION,  // Waiting for player to select cards
        WAITING_FOR_DISCARD,    // Waiting for player to discard cards
        WAITING_FOR_DRAW,       // Waiting for player to draw cards
        EVALUATING_HAND,        // Evaluating the current hand
        ROUND_COMPLETE,         // Current round is complete
        GAME_OVER               // Game is over
    }

    /**
     * Creates a new game service instance.
     */
    public GameService() {
        this.deck = new Deck();
        this.playerHand = new Hand();
        this.discardPile = FXCollections.observableArrayList();
        this.selectedCards = FXCollections.observableArrayList();
        this.gameState = new SimpleObjectProperty<>(GameState.WAITING_FOR_SELECTION);
        this.score = new SimpleIntegerProperty(0);
        this.round = new SimpleIntegerProperty(1);
        this.targetScore = new SimpleIntegerProperty(1000); // Default target score to advance
        this.currentHandTypeDisplay = new SimpleStringProperty("No cards selected");
        this.canDrawCards = new SimpleBooleanProperty(false);
        this.cardsToDrawCount = new SimpleIntegerProperty(0);
        this.roundCompleted = new SimpleBooleanProperty(false);
        this.remainingCards = new SimpleIntegerProperty(deck.getCardCount());
        
        // Initialize the game
        initializeGame();
    }

    /**
     * Initializes the game state.
     */
    private void initializeGame() {
        // Reset the deck but don't shuffle it yet (we'll do that when starting a round)
        deck.resetDeck();
        
        // Force the deck to be in a new round state
        deck.startNewRound(0);
        
        // Reset all properties
        score.set(0);
        round.set(1);
        targetScore.set(100);  // Initial target: 100 points
        
        // Initial game state
        gameState.set(GameState.WAITING_FOR_SELECTION);
        roundCompleted.set(false);
        canDrawCards.set(false);
        cardsToDrawCount.set(0);
        
        // Clear the player's hand (using the mutable access method)
        playerHand.getMutableCards().clear();
        
        // Update remaining cards property
        updateRemainingCards();
    }

    /**
     * Deals the initial hand to the player.
     * In Balatro, players start with 8 cards.
     */
    private void dealInitialHand() {
        // Clear any existing cards in the hand
        playerHand.getMutableCards().clear();
        
        // Reset and shuffle the deck
        deck.resetDeck();
        deck.shuffle();
        
        // Get a copy of all the shuffled cards
        List<Card> allCards = new ArrayList<>(deck.getCards());
        
        // Clear the deck
        deck.getMutableCards().clear();
        
        // Create a diverse hand with cards of different suits and ranks
        List<Card> initialCards = new ArrayList<>();
        
        // We want to ensure a diverse hand (different suits and ranks)
        // Pull cards from the shuffled deck until we have 8 cards of sufficient diversity
        for (int i = 0; i < allCards.size() && initialCards.size() < 8; i++) {
            Card candidate = allCards.get(i);
            
            // Check if this card's rank and suit combination is already in our hand
            boolean isDuplicate = false;
            for (Card existingCard : initialCards) {
                if (existingCard.getRank().equals(candidate.getRank()) && 
                    existingCard.getSuit().equals(candidate.getSuit())) {
                    isDuplicate = true;
                    break;
                }
            }
            
            // If it's not a duplicate, add it to our hand
            if (!isDuplicate) {
                initialCards.add(candidate);
            }
        }
        
        // Put the remaining cards back in the deck
        for (Card card : allCards) {
            if (!initialCards.contains(card)) {
                deck.getMutableCards().add(card);
            }
        }
        
        // Initialize the player's hand with these cards
        if (!initialCards.isEmpty()) {
            playerHand.initializeHand(initialCards);
            updateCurrentHandTypeDisplay();
        }
    }

    /**
     * Selects a card from the player's hand.
     * @param card the card to select
     * @return true if the card was successfully selected
     */
    public boolean selectCard(Card card) {
        // Check if the card is in the player's hand and not already selected
        if (playerHand.getCards().contains(card) && !selectedCards.contains(card)) {
            // Check if we've already selected the maximum number of cards (5)
            if (selectedCards.size() >= Hand.getMaxCardsToPlay()) {
                return false;
            }
            
            selectedCards.add(card);
            updateCurrentHandTypeDisplay();
            return true;
        }
        return false;
    }

    /**
     * Deselects a card from the selected cards.
     * @param card the card to deselect
     * @return true if the card was successfully deselected
     */
    public boolean deselectCard(Card card) {
        boolean removed = selectedCards.remove(card);
        if (removed) {
            updateCurrentHandTypeDisplay();
        }
        return removed;
    }

    /**
     * Updates the display of the current hand type based on selected cards.
     */
    private void updateCurrentHandTypeDisplay() {
        if (selectedCards.isEmpty()) {
            currentHandTypeDisplay.set("No cards selected");
            return;
        }
        
        // Create a temporary hand with the selected cards to evaluate
        Hand tempHand = new Hand();
        for (Card card : selectedCards) {
            tempHand.addCard(card);
        }
        
        // Get the hand type and scores
        HandType handType = tempHand.getHandType();
        int baseScore = tempHand.getBaseScore();
        int multiplier = tempHand.getMultiplier();
        
        // Calculate the sum of card values separately
        int cardValuesSum = selectedCards.stream()
                           .mapToInt(Card::getValue)
                           .sum();
        
        // Calculate the combined base score (hand type base + card values)
        int combinedBaseScore = baseScore + cardValuesSum;
        
        // Calculate the total score
        int totalScore = combinedBaseScore * multiplier;
        
        // Display the hand type and score breakdown
        currentHandTypeDisplay.set(handType.getDisplayName() + 
                                  " (Hand: " + baseScore + 
                                  " + Cards: " + cardValuesSum +
                                  " = " + combinedBaseScore +
                                  ") × Mult: " + multiplier + 
                                  " = " + totalScore);
    }

    /**
     * Discards the currently selected cards.
     * This enables the player to draw the same number of cards.
     * @return true if cards were successfully discarded
     */
    public boolean discardSelectedCards() {
        if (selectedCards.isEmpty() || 
            selectedCards.size() < Hand.getMinCardsToDiscard() || 
            selectedCards.size() > Hand.getMaxCardsToDiscard()) {
            return false;
        }
        
        gameState.set(GameState.WAITING_FOR_DISCARD);
        
        // Create a copy of the selected cards to avoid concurrent modification
        List<Card> cardsToDiscard = new ArrayList<>(selectedCards);
        
        // Discard the selected cards
        int discarded = playerHand.discardCards(cardsToDiscard);
        
        if (discarded > 0) {
            // Add discarded cards to the discard pile
            discardPile.addAll(cardsToDiscard);
            
            // Clear the selection
            selectedCards.clear();
            
            // Set the number of cards that can be drawn
            cardsToDrawCount.set(discarded);
            canDrawCards.set(true);
            
            // Update the display
            updateCurrentHandTypeDisplay();
            
            // Update remaining cards count
            updateRemainingCards();
            
            gameState.set(GameState.WAITING_FOR_DRAW);
            return true;
        }
        
        gameState.set(GameState.WAITING_FOR_SELECTION);
        return false;
    }

    /**
     * Draws cards from the deck to replace discarded cards.
     * Player must have discarded cards first, and can only draw
     * the same number of cards as were discarded.
     * @return the list of drawn cards, or empty list if no cards could be drawn
     */
    public List<Card> drawCards() {
        if (!canDrawCards.get() || cardsToDrawCount.get() <= 0) {
            return new ArrayList<>();
        }
        
        gameState.set(GameState.WAITING_FOR_DRAW);
        
        int cardsToDraw = cardsToDrawCount.get();
        List<Card> drawnCards = new ArrayList<>();
        
        // Draw cards directly from the deck
        for (int i = 0; i < cardsToDraw; i++) {
            if (deck.getCards().isEmpty()) {
                break;
            }
            Card drawnCard = deck.getMutableCards().remove(0);
            drawnCards.add(drawnCard);
        }
        
        // Add the drawn cards to the player's hand
        if (!drawnCards.isEmpty()) {
            for (Card card : drawnCards) {
                playerHand.addCard(card);
            }
        }
        
        // Reset the draw state
        canDrawCards.set(false);
        cardsToDrawCount.set(0);
        
        // Update the display
        updateCurrentHandTypeDisplay();
        
        // Update remaining cards count
        updateRemainingCards();
        
        gameState.set(GameState.WAITING_FOR_SELECTION);
        return drawnCards;
    }

    /**
     * Evaluates the currently selected cards and updates the score.
     * @return the score earned from this hand
     */
    public int evaluateHand() {
        if (selectedCards.isEmpty() || 
            selectedCards.size() < Hand.getMinCardsToPlay() || 
            selectedCards.size() > Hand.getMaxCardsToPlay()) {
            return 0;
        }
        
        gameState.set(GameState.EVALUATING_HAND);
        
        // Create a temporary hand with the selected cards to evaluate
        Hand tempHand = new Hand();
        for (Card card : selectedCards) {
            tempHand.addCard(card);
        }
        
        // Calculate the score
        int handScore = tempHand.getTotalScore();
        score.set(score.get() + handScore);
        
        // Store how many cards we need to draw as replacements
        int cardsToReplace = selectedCards.size();
        
        // Remove played cards from hand and add to discard pile
        List<Card> cardsToDiscard = new ArrayList<>(selectedCards);
        playerHand.discardCards(cardsToDiscard);
        discardPile.addAll(cardsToDiscard);
        
        // Clear the selection
        selectedCards.clear();
        
        // Update the display
        updateCurrentHandTypeDisplay();
        
        // Update remaining cards count
        updateRemainingCards();
        
        // Check if the round is complete - only when score reaches or exceeds target score
        if (score.get() >= targetScore.get()) {
            roundCompleted.set(true);
            gameState.set(GameState.ROUND_COMPLETE);
        } else {
            // Set up to draw replacement cards 
            cardsToDrawCount.set(cardsToReplace);
            canDrawCards.set(true);
            
            // Check if the game is over
            if (deck.isEmpty() && playerHand.getCardCount() < Hand.getMinCardsToPlay()) {
                gameState.set(GameState.GAME_OVER);
            } else {
                gameState.set(GameState.WAITING_FOR_DRAW);
            }
        }
        
        return handScore;
    }

    /**
     * Starts a new round with a fresh deck and hand.
     */
    public void startNewRound() {
        // Reset everything for the new round
        dealInitialHand();
        
        // Reset game state
        gameState.set(GameState.WAITING_FOR_SELECTION);
        
        // Clear the discard pile
        discardPile.clear();
        
        // Clear any selected cards
        selectedCards.clear();
        
        // Update remaining cards property
        updateRemainingCards();
    }

    /**
     * Gets the current game state.
     * @return the current game state
     */
    public GameState getGameState() {
        return gameState.get();
    }

    /**
     * Gets the game state property.
     * @return the game state property
     */
    public ObjectProperty<GameState> gameStateProperty() {
        return gameState;
    }

    /**
     * Gets the player's hand.
     * @return the player's hand
     */
    public Hand getPlayerHand() {
        return playerHand;
    }

    /**
     * Gets the discard pile.
     * @return the discard pile as an observable list
     */
    public ObservableList<Card> getDiscardPile() {
        return discardPile;
    }

    /**
     * Gets the selected cards.
     * @return the selected cards as an observable list
     */
    public ObservableList<Card> getSelectedCards() {
        return selectedCards;
    }

    /**
     * Gets the current score.
     * @return the current score
     */
    public int getScore() {
        return score.get();
    }

    /**
     * Gets the score property.
     * @return the score property
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Gets the current round.
     * @return the current round
     */
    public int getRound() {
        return round.get();
    }

    /**
     * Gets the round property.
     * @return the round property
     */
    public IntegerProperty roundProperty() {
        return round;
    }

    /**
     * Gets the target score needed to complete the current round.
     * @return the target score
     */
    public int getTargetScore() {
        return targetScore.get();
    }

    /**
     * Gets the target score property.
     * @return the target score property
     */
    public IntegerProperty targetScoreProperty() {
        return targetScore;
    }

    /**
     * Gets the current hand type display.
     * @return the current hand type display
     */
    public String getCurrentHandTypeDisplay() {
        return currentHandTypeDisplay.get();
    }

    /**
     * Gets the current hand type display property.
     * @return the current hand type display property
     */
    public StringProperty currentHandTypeDisplayProperty() {
        return currentHandTypeDisplay;
    }

    /**
     * Gets whether the player can draw cards.
     * @return true if the player can draw cards
     */
    public boolean getCanDrawCards() {
        return canDrawCards.get();
    }

    /**
     * Gets the can draw cards property.
     * @return the can draw cards property
     */
    public BooleanProperty canDrawCardsProperty() {
        return canDrawCards;
    }

    /**
     * Gets the number of cards the player can draw.
     * @return the number of cards to draw
     */
    public int getCardsToDrawCount() {
        return cardsToDrawCount.get();
    }

    /**
     * Gets the cards to draw count property.
     * @return the cards to draw count property
     */
    public IntegerProperty cardsToDrawCountProperty() {
        return cardsToDrawCount;
    }

    /**
     * Gets whether the current round is completed.
     * @return true if the round is completed
     */
    public boolean isRoundCompleted() {
        return roundCompleted.get();
    }

    /**
     * Gets the round completed property.
     * @return the round completed property
     */
    public BooleanProperty roundCompletedProperty() {
        return roundCompleted;
    }

    /**
     * Gets the number of cards remaining in the deck.
     * @return the number of cards in the deck
     */
    public int getRemainingCards() {
        return deck.getCardCount();
    }

    /**
     * Starts a new game.
     */
    public void newGame() {
        initializeGame();
        gameState.set(GameState.WAITING_FOR_SELECTION);
    }

    /**
     * Checks if the game is over.
     * @return true if the game is over
     */
    public boolean isGameOver() {
        return gameState.get() == GameState.GAME_OVER;
    }

    /**
     * Gets the deck.
     * @return the deck
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Gets the current round number.
     * 
     * @return the current round number
     */
    public int getRoundNumber() {
        return round.get();
    }
    
    /**
     * Sets the target score for the current round.
     * 
     * @param targetScore the target score to set
     */
    public void setTargetScore(int targetScore) {
        this.targetScore.set(targetScore);
    }

    /**
     * Gets the property tracking the number of cards remaining in the deck.
     * 
     * @return the remaining cards property
     */
    public IntegerProperty remainingCardsProperty() {
        return remainingCards;
    }
    
    /**
     * Updates the remaining cards property.
     * This should be called whenever the deck changes.
     */
    private void updateRemainingCards() {
        remainingCards.set(deck.getCardCount());
    }
}
