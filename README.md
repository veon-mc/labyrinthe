# Labyrinthe

A maze board game built in Java with a JavaFX interface.

## How to Play

TBD

## Tech Stack

- Java
- JavaFX for the user interface
- Maven for build and dependencies
- JUnit for unit tests

The code follows the **MVC** pattern, keeping the game logic (model) independent from the JavaFX interface (view).

## Getting Started

### Prerequisites

- JDK 17 or later
- Maven

### Run the game

```bash
git clone https://github.com/veon-mc/labyrinthe.git
cd labyrinthe
mvn clean javafx:run
```

### Run the tests

```bash
mvn test
```

### Generate the documentation

```bash
mvn javadoc:javadoc
```

The JavaDoc is generated in `target/site/apidocs/`.

## Context

Built as a course project for *Development 3* at HE2B ESI.
