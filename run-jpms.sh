#!/bin/bash

# Build without Spring Boot repackaging
./mvnw clean compile -DskipTests

# Copy dependencies
./mvnw dependency:copy-dependencies

# Set up specific JavaFX jar files for the platform
MAC_JAVAFX_DEPS="./target/dependency/javafx-graphics-21.0.2-mac-aarch64.jar:./target/dependency/javafx-base-21.0.2-mac-aarch64.jar:./target/dependency/javafx-controls-21.0.2-mac-aarch64.jar:./target/dependency/javafx-fxml-21.0.2-mac-aarch64.jar"

# Run the application with specific JavaFX path
java -classpath "./target/classes:$MAC_JAVAFX_DEPS:./target/dependency/*" \
     --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED \
     --add-exports javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED \
     --add-exports javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED \
     --add-opens=java.base/java.lang=ALL-UNNAMED \
     --add-opens=java.base/java.util=ALL-UNNAMED \
     com.balatro.BalatroGame 