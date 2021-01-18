package com.pwr_lab.passwordshaker;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("unused")
public final class PasswordGenerator {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUWXYZ";
    private static final String LOWER = "avcdefghijklmnopqrstuwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*";

    private static class PasswordOptions {
        public boolean upper;
        public boolean lower;
        public boolean digits;
        public boolean special;
        public int minLen;
        public int maxLen;

        public boolean defaultDigits = true;
        public boolean defaultLower = true;
        public boolean defaultUpper = false;
        public boolean defaultSpecial = false;
        public int defaultMinLen = 8;
        public int defaultMaxLen = 8;

        public void setDefaultCharsets() {
            upper = defaultUpper;
            lower = defaultLower;
            digits = defaultDigits;
            special = defaultSpecial;
        }

        public void setDefaults() {
            setDefaultCharsets();
            minLen = defaultMinLen;
            maxLen = defaultMaxLen;
        }

        public void reset() {
            upper = lower = digits = special = false;
        }

        private boolean isCharsetValid() {
            return upper || lower || digits || special;
        }

        public boolean isValid() {
             if (!isCharsetValid()) return false;
             if (minLen < 1 || maxLen < 1) return false;
             return minLen <= maxLen;
        }
    }

    private final Random rand;
    private final PasswordOptions options;

    public PasswordGenerator() {
        rand = new SecureRandom();
        options = new PasswordOptions();
    }

    public String next() {
        if (!isValid()) return "";

        ArrayList<String> charSets = new ArrayList<>(4);
        setCharSets(charSets);

        int passwordLength;
        if (options.minLen == options.maxLen) {
            passwordLength = options.minLen;
        }
        else {
            // in order to get a random number X from [a, b]
            // diff = b - a
            // X = rand.nextInt(diff + 1) // X is in [0, diff]
            // X = a + X // X is in [a, diff + a] = [a, b]
            int diff = options.maxLen - options.minLen;
            passwordLength = rand.nextInt(diff + 1) + options.minLen;
        }

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < passwordLength; i++) {
            // get random character set
            int charSetInd = rand.nextInt(charSets.size());
            String charSet = charSets.get(charSetInd);

            // get random character
            int charInd = rand.nextInt(charSet.length());
            char passChar = charSet.charAt(charInd);

            // append random char to password
            password.append(passChar);
        }

        return password.toString();
    }

    private void setCharSets(ArrayList<String> charSets) {
        if (options.lower) charSets.add(LOWER);
        if (options.upper) charSets.add(UPPER);
        if (options.digits) charSets.add(DIGITS);
        if (options.special) charSets.add(SPECIAL);
    }

    private boolean isValid() {
        return options.isValid();
    }

    public PasswordGenerator setUpper(boolean status) {
        options.upper = status;
        return this;
    }

    public PasswordGenerator setUpper() {
        return setUpper(true);
    }

    public PasswordGenerator setLower(boolean status) {
        options.lower = status;
        return this;
    }

    public PasswordGenerator setLower() {
        return setLower(true);
    }

    public PasswordGenerator setDigits(boolean status) {
        options.digits = status;
        return this;
    }

    public PasswordGenerator setDigits() {
        return setDigits(true);
    }

    public PasswordGenerator setSpecial(boolean status) {
        options.special = status;
        return this;
    }

    public PasswordGenerator setSpecial() {
        return setSpecial(true);
    }

    public PasswordGenerator setMinLen(int len) {
        if (len > 0) {
            options.minLen = len;
        }

        return this;
    }

    public PasswordGenerator setMaxLen(int len) {
        if (len > 0) {
            options.maxLen = len;
        }

        return this;
    }

    public PasswordGenerator setLen(int len) {
        if (len > 0) {
            options.minLen = options.maxLen = len;
        }

        return this;
    }

    public boolean getUpper() {
        return options.upper;
    }

    public boolean getLower() {
        return options.lower;
    }

    public boolean getDigits() {
        return options.digits;
    }

    public boolean getSpecial() {
        return options.special;
    }

    public int getMinLen() {
        return options.minLen;
    }

    public int getMaxLen() {
        return options.maxLen;
    }

    public int getLen() {
        return getMinLen();
    }

    public PasswordGenerator setDefaultDigits(boolean status) {
        options.defaultDigits = status;
        return this;
    }

    public PasswordGenerator setDefaultLower(boolean status) {
        options.defaultLower = status;
        return this;
    }

    public PasswordGenerator setDefaultUpper(boolean status) {
        options.defaultUpper = status;
        return this;
    }

    public PasswordGenerator setDefaultSpecial(boolean status) {
        options.defaultSpecial = status;
        return this;
    }

    public PasswordGenerator setDefaultMinLen(int len) {
        if (len > 0) {
            options.defaultMinLen = len;
        }

        return this;
    }

    public PasswordGenerator setDefaultMaxLen(int len) {
        if (len > 0) {
            options.defaultMaxLen = len;
        }

        return this;
    }

    public PasswordGenerator setDefaultLen(int len) {
        if (len > 0) {
            options.defaultMinLen = options.defaultMaxLen = len;
        }

        return this;
    }

    public PasswordGenerator setDefaultCharsets() {
        options.setDefaultCharsets();
        return this;
    }

    public PasswordGenerator setDefaultOptions() {
        options.setDefaults();
        return this;
    }
}
