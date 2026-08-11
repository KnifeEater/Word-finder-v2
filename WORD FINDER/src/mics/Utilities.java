package mics;

import java.nio.file.Paths;

public class Utilities {

    public static String GetApplicationDataFolder() // Gets the path for the common application data folder.
    {
        return Paths.get(getBaseFolder(), Constants.ApplicationFolderName).toString();
    }

    public static String GetApplicationDataFile(String filename) // Open a file in the common application data folder.
    {
        return Paths.get(getBaseFolder(), Constants.ApplicationFolderName, filename).toString();
    }

    private static String getBaseFolder() {
        String programData = System.getenv("ProgramData"); // Windows equivalent of CommonApplicationData
        return (programData != null) ? programData : System.getProperty("user.home");
    }

    private Utilities() {}
}
