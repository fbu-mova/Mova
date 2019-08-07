package com.example.mova.icons;

import java.security.MessageDigest;

public class MessageDigestHashGenerator implements Identicon.HashGeneratorInterface {
    private MessageDigest messageDigest;

    public MessageDigestHashGenerator(String algorithm) {
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch(Exception e) {
            System.err.println("Error setting algorithim: " + algorithm);
        }
    }

    public byte[] generate(String input) {
        return messageDigest.digest(input.getBytes());
    }
}
