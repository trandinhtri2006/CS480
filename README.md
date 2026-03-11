# CS480 Map Application

A Java Swing desktop application for managing routes and user accounts, backed by a local SQLite database.

## Runtime and Build Requirements

- Recommended runtime for new machines: Java 21 (LTS)
- Minimum runtime currently supported by project build settings: Java 17
- Maven 3.9+ recommended

Downloads:
- JDK: https://www.oracle.com/java/technologies/downloads/
- Maven: https://maven.apache.org/download.cgi

Verify installation:

```powershell
java -version
mvn -version
```

## Windows VM Setup (Fresh Machine)

1. Install JDK 21 and set JAVA_HOME.
2. Install Maven and add Maven bin to PATH.
3. Open PowerShell in the CS480 project root.
4. Run:

```powershell
mvn clean compile exec:java
```

First run may take longer while Maven downloads dependencies.

## Porting to a New Windows VM

If you want to preserve existing users and favorite routes:

1. Copy the entire project folder to the new VM.
2. Copy mapapp.db from the old machine into the project root on the new machine.
3. Run:

```powershell
mvn clean compile exec:java
```

If you want a clean start instead, do not copy mapapp.db.

## Daily Commands

```powershell
# Run app (after at least one successful compile)
mvn exec:java

# Compile only
mvn compile

# Package JAR (target/cs480-1.0-SNAPSHOT.jar)
mvn package

# Clean build output
mvn clean

# Full rebuild
mvn clean compile
```

## Troubleshooting

- Error: java.lang.ClassNotFoundException: App
    - Cause: running exec before classes are compiled in current workspace state.
    - Fix: run mvn clean compile exec:java.

- PowerShell warning about PSReadLine and screen reader mode
    - This warning is not related to Maven or Java execution.

## Project Structure

```
CS480/
|- pom.xml
|- mapapp.db                      # created on first run
`- src/
     `- main/
            |- java/
            |  |- App.java              # entry point
            |  |- db/
            |  |- model/
            |  `- service/
            `- resources/
                 |- Background/
                 `- db/schema.sql
```

## Dependencies

| Library | Version | Purpose |
|---|---|---|
| org.xerial:sqlite-jdbc | 3.51.2.0 | SQLite database driver |

Dependencies are managed by Maven and downloaded automatically.
