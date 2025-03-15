package com.github.utils.src.main;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Utility class with string-related methods.
 */
public class StringUtils {

    /**
     * Checks if a string is valid.
     *
     * <p>
     * A string is considered valid if it is not {@code null}, not empty, and not
     * just whitespace after being trimmed.
     *
     * @param str the string to check
     * @return {@code true} if the string is not {@code null}, not empty, and not
     *         just whitespace after being trimmed, {@code false} otherwise
     */
    public static boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Compares two strings ignoring case and whitespace.
     *
     * <p>
     * This method checks if two strings are equal after trimming whitespace and
     * ignoring case differences.
     *
     * @param str1 the first string to compare
     * @param str2 the second string to compare
     * @return {@code true} if the strings are equal after trimming and ignoring
     *         case, {@code false} otherwise
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 != null && str2 != null && str1.trim().equalsIgnoreCase(str2.trim());
    }

    /**
     * Capitalizes the first letter of a string.
     *
     * <p>
     * If the input string is {@code null} or empty, it is returned unchanged.
     * Otherwise, the first character is converted to uppercase, and the rest of
     * the string remains unchanged.
     *
     * @param str the string to capitalize
     * @return the capitalized string
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Checks if a string is numeric.
     *
     * <p>
     * A string is considered numeric if it consists only of digits.
     *
     * @param str the string to check
     * @return {@code true} if the string is numeric, {@code false} otherwise
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("\\d+");
    }

    /**
     * Checks if a string is alphanumeric.
     *
     * <p>
     * A string is considered alphanumeric if it consists only of letters and
     * digits.
     *
     * @param str the string to check
     * @return {@code true} if the string is alphanumeric, {@code false} otherwise
     */
    public static boolean isAlphaNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("[a-zA-Z0-9]+");
    }

    /**
     * Converts a string to a boolean value.
     *
     * <p>
     * If the input string is {@code null}, this method returns {@code null}.
     * Otherwise, it attempts to parse the string as a boolean value, where
     * "true", "True", "TRUE", etc., are considered true, and "false", "False",
     * "FALSE", etc., are considered false. Any other string will result in a
     * {@code null} return value.
     *
     * @param str the string to convert
     * @return the boolean value of the string, or {@code null} if conversion fails
     */
    public static Boolean stringToBoolean(String str) {
        if (str == null) {
            return null;
        }
        return Boolean.parseBoolean(str);
    }

    /**
     * Converts a string to an integer value.
     *
     * <p>
     * If the input string is {@code null} or empty, this method returns {@code null}.
     * Otherwise, it attempts to parse the string as an integer. If parsing fails,
     * this method returns {@code null}.
     *
     * @param str the string to convert
     * @return the integer value of the string, or {@code null} if conversion fails
     */
    public static Integer stringToInteger(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Joins multiple strings into a single string with a specified delimiter.
     *
     * <p>
     * This method concatenates all input strings with the given delimiter in
     * between each string.
     *
     * @param delimiter the delimiter to use between strings
     * @param elements the strings to join
     * @return the joined string
     */
    public static String joinString(String delimiter, String... elements) {
        return String.join(delimiter, elements);
    }

    /**
     * Normalizes whitespace in a string.
     *
     * <p>
     * This method replaces multiple whitespace characters with a single space.
     *
     * @param str the string to normalize
     * @return the string with normalized whitespace
     */
    public static String normalizeWhitespace(String str) {
        return str.replaceAll("\\s+", " ");
    }

    /**
     * Truncates a string to a specified length.
     *
     * <p>
     * If the input string is longer than the specified length, it is truncated to
     * that length. Otherwise, the original string is returned.
     *
     * @param str the string to truncate
     * @param length the maximum length of the string
     * @return the truncated string
     */
    public static String truncate(String str, int length) {
        if (str == null) {
            return null;
        }
        return str.length() > length ? str.substring(0, length) : str;
    }

    /**
     * Converts a string to camel case.
     *
     * <p>
     * This method splits the input string into words based on whitespace, converts
     * the first word to lowercase, and capitalizes the first letter of each
     * subsequent word.
     *
     * @param str the string to convert
     * @return the string in camel case
     */
    public static String toCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i == 0) {
                sb.append(words[i].toLowerCase());
            } else {
                sb.append(capitalize(words[i]));
            }
        }
        return sb.toString();
    }

    /**
     * Converts a string to snake case.
     *
     * <p>
     * This method replaces all whitespace characters with underscores and converts
     * the entire string to lowercase.
     *
     * @param str the string to convert
     * @return the string in snake case
     */
    public static String toSnakeCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.replaceAll("\\s+", "_").toLowerCase();
    }

    /**
     * URL encodes a string.
     *
     * <p>
     * This method encodes special characters in the input string according to URL
     * encoding rules.
     *
     * @param str the string to encode
     * @return the URL-encoded string
     */
    public static String urlEncode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

    /**
     * URL decodes a string.
     *
     * <p>
     * This method decodes URL-encoded characters in the input string back to their
     * original form.
     *
     * @param str the string to decode
     * @return the URL-decoded string
     */
    public static String urlDecode(String str) {
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }
    
}
