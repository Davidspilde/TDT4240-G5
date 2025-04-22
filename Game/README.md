# Interloper — Frontend (LibGDX)

This is the **frontend** for **Interloper**, a multiplayer social deduction game inspired by *Spyfall*. It is built using **LibGDX** and handles all visual and user interaction. The frontend connects to the backend (also located in this repository) using WebSockets.


---

## Features

- Host or join game lobbies
- Step-by-step in-game tutorial
- Display roles and location information (with spy-specific behavior)
- Real-time voting system with player feedback
- Countdown timer per round
- Round summaries and final scoreboard
- In-game audio and music controls via settings menu
- Modular Scene2D-based UI system
- Retro-themed visuals via a Commodore64-style skin

---

## Project Structure

```
src/main/java/io/github/Spyfall/
├── view/
│   ├── game/             # In-game UI (player info, voting, timer, etc.)
│   ├── lobby/            # Lobby UI (player list, settings, location editor)
│   ├── mainmenu/         # Main menu, how-to-play, join/create dialogs
│   └── ui/               # Reusable UI components (settings icon, popup, timer)
├── model/                # Shared game state (GameModel, GameData, etc.)
├── controller/           # Backend communication (WebSocket interface)
```

---

---

## Technologies

- Java 17
- LibGDX (Scene2D for UI)
- WebSockets for multiplayer communication
- Commodore64-style UI skin
