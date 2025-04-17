# Mini-Balatro

A JavaFX-based implementation of the Balatro card game that simulates the core gameplay mechanics of the popular poker-based roguelike, with plans for expanded online capabilities and cloud integration.

## Project Overview

Mini-Balatro reimagines the popular roguelike deck-building game with a focus on adding online features and community interaction that modern gamers expect. Our implementation:

- Preserves the core poker-based roguelike gameplay experience
- Adds online capabilities and leaderboards (planned)
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

- **Planned Online Features**:
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
                ├── model/                      # Tests for model classes
                ├── view/                       # Tests for view classes
                ├── service/                    # Tests for service classes
                └── balatro_game/               # Integration tests
```

## Key Components

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
     - `Hand(List<Card> cards)` - Constructor with initial cards
     - `addCard(Card card)` - Adds a card to the hand
     - `addCards(List<Card> cards)` - Adds multiple cards to the hand
     - `removeCard(Card card)` - Removes a card from the hand
     - `clearHand()` - Removes all cards from the hand
     - `meetsMinimumRequirements()` - Checks if hand meets minimum play requirements
     - `meetsMaximumRequirements()` - Checks if hand meets maximum play requirements
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

4. **Player.java**: Manages player state, including chips and statistics
   - Properties: name, chips, statistics
   - Methods:
     - `Player(String name)` - Constructor with player name
     - `getName()` - Gets the player's name
     - `setName(String name)` - Sets the player's name
     - `getChips()` - Gets the player's chip count
     - `setChips(int chips)` - Sets the player's chip count
     - `addChips(int amount)` - Adds chips to the player's count
     - `removeChips(int amount)` - Removes chips from the player's count
     - `hasEnoughChips(int amount)` - Checks if player has enough chips
     - `getStatistics()` - Gets the player's statistics
     - `updateStatistic(String key, int value)` - Updates a statistic value
     - `incrementStatistic(String key)` - Increments a statistic value
     - `equals(Object obj)` - Compares players for equality
     - `hashCode()` - Generates a hash code for the player
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
   - Enum Values: STANDARD_JOKER, GREEDY_JOKER, LUSTY_JOKER, WRATHFUL_JOKER, GLUTTONOUS_JOKER, SCARY_FACE, FIBONACCI
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

### View Classes

1. **CardView.java**: Visual representation of a playing card
   - Properties: card, isSelected, UI components (background, labels, animations)
   - Methods:
     - `CardView(Card card)` - Constructor with card model
     - `setupHoverAnimation()` - Sets up the animation for hovering
     - `resizeCard(double containerWidth)` - Adjusts card size based on container
     - `setSelected(boolean selected)` - Marks a card as selected with visual effects
     - `getCard()` - Gets the card model for this view
     - `getSuitSymbol(String suit)` - Gets the Unicode symbol for a suit
     - `getSuitColor(String suit)` - Gets the color for a suit

2. **GameView.java**: Main game interface that integrates all visual components
   - Properties: gameService, stageManager, UI regions and controls
   - Methods:
     - `GameView(GameService gameService, GameStateManager stageManager)` - Constructor
     - `createGameScreen()` - Creates the main game interface
     - `createInfoBar()` - Creates the game info display area
     - `createPlayArea()` - Creates the main play area
     - `createHandArea()` - Creates the player's hand display area
     - `createScrollableAreaWithLabel()` - Creates scrollable areas with labels
     - `updateHandDisplay()` - Updates the display of the player's hand
     - `updateDiscardPileDisplay()` - Updates the discard pile display
     - `handleCardSelection()` - Handles player selecting a card
     - `handleCardDeselection()` - Handles player deselecting a card
     - `createStartScreen()` - Creates the welcome/start screen
     - `showWinScreen()` - Shows the winning screen
     - `showLoseScreen()` - Shows the losing screen
     - `createScoreSection()` - Creates the score display section
     - `updateScoreDisplay()` - Updates the score display
     - `createAnteSection()` - Creates the ante display section
     - `updateAnteDisplay()` - Updates the ante display
     - `createHandLimitBar()` - Creates a progress bar for hand limits
     - `createChipDisplay()` - Creates the chip display
     - `showDiscardOptions()` - Shows options for discarding cards
     - `hideDiscardOptions()` - Hides discard options
     - `createActionButtons()` - Creates the action buttons
     - `createDrawButton()` - Creates the draw button
     - `createDiscardButton()` - Creates the discard button
     - `createPlayButton()` - Creates the play button
     - `createNewRoundButton()` - Creates the new round button
     - `createDeckSizeLabel()` - Creates a label showing deck size

3. **DeckViewerOverlay.java**: Shows the distribution of cards remaining in the deck
   - Properties: deck, overlayContent, suitSections
   - Methods:
     - `DeckViewerOverlay(Deck deck)` - Constructor with deck reference
     - `createContent()` - Creates the content of the overlay
     - `createTitle()` - Creates the title section
     - `createSuitSections()` - Creates sections for each suit
     - `createSuitSection()` - Creates a section for a specific suit
     - `updateCardCount()` - Updates the card count displays
     - `updateDisplay()` - Updates the entire overlay display
     - `show()` - Shows the overlay
     - `hide()` - Hides the overlay
     - `isShowing()` - Checks if the overlay is visible

### Service Classes

1. **GameService.java**: Core game logic for card interactions and scoring
   - Properties: deck, hand, discardPile, selectedCards, score, gameState
   - Methods:
     - `GameService()` - Default constructor
     - `newGame()` - Starts a new game
     - `startNewRound()` - Starts a new round
     - `getRoundNumber()` - Gets the current round number
     - `getGameState()` - Gets the current game state
     - `getDeck()` - Gets the current deck
     - `getHand()` - Gets the player's current hand
     - `getDiscardPile()` - Gets the discard pile
     - `getSelectedCards()` - Gets the currently selected cards
     - `getScore()` - Gets the current score
     - `setTargetScore()` - Sets the target score
     - `getTargetScore()` - Gets the target score
     - `getHandTypeText()` - Gets text describing the current hand type
     - `drawCards()` - Draws cards from the deck
     - `discardCards()` - Discards selected cards
     - `selectCard()` - Selects a card for play
     - `deselectCard()` - Deselects a card
     - `clearSelectedCards()` - Clears all selected cards
     - `evaluateSelectedCards()` - Evaluates the selected cards
     - `calculateScore()` - Calculates the score for selected cards
     - `hasReachedTargetScore()` - Checks if target score has been reached
     - `gameStateProperty()` - Gets the game state property
     - `scoreProperty()` - Gets the score property
     - `targetScoreProperty()` - Gets the target score property
     - `roundNumberProperty()` - Gets the round number property
     - `remainingCardsProperty()` - Gets the remaining cards property
     - `roundCompletedProperty()` - Gets the round completed property

2. **GameStateManager.java**: Manages the overall game progression and state
   - Properties: gameService, players, currentStage, playerChips, ante, gamePhase
   - Methods:
     - `GameStateManager()` - Constructor 
     - `initializeGame()` - Initializes the game state
     - `resetLimitsForNewStage()` - Resets hand and discard limits
     - `startNewGame()` - Starts a new game
     - `startNewRound()` - Starts a new round
     - `recordDiscard()` - Records a discard action
     - `setAnteAmount()` - Sets the ante amount
     - `resetPlayerChips()` - Resets player's chips to starting amount
     - `completeRound()` - Completes the current round
     - `setGamePhase()` - Sets the current game phase
     - `calculateChipsEarned()` - Calculates chips earned based on score
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

- **Starting**: Players begin with 100 chips and a 10-chip ante
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

- Fixed poker hand evaluation logic to correctly identify Straight hands
- Added Level 3 with three stages (Small Blind L3, Big Blind L3, The Hook L3)
- Improved the discard pile area with horizontal scrolling
- Enhanced card selection with green glow effect
- Added deck viewer overlay to show card distribution
- Increased the height of UI components for better visibility

## Online Implementation

We have created a web-based version of the Balatro game using modern web technologies. The web implementation allows players to enjoy the game online, compete with others, and track their scores on a leaderboard.

### Web Frontend

The web frontend is built with:
- **React**: For building the UI components
- **JavaScript**: For type-safe JavaScript code
- **Vite**: For fast development and optimized production builds
- **Tailwind CSS**: For styling and responsive design


The frontend code is located in the `balatro-web` directory. For more details, see the [Web Frontend README](balatro-web/README.md).

### Backend Services

The backend is built with:
- **Spring Boot**: For REST API endpoints and business logic
- **AWS DynamoDB**: For storing user data and game state
- **AWS S3**: For storing assets and static files
- **AWS Cognito**: For user authentication (planned)

### Architecture

The online implementation follows a client-server architecture:
1. **Client (React)**: Handles UI rendering, user interactions, and communicates with the server via REST APIs
2. **Server (Spring Boot)**: Processes game logic, manages user sessions, and stores data in AWS services
3. **Database (DynamoDB)**: Stores user profiles, game states, and leaderboard data
4. **Storage (S3)**: Hosts static assets and the compiled frontend code

### Deployment

The online version is deployed to AWS with:
- Frontend hosted on **S3** and distributed via **CloudFront**
- Backend running on **Elastic Beanstalk**
- CI/CD pipeline using **AWS CodePipeline**

