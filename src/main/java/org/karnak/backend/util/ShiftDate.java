package org.karnak.backend.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.DateTimeUtils;
import org.karnak.backend.data.entity.Argument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShiftDate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftDate.class);

    public ShiftDate() {
    }

    public static String DAbyDays(String date, int shiftDays) {
        LocalDate localDate = DateTimeUtils.parseDA(date);
        LocalDate dummyLocalDate = localDate.minusDays(shiftDays);
        return DateTimeUtils.formatDA(dummyLocalDate);
    }

    public static String TMbySeconds(String time, int shiftSeconds) {
        LocalTime localTime = DateTimeUtils.parseTM(time);
        LocalTime dummyLocalTime = localTime.minusSeconds(shiftSeconds);
        return DateTimeUtils.formatTM(dummyLocalTime);
    }

    public static String DTbyDays(String dateTime, int shiftDays, int shiftSeconds) {
        LocalDateTime localDateTime = LocalDateTime.from(DateTimeUtils.parseDT(dateTime));
        LocalDateTime dummyLocalDateTime = localDateTime.minusDays(shiftDays);
        dummyLocalDateTime = dummyLocalDateTime.minusSeconds(shiftSeconds);
        return DateTimeUtils.formatDT(dummyLocalDateTime);
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
        try {
            verifyShiftArguments(arguments);
        } catch(IllegalArgumentException e) {
            throw e;
        }

        String dcmElValue = dcm.getString(dcmEl.tag()).orElse(null);
        int shiftDays = -1;
        int shiftSeconds = -1;

        for (Argument argument: arguments) {
            final String key = argument.getKey();
            final String value = argument.getValue();

            try {
                if (key.equals("seconds")) {
                    shiftSeconds = Integer.parseInt(value);
                }
                if (key.equals("days")) {
                    shiftDays = Integer.parseInt(value);
                }
            } catch (Exception e) {
                LOGGER.error("args {} is not correct" , value,  e);
            }
        }
        if (dcmElValue != null) {
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

    public static void verifyShiftArguments(List<Argument> arguments) throws IllegalArgumentException {
        if (!arguments.stream().anyMatch(argument -> argument.getKey().equals("seconds")) ||
                !arguments.stream().anyMatch(argument -> argument.getKey().equals("days"))) {
            List<String> args = arguments.stream()
                    .map(argument -> argument.getKey())
                    .collect(Collectors.toList());
            IllegalArgumentException missingParameters = new IllegalArgumentException(
                    "Cannot build the option ShiftDate: Missing argument, the class need [seconds, days] as parameters. Parameters given " + args
            );
            LOGGER.error("Missing argument, the class need seconds and days as parameters", missingParameters);
            throw missingParameters;
        }
    }
}
