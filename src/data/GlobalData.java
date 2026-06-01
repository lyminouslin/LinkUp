package data;

import storage.RankStorage.RankData;

import java.util.HashMap;

public class GlobalData {
    public static String currentUsername = null;
    public static boolean isLoggedIn = false;
    public static final HashMap<String, RankData> currentRankData = new HashMap<>();

    private GlobalData() {}

}
