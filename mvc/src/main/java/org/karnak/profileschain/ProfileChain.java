package org.karnak.profileschain;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
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
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.MDC;
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
    private final Logger LOGGER = LoggerFactory.getLogger(ProfileChain.class);
    private final Marker CLINICAL_MARKER = MarkerFactory.getMarker("CLINICAL");
    private final String PATTERN_WITH_INOUT = "TAGHEX={} TAGINT={} DEIDENTACTION={} TAGVALUEIN={} TAGOUT={}";
    private final String PATTERN_WITH_IN = "TAGHEX={} TAGINT={} DEIDENTACTION={} TAGVALUEIN={}";
    private final String DUMMY_DEIDENT_METHOD = "dDeidentMethod";

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

    public void applyAction(DicomObject dcm, String patientID) {
        for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext(); ) {
            DicomElement dcmEl = iterator.next();
            final Action action = this.profile.getAction(dcmEl);
            if (action != null) {
                try {
                    final String tagValueIn = dcm.getString(dcmEl.tag()).orElse(null);
                    ActionStrategy.Output out = action.execute(dcm, dcmEl.tag(), patientID, null);
                    final String tagValueOut = dcm.getString(dcmEl.tag()).orElse(null);
                    if (out == ActionStrategy.Output.TO_REMOVE) {
                        iterator.remove();
                        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_IN, TagUtils.toString(dcmEl.tag()), dcmEl.tag(), action.getSymbol(), tagValueIn);
                    }else{
                        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(dcmEl.tag()), dcmEl.tag(), action.getSymbol(), tagValueIn, tagValueOut);
                    }
                } catch (final Exception e) {
                    LOGGER.error("Cannot execute the action {}", action, e);
                }
            }
            else if (dcmEl.vr() == VR.SQ) {
                dcmEl.itemStream().forEach(d -> applyAction(d, patientID));
            }
        }
    }

    public void apply(DicomObject dcm) {
        final String SOPinstanceUID = dcm.getString(Tag.SOPInstanceUID).orElse(null);
        final String IssuerOfPatientID = dcm.getString(Tag.IssuerOfPatientID).orElse(null);
        final String PatientID = dcm.getString(Tag.PatientID).orElse(null);
        MDC.put("SOPInstanceUID", SOPinstanceUID);
        MDC.put("issuerOfPatientID", IssuerOfPatientID);
        MDC.put("PatientID", PatientID);

        String pseudonym = getMainzellistePseudonym(dcm);
        String profileChainCodeName = getCodesName(this.profile, new ArrayList<String>()).toString();
        String patientID = generatePatientID(pseudonym, profileChainCodeName);

        if (!StringUtil.hasText(pseudonym)) {
            throw new IllegalStateException("Cannot build a pseudonym");
        }

        applyAction(dcm, patientID);

        setDefaultDeidentTagValue(dcm, patientID, profileChainCodeName, pseudonym);
    }

    public void setDefaultDeidentTagValue(DicomObject dcm, String patientID, String profileChainCodeName, String pseudonym){
        final String profileFilename = profileChainYml.getName();

        final String tagValueInPatientID = dcm.getString(Tag.PatientID).orElse(null);
        dcm.setString(Tag.PatientID, VR.LO,  patientID);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(Tag.PatientID), Tag.PatientID, DUMMY_DEIDENT_METHOD, tagValueInPatientID, patientID);

        final String tagValueInPatientName= dcm.getString(Tag.PatientID).orElse(null);
        dcm.setString(Tag.PatientName, VR.PN,  patientID);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(Tag.PatientName), Tag.PatientName, DUMMY_DEIDENT_METHOD, tagValueInPatientName, patientID);

        final String tagValueInPatientIdentityRemoved = dcm.getString(Tag.PatientID).orElse(null);
        dcm.setString(Tag.PatientIdentityRemoved, VR.CS,  "YES");
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(Tag.PatientIdentityRemoved), Tag.PatientIdentityRemoved, DUMMY_DEIDENT_METHOD, tagValueInPatientIdentityRemoved, "YES");

        // 0012,0063 -> module patient
        // A description or label of the mechanism or method use to remove the Patient's identity
        final String tagValueInDeidentificationMethod = dcm.getString(Tag.PatientID).orElse(null);
        dcm.setString(Tag.DeidentificationMethod, VR.LO, profileChainCodeName);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(Tag.DeidentificationMethod), Tag.DeidentificationMethod, DUMMY_DEIDENT_METHOD, tagValueInDeidentificationMethod, profileChainCodeName);

        final String tagValueInClinicalTrialSponsorName = dcm.getString(Tag.PatientID).orElse(null);
        dcm.setString(Tag.ClinicalTrialSponsorName, VR.LO, profileChainCodeName);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(Tag.ClinicalTrialSponsorName), Tag.ClinicalTrialSponsorName, DUMMY_DEIDENT_METHOD, tagValueInClinicalTrialSponsorName, profileChainCodeName);

        final String tagValueInClinicalTrialProtocolID = dcm.getString(Tag.PatientID).orElse(null);
        dcm.setString(Tag.ClinicalTrialProtocolID, VR.LO, profileFilename);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(Tag.ClinicalTrialProtocolID), Tag.ClinicalTrialProtocolID, DUMMY_DEIDENT_METHOD, tagValueInClinicalTrialProtocolID, profileFilename);

        final String tagValueInClinicalTrialSubjectID = dcm.getString(Tag.PatientID).orElse(null);
        dcm.setString(Tag.ClinicalTrialSubjectID, VR.LO, pseudonym);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(Tag.ClinicalTrialSubjectID), Tag.ClinicalTrialSubjectID, DUMMY_DEIDENT_METHOD, tagValueInClinicalTrialSubjectID, pseudonym);

        final String tagValueInClinicalTrialProtocolName = dcm.getString(Tag.PatientID).orElse(null);
        dcm.setString(Tag.ClinicalTrialProtocolName, VR.LO);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_IN, TagUtils.toString(Tag.ClinicalTrialProtocolName), Tag.ClinicalTrialProtocolName, DUMMY_DEIDENT_METHOD, tagValueInClinicalTrialProtocolName);

        final String tagValueInClinicalTrialSiteID = dcm.getString(Tag.PatientID).orElse(null);
        dcm.setString(Tag.ClinicalTrialSiteID, VR.LO);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_IN, TagUtils.toString(Tag.ClinicalTrialSiteID), Tag.ClinicalTrialSiteID, DUMMY_DEIDENT_METHOD, tagValueInClinicalTrialSiteID);

        final String tagValueInClinicalTrialSiteName = dcm.getString(Tag.PatientID).orElse(null);
        dcm.setString(Tag.ClinicalTrialSiteName, VR.LO);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_IN, TagUtils.toString(Tag.ClinicalTrialSiteName), Tag.ClinicalTrialSiteName, DUMMY_DEIDENT_METHOD, tagValueInClinicalTrialSiteName);


    }

    public String generatePatientID(String pseudonym, String profiles) {
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
