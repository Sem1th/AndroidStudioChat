package com.example.chat;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    private static final String pass = "password";


    private static SecretKeySpec keySpec;

    static {
        MessageDigest shaDigest = null;
        try {
            shaDigest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = pass.getBytes();
            shaDigest.update(bytes, 0, bytes.length);
            byte[] key = shaDigest.digest();
            keySpec = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }
    public static String encrypt(String rawText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES"); //создаем шифр
        cipher.init(Cipher.ENCRYPT_MODE, keySpec); //инициализируем шифр, шифр готовся шифровать держи ключ
        byte[] encrypted = cipher.doFinal(rawText.getBytes()); //шифр вот данные что нужно зашифровать
        return Base64.encodeToString(encrypted, Base64.DEFAULT); //кодируем еще раз наш текст
    }

    public static String decrypt(String cipheredText) throws Exception {
        byte[] ciphered = Base64.decode(cipheredText, Base64.DEFAULT);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] rawText = cipher.doFinal(ciphered);
        return new String(rawText, "UTF-8");

    }
}
