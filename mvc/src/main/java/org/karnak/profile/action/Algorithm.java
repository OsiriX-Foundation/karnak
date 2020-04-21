package org.karnak.profile.action;

import java.util.Random;

import org.dcm4che6.data.VR;


public class Algorithm {

    private String value ="";
    private String stringValue = "";
    private Random random = new Random(0);

    public Algorithm(){
    }

    public String execute(VR vr, String stringValue){
        this.stringValue = stringValue;
        if(this.stringValue!=null){
            Integer seed = this.stringValue.chars().reduce(0, (sumTotal, character) -> sumTotal + character);
            this.random = new Random(seed);

            switch (vr) {
                case LT -> LT();
                case LO-> LO();
                case SH-> SH();
                case TM -> TM();
                case DA -> DA();
                case DT -> DT();
                case PN -> PN();
                case UN -> UN();
                case UT -> UT();
                default -> notImplemented();
            }
            return this.value;
        }
        return null;
    }

    private void notImplemented(){
        this.value = null;
    }
    private void LT() {
        this.value = generateAlphanumeric(32);
    }

    private void LO(){
        this.value = generateAlphanumeric(32);
    }

    private void TM(){
        this.value = RandomDicomDateTime.randomTM(this.random);
    }
    private void DA(){
        this.value = RandomDicomDateTime.randomDA(this.random);
    }
    private void DT(){
        this.value = RandomDicomDateTime.randomDT(this.random);
    }

    private void PN(){
        this.value = "VALUE PN";
    }

    private void SH(){
        this.value = generateAlphanumeric(16);
    }

    private void UN(){
        this.value = generateAlphanumeric(16);
    }

    private void UT(){
        this.value = generateAlphanumeric(32);
    }

    private String generateAlphanumeric(int targetStringLength) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        String generatedString = this.random.ints(leftLimit, rightLimit + 1)
            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
        return generatedString;
    }
}