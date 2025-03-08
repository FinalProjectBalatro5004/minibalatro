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
        
        // Initialize the game
        initializeGame();
    }

    /**
     * Initializes a new game.
     */
    private void initializeGame() {
        deck.resetDeck();
        deck.shuffle();
        playerHand.drawNewCards(new ArrayList<>()); // Clear the hand
        discardPile.clear();
        selectedCards.clear();
        score.set(0);
        round.set(1);
        canDrawCards.set(false);
        cardsToDrawCount.set(0);
        roundCompleted.set(false);
        dealInitialHand();
    }

    /**
     * Deals the initial hand to the player.
     * In Balatro, players start with 8 cards.
     */
    private void dealInitialHand() {
        // Deal 8 cards to the player
        List<Card> initialCards = new ArrayList<>();
        
        // In Balatro, we need to maintain exactly 8 cards
        // Since we're starting with 0 kept cards, we need to "discard" 8 cards
        // and draw 8 new ones
        List<Card> emptyList = new ArrayList<>();
        Deck newDeck = deck.drawCard(8, emptyList);
        
        if (newDeck != null) {
            initialCards.addAll(newDeck.getCards());
            playerHand.drawNewCards(initialCards);
            updateCurrentHandTypeDisplay();
        }
    }

    /**
     * Selects a card from the player's hand.
     * @param card the card to select
     * @return true if the card was successfully selected
     */
    public boolean selectCard(Card card) {
        if (playerHand.getCards().contains(card) && !selectedCards.contains(card)) {
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
        
        // Get the hand type and display it
        HandType handType = tempHand.getHandType();
        int baseScore = tempHand.getBaseScore();
        int multiplier = tempHand.getMultiplier();
        int totalValue = tempHand.getTotalScore();
        
        currentHandTypeDisplay.set(handType.getDisplayName() + 
                                  " (Base: " + baseScore + 
                                  " × Mult: " + multiplier + 
                                  " = " + totalValue + ")");
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
        
        // In Balatro, we need to maintain exactly 8 cards
        // Get the current cards in hand (these are the kept cards)
        List<Card> keptCards = new ArrayList<>(playerHand.getCards());
        
        // Draw the specified number of cards
        Deck newDeck = deck.drawCard(cardsToDraw, keptCards);
        
        if (newDeck != null) {
            // Get all cards from the new deck
            List<Card> allCards = newDeck.getCards();
            
            // The drawn cards are the ones not in the kept cards
            for (Card card : allCards) {
                if (!keptCards.contains(card)) {
                    drawnCards.add(card);
                }
            }
            
            // Clear the hand and add all cards from the new deck
            playerHand.drawNewCards(new ArrayList<>()); // Clear the hand
            playerHand.drawNewCards(allCards);
        }
        
        // Reset the draw state
        canDrawCards.set(false);
        cardsToDrawCount.set(0);
        
        // Update the display
        updateCurrentHandTypeDisplay();
        
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
        
        // Check if the round is complete
        if (score.get() >= targetScore.get()) {
            roundCompleted.set(true);
            gameState.set(GameState.ROUND_COMPLETE);
        } else {
            // Remove played cards from hand and add to discard pile
            List<Card> cardsToDiscard = new ArrayList<>(selectedCards);
            playerHand.discardCards(cardsToDiscard);
            discardPile.addAll(cardsToDiscard);
            
            // Clear the selection
            selectedCards.clear();
            
            // Update the display
            updateCurrentHandTypeDisplay();
            
            // Check if the game is over
            if (deck.isEmpty() && playerHand.getCardCount() < Hand.getMinCardsToPlay()) {
                gameState.set(GameState.GAME_OVER);
            } else {
                gameState.set(GameState.WAITING_FOR_SELECTION);
            }
        }
        
        return handScore;
    }

    /**
     * Starts a new round if the player has completed the current round.
     * @return true if a new round was started
     */
    public boolean startNewRound() {
        if (!roundCompleted.get()) {
            return false;
        }
        
        // Increment the round
        round.set(round.get() + 1);
        
        // Increase the target score for the next round
        targetScore.set(targetScore.get() * 2);
        
        // Reset the deck and shuffle
        deck.resetDeck();
        deck.shuffle();
        
        // Clear the player's hand and discard pile
        playerHand.drawNewCards(new ArrayList<>()); // Clear the hand
        discardPile.clear();
        selectedCards.clear();
        
        // Reset round state
        roundCompleted.set(false);
        canDrawCards.set(false);
        cardsToDrawCount.set(0);
        
        // Deal a new hand
        dealInitialHand();
        
        gameState.set(GameState.WAITING_FOR_SELECTION);
        return true;
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
}
