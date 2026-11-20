# Running on Windows

Open PowerShell in the folder that contains `pom.xml` and run:

```powershell
java -version
mvn -version
mvn clean javafx:run
```

Use JDK 17 and install Maven if `mvn` is not recognized.
If the UI starts with no accounts, click **ADD** to create demonstration accounts; local account JSON files are saved under `data/` and ignored by Git.

If dependencies fail to download or the app does not start, capture the full Maven error and check the Java version before changing the source code.
