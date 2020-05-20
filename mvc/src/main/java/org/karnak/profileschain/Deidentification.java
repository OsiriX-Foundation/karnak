package org.karnak.profileschain;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.action.DefaultDummyValue;
import org.karnak.profileschain.profiles.ProfileChain;
import org.karnak.profileschain.utils.CreateProfile;
import org.karnak.profileschain.utils.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class Deidentification {
    private static final Logger LOGGER = LoggerFactory.getLogger(Profile.class);
    private DefaultDummyValue defaultDummyValue = new DefaultDummyValue();
    String profileYmlPath;
    DicomObject dcm;
    String pseudonym;

    public Deidentification(String profileYmlPath, DicomObject dcm, String pseudonym){
        this.profileYmlPath = profileYmlPath;
        this.dcm = dcm;
        this.pseudonym = pseudonym;
    }

    public void execute() {
        try {
            CreateProfile createProfile = new CreateProfile(this.profileYmlPath);
            ProfileChain standardProfile = createProfile.getProfile();
            for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext();) {
                final DicomElement dcmEl = iterator.next();
                final Action action = standardProfile.getAction(dcmEl);
                action.execute(dcm, dcmEl.tag(), iterator, this.pseudonym, null);

                System.out.println(dcmEl.tag()+" "+action.getStrAction());
            }
        } catch (final Exception e) {
            LOGGER.error("Cannot execute actions", e);
        }
    }
}
