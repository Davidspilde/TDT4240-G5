# Interloper Backend - Spring Boot WebSocket Server

This is the backend server for **Interloper**, a multiplayer Spyfall-inspired game. It is built using Java 17 and Spring Boot, and it uses WebSockets for real-time communication with the LibGDX frontend.

---

## 📁 Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/interloperServer/interloperServer/
│   │       ├── config/                  # WebSocket configuration
│   │       │   └── WebSocketConfig.java
│   │       ├── InterloperServerApplication.java
│   │       ├── model/                   # Game domain models and message classes
│   │       ├── service/                 # Core business logic
│   │       └── websocket/               # WebSocket infrastructure
│   │           ├── GameConnectionService.java
│   │           ├── GameWebSocketHandler.java
│   │           ├── MessageDispatcher.java
│   │           └── handlers/            # Individual WebSocket message handlers
│   └── resources/
│       ├── application.properties       # Spring Boot configuration
│       └── BaseLocations.json           # List of initial possible game locations
└── test/
    └── java/
        └── com/interloperServer/interloperServer/
            ├── InterloperServerApplicationTests.java
            ├── service/                # Unit tests for business logic
            └── websocket/              # WebSocket-related tests
```


---

## 🚀 Running the Server

### Prerequisites

- Java 17+
- Gradle 7+
- Recommended: IntelliJ IDEA or VS Code

### Start the server

```bash
./gradlew bootRun
```

The server will start on: `ws://localhost:8080/ws`

### Build the server

```bash
./gradlew build
```

### Run production JAR

```bash
java -jar build/libs/interloperServer-*.jar
```

---

## 🧪 Running Tests

```bash
./gradlew test
```

Test reports are available under:

```
build/reports/tests/test/index.html
```

---

## 📌 Key Components

- **WebSocketConfig**: WebSocket setup and endpoint registration.
- **GameWebSocketHandler**: Routes incoming WebSocket traffic.
- **MessageDispatcher**: Routes deserialized messages to the correct handler.
- **Handlers**: Process specific message types (e.g. creating lobby, voting, ending round).
- **Services**: Handle core game logic and state transitions.
- **Models**: Represent the game entities and state.
- **Messaging**: JSON-based messages, mirrored on both frontend and backend.

---

## 🔌 WebSocket Endpoint

- URL: `ws://<host>:8080/ws`
- All messages are serialized/deserialized as JSON.

---
