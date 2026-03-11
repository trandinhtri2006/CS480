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

## Maven Setup on a New Windows Machine (Detailed)

If Maven setup is the hardest part, use this exact flow.

1. Install Maven
    - Option A (winget): winget install Apache.Maven
    - Option B (zip): download Maven zip, extract to C:\Tools\apache-maven-3.9.x
2. Set JAVA_HOME to your JDK folder
    - Example: C:\Program Files\Java\jdk-21
3. Add Maven to PATH
    - Add C:\Tools\apache-maven-3.9.x\bin
4. Open a brand new PowerShell window after changing environment variables.
5. Verify configuration:

```powershell
echo $env:JAVA_HOME
Get-Command mvn
mvn -version
```

Expected result:
1. mvn resolves to a real path.
2. Maven reports Java 21 as the runtime.

If Maven still does not work:

1. Confirm no old Maven path appears before the new one:

```powershell
$env:Path -split ';'
```

2. Check user and system environment variables in Windows System Properties and remove stale Maven entries.
3. Restart PowerShell or sign out/in so PATH changes are applied.

Optional Maven settings for locked-down networks:

1. Create %USERPROFILE%\.m2\settings.xml.
2. Add proxy configuration if your VM requires a corporate proxy.
3. Re-run maven with forced update once:

```powershell
mvn -U clean compile
```

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

- Error: mvn is not recognized as a command
    - Cause: Maven bin folder is missing from PATH or shell was not restarted.
    - Fix: add Maven bin to PATH, then open a new PowerShell window.

- Maven uses wrong Java version
    - Cause: JAVA_HOME points to a different JDK, or PATH has another Java first.
    - Fix: point JAVA_HOME to JDK 21 and verify with mvn -version.

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
