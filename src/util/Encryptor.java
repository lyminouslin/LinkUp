package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 轻量级加密工具类
 * 使用 SHA-256 + 随机盐
 */

public class Encryptor {

    // 盐的长度（字节）
    private static final int SALT_LENGTH = 16;

    // 算法名称
    private static final String ALGORITHM = "SHA-256";

    /**
     * 加密密码（注册时调用）
     * @param plainPassword 明文密码
     * @return 加密后的字符串，格式: "盐:哈希值"
     */

    public static String encrypt(String plainPassword) {
        String result;
        try {
            // 1. 生成随机盐
            byte[] saltBytes = new byte[SALT_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(saltBytes);
            String salt = Base64.getEncoder().encodeToString(saltBytes);

            // 2. 计算哈希（密码 + 盐）
            String hash = sha256(plainPassword + salt);

            // 3. 返回 "盐:哈希" 格式
            result = salt + ":" + hash;

        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
        return result;
    }

    /**
     * 验证密码（登录时调用）
     * @param plainPassword 用户输入的明文密码
     * @param storedPassword 数据库存储的加密字符串
     * @return 是否正确
     */
    public static boolean verify(String plainPassword, String storedPassword) {
        if (storedPassword == null || !storedPassword.contains(":")) {
            return false;
        }

        try {
            String[] parts = storedPassword.split(":", 2);
            String salt = parts[0];
            String expectedHash = parts[1];

            // 用相同的盐重新计算哈希
            String actualHash = sha256(plainPassword + salt);

            return actualHash.equals(expectedHash);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * SHA-256 哈希计算
     */
    private static String sha256(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashBytes);
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}