package com.github.utils.src.main;

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
}
