package org.karnak.profile.action;

import java.util.Optional;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import java.util.Iterator;
import org.dcm4che6.data.Tag;
public class DReplace implements Action{

    private Algorithm algo = new Algorithm();

    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator) {
        Optional<DicomElement> dcmItem = dcm.get(tag);
        if(dcmItem.isPresent()) {
            DicomElement dcmEl = dcmItem.get();
            String stringValue = dcm.getString(tag).orElse(null);
            System.out.println(stringValue);
            String vrValue = this.algo.execute(dcmEl.vr(), stringValue);

            if (vrValue != null) {
                dcm.setString(tag, dcmEl.vr(), vrValue);
            }else{
                dcm.setNull(tag, dcmEl.vr());
            }
        }
    }
}