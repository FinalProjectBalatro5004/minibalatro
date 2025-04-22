#!/bin/bash

# Build without Spring Boot repackaging
./mvnw clean compile -DskipTests

# Copy dependencies
./mvnw dependency:copy-dependencies

# Run the application directly with Java
java --module-path="./target/dependency" \
     --add-modules=javafx.controls,javafx.fxml,javafx.graphics \
     --add-opens=java.base/java.lang=ALL-UNNAMED \
     --add-opens=java.base/java.util=ALL-UNNAMED \
     -classpath "./target/classes:./target/dependency/*" \
     com.balatro.BalatroGame 