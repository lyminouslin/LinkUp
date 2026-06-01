package storage;

import util.Encryptor;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserStorage {
    private static final String USER_DATA_FILE = "users.obj";

    @SuppressWarnings("unchecked")
    public static Map<String, String> loadUsers() {
        File file = new File(USER_DATA_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return (Map<String, String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    public static void saveUsers(Map<String, String> users) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(USER_DATA_FILE))) {
            oos.writeObject(users);
        }
    }

    public static void addUser(String username, String encryptedPassword) throws IOException {
        Map<String, String> users = loadUsers();
        users.put(username, encryptedPassword);
        saveUsers(users);
    }

    public static boolean verifyUser(String username, String plainPassword) {
        Map<String, String> users = loadUsers();
        if (!users.containsKey(username)) {
            return false;
        }
        return Encryptor.verify(plainPassword, users.get(username));
    }
}