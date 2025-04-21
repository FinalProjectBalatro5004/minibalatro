package com.balatro.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class PlayerTest {
    private Player player;
    private Card testCard;

    @BeforeEach
    void setUp() {
        player = new Player("TestPlayer");
        testCard = new Card("Hearts", "A", 11);
    }

    @Test
    void testPlayerInitialization() {
        assertNotNull(player.getPlayerId());
        assertEquals("TestPlayer", player.getUsername());
        assertEquals(0, player.getHandSize());
        assertEquals(0, player.getScore());
        assertTrue(player.isActive());
        assertEquals(0, player.getChips());
    }

    @Test
    void testSetUsername() {
        player.setUsername("NewUsername");
        assertEquals("NewUsername", player.getUsername());
    }

    @Test
    void testAddCard() {
        player.addCard(testCard);
        assertEquals(1, player.getHandSize());
        assertTrue(player.getHand().contains(testCard));
    }

    @Test
    void testRemoveCard() {
        player.addCard(testCard);
        assertTrue(player.removeCard(testCard));
        assertEquals(0, player.getHandSize());
        assertFalse(player.getHand().contains(testCard));
    }

    @Test
    void testRemoveNonExistentCard() {
        assertFalse(player.removeCard(testCard));
    }

    @Test
    void testUpdateScore() {
        player.updateScore(100);
        assertEquals(100, player.getScore());
        player.updateScore(-50);
        assertEquals(50, player.getScore());
    }

    @Test
    void testSetActive() {
        player.setActive(false);
        assertFalse(player.isActive());
        player.setActive(true);
        assertTrue(player.isActive());
    }

    @Test
    void testClearHand() {
        player.addCard(testCard);
        player.addCard(new Card("Diamonds", "K", 10));
        assertEquals(2, player.getHandSize());
        player.clearHand();
        assertEquals(0, player.getHandSize());
    }

    @Test
    void testGetHandReturnsCopy() {
        player.addCard(testCard);
        List<Card> hand = player.getHand();
        hand.add(new Card("Diamonds", "K", 10));
        assertEquals(1, player.getHandSize());
    }

    @Test
    void testSetChips() {
        player.setChips(100);
        assertEquals(100, player.getChips());
        player.setChips(50);
        assertEquals(50, player.getChips());
    }

    @Test
    void testToString() {
        player.addCard(testCard);
        player.updateScore(100);
        player.setChips(50);
        String expected = "Player{username='TestPlayer', handSize=1, score=100, active=true, chips=50}";
        assertEquals(expected, player.toString());
    }
} 