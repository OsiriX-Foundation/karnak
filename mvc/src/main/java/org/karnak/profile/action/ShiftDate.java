package org.karnak.profile.action;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

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

    private String dateToString(LocalDate date) {
        String formattedDate = this.DAformater.format(date);
        return formattedDate;
    }

    private String timeToString(LocalTime time) {
        String formattedDate = this.TMformater.format(time);
        return formattedDate;
    }

    public String DAshiftByRandomDays(String date, Integer maxDays) {
        int shiftDays = RandomUtils.createRandomIntBetween(0, maxDays, this.random);
        LocalDate localDate = LocalDate.parse(date, this.DAformater);
        LocalDate dummyLocalDate = localDate.minusDays(shiftDays);
        String dummyDate = dateToString(dummyLocalDate);
        return dummyDate;
    }

    public String TMshiftByRandomSeconds(String date, Integer maxSeconds) {
        int shiftSeconds = RandomUtils.createRandomIntBetween(0, maxSeconds, this.random);
        LocalTime localTime = LocalTime.parse(date, this.TMformater);
        LocalTime dummyLocalTime = localTime.minusSeconds(shiftSeconds);
        String dummyTime = timeToString(dummyLocalTime);
        return dummyTime;
    }
}
