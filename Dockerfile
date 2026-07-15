# Step 1: Use official Maven Docker image with JDK 17
FROM maven:3.8.8-eclipse-temurin-17

# Step 2: Set working directory inside the container
WORKDIR /app

# Step 3: Copy build configurations and source files
COPY pom.xml .
COPY src ./src

# Step 4: Compile the application
RUN mvn clean compile

# Step 5: Expose Javalin server port (7070)
EXPOSE 7070

# Step 6: Start the application using maven exec
CMD ["mvn", "exec:java"]
