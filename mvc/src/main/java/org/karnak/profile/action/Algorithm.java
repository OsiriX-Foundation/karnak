package org.karnak.profile.action;

import java.util.HashMap;
import java.util.Map;
import org.dcm4che3.data.VR;

public class Algorithm {

    private Map<VR, Runnable> vrMap = new HashMap<>();
    
    public Algorithm(){
        this.vrMap.put(VR.LO, () -> this.LO());
        this.vrMap.put(VR.TM, () -> this.TM());
        this.vrMap.put(VR.PN, () -> this.PN());
    }

    public void execute(VR vr){
        this.vrMap.get(vr).run();
    }

    public void LO(){
        System.out.println("LO");
    }

    public void TM(){
        System.out.println("TM");
    }

    public void PN(){
        System.out.println("PN");
    }

}