package org.karnak.profile.action;

import java.util.Optional;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import java.util.Iterator;
import org.dcm4che6.util.TagUtils;

public class DReplace extends Action{

    private DefaultDummyValue algo;
    private String strAction = "D";

    public DReplace() {
        this.algo = new DefaultDummyValue();
    }

    public DReplace(DefaultDummyValue algo) {
        this.algo = algo;
    }

    public String getStrAction() {
        return strAction;
    }

    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator) {
        Optional<DicomElement> dcmItem = dcm.get(tag);
        int tagStudyInstanceUID = TagUtils.intFromHexString("0020000D");
        if(dcmItem.isPresent()) {
            DicomElement dcmEl = dcmItem.get();
            String stringValue = dcm.getString(tag).orElse(null);
            String studyInstanceUID = dcm.getString(tagStudyInstanceUID).orElse(null);
            String vrValue = this.algo.execute(dcmEl.vr(), stringValue, studyInstanceUID);

            if (vrValue != null) {
                dcm.setString(tag, dcmEl.vr(), vrValue);
            }else{
                dcm.setNull(tag, dcmEl.vr());
            }
        }
    }
}