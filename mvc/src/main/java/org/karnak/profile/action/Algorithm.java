package org.karnak.profile.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.dcm4che6.data.VR;


public class Algorithm {

    private String value ="";
    private int seed = 0;

    public Algorithm(){
    }

    public String execute(VR vr, int seed){
        this.seed = seed;
        switch (vr) {
            case LO-> LO();
            case SH-> SH();
            case TM -> TM();
            case PN -> PN();
            default -> notImplemented();
        }
        return this.value;
    }

    private void notImplemented(){
        this.value = "-1";
    }

    private void LO(){
        Random random = new Random(this.seed);
        this.value = generateAlphanumeric(64, random);
    }

    private void TM(){ this.value = "000000"; }

    private void PN(){
        this.value = "VALUE PN";
    }

    private void SH(){
        Random random = new Random(this.seed);
        this.value = generateAlphanumeric(16, random);
    }

    private String generateAlphanumeric(int targetStringLength, Random random) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        String generatedString = random.ints(leftLimit, rightLimit + 1)
            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
        return generatedString;
    }
}