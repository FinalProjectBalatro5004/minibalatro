# Balatro Card Game

A JavaFX-based implementation of the Balatro card game with support for both offline and online features.

## Current Features

- **Card Display**: Beautiful rendering of playing cards with:
  - Rank and suit display in corners
  - Large centered suit symbol
  - Proper color coding (red for Hearts/Diamonds, black for Spades/Clubs)
  - Special yellow Joker card design

- **Game Mechanics**:
  - Deck management with proper shuffling at the start of each round
  - Card drawing with discard options
  - Score tracking and round management
  - Joker cards with different types, multipliers, and activation methods

- **Interactive Features**:
  - Hover effects (cards lift slightly and highlight when mouse hovers)
  - Selection mechanism (cards can be selected/deselected with click)
  - Visual feedback for selected cards (blue border and elevated position)

## Planned Features

- **Online Features**:
  - User Account System
  - Online Leaderboard
  - Multiplayer Support
  
- **Data Management**:
  - Game Progress Saving
  - User Data Storage
  - Game Statistics Tracking
  
- **Cloud Integration**:
  - AWS DynamoDB for User Data
  - AWS S3 for Game Assets
  - Cloud-based Game State Management

## Technical Architecture

- **Frontend**:
  - JavaFX for Desktop UI
  - MVC Pattern for UI Components
  - Responsive Design
  
- **Backend**:
  - Spring Boot Framework
  - RESTful API Services
  - WebSocket for Real-time Features
  
- **Database**:
  - AWS DynamoDB (planned)
  - Local Cache
  
- **Storage**:
  - AWS S3 (planned)
  - Local File System

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── balatro/
│   │           ├── model/      # Data models (Card, Deck, Hand, Joker, etc.)
│   │           ├── view/       # JavaFX views (CardView, JokerView, etc.)
│   │           ├── controller/ # UI controllers
│   │           ├── service/    # Business logic (GameService, etc.)
│   │           ├── enums/      # Enumerations (Suit, Rank, JokerType, etc.)
│   │           ├── BalatroDemoApp.java  # Demo application
│   │           └── CardDemoApp.java     # Card demonstration application
│   └── resources/
│       └── static/    # Static resources
└── test/
    └── java/
        └── com/
            └── balatro/
                └── model/      # Test classes for models
```

## Key Components

### Model Classes

1. **Card**: Represents a playing card with rank and suit.
2. **Deck**: Manages a collection of cards with shuffling and drawing capabilities.
   - Shuffling only occurs at the start of each round
   - Drawing cards with options to keep or discard
3. **Hand**: Represents a player's hand of cards with evaluation capabilities.
4. **Joker**: Special cards that provide multipliers and effects.

### View Classes

1. **CardView**: Visual representation of a playing card.
2. **JokerView**: Visual representation of a joker card.

### Service Classes

1. **GameService**: Manages game state, rounds, and scoring.

## Prerequisites

- Java Development Kit (JDK) 17 or later
- Maven 3.6 or later
- JavaFX 21.0.2

## Running the Application

The project uses Maven for dependency management and build automation. To run the application:

1. Clone the repository:
   ```bash
   git clone https://github.com/FinalProjectBalatro5004/minibalatro.git
   cd balatro-game
   ```

2. Run the Card Demo application using Maven wrapper:
   ```bash
   ./mvnw javafx:run -Djavafx.mainClass=com.balatro.CardDemoApp
   ```
   
   This will display a demonstration of the card visuals and interactions.

3. Run the Balatro Demo application:
   ```bash
   ./mvnw javafx:run -Djavafx.mainClass=com.balatro.BalatroDemoApp
   ```
   
   This will show a more complete demo of the game mechanics including jokers.

4. If you're using Windows, replace `./mvnw` with `mvnw.cmd` in the commands above.

## Game Rules

### Balatro Shuffling Rules
- Shuffling occurs only at the beginning of each round
- Players must use the current order of cards until they reach a specific score
- Once the required score is reached, players can start a new round and shuffle again

### Deck Management
- The game uses a standard 52-card deck
- Cards are drawn and can be kept or discarded
- The total number of cards (kept + discarded) must be 8 in each draw

### Jokers
- Jokers provide multipliers to your score
- Different joker types have different effects
- Jokers can be activated in different ways (Independent, On Scored, etc.)
- Jokers have different rarities (Common, Uncommon, Rare, etc.)

## Development and Testing

To run the tests:
```bash
./mvnw test
```

The test suite includes:
- Unit tests for model classes
- Tests for game mechanics
- Validation of shuffling and card drawing

## Building

To build the project:
```bash
./mvnw clean package
```

This will create a JAR file in the `target` directory.

## Recent Changes

- Added round management to the Deck class
- Implemented proper shuffling rules according to Balatro game mechanics
- Added Joker functionality with different types and activation methods
- Fixed UI issues in the demo applications
- Enhanced test coverage for all game components

