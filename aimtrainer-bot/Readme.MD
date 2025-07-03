# AimTrainer Bot - Java Selenium

This is an automated bot to play [aimtrainer.io](https://aimtrainer.io/challenge) by detecting orange circles on the game canvas and clicking their centers.

---

## Description

- Uses Selenium to open Chrome browser and interact with the game page.
- Captures screenshots of the game canvas.
- Analyzes the image to find orange-colored points (target circles).
- Groups nearby points into clusters representing each circle.
- Automatically moves the mouse and clicks at the cluster centers.
- Stops automatically when the game ends (detects the "PLAY AGAIN" button).

---

## Requirements

- Java 17+ (or compatible)
- Maven for dependency management
- Chrome Browser
- ChromeDriver compatible with your Chrome version (WebDriverManager handles this automatically)

---

## How to Run

1. Clone the repository or create a project with the provided `Main.java` code.

2. Build and run using Maven:

```bash
mvn compile exec:java -Dexec.mainClass="com.aimtrainer.bot.Main"
