package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.karnak.data.AppConfig;

public class UID extends AbstractAction {

    public UID(String symbol) {
        super(symbol);
    }

    @Override
    public void execute(DicomObject dcm, int tag, String pseudo, String dummy) {
        String uidValue = dcm.getString(tag).orElse(null);
        String uidHashed = AppConfig.getInstance().getHmac().uidHash(pseudo, uidValue);
        System.out.println(uidValue + " - " + uidHashed);
        dcm.setString(tag, VR.UI, uidHashed);
    }
}
