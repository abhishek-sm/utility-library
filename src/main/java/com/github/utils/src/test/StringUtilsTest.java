package com.github.utils.src.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.github.utils.src.main.StringUtils;

public class StringUtilsTest {

    @Nested
    class IsValidStringTests {

        @Test
        void testNullString() {
            assertFalse(StringUtils.isValidString(null));
        }

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

    @Nested
    class EqualsIgnoreCaseTests {

        @Test
        void testBothStringsNull() {
            assertTrue(StringUtils.equalsIgnoreCase(null, null));
        }

        @Test
        void testOneStringNull() {
            assertFalse(StringUtils.equalsIgnoreCase(null, "Hello"));
        }

        @Test
        void testStringsEqualIgnoringCase() {
            assertTrue(StringUtils.equalsIgnoreCase("hello", "HELLO"));
        }

        @Test
        void testStringsNotEqual() {
            assertFalse(StringUtils.equalsIgnoreCase("hello", "world"));
        }

        @Test
        void testStringsWithSpaces() {
            assertTrue(StringUtils.equalsIgnoreCase(" hello ", "  HELLO  "));
        }
    }

    @Nested
    class CapitalizeTests {

        @Test
        void testNullString() {
            assertNull(StringUtils.capitalize(null));
        }

        @Test
        void testEmptyString() {
            assertEquals("", StringUtils.capitalize(""));
        }

        @Test
        void testSingleWord() {
            assertEquals("Hello", StringUtils.capitalize("hello"));
        }

        @Test
        void testSentence() {
            assertEquals("Hello world", StringUtils.capitalize("hello world"));
        }
    }

    @Nested
    class IsNumericTests {

        @Test
        void testNullString() {
            assertFalse(StringUtils.isNumeric(null));
        }

        @Test
        void testEmptyString() {
            assertFalse(StringUtils.isNumeric(""));
        }

        @Test
        void testStringWithNonNumericCharacters() {
            assertFalse(StringUtils.isNumeric("123abc"));
        }

        @Test
        void testStringWithOnlyDigits() {
            assertTrue(StringUtils.isNumeric("12345"));
        }

        @Test
        void testStringWithSpaces() {
            assertFalse(StringUtils.isNumeric("123 45"));
        }
    }

    @Nested
    class IsAlphaNumericTests {

        @Test
        void testNullString() {
            assertFalse(StringUtils.isAlphaNumeric(null));
        }

        @Test
        void testEmptyString() {
            assertFalse(StringUtils.isAlphaNumeric(""));
        }

        @Test
        void testStringWithOnlyAlphabets() {
            assertTrue(StringUtils.isAlphaNumeric("abcABC"));
        }

        @Test
        void testStringWithDigitsAndAlphabets() {
            assertTrue(StringUtils.isAlphaNumeric("abc123"));
        }

        @Test
        void testStringWithSpecialCharacters() {
            assertFalse(StringUtils.isAlphaNumeric("abc@123"));
        }
    }

    @Nested
    class StringToBooleanTests {

        @Test
        void testNullString() {
            assertNull(StringUtils.stringToBoolean(null));
        }

        @Test
        void testEmptyString() {
            assertFalse(StringUtils.stringToBoolean(""));
        }

        @Test
        void testStringTrue() {
            assertTrue(StringUtils.stringToBoolean("true"));
        }

        @Test
        void testStringFalse() {
            assertFalse(StringUtils.stringToBoolean("false"));
        }

        @Test
        void testStringInvalid() {
            assertFalse(StringUtils.stringToBoolean("invalid"));
        }
    }

    @Nested
    class StringToIntegerTests {

        @Test
        void testNullString() {
            assertNull(StringUtils.stringToInteger(null));
        }

        @Test
        void testEmptyString() {
            assertNull(StringUtils.stringToInteger(""));
        }

        @Test
        void testValidIntegerString() {
            assertEquals(123, StringUtils.stringToInteger("123"));
        }

        @Test
        void testInvalidIntegerString() {
            assertNull(StringUtils.stringToInteger("abc"));
        }

        @Test
        void testStringWithSpaces() {
            assertNull(StringUtils.stringToInteger("123 abc"));
        }
    }

    @Nested
    class JoinStringTests {

        @Test
        void testJoinWithDelimiter() {
            assertEquals("Hello, World, Java", StringUtils.joinString(", ", "Hello", "World", "Java"));
        }

        @Test
        void testJoinWithNoElements() {
            assertEquals("", StringUtils.joinString(", "));
        }

        @Test
        void testJoinWithSingleElement() {
            assertEquals("Hello", StringUtils.joinString(", ", "Hello"));
        }
    }

    @Nested
    class NormalizeWhitespaceTests {

        @Test
        void testEmptyString() {
            assertEquals("", StringUtils.normalizeWhitespace(""));
        }

        @Test
        void testStringWithExtraWhitespace() {
            assertEquals("Hello world", StringUtils.normalizeWhitespace("Hello     world"));
        }

        @Test
        void testStringWithLeadingAndTrailingWhitespace() {
            assertEquals("Hello world", StringUtils.normalizeWhitespace("  Hello world  "));
        }
    }

    @Nested
    class TruncateTests {

        @Test
        void testNullString() {
            assertNull(StringUtils.truncate(null, 5));
        }

        @Test
        void testEmptyString() {
            assertEquals("", StringUtils.truncate("", 5));
        }

        @Test
        void testStringShorterThanLimit() {
            assertEquals("Hello", StringUtils.truncate("Hello", 10));
        }

        @Test
        void testStringLongerThanLimit() {
            assertEquals("Hello", StringUtils.truncate("Hello, World", 5));
        }

        @Test
        void testStringEqualToLimit() {
            assertEquals("Hello", StringUtils.truncate("Hello", 5));
        }
    }

    @Nested
    class ToCamelCaseTests {

        @Test
        void testNullString() {
            assertNull(StringUtils.toCamelCase(null));
        }

        @Test
        void testEmptyString() {
            assertEquals("", StringUtils.toCamelCase(""));
        }

        @Test
        void testSingleWord() {
            assertEquals("hello", StringUtils.toCamelCase("hello"));
        }

        @Test
        void testMultipleWords() {
            assertEquals("helloWorld", StringUtils.toCamelCase("hello world"));
        }

        @Test
        void testStringWithLeadingAndTrailingSpaces() {
            assertEquals("helloWorld", StringUtils.toCamelCase("  hello world  "));
        }
    }

    @Nested
    class ToSnakeCaseTests {

        @Test
        void testNullString() {
            assertNull(StringUtils.toSnakeCase(null));
        }

        @Test
        void testEmptyString() {
            assertEquals("", StringUtils.toSnakeCase(""));
        }

        @Test
        void testSingleWord() {
            assertEquals("hello", StringUtils.toSnakeCase("hello"));
        }

        @Test
        void testMultipleWords() {
            assertEquals("hello_world", StringUtils.toSnakeCase("hello world"));
        }

        @Test
        void testStringWithLeadingAndTrailingSpaces() {
            assertEquals("hello_world", StringUtils.toSnakeCase("  hello world  "));
        }
    }

    @Nested
    class UrlEncodeDecodeTests {

        @Test
        void testUrlEncode() {
            assertEquals("hello%20world", StringUtils.urlEncode("hello world"));
        }

        @Test
        void testUrlDecode() {
            assertEquals("hello world", StringUtils.urlDecode("hello%20world"));
        }
    }
}

