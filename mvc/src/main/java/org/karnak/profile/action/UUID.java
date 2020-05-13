package org.karnak.profile.action;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.UIDUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dcm4che6.data.DicomElement;
import org.karnak.data.AppConfig;
import org.karnak.data.profile.ProfilePersistence;
import org.karnak.profile.HMAC;

public class UUID implements Action {
    private String strAction = "U";
    private HMAC hmac;{
        hmac = AppConfig.getInstance().getHmac();
    }

    public String getStrAction() {
        return strAction;
    }

    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, String pseudonym, String dummyValue) {
        String uidValue = dcm.getString(tag).orElse(null);
        String uidHashed = hmac.uidHash(pseudonym, uidValue);
        System.out.println(uidValue + " - " + uidHashed);
        dcm.setString(tag, VR.UI, uidHashed);
    }
}
