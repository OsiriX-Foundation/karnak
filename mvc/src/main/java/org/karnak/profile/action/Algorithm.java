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
            String dummyValue = switch (vr) {
                case LT -> LT();
                case LO -> LO();
                case SH -> SH();
                case TM -> TM();
                case DA -> DA();
                case DT -> DT();
                case PN -> PN();
                case UN -> UN();
                case UT -> UT();
                default -> notImplemented();
            };
            return dummyValue;
        }
        return null;
    }

    private String notImplemented(){
        return null;
    }

    private String LT() {
        return RandomUtils.generateAlphanumeric(32, this.random);
    }

    private String LO(){
        return RandomUtils.generateAlphanumeric(32, this.random);
    }

    private String TM(){
        return RandomUtils.randomTM(this.random);
    }

    private String DA(){
        return RandomUtils.randomDA(this.random);
    }

    private String DT(){
        return RandomUtils.randomDT(this.random);
    }

    private String PN(){
        return RandomUtils.generateAlphanumeric(16, this.random);
    }

    private String SH(){
        return RandomUtils.generateAlphanumeric(16, this.random);
    }

    private String UN(){
        return RandomUtils.generateAlphanumeric(16, this.random);
    }

    private String UT(){
        return RandomUtils.generateAlphanumeric(32, this.random);
    }
}
