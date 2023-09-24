package com.example.closersharelocation.securiy;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Encoder {

    public static String encode(String text) {
        if (Objects.equals(text, ""))
            return null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] bytecode = messageDigest.digest(text.getBytes());
            BigInteger integer = new BigInteger(1, bytecode);
            return integer.toString(32);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
