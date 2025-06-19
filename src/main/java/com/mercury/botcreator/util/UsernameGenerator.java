package com.mercury.botcreator.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

@UtilityClass
public class UsernameGenerator {

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    public static String generate(String prefix, int length) {
        int randomPartLength = length - (prefix != null ? prefix.length() : 0);
        if (randomPartLength <= 0) {
            throw new IllegalArgumentException("Length must be greater than prefix length.");
        }

        StringBuilder sb = new StringBuilder(prefix != null ? prefix : "");
        for (int i = 0; i < randomPartLength; i++) {
            sb.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }
}
