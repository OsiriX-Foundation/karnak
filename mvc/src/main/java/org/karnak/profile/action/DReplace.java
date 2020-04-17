package org.karnak.profile.action;

import java.util.Optional;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import java.util.Iterator;
public class DReplace implements Action{

    private Algorithm algo = new Algorithm();

    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator) {
        Optional<DicomElement> dcmItem = dcm.get(tag);
        if(dcmItem.isPresent()) {
            DicomElement dcmEl = dcmItem.get();
            String valueString = dcm.getString(tag).orElse(null);
            System.out.println(valueString);
            int seed = 3; // ByteBuffer.wrap(value).getInt();
            String vrValue = this.algo.execute(dcmEl.vr(), seed);
            if (vrValue != "-1") {
                dcm.setString(tag, dcmEl.vr(), vrValue);
            }
        }
    }
}