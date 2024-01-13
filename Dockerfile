# Stage 1: Build the application using Maven
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copy only the POM file to leverage Docker cache
COPY pom.xml .

# Download dependencies and build the application
RUN mvn dependency:go-offline -B
COPY src src
RUN mvn package -DskipTests

# Stage 2: Create the final Java 17 image
FROM bellsoft/liberica-openjdk-alpine:17.0.7-7


# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the previous stage
COPY --from=build /app/target/pubsub-0.0.1-SNAPSHOT.jar pubsub.jar

# Specify the command to run on container start
CMD ["java", "-jar", "pubsub.jar"]
