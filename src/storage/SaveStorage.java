package storage;

import util.Encryptor;

import java.io.Serializable;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/*数据结构大概是：
用户名+模式 -> 用户存档
*/
public class SaveStorage {
    // 设置保存数据文件名
    private static final String SAVE_DATA_FILE = "saves.obj";
    private static final String CHECK_KEY = "LinkUpSave";

    // 内部类，用于存储模式，剩余时间，分数等等
    public static class SaveData implements Serializable {
        public boolean mode;
        public int leftSeconds, usedSeconds, score, comboCount;
        public int[][] grid;
        public String actionText;
        public String check;
    }

    // 从save.obj读取存档
    @SuppressWarnings("unchecked")
    private static Map<String, SaveData> loadAllSaves() throws IOException, ClassNotFoundException {
        File file = new File(SAVE_DATA_FILE);
        // 如果文件不存在，返回一个空的hashmap
        if (!file.exists()) {
            return new HashMap<>(); 
        }

        //如果文件存在，就用ObjectInputStream打开文件，转换成Map返回
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<String, SaveData>) ois.readObject();
        }
    }

    //把所有的用户存档写入文件
    private static void saveAllSaves(Map<String, SaveData> saves) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_DATA_FILE))) {
            oos.writeObject(saves);
        }
    }
    //读取某一个用户的存档
    public static SaveData loadSave(String username, boolean mode) throws IOException, ClassNotFoundException {
        if (username == null) return null;
        SaveData saveData = loadAllSaves().get(getSaveKey(username, mode));
//        if (saveData != null && !makeCheck(username, saveData).equals(saveData.check)) {
//            throw new SecurityException("save changed");
//        }
        if (saveData != null && !encrypt(username, saveData).equals(saveData.check)) {
            throw new SecurityException("save changed");
        }
        return saveData;
    }
    //保存某一个用户的当前游戏，然后把username和savedata刷入，并saveallsaves
    public static void save(String username, SaveData saveData) throws IOException {
        Map<String, SaveData> saves;
        try {
            saves = loadAllSaves();
        } catch (Exception e) {
            saves = new HashMap<>();
        }
//        saveData.check = makeCheck(username, saveData);
        saveData.check = encrypt(username, saveData);
        saves.put(getSaveKey(username, saveData.mode), saveData);
        saveAllSaves(saves);
    }

    private static String getSaveKey(String username, boolean mode) {
        return username + "_" + (mode ? "hard" : "easy");
    }

    private static String makeCheck(String username, SaveData data) {
        //java当中只要有一个是string，所有的都是string
        int result = (username + CHECK_KEY + data.mode + data.leftSeconds + data.usedSeconds
                + data.score + data.comboCount + data.actionText).hashCode();
        if (data.grid != null) {
            for (int[] row : data.grid) {
                for (int value : row) {
                    result = result * 31 + value;
                }
            }
        }
        return String.valueOf(result);
    }

    private static String encrypt(String username, SaveData data) {
        String result = (username + CHECK_KEY + data.mode + data.leftSeconds + data.usedSeconds
                + data.score + data.comboCount + data.actionText);
        String code = Encryptor.encrypt(result);
        return result;
    }
}
