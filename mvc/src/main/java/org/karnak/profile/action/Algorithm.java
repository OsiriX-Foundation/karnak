package org.karnak.profile.action;

import java.util.Random;

import org.dcm4che6.data.VR;


public class Algorithm {

    private String value ="";
    private String stringValue = "";

    public Algorithm(){
    }

    public String execute(VR vr, String stringValue){
        this.stringValue = stringValue;
        
        switch (vr) {
            case LO-> LO();
            case SH-> SH();
            case TM -> TM();
            case DA -> DA();
            case DT -> DT();
            case PN -> PN();
            default -> notImplemented();
        }
        return this.value;
    }

    private void notImplemented(){
        this.value = "-1";
    }

    private void LO(){
        Integer seed = this.stringValue.chars().reduce(0, (sumTotal, character) -> sumTotal + character);
        Random random = new Random(seed);
        this.value = generateAlphanumeric(64, random);
    }

    private void TM(){
        Integer seed = this.stringValue.chars().reduce(0, (sumTotal, character) -> sumTotal + character);
        Random random = new Random(seed);
        this.value = RandomDicomDateTime.randomTM(random);
    }
    private void DA(){
        Integer seed = this.stringValue.chars().reduce(0, (sumTotal, character) -> sumTotal + character);
        Random random = new Random(seed);
        this.value = RandomDicomDateTime.randomDA(random);
    }
    private void DT(){
        Integer seed = this.stringValue.chars().reduce(0, (sumTotal, character) -> sumTotal + character);
        Random random = new Random(seed);
        this.value = RandomDicomDateTime.randomDT(random);
    }

    private void PN(){
        this.value = "VALUE PN";
    }

    private void SH(){
        Integer seed = this.stringValue.chars().reduce(0, (sumTotal, character) -> sumTotal + character);
        Random random = new Random(seed);
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