package com.ke.bella.openapi.utils;

import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class EncryptUtils {
    /**
     * 脱敏，只显示前2位和后4位
     */
    public static String desensitize(String plainText) {
        if(plainText == null || plainText.length() < 6) {
            return plainText;
        }
        return plainText.substring(0, 2) + "****" + plainText.substring(plainText.length() - 4);
    }

    public static String desensitizeByLength(String plainText) {
        if(plainText == null || plainText.length() < 6) {
            return plainText;
        }
        int index = plainText.length() / 3;
        return plainText.substring(0, index) + "****" + plainText.substring(plainText.length() - 4);
    }

    /**
     * sha256
     */
    public static String sha256(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainText.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // 处理加密算法不支持的异常
            log.error("encrypt error", e);
            return null;
        }
    }

    /**
     * BCrypt 哈希密码
     */
    public static String bcryptHash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * BCrypt 验证密码
     */
    public static boolean bcryptCheck(String rawPassword, String hash) {
        try {
            return BCrypt.checkpw(rawPassword, hash);
        } catch (Exception e) {
            log.error("bcrypt check error", e);
            return false;
        }
    }
}
