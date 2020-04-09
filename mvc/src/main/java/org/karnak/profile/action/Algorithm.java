package org.karnak.profile.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.dcm4che6.data.VR;


public class Algorithm {

    private Map<VR, Runnable> vrMap = new HashMap<>();
    private String value ="";
    private int seed = 0;

    public Algorithm(){
        this.vrMap.put(VR.LO, ()->this.LO());
        this.vrMap.put(VR.TM, ()->this.TM());
        this.vrMap.put(VR.PN, ()->this.PN());
        this.vrMap.put(VR.SH, ()->this.SH());
    }

    public String execute(VR vr, int seed){
        if(this.vrMap.containsKey(vr)){
            this.seed = seed;
            this.vrMap.get(vr).run();
        }else{
            this.value = "-1";
        }
        return this.value;
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