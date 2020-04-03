package org.karnak.profile.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.dcm4che3.data.VR;

public class Algorithm {

    private Map<VR, Runnable> vrMap = new HashMap<>();
    private String value ="";

    public Algorithm(){
        this.vrMap.put(VR.LO, ()->this.LO());
        this.vrMap.put(VR.TM, ()->this.TM());
        this.vrMap.put(VR.PN, ()->this.PN());
        this.vrMap.put(VR.SH, ()->this.SH());
    }

    public String execute(VR vr){
        this.vrMap.get(vr).run();
        return this.value;
    }

    private void LO(){
        this.value = "LO"+new Random().nextInt(536871066);
    }

    private void TM(){ this.value = "VALUE TM"; }

    private void PN(){
        this.value = "VALUE PN";
    }

    private void SH(){
        this.value = "SH"+new Random().nextInt(536871066);
    }

}