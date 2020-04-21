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
        this.value = RandomUtils.generateAlphanumeric(32, this.random);
    }

    private void LO(){
        this.value = RandomUtils.generateAlphanumeric(32, this.random);
    }

    private void TM(){
        this.value = RandomUtils.randomTM(this.random);
    }
    private void DA(){
        this.value = RandomUtils.randomDA(this.random);
    }
    private void DT(){
        this.value = RandomUtils.randomDT(this.random);
    }

    private void PN(){
        this.value = "VALUE PN";
    }

    private void SH(){
        this.value = RandomUtils.generateAlphanumeric(16, this.random);
    }

    private void UN(){
        this.value = RandomUtils.generateAlphanumeric(16, this.random);
    }

    private void UT(){
        this.value = RandomUtils.generateAlphanumeric(32, this.random);
    }

}