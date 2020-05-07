package org.karnak.profile.option.datemanager;

import org.apache.commons.lang3.StringUtils;
import org.karnak.profile.HMAC;
import org.karnak.profile.option.datemanager.RandomUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Random;

import static io.swagger.codegen.v3.config.CodegenConfigurator.LOGGER;

public class ShiftDate {
    private Random random;
    private DateTimeFormatter DAformater = DateTimeFormatter.ofPattern("yyyyMMdd");
    private DateTimeFormatter TMformater = DateTimeFormatter.ofPattern("HHmmss");
    public ShiftDate(String value) {
        this.random = generateRandomShift(value);
    }

    private Random generateRandomShift(String value) {
        HMAC hmac = new HMAC();
        long seed = hmac.longHash(value);
        return new Random(seed);
    }

    private String addMissingMilliSeconds(String time) {
        String[] timeSplit = time.split("\\.");
        if (timeSplit.length > 1) {
            int n = 6-timeSplit[1].length();
            String missingMilliSeconds = timeSplit[1] + StringUtils.repeat('0', n);
            return timeSplit[0].concat(".").concat(missingMilliSeconds);
        }
        return time;
    }

    private LocalTime parseTime(String time) {
        String cleanTime = addMissingMilliSeconds(time);
        DateTimeFormatter hourFormat = new DateTimeFormatterBuilder()
                .appendPattern("HH")
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                .toFormatter();
        DateTimeFormatter hourMinuteFormat = new DateTimeFormatterBuilder()
                .appendPattern("HHmm")
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE,0)
                .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                .toFormatter();
        DateTimeFormatter hourMinuteSecondFormat = new DateTimeFormatterBuilder()
                .appendPattern("HHmmss")
                .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                .toFormatter();
        DateTimeFormatter hourMinuteSecondFractionFormat = new DateTimeFormatterBuilder()
                .appendPattern("HHmmss.SSSSSS")
                .toFormatter();

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(hourMinuteSecondFractionFormat)
                .appendOptional(hourMinuteSecondFormat)
                .appendOptional(hourMinuteFormat)
                .appendOptional(hourFormat)
                .toFormatter();
        try {
            LocalTime timeParse = LocalTime.parse(cleanTime, formatter);
            return timeParse;
        } catch (DateTimeParseException e) {
            // log the time given ?
            LOGGER.error("Unable to parse the time given" , e);
            return null;
        }
    }

    private LocalDate parseDate(String date) {
        DateTimeFormatter yearFormat = new DateTimeFormatterBuilder()
                .appendPattern("yyyy")
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .toFormatter();
        DateTimeFormatter yearMonthFormat = new DateTimeFormatterBuilder()
                .appendPattern("yyyyMM")
                .parseDefaulting(ChronoField.DAY_OF_MONTH,1)
                .toFormatter();

        DateTimeFormatter yearMonthDayFormat = new DateTimeFormatterBuilder()
                .appendPattern("yyyyMMdd")
                .toFormatter();

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(yearMonthDayFormat)
                .appendOptional(yearMonthFormat)
                .appendOptional(yearFormat)
                .toFormatter();
        try {
            LocalDate dateParse = LocalDate.parse(date, formatter);
            return dateParse;
        } catch (DateTimeParseException e) {
            // log the date given ?
            LOGGER.error("Unable to parse the date given" , e);
            throw e;
        }
    }

    private String dateToString(LocalDate date) {
        String formattedDate = this.DAformater.format(date);
        return formattedDate;
    }

    private String timeToString(LocalTime time) {
        String formattedTime = this.TMformater.format(time);
        return formattedTime;
    }

    public String DAshiftByRandomDays(String date, int maxDays) {
        int shiftDays = RandomUtils.createRandomIntBetween(0, maxDays, this.random);
        LocalDate localDate = parseDate(date);
        LocalDate dummyLocalDate = localDate.minusDays(shiftDays);
        String dummyDate = dateToString(dummyLocalDate);
        return dummyDate;
    }

    public String TMshiftByRandomSeconds(String time, int maxSeconds) {
        int shiftSeconds = RandomUtils.createRandomIntBetween(0, maxSeconds, this.random);
        LocalTime localTime = parseTime(time);
        LocalTime dummyLocalTime = localTime.minusSeconds(shiftSeconds);
        String dummyTime = timeToString(dummyLocalTime);
        return dummyTime;
    }

    public String DAshiftByDays(String date, int shiftDays) {
        LocalDate localDate = parseDate(date);
        LocalDate dummyLocalDate = localDate.minusDays(shiftDays);
        String dummyDate = dateToString(dummyLocalDate);
        return dummyDate;
    }

    public String TMshiftBySeconds(String time, int shiftSeconds) {
        LocalTime localTime = parseTime(time);
        LocalTime dummyLocalTime = localTime.minusSeconds(shiftSeconds);
        String dummyTime = timeToString(dummyLocalTime);
        return dummyTime;
    }
}
