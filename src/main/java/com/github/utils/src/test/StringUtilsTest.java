package com.github.utils.src.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.github.utils.src.main.StringUtils;

public class StringUtilsTest {
    @Nested
    class IsValidTests {

        @Test
        void testNullString() {assertFalse(StringUtils.isValidString(null));}

        @Test
        void testEmptyString() {
            assertFalse(StringUtils.isValidString(""));
        }

        @Test
        void testWhitespaceString() {
            assertFalse(StringUtils.isValidString("   "));
        }

        @Test
        void testValidString() {
            assertTrue(StringUtils.isValidString("Hello"));
        }

        @Test
        void testStringWithLeadingAndTrailingWhitespace() {
            assertTrue(StringUtils.isValidString("  Hello  "));
        }

        @Test
        void testStringWithOnlyNonWhitespaceCharacters() {
            assertTrue(StringUtils.isValidString("123"));
        }

        @Test
        void testStringWithMixedCharacters() {
            assertTrue(StringUtils.isValidString("Hello 123"));
        }
    }
}
