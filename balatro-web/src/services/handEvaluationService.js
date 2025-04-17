import axios from 'axios';

// Base URL for the API - adjust for your environment
const API_BASE_URL = 'http://localhost:8080/api';

/**
 * Service for hand evaluation API calls
 */
export const HandEvaluationService = {
  /**
   * Evaluates a hand of cards using the backend service
   * 
   * @param {Array} cards - Array of card objects with rank and suit properties
   * @returns {Promise} - The evaluation result with handType, baseScore, multiplier, etc.
   */
  evaluateHand: async (cards) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/hand/evaluate`, {
        cards: cards.map(card => ({
          rank: card.rank,
          suit: card.suit
        }))
      });
      
      return response.data;
    } catch (error) {
      console.error('Error evaluating hand:', error);
      throw error;
    }
  }
};

export default HandEvaluationService; 