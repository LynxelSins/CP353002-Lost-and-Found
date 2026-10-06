# Build stage
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy pom.xml and download dependencies first (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and package application
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy built jar from builder stage
COPY --from=builder /app/target/CP353002_Group12_LostAndFound-*.jar app.jar

# Expose server port
EXPOSE 8080

# Environment defaults
ENV SPRING_PROFILES_ACTIVE=neon
ENV PORT=8080

ENTRYPOINT ["java", "-jar", "app.jar"]
