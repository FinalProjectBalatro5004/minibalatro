package com.balatro.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.balatro.controller.HandEvaluationController.CardDto;
import com.balatro.controller.HandEvaluationController.EvaluateHandRequest;

/**
 * Test class for the HandEvaluationController.
 * Tests the hand evaluation endpoint and its responses.
 */
class HandEvaluationControllerTest {

    private HandEvaluationController controller;
    private EvaluateHandRequest request;

    /**
     * Sets up the test fixtures before each test method.
     * Creates a HandEvaluationController instance and an empty request.
     */
    @BeforeEach
    void setUp() {
        controller = new HandEvaluationController();
        request = new EvaluateHandRequest();
        request.setCards(new ArrayList<>());
    }

    /**
     * Tests the creation of card DTOs.
     */
    @Test
    void testCardDtoCreation() {
        CardDto card = new CardDto();
        card.setRank("A");
        card.setSuit("Spades");
        
        assertEquals("A", card.getRank(), "Card rank should be A");
        assertEquals("Spades", card.getSuit(), "Card suit should be Spades");
    }
    
    /**
     * Tests that the evaluateHand endpoint correctly evaluates a high card hand.
     */
    @Test
    void testEvaluateHighCard() {
        // Create a high card hand (no pairs, no flush, no straight)
        List<CardDto> cards = new ArrayList<>();
        
        CardDto card1 = new CardDto();
        card1.setRank("A");
        card1.setSuit("Spades");
        
        CardDto card2 = new CardDto();
        card2.setRank("3");
        card2.setSuit("Hearts");
        
        CardDto card3 = new CardDto();
        card3.setRank("5");
        card3.setSuit("Clubs");
        
        CardDto card4 = new CardDto();
        card4.setRank("7");
        card4.setSuit("Diamonds");
        
        CardDto card5 = new CardDto();
        card5.setRank("9");
        card5.setSuit("Spades");
        
        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);
        cards.add(card5);
        
        request.setCards(cards);
        
        // Evaluate the hand
        Map<String, Object> result = controller.evaluateHand(request);
        
        // Verify the response
        assertNotNull(result, "Result should not be null");
        assertEquals("High Card", result.get("handType"), "Hand type should be High Card");
        assertEquals(5, result.get("baseScore"), "Base score should be 5 for High Card");
        assertEquals(1, result.get("multiplier"), "Multiplier should be 1 for High Card");
        
        // Calculate expected card values: A(11) + 3(3) + 5(5) + 7(7) + 9(9) = 35
        assertEquals(35, result.get("cardsValue"), "Card values should be correctly calculated");
        
        // Total score should be (baseScore + cardsValue) * multiplier = (5 + 35) * 1 = 40
        assertEquals(40, result.get("totalScore"), "Total score should be correctly calculated");
    }

    /**
     * Tests that the evaluateHand endpoint correctly evaluates a pair hand.
     */
    @Test
    void testEvaluatePair() {
        // Create a pair hand
        List<CardDto> cards = new ArrayList<>();
        
        CardDto card1 = new CardDto();
        card1.setRank("K");
        card1.setSuit("Spades");
        
        CardDto card2 = new CardDto();
        card2.setRank("K");
        card2.setSuit("Hearts");
        
        CardDto card3 = new CardDto();
        card3.setRank("5");
        card3.setSuit("Clubs");
        
        CardDto card4 = new CardDto();
        card4.setRank("7");
        card4.setSuit("Diamonds");
        
        CardDto card5 = new CardDto();
        card5.setRank("9");
        card5.setSuit("Spades");
        
        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);
        cards.add(card5);
        
        request.setCards(cards);
        
        // Evaluate the hand
        Map<String, Object> result = controller.evaluateHand(request);
        
        // Verify the response
        assertNotNull(result, "Result should not be null");
        assertEquals("Pair", result.get("handType"), "Hand type should be Pair");
        assertEquals(10, result.get("baseScore"), "Base score should be 10 for Pair");
        assertEquals(2, result.get("multiplier"), "Multiplier should be 2 for Pair");
        
        // Calculate expected card values: K(10) + K(10) + 5(5) + 7(7) + 9(9) = 41
        assertEquals(41, result.get("cardsValue"), "Card values should be correctly calculated");
        
        // Total score should be (baseScore + cardsValue) * multiplier = (10 + 41) * 2 = 102
        assertEquals(102, result.get("totalScore"), "Total score should be correctly calculated");
    }

    /**
     * Tests that the evaluateHand endpoint correctly evaluates a flush hand.
     */
    @Test
    void testEvaluateFlush() {
        // Create a flush (all same suit)
        List<CardDto> cards = new ArrayList<>();
        
        CardDto card1 = new CardDto();
        card1.setRank("A");
        card1.setSuit("Spades");
        
        CardDto card2 = new CardDto();
        card2.setRank("K");
        card2.setSuit("Spades");
        
        CardDto card3 = new CardDto();
        card3.setRank("10");
        card3.setSuit("Spades");
        
        CardDto card4 = new CardDto();
        card4.setRank("7");
        card4.setSuit("Spades");
        
        CardDto card5 = new CardDto();
        card5.setRank("3");
        card5.setSuit("Spades");
        
        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);
        cards.add(card5);
        
        request.setCards(cards);
        
        // Evaluate the hand
        Map<String, Object> result = controller.evaluateHand(request);
        
        // Verify the response
        assertNotNull(result, "Result should not be null");
        assertEquals("Flush", result.get("handType"), "Hand type should be Flush");
        assertEquals(35, result.get("baseScore"), "Base score should be 35 for Flush");
        assertEquals(4, result.get("multiplier"), "Multiplier should be 4 for Flush");
        
        // Calculate expected card values: A(11) + K(10) + 10(10) + 7(7) + 3(3) = 41
        assertEquals(41, result.get("cardsValue"), "Card values should be correctly calculated");
        
        // Total score should be (baseScore + cardsValue) * multiplier = (35 + 41) * 4 = 304
        assertEquals(304, result.get("totalScore"), "Total score should be correctly calculated");
    }

    /**
     * Tests that the evaluateHand endpoint correctly evaluates a straight flush hand.
     */
    @Test
    void testEvaluateStraightFlush() {
        // Create a straight flush (sequential ranks, all same suit)
        List<CardDto> cards = new ArrayList<>();
        
        CardDto card1 = new CardDto();
        card1.setRank("9");
        card1.setSuit("Hearts");
        
        CardDto card2 = new CardDto();
        card2.setRank("10");
        card2.setSuit("Hearts");
        
        CardDto card3 = new CardDto();
        card3.setRank("J");
        card3.setSuit("Hearts");
        
        CardDto card4 = new CardDto();
        card4.setRank("Q");
        card4.setSuit("Hearts");
        
        CardDto card5 = new CardDto();
        card5.setRank("K");
        card5.setSuit("Hearts");
        
        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);
        cards.add(card5);
        
        request.setCards(cards);
        
        // Evaluate the hand
        Map<String, Object> result = controller.evaluateHand(request);
        
        // Verify the response
        assertNotNull(result, "Result should not be null");
        assertEquals("Straight Flush", result.get("handType"), "Hand type should be Straight Flush");
        assertEquals(100, result.get("baseScore"), "Base score should be 100 for Straight Flush");
        assertEquals(8, result.get("multiplier"), "Multiplier should be 8 for Straight Flush");
        
        // Calculate expected card values: 9(9) + 10(10) + J(10) + Q(10) + K(10) = 49
        assertEquals(49, result.get("cardsValue"), "Card values should be correctly calculated");
        
        // Total score should be (baseScore + cardsValue) * multiplier = (100 + 49) * 8 = 1192
        assertEquals(1192, result.get("totalScore"), "Total score should be correctly calculated");
    }
} 