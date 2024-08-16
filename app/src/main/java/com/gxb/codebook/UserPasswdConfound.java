package com.gxb.codebook;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class UserPasswdConfound {
    public static String encrypt(String input) {
        StringBuilder passwd = new StringBuilder();

        for (char c : input.toCharArray()) {
            passwd.append(getPost12(c));
        }

        try {
            String key = "WhicePasswd598732146AbCd";
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedPasswd = cipher.doFinal(passwd.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedPasswd);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting: " + e.toString());
        }
    }

    private static char getPost12(char c) {
        if (c >= 'A' && c <= 'Z') {
            return (char) ('A' + (c - 'A' + 13) % 26);
        } else if (c >= 'a' && c <= 'z') {
            return (char) ('a' + (c - 'a' + 13) % 26);
        } else if (c >= '0' && c <= '9') {
            return (char) ('0' + (c - '0' + 5) % 10);
        } else if (c == '@') {
            return '!';
        } else if (c == '#') {
            return '"';
        } else if (c == '$') {
            return '#';
        } else if (c == '%') {
            return '$';
        } else if (c == '^') {
            return '%';
        } else if (c == '&') {
            return '^';
        } else if (c == '*') {
            return '&';
        } else if (c == '.') {
            return '*';
        }
        return c;
    }
}
