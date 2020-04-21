package org.karnak.profile.action;

import java.util.Random;

import org.dcm4che6.data.VR;
import org.dcm4che6.util.UIDUtils;


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
                case AE -> AE();
                case AS -> AS();
                case CS -> CS();
                case DA -> DA();
                case DS -> DS();
                case DT -> DT();
                case FL -> FL();
                case FD -> FD();
                case IS -> IS();
                case LO -> LO();
                case LT -> LT();
                case PN -> PN();
                case SH -> SH();
                case SL -> SL();
                case SS -> SS();
                case ST -> ST();
                case TM -> TM();
                case UI -> UI();
                case UL -> UL();
                case UN -> UN();
                case US -> US();
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

    private String AE() {
        return RandomUtils.generateAlphanumeric(32, this.random);
    }

    private String AS() {
        return RandomUtils.randomAS(this.random);
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

    private String US(){
        int max = (int)Math.pow(2, 16)-1;
        return RandomUtils.generateNumeric(0, max, this.random);
    }

    private String UL(){
        int max = (int)Math.pow(2, 32)-1;
        return RandomUtils.generateNumeric(0, max, this.random);
    }

    private String SS() {
        int min = (int)Math.pow(-2, 15);
        int max = (int)Math.pow(2, 15)-1;
        return RandomUtils.generateNumeric(min, max, this.random);
    }

    private String SL() {
        int min = (int)Math.pow(-2, 31);
        int max = (int)Math.pow(2, 31)-1;
        return RandomUtils.generateNumeric(min, max, this.random);
    }

    private String ST() {
        return RandomUtils.generateAlphanumeric(16, this.random);
    }

    private String IS() {
        int min = (int)Math.pow(-2, 31);
        int max = (int)Math.pow(2, 31)-1;
        return RandomUtils.generateNumeric(min, max, this.random);
    }

    private String FD() {
        int max = (int)Math.pow(2, 32)-1;
        return RandomUtils.generateNumeric(0, max, this.random);
    }

    private String FL() {
        int max = (int)Math.pow(2, 32)-1;
        return RandomUtils.generateNumeric(0, max, this.random);
    }

    private String DS() {
        int max = (int)Math.pow(2, 16)-1;
        return RandomUtils.generateNumeric(0, max, this.random);
    }

    private String UI() {
        return UIDUtils.randomUID();
    }

    private String CS() {
        return RandomUtils.generateUppercase(16, this.random);
    }
}
