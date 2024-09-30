package com.register.wowlibre.infrastructure.util;

import java.security.*;
import java.util.*;

public class RandomString {
    private Random random;
    private char[] symbols;
    private char[] buf;


    public RandomString(final int length, final String alphabet) {
        if (length < 1) {
            throw new IllegalArgumentException();
        }
        if (alphabet.length() < 2) {
            throw new IllegalArgumentException();
        }
        this.random = new SecureRandom();
        this.symbols = alphabet.toCharArray();
        this.buf = new char[length];
    }

    /**
     * Generate a random transaction id.
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }
}
