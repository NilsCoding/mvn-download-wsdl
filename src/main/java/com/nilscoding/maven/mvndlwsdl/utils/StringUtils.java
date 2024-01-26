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

    /**
     * Formats an int value to String, adding leading zeros to match number of digits of related group size if needed.
     * If the number of digits of the group size implies that the value needs leading zeros, then they'll be added.
     * Example: a group size of 250 implies three digits, so formatting 7 becomes &quot;007&quot;.
     * Negative values will be returned as a simple string.
     * @param value     value to format
     * @param groupSize group size to match
     * @return formatted string
     */
    public static String formatLeadingZeros(int value, int groupSize) {
        if (value < 0) {
            return String.valueOf(value);
        }
        if (groupSize < 1) {
            groupSize = 1;
        }
        int size = (int) Math.floor(Math.log10(groupSize)) + 1;
        String format = "%0" + size + "d";
        return String.format(format, value);
    }

}
