package com.balatro.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a player in the card game.
 */
public class Player {
    private final String playerId;
    private String username;
    private List<Card> hand;
    private int score;
    private boolean isActive;
    private int chips;

    /**
     * Creates a new player with the specified username.
     *
     * @param username the player's username
     */
    public Player(String username) {
        this.playerId = UUID.randomUUID().toString();
        this.username = username;
        this.hand = new ArrayList<>();
        this.score = 0;
        this.isActive = true;
        this.chips = 0;
    }

    /**
     * Gets the player's unique ID.
     *
     * @return the player ID
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * Gets the player's username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the player's username.
     *
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the player's current hand of cards.
     *
     * @return list of cards in hand
     */
    public List<Card> getHand() {
        return new ArrayList<>(hand);
    }

    /**
     * Adds a card to the player's hand.
     *
     * @param card the card to add
     */
    public void addCard(Card card) {
        hand.add(card);
    }

    /**
     * Removes a card from the player's hand.
     *
     * @param card the card to remove
     * @return true if the card was removed, false otherwise
     */
    public boolean removeCard(Card card) {
        return hand.remove(card);
    }

    /**
     * Gets the player's current score.
     *
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * Updates the player's score.
     *
     * @param points points to add (or subtract if negative)
     */
    public void updateScore(int points) {
        this.score += points;
    }

    /**
     * Checks if the player is active in the game.
     *
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Sets whether the player is active in the game.
     *
     * @param active true to set active, false to set inactive
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Clears the player's hand of all cards.
     */
    public void clearHand() {
        hand.clear();
    }

    /**
     * Gets the number of cards in the player's hand.
     *
     * @return the number of cards
     */
    public int getHandSize() {
        return hand.size();
    }

    /**
     * Gets the player's chips.
     *
     * @return the number of chips
     */
    public int getChips() {
        return chips;
    }

    /**
     * Sets the player's chips.
     *
     * @param chips the number of chips to set
     */
    public void setChips(int chips) {
        this.chips = chips;
    }

    /**
     * Returns a string representation of the player.   
     *
     * @return a string representation of the player
     */
    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", handSize=" + hand.size() +
                ", score=" + score +
                ", active=" + isActive +
                ", chips=" + chips +
                '}';
    }
}
