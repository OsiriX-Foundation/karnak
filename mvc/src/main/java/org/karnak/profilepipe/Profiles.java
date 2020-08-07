package org.karnak.profilepipe;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.img.op.MaskArea;
import org.dcm4che6.util.TagUtils;
import org.karnak.api.PseudonymApi;
import org.karnak.api.rqbody.Fields;
import org.karnak.data.AppConfig;
import org.karnak.data.profile.ProfileElement;
import org.karnak.data.profile.Profile;
import org.karnak.profilepipe.action.Action;
import org.karnak.profilepipe.action.ActionStrategy;
import org.karnak.profilepipe.profiles.AbstractProfileItem;
import org.karnak.profilepipe.profiles.ProfileItem;
import org.karnak.profilepipe.utils.HMAC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.MDC;
import org.weasis.core.util.StringUtil;
import org.weasis.dicom.param.AttributeEditorContext;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.awt.*;
import java.awt.geom.RectangularShape;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Profiles {
    private final Logger LOGGER = LoggerFactory.getLogger(Profiles.class);
    private final Marker CLINICAL_MARKER = MarkerFactory.getMarker("CLINICAL");
    private final String PATTERN_WITH_INOUT = "TAGHEX={} TAGINT={} DEIDENTACTION={} TAGVALUEIN={} TAGOUT={}";
    private final String PATTERN_WITH_IN = "TAGHEX={} TAGINT={} DEIDENTACTION={} TAGVALUEIN={}";
    private final String DUMMY_DEIDENT_METHOD = "dDeidentMethod";

    private Profile profile;
    private final ArrayList<ProfileItem> profiles;
    private final HMAC hmac;

    public Profiles(Profile profile) {
        this.hmac = AppConfig.getInstance().getHmac();
        this.profile = profile;
        this.profiles = createProfilesList();
    }

    public ArrayList<ProfileItem> createProfilesList() {
        if (profile != null) {
            final List<ProfileElement> listProfileElement = profile.getProfileElements();
            ArrayList<ProfileItem> profiles = new ArrayList<>();

            for (ProfileElement profileElement : listProfileElement) {
                AbstractProfileItem.Type t = AbstractProfileItem.Type.getType(profileElement.getCodename());
                if (t == null) {
                    LOGGER.error("Cannot find the profile codename: {}", profileElement.getCodename());
                } else {
                    Object instanceProfileItem;
                    try {
                        instanceProfileItem = t.getProfileClass()
                                .getConstructor(String.class, String.class, String.class, List.class, List.class)
                                .newInstance(profileElement.getName(), profileElement.getCodename(), profileElement.getAction(), profileElement.getIncludedtag(), profileElement.getExceptedtags());
                        profiles.add((ProfileItem) instanceProfileItem);
                    } catch (Exception e) {
                        LOGGER.error("Cannot build the profile: {}", t.getProfileClass().getName(), e);
                    }
                }
            }
            return profiles;
        }
        return null;
    }

    public String getMainzellistePseudonym(DicomObject dcm) {
        final String patientID = dcm.getString(Tag.PatientID).orElse(null);
        final String patientName = dcm.getString(Tag.PatientName).orElse(null);
        final String patientBirthDate = dcm.getString(Tag.PatientBirthDate).orElse(null);
        final String patientSex = dcm.getString(Tag.PatientSex).orElse(null);
        // Issuer of patientID is recommended to make the patientID universally unique. Can be defined in profile if missing.
        final String issuerOfPatientID = dcm.getString(Tag.IssuerOfPatientID).orElse(profile.getDefaultissueropatientid());

        PseudonymApi pseudonymApi = new PseudonymApi();
        final Fields newPatientFields = new Fields(patientID, patientName, patientBirthDate, patientSex, issuerOfPatientID);
        final String pseudonym = pseudonymApi.createPatient(newPatientFields);
        return pseudonym;
    }

    public void applyAction(DicomObject dcm, String patientID, AttributeEditorContext context) {
        for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext(); ) {
            DicomElement dcmEl = iterator.next();
            for (ProfileItem profile : profiles) {
                final Action action = profile.getAction(dcmEl);
                if (action != null) {
                    try {
                        final String tagValueIn = dcm.getString(dcmEl.tag()).orElse(null);
                        ActionStrategy.Output out = action.execute(dcm, dcmEl.tag(), patientID, null);
                        final String tagValueOut = dcm.getString(dcmEl.tag()).orElse(null);
                        if (out == ActionStrategy.Output.TO_REMOVE) {
                            iterator.remove();
                            LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_IN, TagUtils.toString(dcmEl.tag()), dcmEl.tag(), action.getSymbol(), tagValueIn);
                        } else if(out == ActionStrategy.Output.CLEAN_PIXEL){
                            List<Shape> shapeList = new ArrayList<>();
                            shapeList.add(new Rectangle(25, 15 , 250, 70));
                            shapeList.add(new Rectangle(340, 15 , 150, 50));
                            MaskArea mask = new MaskArea(shapeList, null);
                            context.setMaskArea(mask);
                            LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_IN, TagUtils.toString(dcmEl.tag()), dcmEl.tag(), action.getSymbol(), tagValueIn);
                        } else {
                            LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(dcmEl.tag()), dcmEl.tag(), action.getSymbol(), tagValueIn, tagValueOut);
                        }
                    } catch (final Exception e) {
                        LOGGER.error("Cannot execute the action {}", action, e);
                    }
                    break;
                }
                else if (dcmEl.vr() == VR.SQ) {
                    dcmEl.itemStream().forEach(d -> applyAction(d, patientID, context));
                }
            }
        }
    }

    public void apply(DicomObject dcm, AttributeEditorContext context) {
        final String SOPinstanceUID = dcm.getString(Tag.SOPInstanceUID).orElse(null);
        final String IssuerOfPatientID = dcm.getString(Tag.IssuerOfPatientID).orElse(null);
        final String PatientID = dcm.getString(Tag.PatientID).orElse(null);
        MDC.put("SOPInstanceUID", SOPinstanceUID);
        MDC.put("issuerOfPatientID", IssuerOfPatientID);
        MDC.put("PatientID", PatientID);

        String pseudonym = getMainzellistePseudonym(dcm);
        String profilesCodeName = String.join(
                "-" , profiles.stream().map(profile -> profile.getCodeName()).collect(Collectors.toList())
        );
        BigInteger patientValue = generatePatientID(pseudonym, profilesCodeName);
        String patientName = patientValue.toString(16).toUpperCase();
        String patientID = patientValue.toString();

        if (!StringUtil.hasText(pseudonym)) {
            throw new IllegalStateException("Cannot build a pseudonym");
        }

        applyAction(dcm, patientID, context);

        setDefaultDeidentTagValue(dcm, patientID, patientName, profilesCodeName, pseudonym);
    }

    public void setDefaultDeidentTagValue(DicomObject dcm, String patientID, String patientName, String profilePipeCodeName, String pseudonym){
        final String profileFilename = profile.getName();

        final String tagValueInPatientID = dcm.getString(Tag.PatientID).orElse(null);
        dcm.setString(Tag.PatientID, VR.LO,  patientID);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(Tag.PatientID), Tag.PatientID, DUMMY_DEIDENT_METHOD, tagValueInPatientID, patientID);

        final String tagValueInPatientName= dcm.getString(Tag.PatientID).orElse(null);
        dcm.setString(Tag.PatientName, VR.PN,  patientName);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(Tag.PatientName), Tag.PatientName, DUMMY_DEIDENT_METHOD, tagValueInPatientName, patientName);

        final String tagValueInPatientIdentityRemoved = dcm.getString(Tag.PatientID).orElse(null);
        dcm.setString(Tag.PatientIdentityRemoved, VR.CS,  "YES");
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(Tag.PatientIdentityRemoved), Tag.PatientIdentityRemoved, DUMMY_DEIDENT_METHOD, tagValueInPatientIdentityRemoved, "YES");

        // 0012,0063 -> module patient
        // A description or label of the mechanism or method use to remove the Patient's identity
        final String tagValueInDeidentificationMethod = dcm.getString(Tag.PatientID).orElse(null);
        dcm.setString(Tag.DeidentificationMethod, VR.LO, profilePipeCodeName);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(Tag.DeidentificationMethod), Tag.DeidentificationMethod, DUMMY_DEIDENT_METHOD, tagValueInDeidentificationMethod, profilePipeCodeName);

        final String tagValueInClinicalTrialSponsorName = dcm.getString(Tag.PatientID).orElse(null);
        dcm.setString(Tag.ClinicalTrialSponsorName, VR.LO, profilePipeCodeName);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(Tag.ClinicalTrialSponsorName), Tag.ClinicalTrialSponsorName, DUMMY_DEIDENT_METHOD, tagValueInClinicalTrialSponsorName, profilePipeCodeName);

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

    public BigInteger generatePatientID(String pseudonym, String profiles) {
        byte[] bytes = new byte[16];
        System.arraycopy(hmac.byteHash(pseudonym + profiles), 0, bytes, 0, 16);
        return new BigInteger(1, bytes);
    }
}
