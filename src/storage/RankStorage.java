package storage;

import util.Encryptor;

import java.io.*;
import java.util.TreeMap;

public class RankStorage {
    private static final String RANK_DATA_FILE = "ranks.obj";
    private static final String CHECK_KEY = "LinkUpRankSave";
    private RankStorage() {}

    public static class RankData implements Serializable, Comparable<RankData> {
        private int totalScore;
        private final String username;
        private String hashcode;
        public RankData(int totalScore, String username) {
            this.totalScore = totalScore;
            this.username = username;
            this.hashcode = Encryptor.encrypt(username + totalScore);
        }
        public int getTotalScore() {
            return totalScore;
        }
        public void setTotalScore(int totalScore) {
            this.totalScore = totalScore;
            this.hashcode = Encryptor.encrypt(username + totalScore);
        }
        public String getUsername() {
            return username;
        }
        public String getHashcode() {
            return hashcode;
        }
        @Override
        public int compareTo(RankData o) {
            int deltaScore = Integer.compare(o.totalScore, totalScore);
            if (deltaScore != 0) return deltaScore;
            else return username.compareTo(o.username);
        }
    }
    public static TreeMap<RankData, Integer> loadAllRanks() throws IOException, ClassNotFoundException {
        File file = new File(RANK_DATA_FILE);
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
    public static void saveRanks(TreeMap<RankData, Integer> ranks) throws IOException {
        File file = new File(RANK_DATA_FILE);
        if (!file.exists()) //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(ranks);
        }
    }
}
