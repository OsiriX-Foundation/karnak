package org.karnak.backend.service.profilepipe;

import java.awt.Color;
import java.awt.Shape;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.img.op.MaskArea;
import org.dcm4che6.img.util.DicomObjectUtil;
import org.dcm4che6.util.DateTimeUtils;
import org.dcm4che6.util.TagUtils;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.enums.IdTypes;
import org.karnak.backend.enums.ProfileItemType;
import org.karnak.backend.model.action.ActionItem;
import org.karnak.backend.model.action.Add;
import org.karnak.backend.model.action.Remove;
import org.karnak.backend.model.action.ReplaceNull;
import org.karnak.backend.model.expression.ExprAction;
import org.karnak.backend.model.expression.ExprConditionDestination;
import org.karnak.backend.model.expression.ExpressionResult;
import org.karnak.backend.model.profilepipe.HMAC;
import org.karnak.backend.model.profilepipe.HashContext;
import org.karnak.backend.model.profiles.ActionTags;
import org.karnak.backend.model.profiles.CleanPixelData;
import org.karnak.backend.model.profiles.ProfileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.weasis.core.util.StringUtil;
import org.weasis.dicom.param.AttributeEditorContext;

public class Profiles {

    private static final Logger LOGGER = LoggerFactory.getLogger(Profiles.class);

    private final ProfileEntity profileEntity;
    private final Pseudonym pseudonymUtil;
    private final ArrayList<ProfileItem> profiles;
    private final Map<String, MaskArea> maskMap;
    private final Marker CLINICAL_MARKER = MarkerFactory.getMarker("CLINICAL");

    public Profiles(ProfileEntity profileEntity) {
        this.maskMap = new HashMap<>();
        this.pseudonymUtil = new Pseudonym();
        this.profileEntity = profileEntity;
        this.profiles = createProfilesList();
    }

    public void addMaskMap(Map<? extends String, ? extends MaskArea> maskMap){
        this.maskMap.putAll(maskMap);
    }

    public MaskArea getMask(String key) {
        MaskArea mask = maskMap.get(key);
        if (mask == null) {
            mask = maskMap.get("*");
        }
        return mask;
    }

    public void addMask(String stationName, MaskArea maskArea){
        this.maskMap.put(stationName, maskArea);
    }

    public ArrayList<ProfileItem> createProfilesList() {
        if (profileEntity != null) {
            final List<ProfileElementEntity> listProfileElementEntity = profileEntity
                .getProfileElementEntities();
            ArrayList<ProfileItem> profiles = new ArrayList<>();

            for (ProfileElementEntity profileElementEntity : listProfileElementEntity) {
                ProfileItemType t = ProfileItemType.getType(profileElementEntity.getCodename());
                if (t == null) {
                    LOGGER.error("Cannot find the profile codename: {}",
                        profileElementEntity.getCodename());
                } else {
                    Object instanceProfileItem;
                    try {
                        instanceProfileItem = t.getProfileClass()
                            .getConstructor(ProfileElementEntity.class)
                            .newInstance(profileElementEntity);
                        profiles.add((ProfileItem) instanceProfileItem);
                    } catch (Exception e) {
                        LOGGER.error("Cannot build the profile: {}", t.getProfileClass().getName(), e);
                    }
                }
            }
            profiles.sort(Comparator.comparing(ProfileItem::getPosition));
            profileEntity.getMaskEntities().forEach(
                m -> {
                    Color color = null;
                    if (StringUtil.hasText(m.getColor())) {
                        color = ActionTags.hexadecimal2Color(m.getColor());
                    }
                    List<Shape> shapeList = m.getRectangles().stream().map(Shape.class::cast)
                        .collect(Collectors.toList());
                    addMask(m.getStationName(), new MaskArea(shapeList, color));
                });
            return profiles;
        }
        return null;
    }

    public void applyAction(DicomObject dcm, DicomObject dcmCopy, HMAC hmac,
                            ProfileItem profilePassedInSequence, ActionItem actionPassedInSequence, AttributeEditorContext context) {
        for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext(); ) {
            final DicomElement dcmEl = iterator.next();
            final ExprConditionDestination exprConditionDestination = new ExprConditionDestination(dcmEl.tag(), dcmEl.vr(), dcm, dcmCopy);

            ActionItem currentAction = null;
            ProfileItem currentProfile = null;
            for (ProfileItem profileEntity : profiles.stream()
                .filter(p -> !(p instanceof CleanPixelData))
                .collect(
                    Collectors.toList())) {
                currentProfile = profileEntity;

                if (profileEntity.getCondition() == null) {
                    currentAction = profileEntity.getAction(dcm, dcmCopy, dcmEl, hmac);
                } else {
                    boolean conditionIsOk = (Boolean) ExpressionResult
                        .get(profileEntity.getCondition(), exprConditionDestination, Boolean.class);
                    if (conditionIsOk) {
                        currentAction = profileEntity.getAction(dcm, dcmCopy, dcmEl, hmac);
                    }
                }

                if (currentAction != null) {
                    break;
                }

                if (profileEntity.equals(profilePassedInSequence)) {
                    currentAction = actionPassedInSequence;
                    break;
                }
            }

            if (!(currentAction instanceof Remove) && !(currentAction instanceof ReplaceNull)
                && dcmEl.vr() == VR.SQ) {
                final ProfileItem finalCurrentProfile = currentProfile;
                final ActionItem finalCurrentAction = currentAction;
                dcmEl.itemStream().forEach(
                    d -> applyAction(d, dcmCopy, hmac, finalCurrentProfile, finalCurrentAction,
                        context));
            } else {
                if (currentAction != null) {
                    try {
                        currentAction.execute(dcm, dcmEl.tag(), iterator, hmac);
                    } catch (final Exception e) {
                        LOGGER
                            .error("Cannot execute the currentAction {} for tag: {}", currentAction,
                                TagUtils.toString(dcmEl.tag()), e);
                    }
                }
            }
        }
    }

    public void apply(DicomObject dcm, DestinationEntity destinationEntity,
        AttributeEditorContext context) {
        final String SOPInstanceUID = dcm.getString(Tag.SOPInstanceUID).orElse(null);
        final String SeriesInstanceUID = dcm.getString(Tag.SeriesInstanceUID).orElse(null);
        final String IssuerOfPatientID = dcm.getString(Tag.IssuerOfPatientID).orElse(null);
        final String PatientID = dcm.getString(Tag.PatientID).orElse(null);
        final IdTypes idTypes = destinationEntity.getIdTypes();
        final HMAC hmac = generateHMAC(destinationEntity, PatientID);

        MDC.put("SOPInstanceUID", SOPInstanceUID);
        MDC.put("SeriesInstanceUID", SeriesInstanceUID);
        MDC.put("issuerOfPatientID", IssuerOfPatientID);
        MDC.put("PatientID", PatientID);

        String pseudonym = pseudonymUtil
            .generatePseudonym(destinationEntity, dcm, profileEntity.getDefaultissueropatientid());

        String profilesCodeName = String.join(
            "-", profiles.stream().map(profileEntity -> profileEntity.getCodeName())
                .collect(Collectors.toList())
        );
        BigInteger patientValue = generatePatientID(pseudonym, hmac);
        String newPatientID = patientValue.toString(16).toUpperCase();
        String newPatientName =
            !idTypes.equals(IdTypes.PID) && destinationEntity.getPseudonymAsPatientName()
                ? pseudonym : newPatientID;

        DicomObject dcmCopy = DicomObject.newDicomObject();
        DicomObjectUtil.copyDataset(dcm, dcmCopy);

        // Apply clean pixel data
        Optional<DicomElement> pix = dcm.get(Tag.PixelData);
        if (pix.isPresent() && !profileEntity.getMaskEntities().isEmpty() && profiles.stream()
            .anyMatch(p -> p instanceof CleanPixelData)) {
            String sopClassUID = dcm.getString(Tag.SOPClassUID)
                .orElseThrow(
                    () -> new IllegalStateException("DICOM Object does not contain sopClassUID"));
            String scuPattern = sopClassUID + ".";
            MaskArea mask = getMask(dcm.getString(Tag.StationName).orElse(null));
            // A mask must be applied with all the US and Secondary Capture sopClassUID, and with BurnedInAnnotation
            if (scuPattern.startsWith("1.2.840.10008.5.1.4.1.1.6.") || scuPattern
                .startsWith("1.2.840.10008.5.1.4.1.1.7.")
                || scuPattern.startsWith("1.2.840.10008.5.1.4.1.1.3.") || scuPattern
                .equals("1.2.840.10008.5.1.4.1.1.77.1.1") || "YES"
                .equalsIgnoreCase(dcm.getString(Tag.BurnedInAnnotation).orElse(null))) {
                context.setMaskArea(mask);
                if (mask == null) {
                    throw new IllegalStateException("Cannot clean pixel data to sopClassUID " + sopClassUID);
                }
            }
            else {
                context.setMaskArea(null);
            }
        }

        applyAction(dcm, dcmCopy, hmac, null, null, context);

        setDefaultDeidentTagValue(dcm, newPatientID, newPatientName, profilesCodeName, pseudonym, hmac);

        final Marker CLINICAL_MARKER = MarkerFactory.getMarker("CLINICAL");
        LOGGER.info(CLINICAL_MARKER,
            "SOPInstanceUID_OLD={} SOPInstanceUID_NEW={} SeriesInstanceUID_OLD={} " +
                "SeriesInstanceUID_NEW={} ProjectName={} ProfileName={} ProfileCodenames={}",
            SOPInstanceUID,
            dcm.getString(Tag.SOPInstanceUID).orElse(null),
            SeriesInstanceUID,
            dcm.getString(Tag.SeriesInstanceUID).orElse(null),
            destinationEntity.getProjectEntity().getName(),
            profileEntity.getName(),
            profilesCodeName);
        MDC.clear();
    }

    private HMAC generateHMAC(DestinationEntity destinationEntity, String PatientID) {
        ProjectEntity projectEntity = destinationEntity.getProjectEntity();
        if (projectEntity == null) {
            throw new IllegalStateException(
                "Cannot build the HMAC a project is not associate at the destination");
        }

        byte[] secret = projectEntity.getSecret();
        if (secret == null || secret.length != HMAC.KEY_BYTE_LENGTH) {
            throw new IllegalStateException(
                "Cannot build the HMAC no secret defined in the project associate at the destination");
        }

        if (PatientID == null) {
            throw new IllegalStateException("Cannot build the HMAC no PatientID given");
        }

        HashContext hashContext = new HashContext(secret, PatientID);
        return new HMAC(hashContext);
    }

    public void setDefaultDeidentTagValue(DicomObject dcm, String patientID, String patientName, String profilePipeCodeName,
                                          String pseudonym, HMAC hmac){
        final String profileFilename = profileEntity.getName();
        final ArrayList<ExprAction> defaultDeidentTagValue = new ArrayList<>();
        defaultDeidentTagValue.add(new ExprAction(Tag.PatientID, VR.LO, patientID));
        defaultDeidentTagValue.add(new ExprAction(Tag.PatientName, VR.PN, patientName));
        defaultDeidentTagValue.add(new ExprAction(Tag.PatientIdentityRemoved, VR.CS, "YES"));
        // 0012,0063 -> module patient
        // A description or label of the mechanism or method use to remove the Patient's identity
        defaultDeidentTagValue
            .add(new ExprAction(Tag.DeidentificationMethod, VR.LO, profilePipeCodeName));
        defaultDeidentTagValue
            .add(new ExprAction(Tag.ClinicalTrialSponsorName, VR.LO, profilePipeCodeName));
        defaultDeidentTagValue
            .add(new ExprAction(Tag.ClinicalTrialProtocolID, VR.LO, profileFilename));
        defaultDeidentTagValue.add(new ExprAction(Tag.ClinicalTrialSubjectID, VR.LO, pseudonym));
        defaultDeidentTagValue.add(new ExprAction(Tag.ClinicalTrialProtocolName, VR.LO, null));
        defaultDeidentTagValue.add(new ExprAction(Tag.ClinicalTrialSiteID, VR.LO, null));
        defaultDeidentTagValue.add(new ExprAction(Tag.ClinicalTrialSiteName, VR.LO, null));

        LocalDateTime now = LocalDateTime.now();
        defaultDeidentTagValue
            .add(new ExprAction(Tag.InstanceCreationDate, VR.DA, DateTimeUtils.formatDA(now)));
        defaultDeidentTagValue
            .add(new ExprAction(Tag.InstanceCreationTime, VR.TM, DateTimeUtils.formatTM(now)));

        defaultDeidentTagValue.forEach(newElem -> {
            final ActionItem add = new Add("A", newElem.getTag(), newElem.getVr(),
                newElem.getStringValue());
            add.execute(dcm, newElem.getTag(), null, hmac);
        });
    }


    public BigInteger generatePatientID(String pseudonym, HMAC hmac) {
        byte[] bytes = new byte[16];
        System.arraycopy(hmac.byteHash(pseudonym), 0, bytes, 0, 16);
        return new BigInteger(1, bytes);
    }
}
