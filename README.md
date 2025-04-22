# 🕵️‍♂️ Interloper - A Spyfall-Inspired Multiplayer Game

**Interloper** is a multiplayer social deduction game inspired by [Spyfall](https://en.wikipedia.org/wiki/Spyfall_(card_game)), built with a Java Spring Boot backend and a LibGDX frontend. It supports both desktop and Android platforms.

## 🎮 Game Concept: What Is Interloper?

Interloper is a party game where all players except one — the **Spy** — are informed of a secret location. The players take turns asking each other questions to determine who among them doesn't know the location.

- **Regular players** try to identify the spy without giving away the location.
- The **Spy** attempts to guess the location based on players’ answers or avoid detection altogether.

Each round ends when:
- The players **vote to identify the spy**.
- The **Spy guesses the location**.
- The **timer runs out** and the spy remains hidden.

Interloper brings this experience online through a WebSocket-based architecture and real-time multiplayer support.

---

## 🧠 Tech Stack

- **Backend**: Java 17, Spring Boot, WebSockets
- **Frontend**: LibGDX (cross-platform), Java 17
- **Communication**: JSON messages via WebSocket
- **Platforms**: Desktop (Windows/Linux/macOS), Android

---

## 🚀 How to Run the Project

### ✅ Prerequisites

- Java 17
- Gradle
- Android SDK (for Android build)
- A modern IDE (e.g., IntelliJ IDEA or Android Studio)

---

### 🔧 Backend Setup

**Navigate to:**

```
cd interloperServer
```

**Run the server:**

```bash
./gradlew bootRun
```

Server will start on `ws://localhost:8080/ws`.

**To build the backend:**

```bash
./gradlew build
```

---

### 🖥️ Frontend (Desktop) Setup

**Navigate to the frontend core module directory:**

```
cd Game
```

This is where your desktop game's main class (`Main.java`) lives.

**To run on desktop:**

```bash
./gradlew lwjgl3:run
```

Make sure the backend is running before launching the game.

---

### 📱 Frontend (Android) Setup

**Navigate to the LibGDX Android project:**
```
cd Game/android
```


**Build Android APK:**

```bash
./gradlew android:assembleDebug
```

**Install APK to a connected device:**

```bash
adb install android/build/outputs/apk/debug/android-debug.apk
```
Or use Android studio or similair tools to achiveve this

---

## 💡 Useful Notes

- WebSocket connection is handled by `LocalWebSocketClient` on the frontend.
- Message types follow a strict request-response model with matching classes on both backend and frontend.
- Locations are loaded from `BaseLocations.json` in the backend.
- The backend handles game state, voting logic, and spy detection.
- Round lifecycle and player logic are managed by `GameService`, `RoundService`, and `VotingService`.
- More detailed information on structure for the backend and frontend can be found in their respective project root.

---

