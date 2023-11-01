package com.nilscoding.maven.mvndlwsdl.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * String utilities.
 * @author NilsCoding
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Checks if the given string is logically empty.
     * @param str string to check
     * @return true if empty, false if not empty
     */
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.trim().isEmpty();
    }

    /**
     * Parses the given string as simple options. Format: key1=value1;key2=value2
     * @param str string to parse
     * @return parsed options, can be empty
     */
    public static Map<String, String> parseOptions(String str) {
        Map<String, String> options = new LinkedHashMap<>();
        if (isEmpty(str)) {
            return options;
        }
        String[] pairs = str.split(";");
        for (String onePair : pairs) {
            int kvSplitIndex = onePair.indexOf('=');
            if (kvSplitIndex > 0) {
                String key = onePair.substring(0, kvSplitIndex).trim();
                String value = onePair.substring(kvSplitIndex + 1);
                if (isEmpty(key) == false) {
                    options.put(key, value);
                }
            } else {
                String key = onePair.trim();
                if (isEmpty(key) == false) {
                    options.put(key, null);
                }
            }
        }
        return options;
    }

    /**
     * Parses the given string to an int value, returning the default value if
     * parsing failed.
     * @param str          string to parse
     * @param defaultValue default value
     * @return parsed int value or default value
     */
    public static int parseToInt(String str, int defaultValue) {
        if (isEmpty(str)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str.trim());
        } catch (Exception ex) {
            return defaultValue;
        }
    }

}
