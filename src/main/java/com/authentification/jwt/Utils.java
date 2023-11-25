package com.authentification.jwt;

import java.util.UUID;

public class Utils {
    public static String generateRandomToken() {
        return UUID.randomUUID().toString();
    }
}
