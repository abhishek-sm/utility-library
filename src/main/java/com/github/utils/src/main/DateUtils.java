package com.github.utils.src.main;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.experimental.UtilityClass;

/**
 * Utility class providing various date and time related methods.
 * Methods include getting the current date/time, formatting and parsing dates, 
 * calculating the difference between dates, adding/subtracting time units, and more.
 */
@UtilityClass
public class DateUtils {

    // Common date format patterns
    public static final String ISO_DATE_PATTERN = "yyyy-MM-dd";
    public static final String ISO_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String READABLE_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

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
     * Gets the current date and time with timezone information.
     *
     * @return The current date and time with system default timezone.
     */
    public static ZonedDateTime getCurrentZonedDateTime() {
        return ZonedDateTime.now();
    }

    /**
     * Gets the current date and time in UTC.
     *
     * @return The current date and time in UTC.
     */
    public static ZonedDateTime getCurrentUtcDateTime() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Formats a given LocalDateTime object into a string based on the provided pattern.
     *
     * @param date    The LocalDateTime object to be formatted.
     * @param pattern The pattern to format the date (e.g., "yyyy-MM-dd HH:mm:ss").
     * @return The formatted date string.
     * @throws IllegalArgumentException if the date is null or pattern is invalid
     */
    public static String formatDate(LocalDateTime date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }

    /**
     * Formats a given LocalDate object into a string based on the provided pattern.
     *
     * @param date    The LocalDate object to be formatted.
     * @param pattern The pattern to format the date (e.g., "yyyy-MM-dd").
     * @return The formatted date string.
     * @throws IllegalArgumentException if the date is null or pattern is invalid
     */
    public static String formatDate(LocalDate date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }

    /**
     * Formats a given ZonedDateTime object into a string based on the provided pattern.
     *
     * @param date    The ZonedDateTime object to be formatted.
     * @param pattern The pattern to format the date (e.g., "yyyy-MM-dd HH:mm:ss z").
     * @return The formatted date string.
     * @throws IllegalArgumentException if the date is null or pattern is invalid
     */
    public static String formatDate(ZonedDateTime date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }

    /**
     * Parses a date string into a LocalDateTime object based on the provided pattern.
     *
     * @param dateStr The date string to be parsed.
     * @param pattern The pattern used to parse the date string.
     * @return The parsed LocalDateTime object.
     * @throws IllegalArgumentException if the date string cannot be parsed
     */
    public static LocalDateTime parseDateTime(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateStr, formatter);
    }

    /**
     * Parses a date string into a LocalDate object based on the provided pattern.
     *
     * @param dateStr The date string to be parsed.
     * @param pattern The pattern used to parse the date string.
     * @return The parsed LocalDate object.
     * @throws IllegalArgumentException if the date string cannot be parsed
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateStr, formatter);
    }

    /**
     * Parses a date string into a ZonedDateTime object based on the provided pattern.
     *
     * @param dateStr The date string to be parsed.
     * @param pattern The pattern used to parse the date string.
     * @return The parsed ZonedDateTime object.
     * @throws IllegalArgumentException if the date string cannot be parsed
     */
    public static ZonedDateTime parseZonedDateTime(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return ZonedDateTime.parse(dateStr, formatter);
    }

    /**
     * Calculates the number of years between two given dates.
     *
     * @param start The start date.
     * @param end   The end date.
     * @return The number of years between the start date and the end date.
     * @throws IllegalArgumentException if either date is null
     */
    public static long getYearsBetween(LocalDateTime start, LocalDateTime end) {
        validateDateRange(start, end);
        return ChronoUnit.YEARS.between(start, end);
    }

    /**
     * Calculates the number of years between two given dates.
     *
     * @param start The start date.
     * @param end   The end date.
     * @return The number of years between the start date and the end date.
     * @throws IllegalArgumentException if either date is null
     */
    public static long getYearsBetween(LocalDate start, LocalDate end) {
        validateDateRange(start, end);
        return ChronoUnit.YEARS.between(start, end);
    }

    /**
     * Calculates the number of months between two given dates.
     *
     * @param start The start date.
     * @param end   The end date.
     * @return The number of months between the start date and the end date.
     * @throws IllegalArgumentException if either date is null
     */
    public static long getMonthsBetween(LocalDateTime start, LocalDateTime end) {
        validateDateRange(start, end);
        return ChronoUnit.MONTHS.between(start, end);
    }

    /**
     * Calculates the number of months between two given dates.
     *
     * @param start The start date.
     * @param end   The end date.
     * @return The number of months between the start date and the end date.
     * @throws IllegalArgumentException if either date is null
     */
    public static long getMonthsBetween(LocalDate start, LocalDate end) {
        validateDateRange(start, end);
        return ChronoUnit.MONTHS.between(start, end);
    }

    /**
     * Gets the number of days between two LocalDateTime objects.
     *
     * @param start The start date/time.
     * @param end   The end date/time.
     * @return The number of days between the two dates/times.
     * @throws IllegalArgumentException if either date is null
     */
    public static long getDaysBetween(LocalDateTime start, LocalDateTime end) {
        validateDateRange(start, end);
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Gets the number of days between two LocalDate objects.
     *
     * @param start The start date.
     * @param end   The end date.
     * @return The number of days between the two dates.
     * @throws IllegalArgumentException if either date is null
     */
    public static long getDaysBetween(LocalDate start, LocalDate end) {
        validateDateRange(start, end);
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Gets the number of hours between two LocalDateTime objects.
     *
     * @param start The start date/time.
     * @param end   The end date/time.
     * @return The number of hours between the two dates/times.
     * @throws IllegalArgumentException if either date is null
     */
    public static long getHoursBetween(LocalDateTime start, LocalDateTime end) {
        validateDateRange(start, end);
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Gets the number of minutes between two LocalDateTime objects.
     *
     * @param start The start date/time.
     * @param end   The end date/time.
     * @return The number of minutes between the two dates/times.
     * @throws IllegalArgumentException if either date is null
     */
    public static long getMinutesBetween(LocalDateTime start, LocalDateTime end) {
        validateDateRange(start, end);
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * Gets the number of seconds between two LocalDateTime objects.
     *
     * @param start The start date/time.
     * @param end   The end date/time.
     * @return The number of seconds between the two dates/times.
     * @throws IllegalArgumentException if either date is null
     */
    public static long getSecondsBetween(LocalDateTime start, LocalDateTime end) {
        validateDateRange(start, end);
        return ChronoUnit.SECONDS.between(start, end);
    }

    /**
     * Adds a specified number of days to a given LocalDate.
     *
     * @param date The original date.
     * @param days The number of days to add.
     * @return The new date after adding the specified number of days.
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDate addDays(LocalDate date, int days) {
        validateDate(date);
        return date.plusDays(days);
    }

    /**
     * Adds a specified number of days to a given LocalDateTime.
     *
     * @param dateTime The original date/time.
     * @param days     The number of days to add.
     * @return The new date/time after adding the specified number of days.
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static LocalDateTime addDays(LocalDateTime dateTime, int days) {
        validateDate(dateTime);
        return dateTime.plusDays(days);
    }

    /**
     * Adds a specified number of months to a given LocalDate.
     *
     * @param date   The original date.
     * @param months The number of months to add.
     * @return The new date after adding the specified number of months.
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDate addMonths(LocalDate date, int months) {
        validateDate(date);
        return date.plusMonths(months);
    }

    /**
     * Adds a specified number of months to a given LocalDateTime.
     *
     * @param dateTime The original date/time.
     * @param months   The number of months to add.
     * @return The new date/time after adding the specified number of months.
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static LocalDateTime addMonths(LocalDateTime dateTime, int months) {
        validateDate(dateTime);
        return dateTime.plusMonths(months);
    }

    /**
     * Adds a specified number of years to a given LocalDate.
     *
     * @param date  The original date.
     * @param years The number of years to add.
     * @return The new date after adding the specified number of years.
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDate addYears(LocalDate date, int years) {
        validateDate(date);
        return date.plusYears(years);
    }

    /**
     * Adds a specified number of years to a given LocalDateTime.
     *
     * @param dateTime The original date/time.
     * @param years    The number of years to add.
     * @return The new date/time after adding the specified number of years.
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static LocalDateTime addYears(LocalDateTime dateTime, int years) {
        validateDate(dateTime);
        return dateTime.plusYears(years);
    }

    /**
     * Adds a specified number of hours to a given LocalDateTime.
     *
     * @param dateTime The original date/time.
     * @param hours    The number of hours to add.
     * @return The new date/time after adding the specified number of hours.
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, int hours) {
        validateDate(dateTime);
        return dateTime.plusHours(hours);
    }

    /**
     * Adds a specified number of minutes to a given LocalDateTime.
     *
     * @param dateTime The original date/time.
     * @param minutes  The number of minutes to add.
     * @return The new date/time after adding the specified number of minutes.
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static LocalDateTime addMinutes(LocalDateTime dateTime, int minutes) {
        validateDate(dateTime);
        return dateTime.plusMinutes(minutes);
    }

    /**
     * Adds a specified number of seconds to a given LocalDateTime.
     *
     * @param dateTime The original date/time.
     * @param seconds  The number of seconds to add.
     * @return The new date/time after adding the specified number of seconds.
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static LocalDateTime addSeconds(LocalDateTime dateTime, int seconds) {
        validateDate(dateTime);
        return dateTime.plusSeconds(seconds);
    }

    /**
     * Subtracts a specified number of days from a given LocalDate.
     *
     * @param date The original date.
     * @param days The number of days to subtract.
     * @return The new date after subtracting the specified number of days.
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDate subtractDays(LocalDate date, int days) {
        validateDate(date);
        return date.minusDays(days);
    }

    /**
     * Subtracts a specified number of days from a given LocalDateTime.
     *
     * @param dateTime The original date/time.
     * @param days     The number of days to subtract.
     * @return The new date/time after subtracting the specified number of days.
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static LocalDateTime subtractDays(LocalDateTime dateTime, int days) {
        validateDate(dateTime);
        return dateTime.minusDays(days);
    }

    /**
     * Subtracts a specified number of months from a given LocalDate.
     *
     * @param date   The original date.
     * @param months The number of months to subtract.
     * @return The new date after subtracting the specified number of months.
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDate subtractMonths(LocalDate date, int months) {
        validateDate(date);
        return date.minusMonths(months);
    }

    /**
     * Subtracts a specified number of months from a given LocalDateTime.
     *
     * @param dateTime The original date/time.
     * @param months   The number of months to subtract.
     * @return The new date/time after subtracting the specified number of months.
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static LocalDateTime subtractMonths(LocalDateTime dateTime, int months) {
        validateDate(dateTime);
        return dateTime.minusMonths(months);
    }

    /**
     * Subtracts a specified number of years from a given LocalDate.
     *
     * @param date  The original date.
     * @param years The number of years to subtract.
     * @return The new date after subtracting the specified number of years.
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDate subtractYears(LocalDate date, int years) {
        validateDate(date);
        return date.minusYears(years);
    }

    /**
     * Subtracts a specified number of years from a given LocalDateTime.
     *
     * @param dateTime The original date/time.
     * @param years    The number of years to subtract.
     * @return The new date/time after subtracting the specified number of years.
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static LocalDateTime subtractYears(LocalDateTime dateTime, int years) {
        validateDate(dateTime);
        return dateTime.minusYears(years);
    }

    /**
     * Subtracts a specified number of hours from a given LocalDateTime.
     *
     * @param dateTime The original date/time.
     * @param hours    The number of hours to subtract.
     * @return The new date/time after subtracting the specified number of hours.
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static LocalDateTime subtractHours(LocalDateTime dateTime, int hours) {
        validateDate(dateTime);
        return dateTime.minusHours(hours);
    }

    /**
     * Subtracts a specified number of minutes from a given LocalDateTime.
     *
     * @param dateTime The original date/time.
     * @param minutes  The number of minutes to subtract.
     * @return The new date/time after subtracting the specified number of minutes.
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static LocalDateTime subtractMinutes(LocalDateTime dateTime, int minutes) {
        validateDate(dateTime);
        return dateTime.minusMinutes(minutes);
    }

    /**
     * Subtracts a specified number of seconds from a given LocalDateTime.
     *
     * @param dateTime The original date/time.
     * @param seconds  The number of seconds to subtract.
     * @return The new date/time after subtracting the specified number of seconds.
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static LocalDateTime subtractSeconds(LocalDateTime dateTime, int seconds) {
        validateDate(dateTime);
        return dateTime.minusSeconds(seconds);
    }

    /**
     * Converts a LocalDateTime object to its equivalent UNIX timestamp (seconds since epoch).
     *
     * @param dateTime The LocalDateTime object to convert.
     * @return The UNIX timestamp (seconds since epoch).
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static long toUnixTimestamp(LocalDateTime dateTime) {
        validateDate(dateTime);
        return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * Converts a LocalDate object to its equivalent UNIX timestamp (seconds since epoch).
     * The time is set to the start of day (00:00:00).
     *
     * @param date The LocalDate object to convert.
     * @return The UNIX timestamp (seconds since epoch).
     * @throws IllegalArgumentException if the date is null
     */
    public static long toUnixTimestamp(LocalDate date) {
        validateDate(date);
        return date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * Converts a ZonedDateTime object to its equivalent UNIX timestamp (seconds since epoch).
     *
     * @param dateTime The ZonedDateTime object to convert.
     * @return The UNIX timestamp (seconds since epoch).
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static long toUnixTimestamp(ZonedDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("ZonedDateTime cannot be null");
        }
        return dateTime.toEpochSecond();
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
     * Converts a UNIX timestamp (seconds since epoch) to a LocalDate object.
     *
     * @param timestamp The UNIX timestamp to convert.
     * @return The LocalDate object equivalent to the UNIX timestamp.
     */
    public static LocalDate fromUnixTimestampToLocalDate(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Converts a UNIX timestamp (seconds since epoch) to a ZonedDateTime object.
     *
     * @param timestamp The UNIX timestamp to convert.
     * @param zoneId    The time zone ID for the resulting ZonedDateTime.
     * @return The ZonedDateTime object equivalent to the UNIX timestamp.
     */
    public static ZonedDateTime fromUnixTimestampToZonedDateTime(long timestamp, String zoneId) {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.of(zoneId));
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
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDateTime getStartOfDay(LocalDate date) {
        validateDate(date);
        return date.atStartOfDay();
    }

    /**
     * Gets the end of the day for a given LocalDate.
     *
     * @param date The LocalDate to get the end of.
     * @return A LocalDateTime representing the end of the day.
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDateTime getEndOfDay(LocalDate date) {
        validateDate(date);
        return date.atTime(LocalTime.MAX);
    }

    /**
     * Gets the start of the week for a given LocalDate.
     * Week starts on Monday.
     *
     * @param date The LocalDate to get the start of the week for.
     * @return A LocalDate representing the start of the week.
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDate getStartOfWeek(LocalDate date) {
        validateDate(date);
        return date.with(DayOfWeek.MONDAY);
    }

    /**
     * Gets the end of the week for a given LocalDate.
     * Week ends on Sunday.
     *
     * @param date The LocalDate to get the end of the week for.
     * @return A LocalDate representing the end of the week.
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDate getEndOfWeek(LocalDate date) {
        validateDate(date);
        return date.with(DayOfWeek.SUNDAY);
    }

    /**
     * Gets the start of the month for a given LocalDate.
     *
     * @param date The LocalDate to get the start of the month for.
     * @return A LocalDate representing the start of the month.
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDate getStartOfMonth(LocalDate date) {
        validateDate(date);
        return date.withDayOfMonth(1);
    }

    /**
     * Gets the end of the month for a given LocalDate.
     *
     * @param date The LocalDate to get the end of the month for.
     * @return A LocalDate representing the end of the month.
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDate getEndOfMonth(LocalDate date) {
        validateDate(date);
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * Gets the start of the year for a given LocalDate.
     *
     * @param date The LocalDate to get the start of the year for.
     * @return A LocalDate representing the start of the year.
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDate getStartOfYear(LocalDate date) {
        validateDate(date);
        return date.withDayOfYear(1);
    }

    /**
     * Gets the end of the year for a given LocalDate.
     *
     * @param date The LocalDate to get the end of the year for.
     * @return A LocalDate representing the end of the year.
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDate getEndOfYear(LocalDate date) {
        validateDate(date);
        return date.with(TemporalAdjusters.lastDayOfYear());
    }

    /**
     * Checks if a given LocalDate is in the future.
     *
     * @param date The LocalDate to check.
     * @return True if the date is in the future, false otherwise.
     * @throws IllegalArgumentException if the date is null
     */
    public static boolean isFutureDate(LocalDate date) {
        validateDate(date);
        return date.isAfter(LocalDate.now());
    }

    /**
     * Checks if a given LocalDateTime is in the future.
     *
     * @param dateTime The LocalDateTime to check.
     * @return True if the date/time is in the future, false otherwise.
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static boolean isFutureDateTime(LocalDateTime dateTime) {
        validateDate(dateTime);
        return dateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Checks if a given LocalDate is in the past.
     *
     * @param date The LocalDate to check.
     * @return True if the date is in the past, false otherwise.
     * @throws IllegalArgumentException if the date is null
     */
    public static boolean isPastDate(LocalDate date) {
        validateDate(date);
        return date.isBefore(LocalDate.now());
    }

    /**
     * Checks if a given LocalDateTime is in the past.
     *
     * @param dateTime The LocalDateTime to check.
     * @return True if the date/time is in the past, false otherwise.
     * @throws IllegalArgumentException if the dateTime is null
     */
    public static boolean isPastDateTime(LocalDateTime dateTime) {
        validateDate(dateTime);
        return dateTime.isBefore(LocalDateTime.now());
    }

    /**
     * Checks if a given LocalDate is today.
     *
     * @param date The LocalDate to check.
     * @return True if the date is today, false otherwise.
     * @throws IllegalArgumentException if the date is null
     */
    public static boolean isToday(LocalDate date) {
        validateDate(date);
        return date.isEqual(LocalDate.now());
    }

    /**
     * Checks if a given date is a weekend (Saturday or Sunday).
     *
     * @param date The date to check.
     * @return True if the date is a weekend, false otherwise.
     * @throws IllegalArgumentException if the date is null
     */
    public static boolean isWeekend(LocalDate date) {
        validateDate(date);
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    /**
     * Checks if a given date is a weekday (Monday through Friday).
     *
     * @param date The date to check.
     * @return True if the date is a weekday, false otherwise.
     * @throws IllegalArgumentException if the date is null
     */
    public static boolean isWeekday(LocalDate date) {
        return !isWeekend(date);
    }

    /**
     * Converts a LocalDateTime to a ZonedDateTime in a specific time zone.
     *
     * @param dateTime The LocalDateTime to convert.
     * @param zoneId   The target time zone (e.g., "UTC", "America/New_York").
     * @return The ZonedDateTime in the specified time zone.
     * @throws IllegalArgumentException if the dateTime is null or zoneId is invalid
     */
    public static ZonedDateTime toZonedDateTime(LocalDateTime dateTime, String zoneId) {
        validateDate(dateTime);
        if (zoneId == null || zoneId.isEmpty()) {
            throw new IllegalArgumentException("Zone ID cannot be null or empty");
        }
        return dateTime.atZone(ZoneId.of(zoneId));
    }

    /**
     * Converts a ZonedDateTime to a different time zone.
     *
     * @param zonedDateTime The ZonedDateTime to convert.
     * @param zoneId        The target time zone (e.g., "UTC", "America/New_York").
     * @return The ZonedDateTime converted to the specified time zone.
     * @throws IllegalArgumentException if the zonedDateTime is null or zoneId is invalid
     */
    public static ZonedDateTime convertToTimeZone(ZonedDateTime zonedDateTime, String zoneId) {
        if (zonedDateTime == null) {
            throw new IllegalArgumentException("ZonedDateTime cannot be null");
        }
        if (zoneId == null || zoneId.isEmpty()) {
            throw new IllegalArgumentException("Zone ID cannot be null or empty");
        }
        return zonedDateTime.withZoneSameInstant(ZoneId.of(zoneId));
    }

    /**
     * Converts a LocalDateTime to a different time zone.
     *
     * @param dateTime The LocalDateTime to convert.
     * @param zoneId   The target time zone (e.g., "UTC", "America/New_York").
     * @return The ZonedDateTime converted to the specified time zone.
     * @throws IllegalArgumentException if the dateTime is null or zoneId is invalid
     */
    public static ZonedDateTime convertToTimeZone(LocalDateTime dateTime, String zoneId) {
        validateDate(dateTime);
        if (zoneId == null || zoneId.isEmpty()) {
            throw new IllegalArgumentException("Zone ID cannot be null or empty");
        }
        return dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of(zoneId));
    }

    /**
     * Converts a java.util.Date to java.time.LocalDate.
     *
     * @param date The java.util.Date to convert.
     * @return The LocalDate equivalent of the provided Date.
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Converts a java.util.Date to java.time.LocalDateTime.
     *
     * @param date The java.util.Date to convert.
     * @return The LocalDateTime equivalent of the provided Date.
     * @throws IllegalArgumentException if the date is null
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Converts a java.util.Date to java.time.ZonedDateTime.
     *
     * @param date The java.util.Date to convert.
     * @return The ZonedDateTime equivalent of the provided Date.
     * @throws IllegalArgumentException if the date is null
     */
    public static ZonedDateTime toZonedDateTime(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.toInstant().atZone(ZoneId.systemDefault());
    }

    /**
     * Converts a java.time.LocalDate to java.util.Date.
     *
     * @param localDate The LocalDate to convert.
     * @return The Date equivalent of the provided LocalDate.
     * @throws IllegalArgumentException if the localDate is null
     */
    public static Date toDate(LocalDate localDate) {
        validateDate(localDate);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Converts a java.time.LocalDateTime to java.util.Date.
     *
     * @param localDateTime The LocalDateTime to convert.
     * @return The Date equivalent of the provided LocalDateTime.
     * @throws IllegalArgumentException if the localDateTime is null
     */
    public static Date toDate(LocalDateTime localDateTime) {
        validateDate(localDateTime);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Converts a java.time.ZonedDateTime to java.util.Date.
     *
     * @param zonedDateTime The ZonedDateTime to convert.
     * @return The Date equivalent of the provided ZonedDateTime.
     * @throws IllegalArgumentException if the zonedDateTime is null
     */
    public static Date toDate(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            throw new IllegalArgumentException("ZonedDateTime cannot be null");
        }
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * Gets the day of week for a given date.
     *
     * @param date The date to check.
     * @return The day of week.
     * @throws IllegalArgumentException if the date is null
     */
    public static DayOfWeek getDayOfWeek(LocalDate date) {
        validateDate(date);
        return date.getDayOfWeek();
    }

    /**
     * Gets the quarter of the year for a given date.
     *
     * @param date The date to check.
     * @return The quarter of the year (1-4).
     * @throws IllegalArgumentException if the date is null
     */
    public static int getQuarter(LocalDate date) {
        validateDate(date);
        int month = date.getMonthValue();
        return (month - 1) / 3 + 1;
    }

    /**
     * Gets the last day of month for a given date.
     *
     * @param date The date to check.
     * @return The last day of the month.
     * @throws IllegalArgumentException if the date is null
     */
    public static int getLastDayOfMonth(LocalDate date) {
        validateDate(date);
        return date.lengthOfMonth();
    }

    /**
     * Gets the number of days in the current month.
     *
     * @return The number of days in the current month.
     */
    public static int getDaysInCurrentMonth() {
        return LocalDate.now().lengthOfMonth();
    }

    /**
     * Gets the number of days in the specified month.
     *
     * @param year  The year.
     * @param month The month (1-12).
     * @return The number of days in the specified month.
     * @throws IllegalArgumentException if month is not valid (1-12)
     */
    public static int getDaysInMonth(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        return YearMonth.of(year, month).lengthOfMonth();
    }

    /**
     * Gets the age in years based on a birthdate.
     *
     * @param birthDate The birthdate.
     * @return The age in years.
     * @throws IllegalArgumentException if the birthDate is null
     */
    public static int calculateAge(LocalDate birthDate) {
        validateDate(birthDate);
        return (int) ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }

    /**
     * Gets the age in years based on a birthdate and reference date.
     *
     * @param birthDate     The birthdate.
     * @param referenceDate The reference date.
     * @return The age in years.
     * @throws IllegalArgumentException if either date is null
     */
    public static int calculateAge(LocalDate birthDate, LocalDate referenceDate) {
        validateDateRange(birthDate, referenceDate);
        return (int) ChronoUnit.YEARS.between(birthDate, referenceDate);
    }

    /**
     * Finds the next occurrence of a specific day of the week after a given date.
     *
     * @param date      The reference date.
     * @param dayOfWeek The day of week to find.
     * @return The date of the next occurrence of the specified day of week.
     * @throws IllegalArgumentException if the date is null or dayOfWeek is null
     */
    public static LocalDate getNextDayOfWeek(LocalDate date, DayOfWeek dayOfWeek) {
        validateDate(date);
        if (dayOfWeek == null) {
            throw new IllegalArgumentException("DayOfWeek cannot be null");
        }
        return date.with(TemporalAdjusters.next(dayOfWeek));
    }

    /**
     * Finds the previous occurrence of a specific day of the week before a given date.
     *
     * @param date      The reference date.
     * @param dayOfWeek The day of week to find.
     * @return The date of the previous occurrence of the specified day of week.
     * @throws IllegalArgumentException if the date is null or dayOfWeek is null
     */
    public static LocalDate getPreviousDayOfWeek(LocalDate date, DayOfWeek dayOfWeek) {
        validateDate(date);
        if (dayOfWeek == null) {
            throw new IllegalArgumentException("DayOfWeek cannot be null");
        }
        return date.with(TemporalAdjusters.previous(dayOfWeek));
    }

    /**
     * Checks if two date ranges overlap.
     *
     * @param start1 Start of first range.
     * @param end1   End of first range.
     * @param start2 Start of second range.
     * @param end2   End of second range.
     * @return True if the ranges overlap, false otherwise.
     * @throws IllegalArgumentException if any of the dates are null
     */
    public static boolean doDateRangesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        validateDateRange(start1, end1);
        validateDateRange(start2, end2);
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    /**
     * Creates a list of dates between start and end dates inclusive.
     *
     * @param startDate The start date.
     * @param endDate   The end date.
     * @return A list of dates between start and end inclusive.
     * @throws IllegalArgumentException if either date is null or endDate is before startDate
     */
    public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must not be before start date");
        }

        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        return dates;
    }

    /**
     * Validates that a date is not null.
     *
     * @param date The date to validate.
     * @throws IllegalArgumentException if the date is null
     */
    private static void validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
    }

    /**
     * Validates that a date/time is not null.
     *
     * @param dateTime The date/time to validate.
     * @throws IllegalArgumentException if the dateTime is null
     */
    private static void validateDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("DateTime cannot be null");
        }
    }

    /**
     * Validates that start and end dates are not null.
     *
     * @param start The start date.
     * @param end   The end date.
     * @throws IllegalArgumentException if either date is null
     */
    private static void validateDateRange(LocalDate start, LocalDate end) {
        if (start == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (end == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
    }

    /**
     * Validates that start and end date/times are not null.
     *
     * @param start The start date/time.
     * @param end   The end date/time.
     * @throws IllegalArgumentException if either date/time is null
     */
    private static void validateDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            throw new IllegalArgumentException("Start date/time cannot be null");
        }
        if (end == null) {
            throw new IllegalArgumentException("End date/time cannot be null");
        }
    }

}
