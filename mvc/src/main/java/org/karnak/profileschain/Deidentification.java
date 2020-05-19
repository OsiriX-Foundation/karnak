package org.karnak.profileschain;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.action.DReplace;
import org.karnak.profileschain.option.dummyvalue.DefaultDummyValue;
import org.karnak.profileschain.utils.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Optional;

public class Deidentification {
    private static final Logger LOGGER = LoggerFactory.getLogger(Profile.class);
    private DefaultDummyValue defaultDummyValue = new DefaultDummyValue();
    Profile profile;
    DicomObject dcm;
    String pseudonym;

    public Deidentification(Profile profile, DicomObject dcm, String pseudonym){
        this.profile = profile;
        this.dcm = dcm;
        this.pseudonym = pseudonym;
    }

    private String setDummyValue(DicomObject dcm, int tag, String patientID) {
        Optional<DicomElement> dcmItem = dcm.get(tag);
        if(dcmItem.isPresent()) {
            DicomElement dcmEl = dcmItem.get();
            return this.defaultDummyValue.execute(dcmEl.vr(), dcm, tag, patientID);
        }
        return null;
    }

    public void execute() {
        try {
            for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext();) {
                final DicomElement dcmEl = iterator.next();
                final Action action = this.profile.getActionMap().get(dcmEl.tag());
                String value = null;

                if (action != null) { // if action != keep
                    if (action instanceof DReplace) {
                        value = setDummyValue(this.dcm, dcmEl.tag(), this.pseudonym);
                    }
                    action.execute(dcm, dcmEl.tag(), iterator, this.pseudonym, value);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Cannot execute actions", e);
        }
    }
}
