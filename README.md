# Mini-Balatro

A JavaFX-based implementation of the Balatro card game that simulates the core gameplay mechanics of the popular poker-based roguelike, with plans for expanded online capabilities and cloud integration.

## Project Overview

Mini-Balatro reimagines the popular roguelike deck-building game with a focus on adding online features and community interaction that modern gamers expect. Our implementation:

- Preserves the core poker-based roguelike gameplay experience
- Adds online capabilities and global ranking
- Showcases advanced Object-Oriented Design principles
- Provides a more connected gaming experience

### Target Audience
- Existing Balatro fans looking for expanded features
- New players seeking a more connected gaming experience

## Features

- **Core Card Mechanics**:
  - Standard 52-card deck with proper suits and ranks
  - Card selection and discard functionality
  - Hand evaluation and scoring based on poker rules
  - Deck management with remaining card count display

- **Game Progression**:
  - Three levels with three stages each (Small Blind, Big Blind, The Hook)
  - Increasing score targets for each stage
  - Chip management with ante and rewards
  - Limits on hands played and discards per stage

- **Enhanced User Interface**:
  - Card rendering with suits and ranks
  - Interactive card selection with visual feedback (green glow for selected cards)
  - Scrollable discard pile to manage UI space
  - Deck viewer overlay showing card distribution
  - Game state information display (chips, score, stage)

- **Online Features**:
  - User account system
  - Global leaderboards
  - Statistics tracking
  - Cloud save functionality

## Technical Architecture

Our implementation showcases advanced OOD principles through:
- Proper separation of concerns using MVC architecture
- Strategic application of design patterns (Observer for game state changes, Factory for card creation)
- Clean interfaces and well-defined class hierarchies
- Comprehensive documentation and testing

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── balatro/
│   │           ├── controller/
│   │           │   ├── GameController.java    # Responsible for handling the game state and transitions.  
│   │           │   ├── HandEvaluationController.java # Responsible for evaluating the hand and returning the hand type, score, and other details. 
│   │           ├── model/
│   │           │   ├── ActivationType.java  # Defines types of joker activation
│   │           │   ├── Card.java            # Represents a playing card
│   │           │   ├── Deck.java            # Manages the deck of cards
│   │           │   ├── Hand.java            # Handles card combinations and scoring
│   │           │   ├── HandType.java        # Defines poker hand types
│   │           │   ├── Joker.java           # Represents special joker cards
│   │           │   ├── JokerType.java       # Defines different joker types
│   │           │   ├── Player.java          # Manages player state
│   │           │   └── RarityType.java      # Defines rarity levels
│   │           │
│   │           ├── view/
│   │           │   ├── CardView.java            # Visual representation of cards
│   │           │   ├── DeckViewerOverlay.java   # Shows deck distribution
│   │           │   └── GameView.java            # Main game interface
│   │           │
│   │           ├── service/
│   │           │   ├── GameService.java         # Core game logic
│   │           │   └── GameStateManager.java    # Manages game progression
│   │           │
│   │           └── BalatroGame.java            # Main application class
│   │
│   └── resources/
│       ├── styles/
│       │   └── game.css                        # CSS styling for the game
│       ├── static/                             # Static resources
│       ├── templates/                          # HTML templates
│       └── application.properties              # Application configuration
│
└── test/
    └── java/
        └── com/
            └── balatro/
                ├── controller/                 # Tests for controller classes            
                ├── model/                      # Tests for model classes
                ├── view/                       # Tests for view classes
                ├── service/                    # Tests for service classes
                └── BalatroGameTest.java        # Integration tests
```

## Key Components
### Controller Classes

1. **GameController.java**: Responsible for handling the game state and transitions as a REST API controller
   - Properties: RESTful endpoints for game stages and transitions
   - Methods:
     - `getGameStages()` - Returns all game stages information with their properties
     - `getStageTransitionRules()` - Returns game stage transition rules, including score reset behavior

2. **HandEvaluationController.java**: Responsible for evaluating poker hands and calculating scores via REST API
   - Properties: RESTful endpoints for hand evaluation
   - Methods:
     - `evaluateHand(EvaluateHandRequest request)` - Evaluates a list of cards and returns the hand type, score, and other details
     - `calculateCardValue(String rank)` - Calculates the value of a card based on its rank
     - Inner classes:
       - `EvaluateHandRequest` - DTO for hand evaluation request with card list
       - `CardDto` - DTO for card information with rank and suit

### Model Classes

1. **Card.java**: Represents a playing card with suit, rank, and associated values
   - Properties: suit, rank, value
   - Methods:
     - `Card(String suit, String rank, int value)` - Constructor
     - `getSuit()` - Gets the suit of the card
     - `getRank()` - Gets the rank of the card
     - `getValue()` - Gets the numerical value of the card
     - `isFaceCard()` - Checks if the card is a face card (J, Q, K)
     - `getColor()` - Gets the color of the card (Red/Black)
     - `validateSuit(String suit)` - Validates the suit
     - `validateRank(String rank)` - Validates the rank
     - `validateValue(int value)` - Validates the value
     - `equals(Object obj)` - Compares cards for equality
     - `hashCode()` - Generates a hash code for the card
     - `toString()` - Returns a string representation of the card

2. **Deck.java**: Manages the deck of cards, including shuffling, drawing, and tracking
   - Properties: cards, random, isNewRound, currentScore
   - Methods:
     - `Deck()` - Constructor for a standard deck
     - `Deck(List<Card> cards)` - Constructor with specified cards
     - `createStandardDeck()` - Creates a standard 52-card deck
     - `resetDeck()` - Resets to a standard 52-card deck
     - `shuffle()` - Shuffles all cards in the deck
     - `startNewRound(int requiredScore)` - Starts a new round with a target score
     - `updateScore(int score)` - Updates the current score
     - `canStartNewRound(int requiredScore)` - Checks if a new round can be started
     - `drawCard(int numCardsDiscarded, List<Card> keptCards)` - Draws cards from the deck
     - `getCardCount()` - Gets the number of cards in the deck
     - `isEmpty()` - Checks if the deck is empty
     - `getCards()` - Gets all cards in the deck (unmodifiable)
     - `getMutableCards()` - Gets a mutable list of all cards
     - `isNewRound()` - Checks if this is a new round

3. **Hand.java**: Handles the player's current hand, evaluation, and scoring
   - Properties: cards, handType, baseScore, multiplier
   - Methods:
     - `Hand()` - Default constructor
     - `addCard(Card card)` - Adds a card to the hand
     - `discardCards(List<Card> cardsToDiscard)` - Discards specified cards from the hand
     - `drawNewCards(List<Card> newCards)` - Adds new cards to the hand after discarding
     - `initializeHand(List<Card> initialCards)` - Initializes the hand with a set of cards
     - `getMinCardsToPlay()` - Gets the minimum number of cards that must be played
     - `getMaxCardsToPlay()` - Gets the maximum number of cards that can be played
     - `getMinCardsToDiscard()` - Gets the minimum number of cards that must be discarded
     - `getMaxCardsToDiscard()` - Gets the maximum number of cards that can be discarded
     - `getMaxCards()` - Gets the maximum number of cards allowed in a hand
     - `getMinCards()` - Gets the minimum number of cards allowed in a hand
     - `canGetCards()` - Checks if the player can get more cards
     - `isValidHand()` - Checks if the current hand is valid for playing
     - `getRemainingCardSlots()` - Gets the number of card slots remaining
     - `meetsMinimumRequirements()` - Checks if hand meets minimum requirements
     - `meetsMaximumRequirements()` - Checks if hand meets maximum requirements
     - `getCards()` - Gets all cards in the hand (unmodifiable)
     - `getMutableCards()` - Gets a mutable list of cards (for initialization)
     - `getCardCount()` - Gets the number of cards in the hand
     - `getHandType()` - Gets the current hand type
     - `getBaseScore()` - Gets the base score for the hand
     - `getMultiplier()` - Gets the multiplier for the hand
     - `getTotalScore()` - Gets the total score based on Balatro rules
     - `evaluateHand()` - Evaluates the hand to determine its type
     - `isStraightFlush()` - Checks for a straight flush
     - `isFourOfAKind()` - Checks for four of a kind
     - `isFullHouse()` - Checks for a full house
     - `isFlush()` - Checks for a flush
     - `isStraight()` - Checks for a straight
     - `isThreeOfAKind()` - Checks for three of a kind
     - `isTwoPair()` - Checks for two pair
     - `isPair()` - Checks for a pair
     - `calculateHighCardScore()` - Calculates the score for a high card hand
     - `getRankValue(String rank)` - Gets the numerical rank value for straight evaluation
     - `getPointValue(String rank)` - Gets the point value for scoring
     - `getRankCounts()` - Counts occurrences of each rank in the hand
     - `toString()` - Returns a string representation of the hand
     - `removeCard(Card card)` - Removes a card from the hand


4. **Player.java**: Manages player state, including chips and cards
   - Properties: playerId, username, hand, score, isActive, chips
   - Methods:
     - `Player(String username)` - Constructor with player name
     - `getPlayerId()` - Gets the player's unique ID
     - `getUsername()` - Gets the player's username
     - `setUsername(String username)` - Sets the player's username
     - `getHand()` - Gets the player's hand of cards
     - `addCard(Card card)` - Adds a card to the player's hand
     - `removeCard(Card card)` - Removes a card from the player's hand
     - `getScore()` - Gets the player's score
     - `updateScore(int points)` - Updates the player's score
     - `isActive()` - Checks if the player is active
     - `setActive(boolean active)` - Sets the player's active status
     - `clearHand()` - Clears all cards from the player's hand
     - `getHandSize()` - Gets the number of cards in the player's hand
     - `getChips()` - Gets the player's chip count
     - `setChips(int chips)` - Sets the player's chip count
     - `toString()` - Returns a string representation of the player

5. **ActivationType.java**: Defines when a joker's effect activates
   - Enum Values: INDEPENDENT, ON_SCORED
   - Properties: displayName
   - Methods:
     - `ActivationType(String displayName)` - Constructor
     - `getDisplayName()` - Gets the display name of the activation type

6. **HandType.java**: Defines different types of poker hands and their scoring values
   - Enum Values: HIGH_CARD, PAIR, TWO_PAIR, THREE_OF_A_KIND, STRAIGHT, FLUSH, FULL_HOUSE, FOUR_OF_A_KIND, STRAIGHT_FLUSH
   - Properties: displayName, baseScore, multiplier
   - Methods:
     - `HandType(String displayName, int baseScore, int multiplier)` - Constructor
     - `getDisplayName()` - Gets the display name of the hand type
     - `getBaseScore()` - Gets the base score for this hand type
     - `getMultiplier()` - Gets the multiplier for this hand type

7. **Joker.java**: Represents a special joker card with unique effects
   - Properties: type, multiplier, activationType, rarityType
   - Methods:
     - `Joker(JokerType type, int multiplier, ActivationType activationType, RarityType rarityType)` - Constructor
     - `getType()` - Gets the type of this joker
     - `getMultiplier()` - Gets the multiplier value of this joker
     - `getActivationType()` - Gets when this joker's effect activates
     - `getRarityType()` - Gets the rarity level of this joker
     - `equals(Object obj)` - Compares jokers for equality
     - `hashCode()` - Generates a hash code for the joker
     - `toString()` - Returns a string representation of the joker

8. **JokerType.java**: Defines different types of joker cards with their effects
   - Enum Values: STANDARD_JOKER, GREEDY_JOKER, LUSTY_JOKER, WRATHFUL_JOKER, GLUTTONOUS_JOKER, SCARY_FACE, FIBONACCI, LUCKY_JOKER
   - Properties: name, effect, multiplier, cost, activeSuit, rarity, activationType, unlockRequirement
   - Methods:
     - `JokerType(String name, String effect, int multiplier, int cost, String activeSuit, RarityType rarity, ActivationType activationType, String unlockRequirement)` - Constructor
     - `getName()` - Gets the name of the joker type
     - `getEffect()` - Gets the effect description
     - `getMultiplier()` - Gets the multiplier value
     - `getCost()` - Gets the cost in chips
     - `getActiveSuit()` - Gets the suit this joker affects (if any)
     - `getRarity()` - Gets the rarity level
     - `getActivationType()` - Gets when this joker's effect activates
     - `getUnlockRequirement()` - Gets the requirements to unlock this joker

9. **RarityType.java**: Defines rarity levels for joker cards
   - Enum Values: COMMON, UNCOMMON, RARE, LEGENDARY
   - Properties: displayName
   - Methods:
     - `RarityType(String displayName)` - Constructor
     - `getDisplayName()` - Gets the display name of the rarity level

### Service Classes

1. **GameService.java**: Core game logic for card interactions and scoring
   - Properties: deck, playerHand, discardPile, selectedCards, gameState, score, round, targetScore, currentHandTypeDisplay, canDrawCards, cardsToDrawCount, roundCompleted, remainingCards, currentJoker
   - Methods:
     - `GameService()` - Default constructor
     - `initializeGame()` - Initializes the game state
     - `generateRandomJoker()` - Generates a random joker for the current round
     - `getCurrentJoker()` - Gets the current joker
     - `applyJokerEffects(int baseScore)` - Applies joker effects to the hand score
     - `hasConsecutiveFibonacci(List<Integer> cardValues, int requiredCount)` - Checks for consecutive Fibonacci numbers
     - `dealInitialHand()` - Deals the initial hand to the player
     - `selectCard(Card card)` - Selects a card for play
     - `deselectCard(Card card)` - Deselects a card
     - `updateCurrentHandTypeDisplay()` - Updates the display of the current hand type
     - `discardSelectedCards()` - Discards selected cards
     - `drawCards()` - Draws cards from the deck
     - `evaluateHand()` - Evaluates the selected cards
     - `startNewRound()` - Starts a new round
     - `getGameState()` - Gets the current game state
     - `gameStateProperty()` - Gets the game state property
     - `getPlayerHand()` - Gets the player's current hand
     - `getDiscardPile()` - Gets the discard pile
     - `getSelectedCards()` - Gets the currently selected cards
     - `getScore()` - Gets the current score
     - `scoreProperty()` - Gets the score property
     - `getRound()` - Gets the current round
     - `roundProperty()` - Gets the round property
     - `getTargetScore()` - Gets the target score
     - `setTargetScore(int targetScore)` - Sets the target score
     - `targetScoreProperty()` - Gets the target score property
     - `getCurrentHandTypeDisplay()` - Gets text describing the current hand type
     - `currentHandTypeDisplayProperty()` - Gets the hand type display property
     - `getCanDrawCards()` - Checks if cards can be drawn
     - `canDrawCardsProperty()` - Gets the can draw cards property
     - `getCardsToDrawCount()` - Gets the number of cards to draw
     - `cardsToDrawCountProperty()` - Gets the cards to draw property
     - `isRoundCompleted()` - Checks if the round is completed
     - `roundCompletedProperty()` - Gets the round completed property
     - `getRemainingCards()` - Gets the number of cards remaining in the deck
     - `remainingCardsProperty()` - Gets the remaining cards property
     - `updateRemainingCards()` - Updates the remaining cards count
     - `startNewGame()` - Starts a new game
     - `isGameOver()` - Checks if the game is over
     - `getDeck()` - Gets the current deck
     - `getRoundNumber()` - Gets the current round number
     - `setCurrentJoker(Joker joker)` - Sets the current joker

2. **GameStateManager.java**: Manages the overall game progression and state
   - Properties: gameService, players, currentPlayer, currentRound, currentLevel, currentStage, playerChips, ante, gamePhase, currentPhase, handsPlayedInStage, discardsUsedInStage, maxHandsPerStage, maxDiscardsPerStage, handLimitReached, discardLimitReached, currentJoker
   - Enums: GamePhase, LevelStage
   - Methods:
     - `GameStateManager()` - Constructor 
     - `initializeGame()` - Initializes the game state
     - `resetLimitsForNewStage()` - Resets hand and discard limits
     - `startNewGame()` - Starts a new game
     - `startNewRound()` - Starts a new round
     - `recordDiscard()` - Records a discard action
     - `setAnteAmount(int amount)` - Sets the ante amount
     - `resetPlayerChips()` - Resets player's chips to starting amount
     - `completeRound()` - Completes the current round
     - `setGamePhase(GamePhase phase)` - Sets the current game phase
     - `calculateChipsEarned(int score)` - Calculates chips earned based on score
     - `advanceGamePhase()` - Advances to the next game phase
     - `getGameService()` - Gets the game service
     - `getCurrentPlayer()` - Gets the current player
     - `getCurrentRound()` - Gets the current round number
     - `currentRoundProperty()` - Gets the current round property
     - `getCurrentLevel()` - Gets the current level
     - `currentLevelProperty()` - Gets the current level property
     - `getCurrentStage()` - Gets the current level stage
     - `currentStageProperty()` - Gets the current stage property
     - `getPlayerChips()` - Gets the player's chips
     - `playerChipsProperty()` - Gets the player chips property
     - `getAnte()` - Gets the current ante amount
     - `anteProperty()` - Gets the ante property
     - `getGamePhase()` - Gets the current game phase
     - `gamePhaseProperty()` - Gets the game phase property
     - `getCurrentPhase()` - Gets the current phase enum
     - `currentPhaseProperty()` - Gets the current phase property
     - `isGameOver()` - Checks if the game is over
     - `getHandsPlayedInStage()` - Gets hands played in current stage
     - `handsPlayedInStageProperty()` - Gets hands played property
     - `getDiscardsUsedInStage()` - Gets discards used in current stage
     - `discardsUsedInStageProperty()` - Gets discards used property
     - `getMaxHandsPerStage()` - Gets maximum hands per stage
     - `maxHandsPerStageProperty()` - Gets maximum hands property
     - `getMaxDiscardsPerStage()` - Gets maximum discards per stage
     - `maxDiscardsPerStageProperty()` - Gets maximum discards property
     - `isHandLimitReached()` - Checks if hand limit is reached
     - `handLimitReachedProperty()` - Gets hand limit reached property
     - `isDiscardLimitReached()` - Checks if discard limit is reached
     - `discardLimitReachedProperty()` - Gets discard limit reached property
     - `generateRandomJoker()` - Generates a random joker for the current round
     - `getCurrentJoker()` - Gets the current joker

### View Classes

1. **CardView.java**: Visual representation of a playing card
   - Properties: card, isSelected, background, cardContent, topLabel, centerLabel, bottomLabel, hoverAnimation, selectAnimation, dropShadow
   - Methods:
     - `CardView(Card card)` - Constructor with card model
     - `setupHoverAnimation()` - Sets up animations for hover and selection
     - `resizeCard(double containerWidth)` - Maintains consistent card sizing
     - `setSelected(boolean selected)` - Marks a card as selected with visual effects
     - `getCard()` - Gets the card model for this view
     - `getSuitSymbol(String suit)` - Gets the Unicode symbol for a suit
     - `getSuitColor(String suit)` - Gets the color for a suit

2. **DeckViewerOverlay.java**: Shows the distribution of cards remaining in the deck
   - Properties: deck, cardGrid, cardCells, suits, ranks, fadeIn, fadeOut, totalCardsLabel
   - Methods:
     - `DeckViewerOverlay(Deck deck)` - Constructor with deck reference
     - `createCardGrid()` - Creates the grid for displaying card distribution
     - `handleCellHover(MouseEvent event, Label cell, boolean isEntering)` - Handles hover effects for cells
     - `updateCardDistribution()` - Updates the display based on current deck
     - `show()` - Shows the overlay with animation
     - `hide()` - Hides the overlay with animation
     - `getSuitSymbol(String suit)` - Gets the Unicode symbol for a suit
     - `getSuitColor(String suit)` - Gets the color for a suit

3. **GameView.java**: Main game interface that integrates all visual components
   - Properties: gameManager, gameService, UI components (labels, areas, buttons, overlays)
   - Methods:
     - `GameView()` - Default constructor that initializes game and UI
     - `createGameInfoPanel()` - Creates the game information panel
     - `createInfoValueLabel(String title, String value)` - Creates labeled info displays
     - `createButtonsPanel()` - Creates action buttons panel
     - `createAreaWithLabel(String title, HBox content)` - Creates labeled card areas
     - `createScrollableAreaWithLabel(String title, HBox content)` - Creates scrollable areas
     - `createNotificationOverlay()` - Creates notification system
     - `showNotification()` - Shows notification messages
     - `setupBindings()` - Sets up data bindings for reactive UI
     - `handleDrawCards()` - Handles draw cards action
     - `handlePlayHand()` - Handles play hand action
     - `handleDiscardCards()` - Handles discard cards action
     - `handleNextRound()` - Handles next round action
     - `updateHandDisplay()` - Updates the player's hand display
     - `updateSelectedCardsDisplay()` - Updates selected cards display
     - `updateJokerDisplay()` - Updates joker display
     - `updateAllCardDisplays()` - Updates all card displays
     - `handleCardSelection(CardView cardView, Card card)` - Handles card selection
     - `handleCardDeselection(CardView cardView, Card card)` - Handles card deselection
     - `createStartScreen()` - Creates the welcome/start screen
     - `startGame()` - Starts the game
     - `updateDeckSizeDisplay()` - Updates the deck size display
     - `createDeckDisplayArea()` - Creates the deck display area

## Running the Application

### Prerequisites

- Java Development Kit (JDK) 17 or later
- Maven 3.6 or later
- JavaFX 21.0.2

### Why Maven for This Project

Maven is essential for this Java project for several important reasons:

1. **Dependency Management**: Maven automatically handles all project dependencies (like JavaFX, testing libraries) by downloading and managing the correct versions. This eliminates the need to manually download JAR files and configure the classpath.

2. **Build Standardization**: Maven provides a standard build lifecycle (compile, test, package, install, deploy) that makes the build process consistent and predictable.

3. **JavaFX Integration**: Maven's JavaFX plugin simplifies running JavaFX applications by handling module path configurations that would otherwise require complex command-line arguments.

4. **Cross-Platform Compatibility**: Maven works consistently across different operating systems, making it easier to develop and build the project on Windows, macOS, or Linux.

5. **Testing Integration**: Maven's test phase integrates seamlessly with JUnit and other testing frameworks, facilitating both unit and integration testing.

6. **Future Expansion**: As the project grows to include planned online features, Maven will simplify adding new dependencies (like Spring Boot for web services or database connectors).

### About Maven Wrapper (mvnw)

This project uses Maven Wrapper (mvnw), which is a script that allows you to run Maven commands without having Maven installed on your machine. The wrapper script automatically downloads the appropriate Maven version and uses it to build the project. This ensures that everyone working with the project uses the same Maven version, avoiding "works on my machine" issues.

Key benefits:
- No need to install Maven separately
- Consistent build environment across different development machines
- Easier onboarding for new team members
- The wrapper files (mvnw, mvnw.cmd, and .mvn directory) are included in the repository

### Steps to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/YourUsername/mini-balatro.git
   cd mini-balatro
   ```

2. Run the application using Maven wrapper:
   ```bash
   ./mvnw clean javafx:run
   ```
   
   For Windows users:
   ```bash
   mvnw.cmd clean javafx:run
   ```

## Game Rules

### Level Progression

- **Level 1**:
  - Small Blind: Score target 300
  - Big Blind: Score target 450
  - The Hook: Score target 600

- **Level 2**:
  - Small Blind L2: Score target 800
  - Big Blind L2: Score target 1200
  - The Hook L2: Score target 1600

- **Level 3**:
  - Small Blind L3: Score target 2000
  - Big Blind L3: Score target 3000
  - The Hook L3: Score target 4000

### Gameplay Mechanics

- **Starting**: Players begin with 100 chips and a 10-chip/50-chip/100-chip ante
- **Hands**: Maximum of 4 hands can be played per stage
- **Discards**: Maximum of 4 discards can be used per stage
- **Advancement**:
  - Complete a stage by reaching the target score
  - Complete all stages in a level to advance to the next level
  - Rewards are given when advancing to a new level (3x the ante)

### Card Selection and Play

1. Click on cards to select them for scoring
2. Selected cards are highlighted with a green glow and move upward
3. Score is calculated based on poker hand combinations
4. Discard unwanted cards to draw new ones
5. Progress through stages by reaching the target score

## Development

### Building

```bash
./mvnw clean package
```

### Testing

```bash
./mvnw test
```

## Team Members

- Eris Xie
- NianChao Wang
- ZiChen Tian

## Recent Changes

- Added LUCKY_JOKER with ON_SCORED activation type that gives bonuses for hands with two 7s
- Improved JavaFX tests with better error handling and initialization
- Added new controller classes for better separation of concerns (GameController and HandEvaluationController)
- Fixed poker hand evaluation logic to correctly identify Straight hands
- Added Level 3 with three stages (Small Blind L3, Big Blind L3, The Hook L3)
- Improved the discard pile area with horizontal scrolling
- Enhanced card selection with green glow effect
- Added deck viewer overlay to show card distribution
- Increased the height of UI components for better visibility

## Online Implementation

We have created a web-based version of the Balatro game using modern web technologies. The web implementation allows players to enjoy the game online, compete with others, and track their scores on a ranking system.

### Web Frontend

The web frontend is built with:
- **React**: For building the UI components
- **JavaScript**: For type-safe JavaScript code
- **Vite**: For fast development and optimized production builds
- **Tailwind CSS**: For styling and responsive design


The frontend code is located in the `balatro-web` directory. For more details, see the [Web Frontend README](balatro-web/README.md).

### Backend Services

The backend is built with:
- **Spring Boot**: For REST API endpoints and game logic controllers
- **DynamoDB**: For storing user profiles and game state
- **AWS Cognito**: For user authentication and identity management

The backend implementation includes:
1. **REST Controllers**:
   - `GameController`: Provides endpoints for accessing game stages and state transitions
   - `HandEvaluationController`: Provides endpoints for evaluating poker hands and calculating scores

    Spring Boot is used in the REST API controllers:
      GameController.java has @RestController and @RequestMapping annotations
      HandEvaluationController.java also uses Spring Boot annotations
    These controllers provide endpoints for:
      Getting game stages information (getGameStages())
      Getting stage transition rules (getStageTransitionRules())
      Evaluating poker hands (evaluateHand())

2. **Data Storage**:
   - DynamoDB Table (`BalatroUsers`): Stores user profiles, scores, and game history
   - No S3 implementation is currently present in the codebase

3. **Authentication**:
   - AWS Cognito is configured but implementation appears to be in progress

### Architecture

The project follows a dual-architecture approach:

1. **JavaFX Desktop Application**:
   - Built with Java and JavaFX for desktop platforms
   - MVC architecture with model, view, and controller packages
   - Local game state management without remote data storage

2. **Web Implementation**:
   - **Client**: React-based frontend for browser access
   - **Server**: Spring Boot REST API controllers for game logic
   - **Database**: DynamoDB for user profiles and scores
   - **Authentication**: AWS Cognito for user identity management (in progress)

### Deployment

1. **JavaFX Desktop Application**:
   - Distributed as a packaged JAR file
   - Requires Java 17+ with JavaFX modules
   - Built using Maven

2. **Web Implementation**:
   - Frontend hosted on web servers
   - Backend deployed as Spring Boot application
   - Database hosted on AWS DynamoDB

# JavaFX-version
