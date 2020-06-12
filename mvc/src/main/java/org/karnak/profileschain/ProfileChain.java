package org.karnak.profileschain;

import org.apache.logging.log4j.*;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.karnak.api.PseudonymApi;
import org.karnak.api.rqbody.Fields;
import org.karnak.data.AppConfig;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.action.ActionStrategy;
import org.karnak.profileschain.profilebody.ProfileBody;
import org.karnak.profileschain.profilebody.ProfileChainBody;
import org.karnak.profileschain.profiles.AbstractProfileItem;
import org.karnak.profileschain.profiles.ProfileItem;
import org.karnak.profileschain.utils.HMAC;

import org.weasis.core.util.StringUtil;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ProfileChain {
    private Logger LOGGER = LogManager.getLogger(ProfileChain.class.getName());
    private static final Level CLINICAL_LEVEL = Level.forName("CLINICAL", 35);

    private final URL profileURL;
    private final ProfileChainBody profileChainYml;
    private ProfileItem profile;
    private HMAC hmac;{
        hmac = AppConfig.getInstance().getHmac();
    }

    public ProfileChain(URL profileURL) {
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
                        instanceProfileItem = t.getProfileClass().getConstructor(String.class, String.class, ProfileItem.class).newInstance(profileYml.getName(), profileYml.getCodename(), parent);
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

    public void getSequence(DicomElement dcmEl, String patientName, ActionStrategy.Output output, Marker SOPinstanceUIDMarker) {
        final VR vr = dcmEl.vr();
        if (vr == VR.SQ && output != ActionStrategy.Output.TO_REMOVE) {
            List<DicomObject> ldcm = dcmEl.itemStream().collect(Collectors.toList());
            for (DicomObject dcm: ldcm) {
                applyAction(dcm, patientName, SOPinstanceUIDMarker);
            }
        }
    }

    public void applyAction(DicomObject dcm, String patientID, Marker SOPinstanceUIDMarker) {
        for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext(); ) {
            DicomElement dcmEl = iterator.next();
            final Action action = this.profile.getAction(dcmEl);
            try {
                final String tagValueIn = dcm.getString(dcmEl.tag()).orElse(null);
                ActionStrategy.Output out = action.execute(dcm, dcmEl.tag(), patientID, null);
                getSequence(dcmEl, patientID, out, SOPinstanceUIDMarker);

                String tagValueOut = dcm.getString(dcmEl.tag()).orElse(null);
                String patternValueOut = " TAGVALUEOUT=" + tagValueOut;
                if (out == ActionStrategy.Output.TO_REMOVE) {
                    iterator.remove();
                    patternValueOut = "";
                }

                LOGGER.log(CLINICAL_LEVEL, SOPinstanceUIDMarker,"PATIENTID=" + patientID + " TAGHEX=" + TagUtils.toString(dcmEl.tag()) + " TAGINT=" + dcmEl.tag() + " DEIDENTACTION=" + action.getSymbol() + " TAGVALUEIN=" + tagValueIn + patternValueOut);

            } catch (final Exception e) {
                LOGGER.error("Cannot execute the action {}", action, e);
            }
        }
    }

    public void apply(DicomObject dcm) {
        final Marker SOPinstanceUIDMarker = MarkerManager.getMarker(dcm.getString(Tag.SOPInstanceUID).orElse(null));

        String pseudonym = getMainzellistePseudonym(dcm);
        String profileChainCodeName = getCodesName(this.profile, new ArrayList<String>()).toString();
        String patientID = generatePatientID(pseudonym, profileChainCodeName);

        if(!StringUtil.hasText(pseudonym)){
            throw new IllegalStateException("Cannot build a pseudonym");
        }

        applyAction(dcm, patientID, SOPinstanceUIDMarker);

        setDefaultDeidentTagValue(dcm, patientID, profileChainCodeName, pseudonym, SOPinstanceUIDMarker);
    }

    public void setDefaultDeidentTagValue(DicomObject dcm, String patientID, String profileChainCodeName, String pseudonym, Marker SOPinstanceUIDMarker){
        final String profileFilename = profileChainYml.getName();

        dcm.setString(Tag.PatientID, VR.LO,  patientID);
        LOGGER.log(CLINICAL_LEVEL, SOPinstanceUIDMarker,"PATIENTID=" + patientID + " TAGHEX=" + TagUtils.toString(Tag.PatientID) + " TAGINT=" + Tag.PatientID + " DEIDENTACTION=DeidentMethodValue" + " TAGVALUEOUT=" + patientID);

        dcm.setString(Tag.PatientName, VR.PN,  patientID);
        LOGGER.log(CLINICAL_LEVEL, SOPinstanceUIDMarker,"PatientName=" + patientID + " TAGHEX=" + TagUtils.toString(Tag.PatientName) + " TAGINT=" + Tag.PatientName + " DEIDENTACTION=DeidentMethodValue" + " TAGVALUEOUT=" + patientID);

        dcm.setString(Tag.PatientIdentityRemoved, VR.CS,  "YES");
        LOGGER.log(CLINICAL_LEVEL, SOPinstanceUIDMarker,"PatientIdentityRemoved=YES" + " TAGHEX=" + TagUtils.toString(Tag.PatientIdentityRemoved) + " TAGINT=" + Tag.PatientIdentityRemoved + " DEIDENTACTION=DeidentMethodValue" + " TAGVALUEOUT=YES");

        // 0012,0063 -> module patient
        // A description or label of the mechanism or method use to remove the Patient's identity
        dcm.setString(Tag.DeidentificationMethod, VR.LO, profileChainCodeName);
        LOGGER.log(CLINICAL_LEVEL, SOPinstanceUIDMarker,"DeidentificationMethod=" + profileChainCodeName + " TAGHEX=" + TagUtils.toString(Tag.DeidentificationMethod) + " TAGINT=" + Tag.DeidentificationMethod + " DEIDENTACTION=DeidentMethodValue" + " TAGVALUEOUT=" + profileChainCodeName);

        dcm.setString(Tag.ClinicalTrialSponsorName, VR.LO, profileChainCodeName);
        LOGGER.log(CLINICAL_LEVEL, SOPinstanceUIDMarker,"ClinicalTrialSponsorName=" + profileChainCodeName + " TAGHEX=" + TagUtils.toString(Tag.ClinicalTrialSponsorName) + " TAGINT=" + Tag.ClinicalTrialSponsorName + " DEIDENTACTION=DeidentMethodValue" + " TAGVALUEOUT=" + profileChainCodeName);

        dcm.setString(Tag.ClinicalTrialProtocolID, VR.LO, profileFilename);
        LOGGER.log(CLINICAL_LEVEL, SOPinstanceUIDMarker,"ClinicalTrialProtocolID=" + profileFilename + " TAGHEX=" + TagUtils.toString(Tag.ClinicalTrialProtocolID) + " TAGINT=" + Tag.ClinicalTrialProtocolID + " DEIDENTACTION=DeidentMethodValue" + " TAGVALUEOUT=" + profileFilename);

        dcm.setString(Tag.ClinicalTrialSubjectID, VR.LO, pseudonym);
        LOGGER.log(CLINICAL_LEVEL, SOPinstanceUIDMarker,"ClinicalTrialSubjectID=" + pseudonym + " TAGHEX=" + TagUtils.toString(Tag.ClinicalTrialSubjectID) + " TAGINT=" + Tag.ClinicalTrialSubjectID + " DEIDENTACTION=DeidentMethodValue" + " TAGVALUEOUT=" + pseudonym);

        dcm.setString(Tag.ClinicalTrialProtocolName, VR.LO);
        LOGGER.log(CLINICAL_LEVEL, SOPinstanceUIDMarker,"ClinicalTrialProtocolName=" + " TAGHEX=" + TagUtils.toString(Tag.ClinicalTrialProtocolName) + " TAGINT=" + Tag.ClinicalTrialProtocolName + " DEIDENTACTION=DeidentMethodValue" + " TAGVALUEOUT=");

        dcm.setString(Tag.ClinicalTrialSiteID, VR.LO);
        LOGGER.log(CLINICAL_LEVEL, SOPinstanceUIDMarker,"ClinicalTrialSiteID=" + " TAGHEX=" + TagUtils.toString(Tag.ClinicalTrialSiteID) + " TAGINT=" + Tag.ClinicalTrialSiteID + " DEIDENTACTION=DeidentMethodValue" + " TAGVALUEOUT=");

        dcm.setString(Tag.ClinicalTrialSiteName, VR.LO);
        LOGGER.log(CLINICAL_LEVEL, SOPinstanceUIDMarker,"ClinicalTrialSiteName=" + " TAGHEX=" + TagUtils.toString(Tag.ClinicalTrialSiteName) + " TAGINT=" + Tag.ClinicalTrialSiteName + " DEIDENTACTION=DeidentMethodValue" + " TAGVALUEOUT=");

    }

    public String generatePatientID(String pseudonym, String profiles) {
        byte[] bytes = new byte[16];
        System.arraycopy(hmac.byteHash(pseudonym+profiles), 0 , bytes, 0, 16);
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
