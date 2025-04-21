package com.balatro.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.balatro.model.Card;
import com.balatro.model.Hand;

/**
 * This controller is responsible for evaluating the hand and returning the hand type, score, and other details.    
 */
/**
 * REST Controller for hand evaluation operations
 */
@RestController
@RequestMapping("/api/hand")
@CrossOrigin(origins = "*") // For development - restrict in production
public class HandEvaluationController {

    /**
     * Evaluates a list of cards and returns the hand type, score, and other details
     * 
     * @param request Contains the list of cards to evaluate
     * @return Hand evaluation result including type, score, multiplier, etc.
     */
    @PostMapping("/evaluate")
    public Map<String, Object> evaluateHand(@RequestBody EvaluateHandRequest request) {
        Hand hand = new Hand();
        
        // Add each card to the hand
        for (CardDto cardDto : request.getCards()) {
            // Create a new card with rank, suit, and calculate its value
            int value = calculateCardValue(cardDto.getRank());
            Card card = new Card(cardDto.getSuit(), cardDto.getRank(), value);
            hand.addCard(card);
        }
        
        // Return evaluation results
        return Map.of(
            "handType", hand.getHandType().getDisplayName(),
            "baseScore", hand.getBaseScore(),
            "multiplier", hand.getMultiplier(),
            "totalScore", hand.getTotalScore(),
            "cardsValue", hand.getTotalScore() / hand.getMultiplier() - hand.getBaseScore()
        );
    }
    
    /**
     * Calculates the value of a card based on its rank
     * 
     * @param rank The card rank
     * @return The card's point value (A=11, K/Q/J=10, others=face value)
     */
    private int calculateCardValue(String rank) {
        return switch (rank) {
            case "A" -> 11;  // Ace is worth 11 points
            case "K", "Q", "J" -> 10;  // Face cards are worth 10 points
            default -> Integer.parseInt(rank);  // Number cards are worth their number
        };
    }
    
    /**
     * Request DTO for card evaluation
     */
    public static class EvaluateHandRequest {
        private List<CardDto> cards;
        
        public List<CardDto> getCards() {
            return cards;
        }
        
        public void setCards(List<CardDto> cards) {
            this.cards = cards;
        }
    }
    
    /**
     * DTO for card information
     */
    public static class CardDto {
        private String rank;
        private String suit;
        
        public String getRank() {
            return rank;
        }
        
        public void setRank(String rank) {
            this.rank = rank;
        }
        
        public String getSuit() {
            return suit;
        }
        
        public void setSuit(String suit) {
            this.suit = suit;
        }
    }
} 