package org.karnak.profile.action;

import java.util.HashMap;
import java.util.Map;
import org.dcm4che3.data.VR;

public class Algorithm {

    private Map<VR, String> vrMap = new HashMap<>();
    
    public Algorithm(){
        this.vrMap.put(VR.LO, this.LO());
        this.vrMap.put(VR.TM, this.TM());
        this.vrMap.put(VR.PN, this.PN());
    }

    public String execute(VR vr){
        return this.vrMap.get(vr);
    }

    private String LO(){
        return "VALUE LO";
    }

    private String TM(){
        return "VALUE LO";
    }

    private String PN(){
        return "VALUE LO";
    }

}