# CS480 Map Application

A Java Swing desktop application for managing routes and user accounts, backed by a local SQLite database.

## Prerequisites

- **Java 17** or higher — [Download JDK](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.6+** — bundled with IntelliJ IDEA or NetBeans, or [download standalone](https://maven.apache.org/download.cgi)

To verify your installation:
```bash
java -version
mvn -version
```

## Running the Application

From the project root directory (`CS480/`):

```bash
mvn exec:java
```

That's it. Maven will download dependencies automatically on the first run.

## Other Useful Commands

```bash
# Compile only (check for errors without launching)
mvn compile

# Package into a JAR file (output: target/cs480-1.0-SNAPSHOT.jar)
mvn package

# Clean compiled output
mvn clean

# Clean and recompile from scratch
mvn clean compile
```

## Project Structure

```
CS480/
├── pom.xml                        # Maven build file
├── mapapp.db                      # SQLite database (created on first run)
└── src/
    └── main/
        ├── java/
        │   ├── App.java           # Entry point
        │   ├── LoginPage.java
        │   ├── CreateAccountPage.java
        │   ├── HomePage.java
        │   ├── SettingPage.java
        │   ├── ChangePassword.java
        │   ├── ChangeUsername.java
        │   ├── ChangeFavRoute.java
        │   ├── ForgotPassword.java
        │   ├── db/
        │   │   └── SQLHandler.java
        │   ├── model/
        │   │   ├── User.java
        │   │   ├── FavoriteRoute.java
        │   │   └── FavoriteRouteSummary.java
        │   └── service/
        │       ├── AuthService.java
        │       └── FavoriteService.java
        └── resources/
            ├── Background/
            │   └── loginpageBG.jpg
            └── db/
                └── schema.sql
```

## Dependencies

| Library | Version | Purpose |
|---|---|---|
| `org.xerial:sqlite-jdbc` | 3.51.2.0 | SQLite database driver |

All dependencies are managed by Maven and downloaded automatically.
