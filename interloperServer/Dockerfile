FROM eclipse-temurin:17-jdk

# Copy and build the app
WORKDIR /app
COPY . .
RUN ./gradlew build

# Use the generated JAR (adjust path if needed)
CMD ["java", "-jar", "build/libs/interloperServer-0.0.1-SNAPSHOT.jar "]
