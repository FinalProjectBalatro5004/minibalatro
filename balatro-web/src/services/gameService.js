import axios from 'axios';

// Base URL for the API - adjust for your environment
const API_BASE_URL = 'http://localhost:8080/api';

/**
 * Service for game-related API calls
 */
export const GameService = {
  /**
   * Fetches all game stages from the backend
   * 
   * @returns {Promise} - Promise with game stages data
   */
  getGameStages: async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/game/stages`);
      return response.data;
    } catch (error) {
      console.error('Error fetching game stages:', error);
      throw error;
    }
  },
  
  /**
   * Fetches game stage transition rules from the backend
   * 
   * @returns {Promise} - Promise with transition rules data
   */
  getStageTransitionRules: async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/game/stage-transitions`);
      return response.data;
    } catch (error) {
      console.error('Error fetching stage transition rules:', error);
      throw error;
    }
  }
};

export default GameService; 