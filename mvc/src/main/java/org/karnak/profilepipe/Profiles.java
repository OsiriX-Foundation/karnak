package org.karnak.profilepipe;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.img.util.DicomObjectUtil;
import org.dcm4che6.internal.DicomObjectImpl;
import org.dcm4che6.util.TagUtils;
import org.karnak.api.PseudonymApi;
import org.karnak.api.rqbody.Fields;
import org.karnak.data.AppConfig;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.IdTypes;
import org.karnak.data.profile.ProfileElement;
import org.karnak.data.profile.Profile;
import org.karnak.profilepipe.action.*;
import org.karnak.profilepipe.profiles.AbstractProfileItem;
import org.karnak.profilepipe.profiles.ProfileItem;
import org.karnak.profilepipe.utils.ExprDCMElem;
import org.karnak.profilepipe.utils.HMAC;
import org.karnak.util.SpecialCharacter;
import org.slf4j.*;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.weasis.core.util.StringUtil;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class Profiles {
    private final Logger LOGGER = LoggerFactory.getLogger(Profiles.class);

    private Profile profile;
    private final ArrayList<ProfileItem> profiles;
    private final HMAC hmac;

    public Profiles(Profile profile) {
        this.hmac = AppConfig.getInstance().getHmac();
        this.profile = profile;
        this.profiles = createProfilesList();
    }

    public Profiles(Profile profile, HMAC hmac) {
        this.hmac = hmac;
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
                                .getConstructor(ProfileElement.class)
                                .newInstance(profileElement);
                        profiles.add((ProfileItem) instanceProfileItem);
                    } catch (Exception e) {
                        LOGGER.error("Cannot build the profile: {}", t.getProfileClass().getName(), e);
                    }
                }
            }
            profiles.sort(Comparator.comparing(ProfileItem::getPosition));
            return profiles;
        }
        return null;
    }

    public String getMainzellistePseudonym(DicomObject dcm, String externalPseudonym, IdTypes idTypes) {
        final String patientID = dcm.getString(Tag.PatientID).orElse(null);
        final String patientName = dcm.getString(Tag.PatientName).orElse(null);
        final String patientBirthDate = dcm.getString(Tag.PatientBirthDate).orElse(null);
        final String patientSex = dcm.getString(Tag.PatientSex).orElse(null);
        // Issuer of patientID is recommended to make the patientID universally unique. Can be defined in profile if missing.
        final String issuerOfPatientID = dcm.getString(Tag.IssuerOfPatientID).orElse(profile.getDefaultissueropatientid());

        PseudonymApi pseudonymApi = new PseudonymApi(externalPseudonym);
        final Fields newPatientFields = new Fields(patientID, patientName, patientBirthDate, patientSex, issuerOfPatientID);

        return pseudonymApi.createPatient(newPatientFields, idTypes);
    }

    private String getExtIDInDicom(DicomObject dcm, Destination destination) {
        if (destination.getIdTypes().equals(IdTypes.ADD_EXTID)) {
            String cleanTag = destination.getTag().replaceAll("[(),]", "").toUpperCase();
            final String tagValue = dcm.getString(TagUtils.intFromHexString(cleanTag)).orElse(null);
            if (tagValue != null && destination.getDelimiter() != null && destination.getPosition() != null) {
                String delimiterSpec = SpecialCharacter.escapeSpecialRegexChars(destination.getDelimiter());
                try {
                    return tagValue.split(delimiterSpec)[destination.getPosition()];
                } catch (ArrayIndexOutOfBoundsException e) {
                    LOGGER.error("Can not split the external pseudonym", e);
                    return null;
                }
            } else if (tagValue != null) {
                return tagValue;
            }
        }
        return null;
    }

    public void applyAction(DicomObject dcm, DicomObject dcmCopy, String patientID, ProfileItem profilePassedInSequence) {
        for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext(); ) {
            final DicomElement dcmEl = iterator.next();
            final ExprDCMElem exprDCMElem = new ExprDCMElem(dcmEl.tag(), dcmEl.vr(), dcm, dcmCopy);

            ActionItem currentAction = null;
            ProfileItem currentProfile = null;
            for (ProfileItem profile : profiles) {
                currentProfile = profile;

                boolean conditionIsOk = getResultCondition(profile.getCondition(), exprDCMElem);
                if (conditionIsOk) {
                    currentAction = profile.getAction(dcm, dcmCopy, dcmEl, patientID);
                }

                if (currentAction != null || profile.equals(profilePassedInSequence)) {
                    break;
                }
            }

            if ( (!(Remove.class.isInstance(currentAction)) || !(ReplaceNull.class.isInstance(currentAction))) && dcmEl.vr() == VR.SQ) {
                final ProfileItem finalCurrentProfile = currentProfile;
                dcmEl.itemStream().forEach(d -> applyAction(d, dcmCopy, patientID, finalCurrentProfile));
            } else {
                if (currentAction != null) {
                    try {
                        currentAction.execute(dcm, dcmEl.tag(), iterator, patientID);
                    } catch (final Exception e) {
                        LOGGER.error("Cannot execute the currentAction {} for tag: {}", currentAction,  TagUtils.toString(dcmEl.tag()), e);
                    }
                }
            }
        }
    }

    public void apply(DicomObject dcm, Destination destination) {
        final String SOPinstanceUID = dcm.getString(Tag.SOPInstanceUID).orElse(null);
        final String IssuerOfPatientID = dcm.getString(Tag.IssuerOfPatientID).orElse(null);
        final String PatientID = dcm.getString(Tag.PatientID).orElse(null);
        final String stringExtIDInDicom = getExtIDInDicom(dcm, destination);
        final IdTypes idTypes = destination.getIdTypes();

        MDC.put("SOPInstanceUID", SOPinstanceUID);
        MDC.put("issuerOfPatientID", IssuerOfPatientID);
        MDC.put("PatientID", PatientID);

        String mainzellistePseudonym = getMainzellistePseudonym(dcm, stringExtIDInDicom, idTypes);
        String profilesCodeName = String.join(
                "-" , profiles.stream().map(profile -> profile.getCodeName()).collect(Collectors.toList())
        );
        BigInteger patientValue = generatePatientID(mainzellistePseudonym, profilesCodeName);
        String patientName = !idTypes.equals(IdTypes.PID) && destination.getPseudonymAsPatientName() == true ?
                mainzellistePseudonym : patientValue.toString(16).toUpperCase();
        String patientID = patientValue.toString();

        if (!StringUtil.hasText(mainzellistePseudonym)) {
            throw new IllegalStateException("Cannot build a pseudonym");
        }

        DicomObject dcmCopy = new DicomObjectImpl();
        DicomObjectUtil.copyDataset(dcm, dcmCopy);
        applyAction(dcm, dcmCopy, patientID, null);

        setDefaultDeidentTagValue(dcm, patientID, patientName, profilesCodeName, mainzellistePseudonym);
    }

    public void setDefaultDeidentTagValue(DicomObject dcm, String patientID, String patientName, String profilePipeCodeName, String pseudonym){
        final String profileFilename = profile.getName();
        final ArrayList<ExprDCMElem> defaultDeidentTagValue = new ArrayList<>();
        defaultDeidentTagValue.add(new ExprDCMElem(Tag.PatientID, VR.LO, patientID));
        defaultDeidentTagValue.add(new ExprDCMElem(Tag.PatientName, VR.PN, patientName));
        defaultDeidentTagValue.add(new ExprDCMElem(Tag.PatientIdentityRemoved, VR.CS, "YES"));
        // 0012,0063 -> module patient
        // A description or label of the mechanism or method use to remove the Patient's identity
        defaultDeidentTagValue.add(new ExprDCMElem(Tag.DeidentificationMethod, VR.LO, profilePipeCodeName));
        defaultDeidentTagValue.add(new ExprDCMElem(Tag.ClinicalTrialSponsorName, VR.LO, profilePipeCodeName));
        defaultDeidentTagValue.add(new ExprDCMElem(Tag.ClinicalTrialProtocolID, VR.LO, profileFilename));
        defaultDeidentTagValue.add(new ExprDCMElem(Tag.ClinicalTrialSubjectID, VR.LO, pseudonym));
        defaultDeidentTagValue.add(new ExprDCMElem(Tag.ClinicalTrialProtocolName, VR.LO, (String) null));
        defaultDeidentTagValue.add(new ExprDCMElem(Tag.ClinicalTrialSiteID, VR.LO, (String) null));
        defaultDeidentTagValue.add(new ExprDCMElem(Tag.ClinicalTrialSiteName, VR.LO, (String) null));

        defaultDeidentTagValue.forEach(newElem -> {
            final ActionItem add = new Add("A", newElem.getTag(), newElem.getVr(), newElem.getStringValue());
            add.execute(dcm, newElem.getTag(), null, patientID);
        });
    }

    public static boolean getResultCondition(String condition, ExprDCMElem exprDCMElem){
        final Logger LOGGER = LoggerFactory.getLogger(Profiles.class);
        if (condition!=null) {
            try {
                //https://docs.spring.io/spring/docs/3.0.x/reference/expressions.html
                final ExpressionParser parser = new SpelExpressionParser();
                final EvaluationContext context = new StandardEvaluationContext(exprDCMElem);
                final String cleanCondition = exprDCMElem.conditionInterpreter(condition);
                context.setVariable("VR", VR.class);
                context.setVariable("Tag", Tag.class);
                final Expression exp = parser.parseExpression(cleanCondition);
                return exp.getValue(context, Boolean.class);
            } catch (final Exception e) {
                LOGGER.error("Cannot execute the parser expression for this expression: {}", condition, e);
            }
        }
        return true; // if there is no condition we return true by default
    }

    public BigInteger generatePatientID(String pseudonym, String profiles) {
        byte[] bytes = new byte[16];
        System.arraycopy(hmac.byteHash(pseudonym + profiles), 0, bytes, 0, 16);
        return new BigInteger(1, bytes);
    }
}
