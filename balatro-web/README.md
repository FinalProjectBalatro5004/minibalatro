# Mini Balatro Web

A React-based web implementation of the Balatro card game that simulates the core gameplay mechanics of the popular poker-based roguelike.

## Project Overview

Mini-Balatro Web is a web-based version of the Balatro card game, developed with modern web technologies. It preserves the core poker-based gameplay experience while offering a responsive, browser-based interface accessible to all players.

**Important Note**: This web implementation is directly adapted from our original Java/JavaFX codebase. Rather than completely rewriting the game logic, we've carefully ported and translated the existing Java functionality to JavaScript/React, maintaining the same core algorithms and game mechanics. This approach ensures consistency between both versions while taking advantage of web technologies for broader accessibility.

### Key Features

- **Core Card Mechanics**
  - Standard 52-card deck with proper suits and ranks
  - Card selection and discard functionality
  - Hand evaluation and scoring based on poker rules
  - Deck management with remaining card count display

- **Game Progression**
  - Three levels with three stages each (Small Blind, Big Blind, The Hook)
  - Increasing score targets for each stage
  - Chip management with bite/ante system and rewards
  - Limits on hands played and discards per stage

- **Enhanced User Interface**
  - Interactive card selection with visual feedback
  - Scrollable discard pile
  - Dynamic FuzzyText title effect
  - Responsive design that works on various screen sizes

## Technical Stack

- **React**: For building the UI components
- **Tailwind CSS**: For styling and responsive design
- **Framer Motion**: For animations and transitions
- **Vite**: For fast development and optimized production builds

## Relationship to Java Implementation

The web version of Mini Balatro maintains direct correlations to our Java codebase:

- **Model Translation**: Card, Deck, and Hand logic from Java classes has been translated to JavaScript equivalents
- **Game Flow Logic**: The state management in React follows the same patterns established in our GameService.java and GameStateManager.java
- **Algorithms Preserved**: Key algorithms like hand evaluation, deck shuffling, and score calculation were ported directly from Java
- **Visual Components**: While reimagined for web, the visual components follow the same structure and organization as our JavaFX views

This approach allowed us to:
1. Maintain consistency across platforms
2. Avoid duplicating development effort
3. Leverage our well-tested game logic
4. Focus on optimizing the UI for web rather than rewriting core functionality

## Project Structure

```
balatro-web/
├── src/
│   ├── components/
│   │   ├── Balatro.jsx              # Animated background component
│   │   ├── ui/
│   │   │   ├── FuzzyText.jsx        # Text effect component
│   │   │   └── Toast.jsx            # Notification component
│   │   └── game/
│   │       ├── Card.jsx             # Card component
│   │       └── GameBoard.jsx        # Main game board component
│   ├── services/
│   │   ├── gameService.js           # Game data service
│   │   └── handEvaluationService.js # Hand evaluation service
│   ├── pages/
│   │   └── ...                      # Page components
│   ├── assets/                      # Static assets
│   ├── App.jsx                      # Main application component
│   ├── App.css                      # Global styles
│   ├── index.css                    # Tailwind CSS imports
│   └── main.jsx                     # Entry point
├── public/                          # Public assets
├── index.html                       # HTML template
├── package.json                     # Dependencies and scripts
├── vite.config.js                   # Vite configuration
└── tailwind.config.js               # Tailwind CSS configuration
```

## Key Components and Functions

### Game Components

#### `GameBoard.jsx` - Main Game Component

This is the core component that manages the entire game state and UI, adapted from our Java GameService and GameStateManager classes.

- **State Management Functions**:
  - `useState` hooks manage game state (score, chips, deck, hand, etc.)
  - `useEffect` hooks handle initialization and state validation

- **Game Logic Functions**:
  - `generateDeck()`: Creates a standard 52-card deck (adapted from Deck.java)
  - `shuffleDeck(deck)`: Implements Fisher-Yates shuffle algorithm (adapted from Deck.java)
  - `getRankValue(rank)`: Converts card ranks to numerical values for comparisons
  - `evaluateHand(cards)`: Evaluates poker hand types and calculates scores (adapted from Hand.java)
  - `validateHandSize(hand)`: Ensures the player's hand always has exactly 8 cards

- **Game Flow Functions**:
  - `handleBiteSelection(amount)`: Processes the initial ante/bite selection
  - `startNewGame()`: Initializes a new game with fresh deck and state
  - `resetStage(nextStage)`: Handles stage transitions with deck reset
  - `handlePlayHand()`: Processes playing a hand and scoring
  - `handleDiscard()`: Manages discarding and drawing new cards
  - `handleCardSelect(card)`: Manages card selection for play or discard

- **UI Helper Functions**:
  - `showToast(message, type)`: Displays notifications to the player
  - `hideToast()`: Removes notifications

#### `Card.jsx` - Card Component

Renders individual playing cards with proper suits, ranks, and selection effects. Adapted from our CardView.java class.

- `Card({ rank, suit, selected, disabled, onClick })`: Renders a card with appropriate styling
- Handles card selection visual effects and click events

### UI Components

#### `FuzzyText.jsx` - Text Effect Component

Creates a dynamic "fuzzy" text effect used for the game title.

- `FuzzyText({ children, fontSize, fontWeight, color, baseIntensity, hoverIntensity, enableHover })`: Renders text with canvas-based effects
- `useEffect` hook sets up canvas rendering and animation
- Responds to mouse hover with intensity changes

#### `Toast.jsx` - Notification Component

Displays temporary notifications to the player.

- `Toast({ message, isVisible, onClose, type })`: Renders notification messages
- Automatically hides after a set duration
- Supports different types (success, warning, error, info)

#### `Balatro.jsx` - Background Effect Component

Creates the animated background effect seen throughout the game.

- Renders a dynamic, interactive background
- Responds to mouse movements for parallax effects

### Service Components

#### `gameService.js` - Game Data Service

Manages API interactions for game data, adapted from Java service classes.

- `getGameStages()`: Fetches game stage configurations
- `getStageTransitionRules()`: Fetches stage transition rules

#### `handEvaluationService.js` - Hand Evaluation Service

Provides card hand evaluation functionality, directly adapted from Hand.java.

- `evaluateHand(cards)`: Evaluates a hand of cards to determine its type and score

## Game Mechanics

### Bite/Ante System

- Players start with 100 chips and select a bite amount (10, 50, or 100)
- The selected bite is deducted from their starting chips
- When completing a level, players earn 4x their bite amount

### Hand Evaluation

The game evaluates poker hands with the following values:
- **High Card**: 5 points × 1
- **Pair**: 10 points × 2
- **Two Pair**: 20 points × 2
- **Three of a Kind**: 30 points × 3
- **Straight**: 30 points × 4
- **Flush**: 35 points × 4
- **Full House**: 40 points × 4
- **Four of a Kind**: 60 points × 7
- **Straight Flush**: 100 points × 8

The total score for a hand is: (Hand base score + Sum of card values) × Multiplier

### Game Progression

- Each stage has a target score to achieve
- Upon reaching the target score, the player advances to the next stage
- The score resets to 0, and a new deck is shuffled for each new stage
- Each level consists of three stages: Small Blind, Big Blind, and The Hook
- Level difficulty increases with higher target scores

## Running the Application

### Prerequisites

- Node.js 16.0 or later
- npm 7.0 or later

### Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/FinalProjectBalatro5004/minibalatro.git
   cd mini-balatro/balatro-web
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

4. Open your browser and navigate to:
   ```
   http://localhost:5173
   ```

### Building for Production

1. Create a production build:
   ```bash
   npm run build
   ```

2. Preview the production build:
   ```bash
   npm run preview
   ```

## Contributing

Contributions to the Mini Balatro Web project are welcome. Please follow these steps:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

