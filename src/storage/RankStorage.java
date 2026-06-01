package storage;

import java.io.*;
import java.util.TreeMap;

public class RankStorage {
    private static final String SAVE_DATA_FILE = "ranks.obj";
    private static final String CHECK_KEY = "LinkUpRankSave";
    private RankStorage() {}

    public static class RankData implements Serializable, Comparable<RankData> {
        private int totalScore;
        private String username;
        private String hashcode;
        public RankData(int totalScore, String username, String hashcode) {
            this.totalScore = totalScore;
            this.username = username;
            this.hashcode = hashcode;
        }
        public int getTotalScore() {
            return totalScore;
        }
        public void setTotalScore(int totalScore) {
            this.totalScore = totalScore;
        }
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getHashcode() {
            return hashcode;
        }
        public void setHashcode(String hashcode) {
            this.hashcode = hashcode;
        }
        @Override
        public int compareTo(RankData o) {
            int deltaScore = Integer.compare(o.totalScore, totalScore);
            if (deltaScore != 0) return deltaScore;
            else return username.compareTo(o.username);
        }
    }
    public static TreeMap<RankData, Integer> loadRanks() throws IOException, ClassNotFoundException {
        File file = new File(SAVE_DATA_FILE);
        // 如果文件不存在，返回一个空的treemap
        if (!file.exists()) {
            return new TreeMap<>();
        }
        //如果文件存在，就用ObjectInputStream打开文件，转换成Map返回
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            //noinspection unchecked
            return (TreeMap<RankData, Integer>) ois.readObject();
        }
    }
    public static boolean saveRanks(TreeMap<RankData, Integer> ranks) throws IOException {
        File file = new File(SAVE_DATA_FILE);
        boolean succeed = file.createNewFile();
        if (!file.exists()) succeed = file.createNewFile();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(ranks);
            return true;
        }
    }
}
