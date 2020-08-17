package org.karnak.profilepipe.option.datemanager;

import org.apache.commons.lang3.StringUtils;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.data.profile.Argument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.Collectors;

public class ShiftDate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftDate.class);
    private static DateTimeFormatter DAformater = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static DateTimeFormatter TMformater = DateTimeFormatter.ofPattern("HHmmss.SSSSSS");
    private static DateTimeFormatter DTformatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSSSSS");

    public ShiftDate() {
    }

    private static String addMissingMilliSeconds(String time) {
        String[] timeSplit = time.split("\\.");
        if (timeSplit.length > 1) {
            int n = 6-timeSplit[1].length();
            String missingMilliSeconds = timeSplit[1] + StringUtils.repeat('0', n);
            return timeSplit[0].concat(".").concat(missingMilliSeconds);
        }
        return time;
    }

    private static LocalTime parseTime(String time) {
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
            LOGGER.error("Format of chosen time (should be [HH-HHmmss.SSSSSS]) is invalid: " + time, e);
            throw e;
        }
    }

    private static LocalDate parseDate(String date) {
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
            LOGGER.error("Format of chosen time (should be [yyyy-yyyyMMdd]) is invalid: " + date, e);
            throw e;
        }
    }

    private static LocalDateTime parseDateTime(String dateTime) {
        String cleanDateTime = addMissingMilliSeconds(dateTime);
        DateTimeFormatter yearFormat = new DateTimeFormatterBuilder()
                .appendPattern("yyyy")
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                .toFormatter();
        DateTimeFormatter yearMonthFormat = new DateTimeFormatterBuilder()
                .appendPattern("yyyyMM")
                .parseDefaulting(ChronoField.DAY_OF_MONTH,1)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                .toFormatter();
        DateTimeFormatter yearMonthDayFormat = new DateTimeFormatterBuilder()
                .appendPattern("yyyyMMdd")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                .toFormatter();
        DateTimeFormatter hourFormat = new DateTimeFormatterBuilder()
                .appendPattern("yyyyMMddHH")
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                .toFormatter();
        DateTimeFormatter hourMinuteFormat = new DateTimeFormatterBuilder()
                .appendPattern("yyyyMMddHHmm")
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE,0)
                .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                .toFormatter();
        DateTimeFormatter hourMinuteSecondFormat = new DateTimeFormatterBuilder()
                .appendPattern("yyyyMMddHHmmss")
                .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                .toFormatter();
        DateTimeFormatter hourMinuteSecondFractionFormat = new DateTimeFormatterBuilder()
                .appendPattern("yyyyMMddHHmmss.SSSSSS")
                .toFormatter();

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(hourMinuteSecondFractionFormat)
                .appendOptional(hourMinuteSecondFormat)
                .appendOptional(hourMinuteFormat)
                .appendOptional(hourFormat)
                .appendOptional(yearMonthDayFormat)
                .appendOptional(yearMonthFormat)
                .appendOptional(yearFormat)
                .toFormatter();
        try {
            LocalDateTime dateParse = LocalDateTime.parse(cleanDateTime, formatter);
            return dateParse;
        } catch (DateTimeParseException e) {
            LOGGER.error("Format of chosen time (should be [yyyy-yyyyMMddHHmmss.SSSSSS]) is invalid: " + dateTime, e);
            throw e;
        }
    }

    private static String dateToString(LocalDate date) {
        String formattedDate = DAformater.format(date);
        return formattedDate;
    }

    private static String timeToString(LocalTime time) {
        String formattedTime = TMformater.format(time);
        return formattedTime;
    }

    private static String dateTimeToString(LocalDateTime dateTime) {
        String formattedDateTime = DTformatter.format(dateTime);
        return formattedDateTime;
    }

    public static String DAbyDays(String date, int shiftDays) {
        LocalDate localDate = parseDate(date);
        LocalDate dummyLocalDate = localDate.minusDays(shiftDays);
        return dateToString(dummyLocalDate);
    }

    public static String TMbySeconds(String time, int shiftSeconds) {
        LocalTime localTime = parseTime(time);
        LocalTime dummyLocalTime = localTime.minusSeconds(shiftSeconds);
        return timeToString(dummyLocalTime);
    }

    public static String DTbyDays(String dateTime, int shiftDays, int shiftSeconds) {
        if (dateTime.length() > 8) {
            LocalDateTime localDateTime = parseDateTime(dateTime);
            LocalDateTime dummyLocalDateTime = localDateTime.minusDays(shiftDays);
            dummyLocalDateTime = dummyLocalDateTime.minusSeconds(shiftSeconds);
            return dateTimeToString(dummyLocalDateTime);
        }
        return DAbyDays(dateTime, shiftDays);
    }

    private static String addMissingZero(String age, int nMissingValue) {
        int n = nMissingValue-age.length();
        String missingZero = StringUtils.repeat('0', n) + age;
        return missingZero;
    }

    public static String ASbyDays(String age, int shiftDays) {
        String valueAge = age.substring(0, 3);
        int intAge = Integer.parseInt(valueAge);

        int maxSubstring = age.length();
        String formatAge = age.substring(3, maxSubstring);

        int intDummyAge = switch (formatAge) {
            case "Y" -> intAge + shiftDays/365;
            case "M" -> intAge + shiftDays/30;
            case "W" -> intAge + shiftDays/7;
            default -> intAge + shiftDays;
        };

        return addMissingZero(String.valueOf(intDummyAge), 3) + formatAge;
    }

    public static String shift(DicomObject dcm, DicomElement dcmEl, List<Argument> arguments) {
        String dcmElValue = dcm.getString(dcmEl.tag()).orElse(null);
        int shiftDays = -1;
        int shiftSeconds = -1;

        try {
            verifyShiftArguments(arguments);
        } catch(IllegalArgumentException e) {
            throw e;
        }

        for (Argument argument: arguments) {
            final String key = argument.getKey();
            final String value = argument.getValue();

            try {
                if (key == "seconds") {
                    shiftSeconds = Integer.parseInt(value);
                }
                if (key == "days") {
                    shiftDays = Integer.parseInt(value);
                }
            } catch (Exception e) {
                LOGGER.error("args {} is not correct" , value,  e);
            }
        }
        if(dcmElValue != null){
            return switch (dcmEl.vr()) {
                case AS -> ASbyDays(dcmElValue, shiftDays);
                case DA -> DAbyDays(dcmElValue, shiftDays);
                case DT -> DTbyDays(dcmElValue, shiftDays, shiftSeconds);
                case TM -> TMbySeconds(dcmElValue, shiftSeconds);
                default -> null;
            };
        } else {
            return null;
        }
    }

    private static void verifyShiftArguments(List<Argument> arguments) throws IllegalArgumentException {
        if (!arguments.stream().anyMatch(argument -> argument.getKey().equals("seconds")) ||
                !arguments.stream().anyMatch(argument -> argument.getKey().equals("days"))) {
            List<String> args = arguments.stream()
                    .map(argument -> argument.getKey())
                    .collect(Collectors.toList());
            IllegalArgumentException missingParameters = new IllegalArgumentException(
                    "Missing argument, the class need [seconds, days] as parameters. Parameters given " + args
            );
            LOGGER.error("Missing argument, the class need seconds and days as parameters", missingParameters);
            throw missingParameters;
        }
    }
}
