import React, { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import Card from './Card';
import Balatro from '../Balatro';
import { motion } from 'framer-motion';
import HandEvaluationService from '../../services/handEvaluationService';
import GameService from '../../services/gameService';
import Toast from '../ui/Toast';
import FuzzyText from '../ui/FuzzyText';
import { authService } from '../../services/authService';

const VALID_SUITS = ["Hearts", "Diamonds", "Clubs", "Spades"];
const VALID_RANKS = ["A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"];

// Hand types with name, base score and multiplier (from HandType.java)
const HAND_TYPES = [
  { name: "High Card", baseScore: 5, multiplier: 1 },
  { name: "Pair", baseScore: 10, multiplier: 2 },
  { name: "Two Pair", baseScore: 20, multiplier: 2 },
  { name: "Three of a Kind", baseScore: 30, multiplier: 3 },
  { name: "Straight", baseScore: 30, multiplier: 4 },
  { name: "Flush", baseScore: 35, multiplier: 4 },
  { name: "Full House", baseScore: 40, multiplier: 4 },
  { name: "Four of a Kind", baseScore: 60, multiplier: 7 },
  { name: "Straight Flush", baseScore: 100, multiplier: 8 }
];



// Function to generate a full deck of 52 cards
const generateDeck = () => {
  const deck = [];
  let id = 1;
  
  for (const suit of VALID_SUITS) {
    for (const rank of VALID_RANKS) {
      // Calculate card value
      let value;
      if (rank === "A") {
        value = 11;
      } else if (["J", "Q", "K"].includes(rank)) {
        value = 10;
      } else {
        value = parseInt(rank);
      }
      
      deck.push({
        id: id++,
        rank,
        suit,
        value
      });
    }
  }
  return deck;
};

// Fisher-Yates shuffle algorithm
const shuffleDeck = (deck) => {
  const shuffled = [...deck];
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }
  return shuffled;
};

// Helper function to get rank value for straight evaluation
const getRankValue = (rank) => {
  if (rank === "A") return 14; // Ace high
  if (rank === "K") return 13;
  if (rank === "Q") return 12;
  if (rank === "J") return 11;
  return parseInt(rank);
};

const GameBoard = () => {
  const navigate = useNavigate();
  
  // Game state
  const [score, setScore] = useState(0);
  const [round, setRound] = useState(1);
  const [level, setLevel] = useState(1);
  const [stage, setStage] = useState('Small Blind');
  const [ante, setAnte] = useState(5);
  const [chips, setChips] = useState(100);
  const [biteAmount, setBiteAmount] = useState(0);
  const [gamePhase, setGamePhase] = useState('Bite Selection');
  const [hoverIntensity, setHoverIntensity] = useState(0.5);
  const [enableHover, setEnableHover] = useState(true);
  
  // Joker state
  const [activeJokers, setActiveJokers] = useState([]);
  
  // Game stages
  const [gameStages, setGameStages] = useState([]);
  
  // Stage transition rules
  const [stageTransitionRules, setStageTransitionRules] = useState({ transitions: [] });
  
  // Card management
  const [deck, setDeck] = useState([]);
  const [playerHand, setPlayerHand] = useState([]);
  const [selectedCards, setSelectedCards] = useState([]);
  const [discardPile, setDiscardPile] = useState([]);
  const [discardCount, setDiscardCount] = useState(0);
  const [handCount, setHandCount] = useState(0);
  const [handEvaluation, setHandEvaluation] = useState(null);
  
  // Toast state
  const [toast, setToast] = useState({ visible: false, message: '', type: 'warning' });
  
  // Show Toast helper function
  const showToast = (message, type = 'warning') => {
    setToast({ visible: true, message, type });
  };
  
  // Close Toast helper function
  const hideToast = () => {
    setToast(prev => ({ ...prev, visible: false }));
  };
  
  // Helper function to validate and ensure the hand size is exactly 8
  const validateHandSize = (hand) => {
    if (hand.length < 8) {
      console.warn(`Hand has only ${hand.length} cards, adding cards to reach 8`);
      // If we have fewer than 8 cards, try to draw from deck
      if (deck.length > 0) {
        const cardsNeeded = 8 - hand.length;
        const newCards = deck.slice(0, cardsNeeded);
        const newDeck = deck.slice(cardsNeeded);
        setDeck(newDeck);
        return [...hand, ...newCards];
      }
    } else if (hand.length > 8) {
      console.warn(`Hand has ${hand.length} cards, trimming to 8`);
      // If we have more than 8 cards, trim to 8
      return hand.slice(0, 8);
    }
    return hand;
  };

  useEffect(() => {
    // Validate player hand size whenever it changes
    if (playerHand.length !== 8) {
      setPlayerHand(prevHand => validateHandSize(prevHand));
    }
  }, [playerHand]);

  // Add effect to ensure deck is updated correctly
  useEffect(() => {
    // Log the current deck size for debugging
    console.log(`Current deck size: ${deck.length}`);
    
    // This is just for debugging - we should always maintain the proper deck count naturally
    if (deck.length > 44 && playerHand.length === 8) {
      console.warn(`Deck has ${deck.length} cards, which is more than expected 44, trimming`);
      setDeck(prev => prev.slice(0, 44));
    }
  }, [deck]);
  
  // Initialize game
  useEffect(() => {
    // Get game stages and transition rules
    const fetchGameData = async () => {
      try {
        // Get game stages
        const stages = await GameService.getGameStages();
        setGameStages(stages);
        console.log('Game stages loaded:', stages);
        
        // Get stage transition rules
        const rules = await GameService.getStageTransitionRules();
        setStageTransitionRules(rules);
        console.log('Stage transition rules loaded:', rules);
      } catch (error) {
        console.error('Failed to load game data:', error);
        // Use default stages and rules as backup
        setGameStages([
          { id: "SMALL_BLIND", targetScore: 300, displayName: "Small Blind", level: 1, ante: 5 },
          { id: "BIG_BLIND", targetScore: 450, displayName: "Big Blind", level: 1, ante: 10 },
          { id: "THE_HOOK", targetScore: 600, displayName: "The Hook", level: 1, ante: 15 },
          { id: "SMALL_BLIND_L2", targetScore: 800, displayName: "Small Blind L2", level: 2, ante: 20 },
          { id: "BIG_BLIND_L2", targetScore: 1200, displayName: "Big Blind L2", level: 2, ante: 25 },
          { id: "THE_HOOK_L2", targetScore: 1600, displayName: "The Hook L2", level: 2, ante: 30 },
          { id: "SMALL_BLIND_L3", targetScore: 2000, displayName: "Small Blind L3", level: 3, ante: 40 },
          { id: "BIG_BLIND_L3", targetScore: 3000, displayName: "Big Blind L3", level: 3, ante: 50 },
          { id: "THE_HOOK_L3", targetScore: 4000, displayName: "The Hook L3", level: 3, ante: 60 }
        ]);
        
        // Default transition rules
        setStageTransitionRules({
          resetScoreOnLevelAdvance: true,
          transitions: [
            { fromStage: "THE_HOOK", toStage: "SMALL_BLIND_L2", resetScore: true },
            { fromStage: "THE_HOOK_L2", toStage: "SMALL_BLIND_L3", resetScore: true },
            { fromStage: "SMALL_BLIND", toStage: "BIG_BLIND", resetScore: false },
            { fromStage: "BIG_BLIND", toStage: "THE_HOOK", resetScore: false }
          ]
        });
      }
    };
    
    fetchGameData();
    // Don't start new game automatically, wait for bite selection
  }, []);
  
  // 初始化时从数据库加载最高chips
  useEffect(() => {
    const loadUserData = async () => {
      try {
        const username = localStorage.getItem('username');
        if (username) {
          const userData = await authService.getUserData(username);
          if (userData && userData.highestChips) {
            setChips(userData.highestChips);
          }
        }
      } catch (error) {
        console.error('Error loading user data:', error);
      }
    };

    loadUserData();
  }, []);
  
  // Handle bite selection
  const handleBiteSelection = (amount) => {
    // Set the bite amount
    setBiteAmount(amount);
    
    // Deduct bite from starting chips (100)
    setChips(100 - amount);
    
    // Set ante based on bite
    setAnte(amount);
    
    // Start the game with the selected bite
    startNewGame();
    
    // Change game phase to playing
    setGamePhase('Playing Cards');
  };
  
  const startNewGame = () => {
    // Create a fresh deck of 52 cards
    const fullDeck = generateDeck();
    
    // Shuffle the deck
    const shuffledDeck = shuffleDeck(fullDeck);
    
    // Always draw exactly 8 initial cards
    const initialHand = shuffledDeck.slice(0, 8);
    
    // The remaining 44 cards stay in the deck
    const remainingDeck = shuffledDeck.slice(8);
    
    // Reset all game state
    setDeck(remainingDeck); // Will be exactly 44 cards
    setPlayerHand(initialHand); // Will be exactly 8 cards
    setSelectedCards([]);
    setDiscardPile([]);
    setScore(0);
    setRound(1);
    setLevel(1);
    
    // Start with the first stage
    const firstStage = gameStages.length > 0 ? gameStages[0] : { displayName: 'Small Blind', level: 1, ante: 5 };
    setStage(firstStage.displayName);
    
    // Don't reset chips here as we've already set them in handleBiteSelection
    // Don't reset ante here as we've already set it in handleBiteSelection
    
    setDiscardCount(0);
    setHandCount(0);
    
    // Reset hand evaluation
    setHandEvaluation(null);
  };
  
  // Function to handle card selection
  const handleCardSelect = async (card) => {
    // If the card is already selected, remove it
    let newSelection;
    if (selectedCards.some(c => c.id === card.id)) {
      newSelection = selectedCards.filter(c => c.id !== card.id);
      setSelectedCards(newSelection);
    } else {
      // Add the card to selection (max 5 cards)
      if (selectedCards.length >= 5) {
        // Use Toast instead of alert
        showToast('You can only select up to 5 cards at a time', 'warning');
        return;
      }
      
      newSelection = [...selectedCards, card];
      setSelectedCards(newSelection);
    }

    // Evaluate hand if cards are selected, otherwise reset evaluation
    if (newSelection.length > 0) {
      try {
        // Call the backend API to evaluate the hand
        const result = await HandEvaluationService.evaluateHand(newSelection);
        setHandEvaluation(result);
      } catch (error) {
        console.error('Error evaluating hand:', error);
        // Fallback to local evaluation if API fails
        const localEvaluation = evaluateHand(newSelection);
        setHandEvaluation(localEvaluation);
      }
    } else {
      // Reset hand evaluation when no cards are selected
      setHandEvaluation(null);
    }
  };
  
  // Function to evaluate the hand type
  const evaluateHand = (cards) => {
    if (!cards || cards.length === 0) {
      return null;
    }
    
    // Create a rank frequency map
    const rankFrequency = {};
    for (const card of cards) {
      rankFrequency[card.rank] = (rankFrequency[card.rank] || 0) + 1;
    }
    
    // Check if hand is a flush (all cards of the same suit)
    // Only valid with 5 cards
    const isFlush = cards.length >= 5 && cards.every(card => card.suit === cards[0].suit);
    
    // Check if hand is a straight (consecutive ranks)
    let isStraight = false;
    if (cards.length >= 5) {
      // Get rank values and sort them
      const rankValues = cards.map(card => getRankValue(card.rank)).sort((a, b) => a - b);
      
      // Remove duplicates
      const uniqueRanks = [...new Set(rankValues)];
      
      // Check if the ranks form a sequence
      if (uniqueRanks.length >= 5) {
        // Take just 5 consecutive ranks if there are more
        const consecutive = uniqueRanks.slice(0, 5);
        isStraight = consecutive.every((val, index) => 
          index === 0 || val === consecutive[index - 1] + 1
        );
      }
    }
    
    // Count the number of pairs, trips, and quads
    const pairs = Object.values(rankFrequency).filter(count => count === 2).length;
    const trips = Object.values(rankFrequency).filter(count => count === 3).length;
    const quads = Object.values(rankFrequency).filter(count => count === 4).length;
    
    // Determine hand type
    let handType;
    if (isFlush && isStraight) {
      handType = HAND_TYPES[8]; // Straight Flush
    } else if (quads > 0) {
      handType = HAND_TYPES[7]; // Four of a Kind
    } else if (trips > 0 && pairs > 0) {
      handType = HAND_TYPES[6]; // Full House
    } else if (isFlush) {
      handType = HAND_TYPES[5]; // Flush
    } else if (isStraight) {
      handType = HAND_TYPES[4]; // Straight
    } else if (trips > 0) {
      handType = HAND_TYPES[3]; // Three of a Kind
    } else if (pairs >= 2) {
      handType = HAND_TYPES[2]; // Two Pair
    } else if (pairs === 1) {
      handType = HAND_TYPES[1]; // Pair
    } else {
      handType = HAND_TYPES[0]; // High Card
    }

    // Calculate base score and multiplier
    let baseScore = handType.baseScore;
    let multiplier = handType.multiplier;
    let cardsValue = 0;

    // Apply joker buffs
    for (const joker of activeJokers) {
      if (joker.activationType === 'INDEPENDENT') {
        // Independent jokers add to multiplier directly
        multiplier += joker.multiplier;
      } else if (joker.activationType === 'ON_SCORED') {
        // On scored jokers check for specific conditions
        if (joker.activeSuit) {
          // Suit-specific jokers - only activate with 5 cards of the same suit
          const matchingCards = cards.filter(card => card.suit === joker.activeSuit);
          if (matchingCards.length >= 5) {
            multiplier += joker.multiplier;
          }
        } else if (joker.name === 'Scary Face') {
          // Face card joker
          const faceCards = cards.filter(card => ['J', 'Q', 'K'].includes(card.rank));
          if (faceCards.length > 0) {
            cardsValue += faceCards.length * 30; // Add 30 chips per face card
          }
        }
      }
    }

    // Calculate total card values
    for (const card of cards) {
      cardsValue += card.value;
    }

    const totalScore = (baseScore + cardsValue) * multiplier;

    return {
      handType: handType.name,
      baseScore,
      multiplier,
      cardsValue,
      totalScore
    };
  };
  
  // Add state for current level multiplier
  const [levelMultiplier, setLevelMultiplier] = useState(1);

  // Function to get current joker cost
  const getJokerCost = (baseCost) => {
    return baseCost * levelMultiplier;
  };

  // Add a function to reset the stage with a new deck
  const resetStage = (nextStage) => {
    // Reset score to 0
    setScore(0);
    
    // Create a new deck of 52 cards
    const fullDeck = generateDeck();
    
    // Shuffle the new deck
    const shuffledDeck = shuffleDeck(fullDeck);
    
    // Always draw exactly 8 initial cards for the player's hand
    const initialHand = shuffledDeck.slice(0, 8);
    
    // Keep the remaining 44 cards in the deck
    const remainingDeck = shuffledDeck.slice(8);
    
    // Update the deck and player's hand
    setDeck(remainingDeck); // This will be 44 cards (52 - 8)
    setPlayerHand(initialHand); // This will be 8 cards
    
    // Clear discard pile for the new stage
    setDiscardPile([]);
    
    // Clear selected cards
    setSelectedCards([]);
    
    // Reset hand evaluation
    setHandEvaluation(null);
    
    // Check if we're moving to a new level
    if (nextStage.level > level) {
      // Award 10x bite amount for completing a level
      const reward = biteAmount * 10;
      setChips(prevChips => prevChips + reward);
      
      // Double the level multiplier for joker prices
      setLevelMultiplier(prev => prev * 2);
      
      // Reset joker purchase status and active jokers when entering a new level
      setHasPurchasedJoker(false);
      setActiveJokers([]);
      
      showToast(`Advanced to Level ${nextStage.level}! Earned ${reward} chips! Joker prices have doubled!`, 'success');
    } else {
      // Keep jokers when moving to next stage within the same level
      setHasPurchasedJoker(false); // Only reset purchase status
    }
    
    // Show success toast
    showToast(`Advanced to ${nextStage.displayName}! New cards and deck prepared.`, 'success');
    
    // Update game stage
    setStage(nextStage.displayName);
    setLevel(nextStage.level);
    setAnte(nextStage.ante);
    
    // Reset hand and discard counts for the new stage
    setHandCount(0);
    setDiscardCount(0);
  };
  
  const [highestChips, setHighestChips] = useState(0);

  // 初始化时从localStorage获取最高chips
  useEffect(() => {
    const savedHighestChips = localStorage.getItem('highestChips');
    if (savedHighestChips) {
      setHighestChips(parseInt(savedHighestChips));
    }
  }, []);

  // 更新最高chips并保存到数据库
  const updateHighestChips = async (newChips) => {
    if (newChips > highestChips) {
      setHighestChips(newChips);
      localStorage.setItem('highestChips', newChips.toString());
      
      try {
        const username = localStorage.getItem('username');
        if (username) {
          await authService.updateUserData(username, { highestChips: newChips });
          console.log('Highest chips updated successfully');
        }
      } catch (error) {
        console.error('Error updating highest chips:', error);
      }
    }
  };

  // 在chips变化时检查是否需要更新最高值
  useEffect(() => {
    updateHighestChips(chips);
  }, [chips]);

  // 在游戏结束时保存一次
  const handleGameEnd = () => {
    updateHighestChips(chips);
  };

  const handlePlayHand = () => {
    if (selectedCards.length === 0) {
      // Use Toast instead of alert
      showToast('Please select cards to play', 'info');
      return;
    }
    
    // Update hand count
    const newHandCount = handCount + 1;
    setHandCount(newHandCount);
    
    // Evaluate hand and update score
    const evaluation = evaluateHand(selectedCards);
    if (evaluation) {
      const newScore = score + evaluation.totalScore;
      setScore(newScore);
      
      // Show success toast with score
      showToast(`Hand played! Score: +${evaluation.totalScore}`, 'success');
      
      // Check if we've met the target score for the current stage
      if (gameStages.length > 0) {
        const currentStageIndex = gameStages.findIndex(gameStage => 
          gameStage.displayName === stage && gameStage.level === level
        );
        
        if (currentStageIndex >= 0) {
          const currentStage = gameStages[currentStageIndex];
          
          // If we've reached or exceeded the target score
          if (newScore >= currentStage.targetScore) {
            // Move to the next stage
            if (currentStageIndex < gameStages.length - 1) {
              const nextStage = gameStages[currentStageIndex + 1];
              
              // Use the resetStage function to handle stage transition
              resetStage(nextStage);
              
              // Skip the rest of the hand processing since we've transitioned stages
              return;
            } else {
              // Game completed
              showToast('Congratulations! You completed all stages!', 'success');
            }
          }
        }
      }
    }
    
    // Move selected cards to discard pile
    setDiscardPile(prev => [...prev, ...selectedCards]);
    
    // Remove selected cards from player's hand
    const updatedHand = playerHand.filter(card => !selectedCards.some(c => c.id === card.id));
    
    // Draw new cards if available
    const cardsToDrawCount = selectedCards.length;
    if (deck.length >= cardsToDrawCount) {
      const newCards = deck.slice(0, cardsToDrawCount);
      const newDeck = deck.slice(cardsToDrawCount);
      
      // Ensure we're keeping exactly 8 cards in hand
      const newHand = [...updatedHand, ...newCards];
      
      setPlayerHand(newHand);
      setDeck(newDeck);
    } else {
      // Not enough cards in deck
      setPlayerHand(updatedHand);
      showToast('Not enough cards in the deck to draw!', 'warning');
    }
    
    // Clear selection after playing
    setSelectedCards([]);
    setHandEvaluation(null);
  };
  
  const handleDiscard = () => {
    if (selectedCards.length === 0) {
      // Use Toast instead of alert
      showToast('Please select cards to discard', 'info');
      return;
    }
    
    // Check that we're not trying to discard more than 5 cards
    if (selectedCards.length > 5) {
      // Use Toast instead of alert
      showToast('You cannot discard more than 5 cards at once', 'error');
      return;
    }
    
    // Limit discards to 4 per game phase
    if (discardCount >= 4) {
      // Use Toast instead of alert
      showToast('You have reached the maximum of 4 discards for this phase', 'error');
      return;
    }
    
    // Update discard count
    setDiscardCount(prev => prev + 1);
    
    // Move selected cards to discard pile
    setDiscardPile(prev => [...prev, ...selectedCards]);
    
    // Remove selected cards from player's hand
    const updatedHand = playerHand.filter(card => !selectedCards.some(c => c.id === card.id));
    
    // Draw new cards if available
    const cardsToDrawCount = selectedCards.length;
    if (deck.length >= cardsToDrawCount) {
      const newCards = deck.slice(0, cardsToDrawCount);
      const newDeck = deck.slice(cardsToDrawCount);
      
      // Ensure we're keeping exactly 8 cards in hand
      const newHand = [...updatedHand, ...newCards];
      
      setPlayerHand(newHand);
      setDeck(newDeck);
    } else {
      // Not enough cards in deck
      setPlayerHand(updatedHand);
      showToast('Not enough cards in the deck to draw!', 'warning');
    }
    
    // Clear selection after discarding
    setSelectedCards([]);
    setHandEvaluation(null);
    
    // Success notification
    showToast(`Discarded ${selectedCards.length} cards`, 'success');
  };
  
  // Add joker management functions
  const addJoker = (joker) => {
    setActiveJokers(prev => [...prev, joker]);
    setHasPurchasedJoker(true);
    showToast(`Added ${joker.name} to your jokers!`, 'success');
  };

  const removeJoker = (jokerName) => {
    setActiveJokers(prev => prev.filter(j => j.name !== jokerName));
    showToast(`Removed ${jokerName} from your jokers!`, 'warning');
  };

  // Reset joker purchase status when stage changes
  const [hasPurchasedJoker, setHasPurchasedJoker] = useState(false);
  useEffect(() => {
    setHasPurchasedJoker(false);
  }, [stage]);

  // Add joker types constant
  const JOKER_TYPES = [
    {
      name: "Standard Joker",
      effect: "Basic joker that adds +1 multiplier",
      multiplier: 1,
      baseCost: 25,
      activeSuit: null,
      rarity: "COMMON",
      activationType: "INDEPENDENT",
      unlockRequirement: "Available after first round with sufficient chips."
    },
    {
      name: "Scary Face",
      effect: "Played face cards (J, Q, K) give +30 Chips when scored",
      multiplier: 0,
      baseCost: 35,
      activeSuit: null,
      rarity: "COMMON",
      activationType: "ON_SCORED",
      unlockRequirement: "Available after first round with sufficient chips."
    },
    {
      name: "Greedy Joker",
      effect: "When scoring a hand with 5 Diamond cards, gives +3 Mult",
      multiplier: 3,
      baseCost: 40,
      activeSuit: "Diamonds",
      rarity: "COMMON",
      activationType: "ON_SCORED",
      unlockRequirement: "Available after first round with sufficient chips."
    },
    {
      name: "Lusty Joker",
      effect: "When scoring a hand with 5 Heart cards, gives +3 Mult",
      multiplier: 3,
      baseCost: 40,
      activeSuit: "Hearts",
      rarity: "COMMON",
      activationType: "ON_SCORED",
      unlockRequirement: "Available after first round with sufficient chips."
    },
    {
      name: "Wrathful Joker",
      effect: "When scoring a hand with 5 Spade cards, gives +3 Mult",
      multiplier: 3,
      baseCost: 40,
      activeSuit: "Spades",
      rarity: "COMMON",
      activationType: "ON_SCORED",
      unlockRequirement: "Available after first round with sufficient chips."
    },
    {
      name: "Gluttonous Joker",
      effect: "When scoring a hand with 5 Club cards, gives +3 Mult",
      multiplier: 3,
      baseCost: 40,
      activeSuit: "Clubs",
      rarity: "COMMON",
      activationType: "ON_SCORED",
      unlockRequirement: "Available after first round with sufficient chips."
    }
  ];

  // Add joker shop UI
  const renderJokerShop = () => {
    if (gamePhase !== 'Playing Cards') return null;

    // Check if player has reached target score
    const currentStage = gameStages.find(s => s.displayName === stage && s.level === level);
    const hasReachedTarget = currentStage && score >= currentStage.targetScore;

    // Don't show shop if target is reached
    if (hasReachedTarget) return null;

    return (
      <div className="mt-4 p-4 bg-gray-800 rounded-lg">
        <h3 className="text-lg font-bold text-purple-400 mb-4">Joker Shop</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {JOKER_TYPES.map((joker, index) => (
            <div key={index} className="p-3 bg-gray-700 rounded-lg">
              <div className="flex justify-between items-start">
                <div>
                  <h4 className="font-bold text-white">{joker.name}</h4>
                  <p className="text-sm text-gray-300">{joker.effect}</p>
                  {joker.activeSuit && (
                    <span className="text-xs text-yellow-400">({joker.activeSuit})</span>
                  )}
                </div>
                <div className="text-right">
                  <p className="text-sm text-gray-400">Cost: {getJokerCost(joker.baseCost)} chips</p>
                  <p className="text-xs text-gray-500">{joker.rarity}</p>
                </div>
              </div>
              <button
                onClick={() => {
                  const currentCost = getJokerCost(joker.baseCost);
                  if (chips >= currentCost) {
                    setChips(prev => prev - currentCost);
                    addJoker(joker);
                  } else {
                    showToast(`Not enough chips! Need ${currentCost} chips to buy ${joker.name}`, 'error');
                  }
                }}
                className="mt-2 w-full bg-purple-600 hover:bg-purple-700 text-white py-1 px-3 rounded text-sm"
                disabled={chips < getJokerCost(joker.baseCost) || hasPurchasedJoker}
              >
                {hasPurchasedJoker ? "Already Purchased" : chips < getJokerCost(joker.baseCost) ? "Not Enough Chips" : "Buy"}
              </button>
            </div>
          ))}
        </div>
      </div>
    );
  };

  // Add joker management UI
  const renderJokerManagement = () => {
    if (activeJokers.length === 0) return null;

    return (
      <div className="mt-4 p-4 bg-gray-800 rounded-lg">
        <h3 className="text-lg font-bold text-purple-400 mb-4">Your Jokers</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {activeJokers.map((joker, index) => (
            <div key={index} className="p-3 bg-gray-700 rounded-lg">
              <div className="flex justify-between items-start">
                <div>
                  <h4 className="font-bold text-white">{joker.name}</h4>
                  <p className="text-sm text-gray-300">{joker.effect}</p>
                  {joker.activeSuit && (
                    <span className="text-xs text-yellow-400">({joker.activeSuit})</span>
                  )}
                </div>
                <button
                  onClick={() => removeJoker(joker.name)}
                  className="bg-red-600 hover:bg-red-700 text-white py-1 px-3 rounded text-sm"
                >
                  Remove
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  };
  
  return (
    <div className="h-full overflow-auto bg-gradient-to-b from-blue-950 to-purple-950 text-white">
      {/* Toast组件 */}
      <Toast 
        message={toast.message} 
        isVisible={toast.visible} 
        onClose={hideToast} 
        type={toast.type}
      />
      
      <div className="min-h-screen relative overflow-hidden">
        {/* Balatro Background from React Bits */}
        <div className="absolute inset-0 z-0">
          <Balatro
            isRotate={false}
            mouseInteraction={true}
            pixelFilter={700}
          />
        </div>
        
        <div className="relative z-10 min-h-screen text-white p-4">
          <div className="max-w-7xl mx-auto">
            {/* Header with game controls */}
            <div className="flex justify-between items-center mb-6 bg-gray-800 bg-opacity-80 p-4 rounded-lg backdrop-blur-sm shadow-lg">
              <div className="flex-shrink-0">
                <FuzzyText 
                  baseIntensity={0.09} 
                  hoverIntensity={0.19} 
                  enableHover={enableHover}
                  fontSize="clamp(1.5rem, 3vw, 2.5rem)"
                  color="rgba(255, 100, 150, 1)"
                >
                  Mini Balatro Web
                </FuzzyText>
              </div>
              <div className="flex gap-4">
                {localStorage.getItem('username') && (
                  <div className="flex items-center gap-4">
                    <span className="text-white">
                      Welcome, {localStorage.getItem('username')}!
                    </span>
                    <span className="text-yellow-400">
                      Highest Chips: {highestChips}
                    </span>
                    <button
                      onClick={() => {
                        authService.logout();
                        window.location.href = '/';
                      }}
                      className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg transition-colors"
                    >
                      Logout
                    </button>
                  </div>
                )}
                <button 
                  onClick={() => navigate('/')}
                  className="px-4 py-2 bg-gray-700 hover:bg-gray-600 rounded-lg transition-colors shadow-md"
                >
                  Home
                </button>
                <button 
                  onClick={() => navigate('/ranking')}
                  className="px-4 py-2 bg-blue-700 hover:bg-blue-400 rounded-lg transition-colors shadow-md"
                >
                  Ranking
                </button>
              </div>
            </div>
            
            {/* Bite Selection Screen */}
            {gamePhase === 'Bite Selection' && (
              <div className="bg-gray-800 bg-opacity-80 backdrop-blur-sm p-8 rounded-xl shadow-lg mx-auto max-w-2xl text-center">
                <h2 className="text-3xl font-bold mb-6 bg-clip-text text-transparent bg-gradient-to-r from-yellow-400 to-purple-500">Select Your Bite Amount</h2>
                
                <p className="text-gray-300 mb-8">
                  Your bite amount will be deducted from your initial 100 chips.
                  <br />When you complete a level, you'll receive 10x your bite amount as a reward!
                </p>
                
                <div className="flex justify-center gap-6 mb-8">
                  <button 
                    onClick={() => handleBiteSelection(10)}
                    className="px-6 py-4 bg-gradient-to-r from-blue-700 to-blue-900 hover:from-blue-600 hover:to-blue-800 rounded-lg font-bold text-xl transition-all shadow-lg"
                  >
                    10 Chips
                  </button>
                  
                  <button 
                    onClick={() => handleBiteSelection(50)}
                    className="px-6 py-4 bg-gradient-to-r from-green-700 to-green-900 hover:from-green-600 hover:to-green-800 rounded-lg font-bold text-xl transition-all shadow-lg"
                  >
                    50 Chips
                  </button>
                  
                  <button 
                    onClick={() => handleBiteSelection(100)}
                    className="px-6 py-4 bg-gradient-to-r from-purple-700 to-purple-900 hover:from-purple-600 hover:to-purple-800 rounded-lg font-bold text-xl transition-all shadow-lg"
                  >
                    100 Chips
                  </button>
                </div>
              </div>
            )}
            
            {/* Game Content - Only shown after bite selection */}
            {gamePhase === 'Playing Cards' && (
              <>
                {/* Game progress dashboard */}
                <div className="mb-6 bg-gray-800 bg-opacity-80 backdrop-blur-sm p-4 rounded-xl shadow-lg">
                  <div className="grid grid-cols-2 md:grid-cols-6 gap-3 text-center">
                    <div>
                      <h3 className="text-gray-400 text-xs font-semibold">PHASE</h3>
                      <p className="text-white font-bold">{gamePhase}</p>
                    </div>
                    
                    <div>
                      <h3 className="text-gray-400 text-xs font-semibold">LEVEL</h3>
                      <p className="text-white font-bold">{level}/3</p>
                    </div>
                    
                    <div>
                      <h3 className="text-gray-400 text-xs font-semibold">STAGE</h3>
                      <p className="text-white font-bold">{stage}</p>
                    </div>
                    
                    <div>
                      <h3 className="text-gray-400 text-xs font-semibold">ANTE</h3>
                      <p className="text-white font-bold">{ante}</p>
                    </div>
                    
                    <div>
                      <h3 className="text-gray-400 text-xs font-semibold">HANDS</h3>
                      <p className="text-white font-bold">{handCount}/4</p>
                    </div>
                    
                    <div>
                      <h3 className="text-gray-400 text-xs font-semibold">DISCARDS</h3>
                      <p className="text-white font-bold">{discardCount}/4</p>
                    </div>
                  </div>
                </div>
                
                {/* Game stats & dashboard */}
                <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
                  <div className="bg-gray-800 bg-opacity-80 backdrop-blur-sm p-6 rounded-xl shadow-lg border border-yellow-500/20">
                    <h2 className="text-xl font-bold mb-2 text-center text-gray-400">Score</h2>
                    <p className="text-4xl font-bold text-yellow-400 text-center">{score}</p>
                    {gameStages.length > 0 && (
                      <p className="text-sm text-gray-400 text-center mt-2">
                        Target: {gameStages.find(s => s.displayName === stage && s.level === level)?.targetScore || '-'}
                      </p>
                    )}
                  </div>
                  
                  <div className="bg-gray-800 bg-opacity-80 backdrop-blur-sm p-6 rounded-xl shadow-lg border border-blue-500/20">
                    <h2 className="text-xl font-bold mb-2 text-center text-gray-400">Chips</h2>
                    <p className="text-4xl font-bold text-green-400 text-center">{chips}</p>
                    <p className="text-sm text-gray-400 text-center mt-2">
                      Bite: {biteAmount}
                    </p>
                  </div>
                  
                  <div className="bg-gray-800 bg-opacity-80 backdrop-blur-sm p-6 rounded-xl shadow-lg border border-green-500/20">
                    <h2 className="text-xl font-bold mb-2 text-center text-gray-400">Deck</h2>
                    <p className="text-4xl font-bold text-blue-400 text-center">{deck.length}</p>
                  </div>
                  <div className="bg-gray-800 bg-opacity-80 backdrop-blur-sm p-6 rounded-xl shadow-lg border border-green-500/20">
                  <h2 className="text-xl font-bold mb-2 text-center text-gray-400">Current Hand</h2>
                  {/* <h2 className="text-2xl font-bold mb-3 text-center bg-clip-text text-transparent bg-gradient-to-r from-purple-400 to-pink-500">Current Hand</h2> */}
                  {selectedCards.length > 0 && handEvaluation ? (
                    <div className="text-center">
                      <p className="text-s font-bold mb-3 text-center bg-clip-text text-transparent bg-gradient-to-r from-purple-400 to-pink-500">
                        {handEvaluation.handType}
                      </p>
                      <p className="text-sm font-bold text-white mb-2">
                        (H: {handEvaluation.baseScore} + C: {handEvaluation.cardsValue}) × Mul: {handEvaluation.multiplier}
                        {activeJokers.length > 0 && (
                          <span className="text-purple-400">
                            {activeJokers.map(joker => {
                              if (joker.activationType === 'INDEPENDENT') {
                                return ` (+${joker.multiplier} from ${joker.name})`;
                              } else if (joker.activationType === 'ON_SCORED') {
                                if (joker.activeSuit) {
                                  const matchingCards = selectedCards.filter(card => card.suit === joker.activeSuit);
                                  if (matchingCards.length >= 5) {
                                    return ` (+${joker.multiplier} from ${joker.name})`;
                                  }
                                } else if (joker.name === 'Scary Face') {
                                  const faceCards = selectedCards.filter(card => ['J', 'Q', 'K'].includes(card.rank));
                                  if (faceCards.length > 0) {
                                    return ` (+${faceCards.length * 30} Chips from ${joker.name})`;
                                  }
                                }
                              }
                              return '';
                            }).join('')}
                          </span>
                        )}
                        = <span className="text-yellow-400 font-bold">{handEvaluation.totalScore}</span>
                      </p>
                    </div>
                  ) : (
                    <p className="text-center text-gray-400 italic">Select cards to evaluate your hand</p>
                  )}
                  </div>
                </div>
                
                {/* Hand section */}
                <div className="mb-8">
                  <h2 className="text-2xl font-bold mb-4 text-center bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-indigo-500">Your Hand</h2>
                  
                  <div className="flex flex-wrap gap-3 justify-center mb-6">
                    {playerHand.map(card => (
                      <Card
                        key={card.id}
                        rank={card.rank}
                        suit={card.suit}
                        selected={selectedCards.some(c => c.id === card.id)}
                        onClick={() => handleCardSelect(card)}
                      />
                    ))}
                  </div>
                  
                  <div className="flex justify-center gap-4 mt-8">
                    <button 
                      onClick={handlePlayHand}
                      className="px-8 py-3 bg-gradient-to-r from-green-600 to-green-800 hover:from-green-500 hover:to-green-700 rounded-lg font-bold transition-all shadow-lg"
                      disabled={selectedCards.length === 0}
                    >
                      Play Hand
                    </button>
                    <button 
                      onClick={handleDiscard}
                      className="px-8 py-3 bg-gradient-to-r from-red-700 to-red-900 hover:from-red-600 hover:to-red-800 rounded-lg font-bold transition-all shadow-lg"
                      disabled={discardCount >= 4 || selectedCards.length === 0}
                    >
                      Discard ({4 - discardCount} left)
                    </button>
                  </div>
                </div>

                {/* Joker Management - Always show if there are active jokers */}
                {renderJokerManagement()}
                
                {/* Discard pile */}
                <div className="relative mb-6">
                  <h2 className="text-2xl font-bold mb-4 text-center bg-clip-text text-transparent bg-gradient-to-r from-red-400 to-pink-500">Discard Pile</h2>
                  <div className="bg-gray-800 bg-opacity-50 backdrop-blur-sm rounded-xl p-6 min-h-36 flex flex-wrap items-center justify-center gap-2 shadow-lg border border-gray-700">
                    {discardPile.length === 0 ? (
                      <p className="text-gray-500 italic">No cards discarded yet</p>
                    ) : (
                      discardPile.map(card => (
                        <div key={card.id} className="transform rotate-6 translate-y-6">
                          <Card 
                            rank={card.rank} 
                            suit={card.suit} 
                            disabled={true}
                          />
                        </div>
                      ))
                    )}
                  </div>
                </div>
                
                {/* Joker Shop - Show only when conditions are met */}
                {renderJokerShop()}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default GameBoard; 