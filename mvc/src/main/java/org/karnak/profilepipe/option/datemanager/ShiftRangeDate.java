package org.karnak.profilepipe.option.datemanager;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.data.AppConfig;
import org.karnak.data.profile.Argument;
import org.karnak.profilepipe.utils.HMAC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ShiftRangeDate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftRangeDate.class);

    private final ShiftDate shiftDate = new ShiftDate();
    private final HMAC hmac;

    public ShiftRangeDate() {
        this.hmac = AppConfig.getInstance().getHmac();
    }

    public ShiftRangeDate(HMAC hmac) {
        this.hmac = hmac;
    }

    public String shift(DicomObject dcm, DicomElement dcmEl, List<Argument> arguments, String PatientID) {
        try {
            verifyShiftArguments(arguments);
        } catch(IllegalArgumentException e) {
            throw e;
        }
        int shiftMaxDays = -1;
        int shiftMaxSeconds = -1;
        int shiftMinDays = 0;
        int shiftMinSeconds = 0;
        for (Argument argument: arguments) {
            final String key = argument.getKey();
            final String value = argument.getValue();

            try {
                if (key.equals("max_seconds")) {
                    shiftMaxSeconds = Integer.parseInt(value);
                }
                if (key.equals("max_days")) {
                    shiftMaxDays = Integer.parseInt(value);
                }
                if (key.equals("min_seconds")) {
                    shiftMinSeconds = Integer.parseInt(value);
                }
                if (key.equals("min_days")) {
                    shiftMinDays = Integer.parseInt(value);
                }
            } catch (Exception e) {
                LOGGER.error("args {} is not correct" , value,  e);
            }
        }
        String dcmElValue = dcm.getString(dcmEl.tag()).orElse(null);
        int shiftDays = (int) hmac.scaleHash(PatientID, shiftMinDays, shiftMaxDays);
        int shiftSeconds = (int) hmac.scaleHash(PatientID, shiftMinSeconds, shiftMaxSeconds);

        if (dcmElValue != null) {
            return switch (dcmEl.vr()) {
                case AS -> ShiftDate.ASbyDays(dcmElValue, shiftDays);
                case DA -> ShiftDate.DAbyDays(dcmElValue, shiftDays);
                case DT -> ShiftDate.DTbyDays(dcmElValue, shiftDays, shiftSeconds);
                case TM -> ShiftDate.TMbySeconds(dcmElValue, shiftSeconds);
                default -> null;
            };
        }

        return null;
    }

    public static void verifyShiftArguments(List<Argument> arguments) throws IllegalArgumentException {
        if (!arguments.stream().anyMatch(argument -> argument.getKey().equals("max_seconds")) ||
                !arguments.stream().anyMatch(argument -> argument.getKey().equals("max_days"))) {
            List<String> args = arguments.stream()
                    .map(argument -> argument.getKey())
                    .collect(Collectors.toList());
            String text = "Cannot build the option ShiftRangeDate: Missing argument, the class minimum need [max_seconds, max_days] as parameters. Parameters given " + args;

            IllegalArgumentException missingParameters = new IllegalArgumentException(text);
            LOGGER.error(text, missingParameters);
            throw missingParameters;
        }
    }
}
