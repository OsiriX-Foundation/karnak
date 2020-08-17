package org.karnak.profilepipe;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.karnak.api.PseudonymApi;
import org.karnak.api.rqbody.Fields;
import org.karnak.data.AppConfig;
import org.karnak.data.profile.ProfileElement;
import org.karnak.data.profile.Profile;
import org.karnak.profilepipe.action.ActionItem;
import org.karnak.profilepipe.action.Add;
import org.karnak.profilepipe.action.Remove;
import org.karnak.profilepipe.profiles.AbstractProfileItem;
import org.karnak.profilepipe.profiles.ProfileItem;
import org.karnak.profilepipe.utils.MyDCMElem;
import org.karnak.profilepipe.utils.HMAC;
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

    public void applyAction(DicomObject dcm, String patientID) {
        for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext(); ) {
            final DicomElement dcmEl = iterator.next();
            final MyDCMElem myDCMElem = new MyDCMElem(dcmEl.tag(), dcmEl.vr(), dcm);

            for (ProfileItem profile : profiles) {
                final boolean conditionIsOk = getResultCondition(profile.getCondition(), myDCMElem);
                final ActionItem action = profile.getAction(dcm, dcmEl, patientID);
                if (action != null && conditionIsOk) {
                    try {
                        action.execute(dcm, dcmEl.tag(), iterator, patientID);
                    } catch (final Exception e) {
                        LOGGER.error("Cannot execute the action {} for tag: {}", action,  TagUtils.toString(dcmEl.tag()), e);
                    }
                    break;
                }

                if (!(Remove.class.isInstance(action)) && dcmEl.vr() == VR.SQ) {
                    dcmEl.itemStream().forEach(d -> applyAction(d, patientID));
                }
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
        String profilesCodeName = String.join(
                "-" , profiles.stream().map(profile -> profile.getCodeName()).collect(Collectors.toList())
        );
        BigInteger patientValue = generatePatientID(pseudonym, profilesCodeName);
        String patientName = patientValue.toString(16).toUpperCase();
        String patientID = patientValue.toString();

        if (!StringUtil.hasText(pseudonym)) {
            throw new IllegalStateException("Cannot build a pseudonym");
        }

        applyAction(dcm, patientID);

        setDefaultDeidentTagValue(dcm, patientID, patientName, profilesCodeName, pseudonym);
    }

    public void setDefaultDeidentTagValue(DicomObject dcm, String patientID, String patientName, String profilePipeCodeName, String pseudonym){
        final String profileFilename = profile.getName();
        final ArrayList<MyDCMElem> defaultDeidentTagValue = new ArrayList<>();
        defaultDeidentTagValue.add(new MyDCMElem(Tag.PatientID, VR.LO, patientID));
        defaultDeidentTagValue.add(new MyDCMElem(Tag.PatientName, VR.PN, patientName));
        defaultDeidentTagValue.add(new MyDCMElem(Tag.PatientIdentityRemoved, VR.CS, "YES"));
        // 0012,0063 -> module patient
        // A description or label of the mechanism or method use to remove the Patient's identity
        defaultDeidentTagValue.add(new MyDCMElem(Tag.DeidentificationMethod, VR.LO, profilePipeCodeName));
        defaultDeidentTagValue.add(new MyDCMElem(Tag.ClinicalTrialSponsorName, VR.LO, profilePipeCodeName));
        defaultDeidentTagValue.add(new MyDCMElem(Tag.ClinicalTrialProtocolID, VR.LO, profileFilename));
        defaultDeidentTagValue.add(new MyDCMElem(Tag.ClinicalTrialSubjectID, VR.LO, pseudonym));
        defaultDeidentTagValue.add(new MyDCMElem(Tag.ClinicalTrialProtocolName, VR.LO, (String) null));
        defaultDeidentTagValue.add(new MyDCMElem(Tag.ClinicalTrialSiteID, VR.LO, (String) null));
        defaultDeidentTagValue.add(new MyDCMElem(Tag.ClinicalTrialSiteName, VR.LO, (String) null));

        defaultDeidentTagValue.forEach(newElem -> {
            final ActionItem add = new Add("A", newElem.getStringValue(), newElem.getVr());
            add.execute(dcm, newElem.getTag(), null, patientID);
        });
    }

    public static boolean getResultCondition(String condition, MyDCMElem myDCMElem){
        final Logger LOGGER = LoggerFactory.getLogger(Profiles.class);
        if (condition!=null) {
            try {
                //https://docs.spring.io/spring/docs/3.0.x/reference/expressions.html
                final ExpressionParser parser = new SpelExpressionParser();
                final EvaluationContext context = new StandardEvaluationContext(myDCMElem);
                final String cleanCondition = myDCMElem.conditionInterpreter(condition);
                context.setVariable("VR", VR.class);
                context.setVariable("TAG", Tag.class);
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
