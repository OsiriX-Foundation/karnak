package org.karnak.profileschain;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.karnak.api.PseudonymApi;
import org.karnak.api.rqbody.Fields;
import org.karnak.data.AppConfig;
import org.karnak.data.profile.Policy;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.action.ActionStrategy;
import org.karnak.profileschain.profilebody.ProfileBody;
import org.karnak.profileschain.profilebody.ProfileChainBody;
import org.karnak.profileschain.profiles.AbstractProfileItem;
import org.karnak.profileschain.profiles.ProfileItem;
import org.karnak.profileschain.utils.HMAC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.core.util.StringUtil;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileChain.class);

    private final URL profileURL;
    private final ProfileChainBody profileChainYml;
    private final ProfileItem profile;
    private final HMAC hmac;

    public ProfileChain(URL profileURL) {
        this.hmac = AppConfig.getInstance().getHmac();
        this.profileURL = profileURL;
        this.profileChainYml = init(profileURL);
        this.profile = createProfileChain();
    }

    private ProfileChainBody init(URL profileURL) {
        try (InputStream inputStream = profileURL.openStream()) {
            final Yaml yaml = new Yaml(new Constructor(ProfileChainBody.class));
            return yaml.load(inputStream);
        } catch (final Exception e) {
            LOGGER.error("Cannot load yaml {}", profileURL, e);
        }
        return null;
    }

    public ProfileItem createProfileChain() {
        if (profileChainYml != null) {
            final List<ProfileBody> profilesYml = profileChainYml.getProfiles();
            ProfileItem parent = null;
            for (ProfileBody profileYml : profilesYml) {
                AbstractProfileItem.Type t = AbstractProfileItem.Type.getType(profileYml.getCodename());
                if (t == null) {
                    LOGGER.error("Cannot find the profile codename: {}", profileYml.getCodename());
                } else {
                    Object instanceProfileItem;
                    try {
                        Policy type = Policy.valueOf(profileYml.getPolicy());
                        instanceProfileItem = t.getProfileClass().getConstructor(String.class, String.class, Policy.class, ProfileItem.class).newInstance(profileYml.getName(), profileYml.getCodename(), type, parent);
                        parent = (ProfileItem) instanceProfileItem;
                    } catch (Exception e) {
                        LOGGER.error("Cannot build the profile: {}", t.getProfileClass().getName());
                    }
                }
            }
            return parent;
        }
        return null;
    }

    public URL getProfileURL() {
        return profileURL;
    }

    public String getMainzellistePseudonym(DicomObject dcm) {
        final String patientID = dcm.getString(Tag.PatientID).orElse(null);
        final String patientName = dcm.getString(Tag.PatientName).orElse(null);
        final String patientBirthDate = dcm.getString(Tag.PatientBirthDate).orElse(null);
        final String patientSex = dcm.getString(Tag.PatientSex).orElse(null);
        // Issuer of patientID is recommended to make the patientID universally unique. Can be defined in profile if missing.
        final String issuerOfPatientID = dcm.getString(Tag.IssuerOfPatientID).orElse(profileChainYml.getDefaultIssuerOfPatientID());

        PseudonymApi pseudonymApi = new PseudonymApi();
        final Fields newPatientFields = new Fields(patientID, patientName, patientBirthDate, patientSex, issuerOfPatientID);
        final String pseudonym = pseudonymApi.createPatient(newPatientFields);
        return pseudonym;
    }

    public void getSequence(DicomElement dcmEl, String patientName, ActionStrategy.Output output) {
        final VR vr = dcmEl.vr();
        if (vr == VR.SQ && output != ActionStrategy.Output.TO_REMOVE) {
            List<DicomObject> ldcm = dcmEl.itemStream().collect(Collectors.toList());
            for (DicomObject dcm : ldcm) {
                applyAction(dcm, patientName);
            }
        }
    }

    public void applyAction(DicomObject dcm, String patientName) {
        for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext(); ) {
            DicomElement dcmEl = iterator.next();
            final Action action = this.profile.getAction(dcmEl);
            if (action != null) {
                try {
                    ActionStrategy.Output out = action.execute(dcm, dcmEl.tag(), patientName, null);
                    getSequence(dcmEl, patientName, out);
                    if (out == ActionStrategy.Output.TO_REMOVE) {
                        iterator.remove();
                    }
                } catch (final Exception e) {
                    LOGGER.error("Cannot execute the action {}", action, e);
                }
            }
        }
    }

    public void apply(DicomObject dcm) {
        String pseudonym = getMainzellistePseudonym(dcm);
        String profileChainCodeName = getCodesName(this.profile, new ArrayList<String>()).toString();
        String patientName = generatePatientName(pseudonym, profileChainCodeName);

        if (!StringUtil.hasText(pseudonym)) {
            throw new IllegalStateException("Cannot build a pseudonym");
        }

        applyAction(dcm, patientName);

        String profileFilename = profileChainYml.getName();
        dcm.setString(Tag.PatientID, VR.LO, patientName);
        dcm.setString(Tag.PatientName, VR.PN, patientName);
        dcm.setString(Tag.PatientIdentityRemoved, VR.CS, "YES");
        // 0012,0063 -> module patient
        // A description or label of the mechanism or method use to remove the Patient's identity
        dcm.setString(Tag.DeidentificationMethod, VR.LO, profileChainCodeName);
        dcm.setString(Tag.ClinicalTrialSponsorName, VR.LO, profileChainCodeName);
        dcm.setString(Tag.ClinicalTrialProtocolID, VR.LO, profileFilename);
        dcm.setString(Tag.ClinicalTrialSubjectID, VR.LO, pseudonym);
        dcm.setString(Tag.ClinicalTrialProtocolName, VR.LO);
        dcm.setString(Tag.ClinicalTrialSiteID, VR.LO);
        dcm.setString(Tag.ClinicalTrialSiteName, VR.LO);
    }

    public String generatePatientName(String pseudonym, String profiles) {
        byte[] bytes = new byte[16];
        System.arraycopy(hmac.byteHash(pseudonym + profiles), 0, bytes, 0, 16);
        return new BigInteger(1, bytes).toString();
    }

    public ArrayList<String> getCodesName(ProfileItem profile, ArrayList<String> output) {
        output.add(profile.getCodeName());
        if (profile.getProfileParent() != null) {
            return getCodesName(profile.getProfileParent(), output);
        }
        return output;
    }
}
