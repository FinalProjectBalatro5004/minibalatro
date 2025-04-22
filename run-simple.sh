#!/bin/bash

# Build without Spring Boot repackaging
./mvnw clean compile -DskipTests

# Copy dependencies
./mvnw dependency:copy-dependencies

# Run the application using classpath only (avoiding module path issues)
java -classpath "./target/classes:./target/dependency/*" \
     --add-opens=java.base/java.lang=ALL-UNNAMED \
     --add-opens=java.base/java.util=ALL-UNNAMED \
     com.balatro.BalatroGame 