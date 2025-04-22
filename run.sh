#!/bin/bash

# Build the project (skip tests for faster startup)
./mvnw clean package -DskipTests

# Copy dependencies to target/dependency
./mvnw dependency:copy-dependencies

# Determine the JavaFX path from the dependencies
JAVAFX_PATH="./target/dependency"

# Run the application with JavaFX modules properly configured
java --module-path="$JAVAFX_PATH" \
     --add-modules=javafx.controls,javafx.fxml,javafx.graphics \
     --add-opens=java.base/java.lang=ALL-UNNAMED \
     --add-opens=java.base/java.util=ALL-UNNAMED \
     -jar target/balatro-game-0.0.1-SNAPSHOT.jar 