package data;

import storage.RankStorage.RankData;

import java.util.HashMap;

public class GlobalData {
    private static GlobalData instance;

    public static String currentUsername = null;
    public static boolean isLoggedIn = false;
    public static int currentScore = 0;
    public static int currentLevel = 1;
    public static HashMap<String, RankData> currentRankData = new HashMap<>();

    private GlobalData() {}

}
