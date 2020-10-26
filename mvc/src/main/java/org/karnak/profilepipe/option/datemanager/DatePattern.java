package org.karnak.profilepipe.option.datemanager;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.DateTimeUtils;
import org.karnak.data.profile.Argument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DatePattern {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatePattern.class);

    public static String formatDA(String date, String option) {
        LocalDate localDate = DateTimeUtils.parseDA(date);

        switch (option) {
            case "remove_day" :
                localDate = localDate.minusDays(localDate.getDayOfMonth() - 1);
                break;
            case "remove_month_day" :
                localDate = localDate.minusDays(localDate.getDayOfMonth() - 1);
                localDate = localDate.minusMonths(localDate.getMonthValue() - 1);
        };

        String newLocalDate = localDate.format(DateTimeFormatter.ofPattern("YYYYMMdd"));
        return newLocalDate;
    }



    public static String formatDT(String dateTime, String option) {

        LocalDateTime localDateTime = LocalDateTime.from(DateTimeUtils.parseDT(dateTime));

        switch (option) {
            case "remove_day" :
                localDateTime = localDateTime.minusDays(localDateTime.getDayOfMonth() - 1);
                break;
            case "remove_month_day" :
                localDateTime = localDateTime.minusDays(localDateTime.getDayOfMonth() - 1);
                localDateTime = localDateTime.minusMonths(localDateTime.getMonthValue() - 1);
        };

        String newLocalDate = DateTimeUtils.formatDT(localDateTime);
        return newLocalDate;
    }

    public static String format(DicomObject dcm, DicomElement dcmEl, List<Argument> arguments) {
        try {
            verifyPatternArguments(arguments);
        } catch(IllegalArgumentException e) {
            throw e;
        }

        String dcmElValue = dcm.getString(dcmEl.tag()).orElse(null);
        String format ="";

        for (Argument argument: arguments) {
            final String key = argument.getKey();
            final String value = argument.getValue();

            try {
                if (key.equals("pattern")) {
                    format = value;
                }
            } catch (Exception e) {
                LOGGER.error("args {} is not correct" , value,  e);
            }
        }
        if (dcmElValue != null) {
            return switch (dcmEl.vr()) {
                case DA -> formatDA(dcmElValue, format);
                case DT -> formatDT(dcmElValue, format);
                default -> null;
            };
        } else {
            return null;
        }
    }

    public static void verifyPatternArguments(List<Argument> arguments) throws IllegalArgumentException {
        if (!arguments.stream().anyMatch(argument -> argument.getKey().equals("pattern"))) {
            List<String> args = arguments.stream()
                    .map(argument -> argument.getKey())
                    .collect(Collectors.toList());
            IllegalArgumentException missingParameters = new IllegalArgumentException(
                    "Cannot build the option DatePattern: Missing argument, the class need [pattern] as parameters. Parameters given " + args
            );
            LOGGER.error("Missing argument, the class need pattern as parameters", missingParameters);
            throw missingParameters;
        }

/*
        if (!arguments.stream().anyMatch(argument -> argument.getValue().equals("remove_day"))) {
            List<String> args = arguments.stream()
                    .map(argument -> argument.getValue())
                    .collect(Collectors.toList());
            IllegalArgumentException missingParameters = new IllegalArgumentException(
                    "Cannot build the option DatePattern: Missing argument, the class doesn't know this value: " + args
            );
            LOGGER.error("Missing argument, the class need a correct value", missingParameters);
            throw missingParameters;
        }

 */
    }
}
