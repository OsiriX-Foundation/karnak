package org.karnak.profile.action;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

public class ShiftDate {
    private Random random;
    public ShiftDate(String StudyInstanceUID) {
        this.random = generateRandomShift(StudyInstanceUID);
    }

    private Random generateRandomShift(String StudyInstanceUID) {
        HMAC hmac = new HMAC();
        long seed = hmac.longHash(StudyInstanceUID);
        return new Random(seed);
    }

    private String getSubString(String value, Integer indexMin, Integer indexMax) {
        if (value.length() >= indexMax && indexMin >= 0) {
            return value.substring(indexMin, indexMax);
        }
        return null;
    }

    private ArrayList<Integer> splitStringDate(String date) {
        ArrayList<Integer> splitedDate = new ArrayList<>();
        splitedDate.add(Integer.parseInt(getSubString(date, 0, 4)));
        splitedDate.add(Integer.parseInt(getSubString(date, 4, 6)));
        splitedDate.add(Integer.parseInt(getSubString(date, 6, 8)));
        return splitedDate;
    }

    private static String dateToString(LocalDate date) {
        DateTimeFormatter formatDA = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = formatDA.format(date);
        return formattedDate;
    }

    /*
    * Si le jour est 1 et le shift est de 4
    * 1-4 = -3 -> mois précédant moins 2 jours
    * */
    public void shiftByDay(String date, Integer day) {
        Integer shiftDay = RandomUtils.createRandomIntBetween(0, day, this.random);
        ArrayList<Integer> splitedDate = splitStringDate(date);
        splitedDate.set(2, splitedDate.get(2) - shiftDay);
        LocalDate localDate = LocalDate.of(splitedDate.get(0), splitedDate.get(1), splitedDate.get(2));
        String dummyDate = dateToString(localDate);
        System.out.println(date);
        System.out.println(dummyDate);
    }

    public void shiftByMonthDay(String date, Integer month, Integer day) {
        Integer shiftDay = RandomUtils.createRandomIntBetween(0, day, this.random);
        Integer shiftMonth = RandomUtils.createRandomIntBetween(0, month, this.random);
    }

    public void shiftByYearMonthDay(String date, Integer year, Integer month, Integer day) {
        Integer shiftDay = RandomUtils.createRandomIntBetween(0, day, this.random);
        Integer shiftMonth = RandomUtils.createRandomIntBetween(0, month, this.random);
        Integer shiftYear = RandomUtils.createRandomIntBetween(0, year, this.random);
    }
}
