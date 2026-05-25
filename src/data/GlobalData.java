package data;

public class GlobalData {
    private static GlobalData instance;

    public static String currentUsername = null;
    public static boolean isLoggedIn = false;
    public static int currentScore = 0;
    public static int currentLevel = 1;

    private GlobalData() {}

}
