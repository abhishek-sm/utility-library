package com.github.utils.src.main;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import lombok.experimental.UtilityClass;

/**
 * Utility class providing various date and time related methods.
 * Methods include getting the current date/time, formatting and parsing dates, 
 * calculating the difference between dates, adding/subtracting days, and more.
 */
@UtilityClass
public class DateUtils {

    /**
     * Gets the current date.
     *
     * @return The current date.
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /**
     * Gets the current time.
     *
     * @return The current time.
     */
    public static LocalTime getCurrentTime() {
        return LocalTime.now();
    }

    /**
     * Gets the current date and time.
     *
     * @return The current date and time.
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    /**
     * Formats a given LocalDateTime object into a string based on the provided pattern.
     *
     * @param date    The LocalDateTime object to be formatted.
     * @param pattern The pattern to format the date (e.g., "yyyy-MM-dd HH:mm:ss").
     * @return The formatted date string.
     */
    public static String formatDate(LocalDateTime date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }

    /**
     * Parses a date string into a LocalDateTime object based on the provided pattern.
     *
     * @param dateStr The date string to be parsed.
     * @param pattern The pattern used to parse the date string.
     * @return The parsed LocalDateTime object.
     */
    public static LocalDateTime parseDate(String dateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateStr, formatter);
    }

    /**
     * Calculates the number of years between two given dates.
     *
     * @param start The start date.
     * @param end The end date.
     * @return The number of years between the start date and the end date.
     */
    public static long getYearsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.YEARS.between(start, end);
    }

    /**
     * Calculates the number of months between two given dates.
     *
     * @param start The start date.
     * @param end The end date.
     * @return The number of months between the start date and the end date.
     */
    public static long getMonthsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MONTHS.between(start, end);
    }

    /**
     * Gets the number of days between two LocalDateTime objects.
     *
     * @param start The start date/time.
     * @param end   The end date/time.
     * @return The number of days between the two dates/times.
     */
    public static long getDaysBetweenDates(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Gets the number of hours between two LocalDateTime objects.
     *
     * @param start The start date/time.
     * @param end   The end date/time.
     * @return The number of hours between the two dates/times.
     */
    public static long getHoursBetweenDates(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Adds a specified number of days to a given LocalDate.
     *
     * @param date The original date.
     * @param days The number of days to add.
     * @return The new date after adding the specified number of days.
     */
    public static LocalDate addDays(LocalDate date, int days) {
        return date.plusDays(days);
    }

    /**
     * Subtracts a specified number of days from a given LocalDate.
     *
     * @param date The original date.
     * @param days The number of days to subtract.
     * @return The new date after subtracting the specified number of days.
     */
    public static LocalDate subtractDays(LocalDate date, int days) {
        return date.minusDays(days);
    }

    /**
     * Converts a LocalDateTime object to its equivalent UNIX timestamp (seconds since epoch).
     *
     * @param dateTime The LocalDateTime object to convert.
     * @return The UNIX timestamp (seconds since epoch).
     */
    public static long toUnixTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * Converts a UNIX timestamp (seconds since epoch) to a LocalDateTime object.
     *
     * @param timestamp The UNIX timestamp to convert.
     * @return The LocalDateTime object equivalent to the UNIX timestamp.
     */
    public static LocalDateTime fromUnixTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }

    /**
     * Checks if a given year is a leap year.
     *
     * @param year The year to check.
     * @return True if the year is a leap year, false otherwise.
     */
    public static boolean isLeapYear(int year) {
        return Year.of(year).isLeap();
    }

    /**
     * Gets the start of the day for a given LocalDate.
     *
     * @param date The LocalDate to get the start of.
     * @return A LocalDateTime representing the start of the day.
     */
    public static LocalDateTime getStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * Gets the end of the day for a given LocalDate.
     *
     * @param date The LocalDate to get the end of.
     * @return A LocalDateTime representing the end of the day.
     */
    public static LocalDateTime getEndOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }

    /**
     * Checks if a given LocalDate is in the future.
     *
     * @param date The LocalDate to check.
     * @return True if the date is in the future, false otherwise.
     */
    public static boolean isFutureDate(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }

    /**
     * Checks if a given LocalDate is in the past.
     *
     * @param date The LocalDate to check.
     * @return True if the date is in the past, false otherwise.
     */
    public static boolean isPastDate(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    /**
     * Converts a LocalDateTime to a specific time zone.
     *
     * @param dateTime The LocalDateTime to convert.
     * @param zoneId   The target time zone (e.g., "UTC", "America/New_York").
     * @return The LocalDateTime converted to the specified time zone.
     */
    public static ZonedDateTime convertToTimeZone(LocalDateTime dateTime, String zoneId) {
        return dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of(zoneId));
    }
    
}
