package com.github.utils.constants;

import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

/**
 * A utility class that contains various regular expression patterns used throughout the application.
 */
@UtilityClass
public class RegexPattern {

    /**
     * Regular expression pattern for validating IPv4 addresses.
     * <p>
     * This pattern matches a valid IPv4 address, which consists of four decimal numbers between 0 and 255,
     * separated by dots (e.g., "192.168.0.1").
     * Each octet in the address must be in the range of 0 to 255, and the address must have exactly four octets.
     * </p>
     * <p>
     * Example valid IPv4 addresses:
     * <ul>
     *     <li>192.168.0.1</li>
     *     <li>255.255.255.255</li>
     *     <li>0.0.0.0</li>
     * </ul>
     * </p>
     */
    public static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    /**
     * Regular expression pattern for validating IPv6 addresses.
     * <p>
     * This pattern matches a valid IPv6 address, which consists of eight groups of four hexadecimal digits,
     * separated by colons (e.g., "2001:0db8:85a3:0000:0000:8a2e:0370:7334").
     * The address can also have shorthand notation, where consecutive groups of zeros can be replaced with "::".
     * </p>
     * <p>
     * Example valid IPv6 addresses:
     * <ul>
     *     <li>2001:0db8:85a3:0000:0000:8a2e:0370:7334</li>
     *     <li>fe80::1ff:fe23:4567:890a</li>
     *     <li>::1</li>
     *     <li>2001:0db8:85a3::8a2e:0370:7334</li>
     * </ul>
     * </p>
     */
    public static final Pattern IPV6_PATTERN = Pattern.compile(
            "^(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|" +
                    "([0-9a-fA-F]{1,4}:){1,7}:|" +
                    "([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|" +
                    "([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|" +
                    "([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|" +
                    "([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|" +
                    "([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|" +
                    "[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|" +
                    ":((:[0-9a-fA-F]{1,4}){1,7}|:)|" +
                    "fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|" +
                    "::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|" +
                    "([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))$");
}
