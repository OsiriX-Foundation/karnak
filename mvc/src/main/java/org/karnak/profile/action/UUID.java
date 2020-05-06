package org.karnak.profile.action;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.UIDUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dcm4che6.data.DicomElement;
public class UUID implements Action {
    private final Map<String, String> UIDMap = new HashMap<>();
    private String strAction = "U";

    public String getStrAction() {
        return strAction;
    }

    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator) {

        String uidValue = dcm.getString(tag).orElse(null);

        if( UIDMap.containsKey(uidValue) ){
            String uidDeidentValue = UIDMap.get(uidValue);
            dcm.setString(tag, VR.UI, uidDeidentValue);
        }else{
            String uidDeidentValue =  UIDUtils.randomUID();
            UIDMap.put(uidValue, uidDeidentValue);
            dcm.setString(tag, VR.UI, uidDeidentValue);
        }
        
    }
}
