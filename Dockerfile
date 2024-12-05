# Build stage
FROM amazoncorretto:17 AS builder

WORKDIR /app

# Copy Maven wrapper and configuration
COPY ./pom.xml ./
COPY .mvn .mvn
COPY mvnw .

# Fix line endings and set permissions for Maven wrapper
RUN sed -i 's/\r$//' ./mvnw && chmod +x ./mvnw

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline -B

# Copy application source code
COPY ./src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM amazoncorretto:17

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=builder /app/target/wowlibre-0.0.1-SNAPSHOT.jar .

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Expose the application port
EXPOSE 8091

# Start the application
ENTRYPOINT ["java", "-jar", "wowlibre-0.0.1-SNAPSHOT.jar"]
