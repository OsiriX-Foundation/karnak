/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.profilepipe;

import java.awt.Color;
import java.awt.Shape;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.BulkData;
import org.dcm4che3.data.Fragments;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.img.op.MaskArea;
import org.dcm4che3.util.TagUtils;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.enums.ProfileItemType;
import org.karnak.backend.enums.PseudonymType;
import org.karnak.backend.model.action.ActionItem;
import org.karnak.backend.model.action.Remove;
import org.karnak.backend.model.action.ReplaceNull;
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

public class Profile {

  private static final Logger LOGGER = LoggerFactory.getLogger(Profile.class);

  private final List<ProfileItem> profiles;
  private final Pseudonym pseudonym;
  private final Map<String, MaskArea> maskMap;

  public Profile(ProfileEntity profileEntity) {
    this.maskMap = new HashMap<>();
    this.pseudonym = new Pseudonym();
    this.profiles = createProfilesList(profileEntity);
  }

  public void addMaskMap(Map<? extends String, ? extends MaskArea> maskMap) {
    this.maskMap.putAll(maskMap);
  }

  public MaskArea getMask(String key) {
    MaskArea mask = maskMap.get(key);
    if (mask == null) {
      mask = maskMap.get("*");
    }
    return mask;
  }

  public void addMask(String stationName, MaskArea maskArea) {
    this.maskMap.put(stationName, maskArea);
  }

  public List<ProfileItem> createProfilesList(final ProfileEntity profileEntity) {
    if (profileEntity != null) {
      final Set<ProfileElementEntity> listProfileElementEntity =
          profileEntity.getProfileElementEntities();
      List<ProfileItem> profileItems = new ArrayList<>();

      for (ProfileElementEntity profileElementEntity : listProfileElementEntity) {
        ProfileItemType t = ProfileItemType.getType(profileElementEntity.getCodename());
        if (t == null) {
          LOGGER.error("Cannot find the profile codename: {}", profileElementEntity.getCodename());
        } else {
          Object instanceProfileItem;
          try {
            instanceProfileItem =
                t.getProfileClass()
                    .getConstructor(ProfileElementEntity.class)
                    .newInstance(profileElementEntity);
            profileItems.add((ProfileItem) instanceProfileItem);
          } catch (Exception e) {
            LOGGER.error("Cannot build the profile: {}", t.getProfileClass().getName(), e);
          }
        }
      }
      profileItems.sort(Comparator.comparing(ProfileItem::getPosition));
      profileEntity
          .getMaskEntities()
          .forEach(
              m -> {
                Color color = null;
                if (StringUtil.hasText(m.getColor())) {
                  color = ActionTags.hexadecimal2Color(m.getColor());
                }
                List<Shape> shapeList =
                    m.getRectangles().stream().map(Shape.class::cast).collect(Collectors.toList());
                addMask(m.getStationName(), new MaskArea(shapeList, color));
              });
      return profileItems;
    }
    return Collections.emptyList();
  }

  public void applyAction(
      Attributes dcm,
      Attributes dcmCopy,
      HMAC hmac,
      ProfileItem profilePassedInSequence,
      ActionItem actionPassedInSequence,
      AttributeEditorContext context) {
    for (int tag : dcm.tags()) {
      VR vr = dcm.getVR(tag);
      final ExprConditionDestination exprConditionDestination =
          new ExprConditionDestination(tag, vr, dcm, dcmCopy);

      ActionItem currentAction = null;
      ProfileItem currentProfile = null;
      for (ProfileItem profileEntity :
          profiles.stream()
              .filter(p -> !(p instanceof CleanPixelData))
              .collect(Collectors.toList())) {
        currentProfile = profileEntity;

        if (profileEntity.getCondition() == null) {
          currentAction = profileEntity.getAction(dcm, dcmCopy, tag, hmac);
        } else {
          boolean conditionIsOk =
              (Boolean)
                  ExpressionResult.get(
                      profileEntity.getCondition(), exprConditionDestination, Boolean.class);
          if (conditionIsOk) {
            currentAction = profileEntity.getAction(dcm, dcmCopy, tag, hmac);
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

      if (!(currentAction instanceof Remove)
          && !(currentAction instanceof ReplaceNull)
          && vr == VR.SQ) {
        final ProfileItem finalCurrentProfile = currentProfile;
        final ActionItem finalCurrentAction = currentAction;
        Sequence seq = dcm.getSequence(tag);
        if (seq != null) {
          for (Attributes d : seq) {
            applyAction(d, dcmCopy, hmac, finalCurrentProfile, finalCurrentAction, context);
          }
        }
      } else {
        if (currentAction != null) {
          try {
            currentAction.execute(dcm, tag, hmac);
          } catch (final Exception e) {
            LOGGER.error(
                "Cannot execute the currentAction {} for tag: {}",
                currentAction,
                TagUtils.toString(tag),
                e);
          }
        }
      }
    }
  }

  public void apply(
      Attributes dcm,
      DestinationEntity destinationEntity,
      ProfileEntity profileEntity,
      AttributeEditorContext context) {
    final String SOPInstanceUID = dcm.getString(Tag.SOPInstanceUID);
    final String SeriesInstanceUID = dcm.getString(Tag.SeriesInstanceUID);
    final String IssuerOfPatientID = dcm.getString(Tag.IssuerOfPatientID);
    final String PatientID = dcm.getString(Tag.PatientID);
    final PseudonymType pseudonymType = destinationEntity.getPseudonymType();
    final HMAC hmac = generateHMAC(destinationEntity, PatientID);

    MDC.put("SOPInstanceUID", SOPInstanceUID);
    MDC.put("SeriesInstanceUID", SeriesInstanceUID);
    MDC.put("issuerOfPatientID", IssuerOfPatientID);
    MDC.put("PatientID", PatientID);

    String pseudonym =
        this.pseudonym.generatePseudonym(
            destinationEntity, dcm, profileEntity.getDefaultIssuerOfPatientId());

    String profilesCodeName =
        profiles.stream().map(ProfileItem::getCodeName).collect(Collectors.joining("-"));
    BigInteger patientValue = generatePatientID(pseudonym, hmac);
    String newPatientID = patientValue.toString(16).toUpperCase();
    String newPatientName =
        !pseudonymType.equals(PseudonymType.MAINZELLISTE_PID)
                && destinationEntity.getPseudonymAsPatientName().booleanValue()
            ? pseudonym
            : newPatientID;

    Attributes dcmCopy = new Attributes(dcm);
    // Apply clean pixel data
    Object pix = dcm.getValue(Tag.PixelData);
    if ((pix instanceof BulkData || pix instanceof Fragments)
        && !profileEntity.getMaskEntities().isEmpty()
        && profiles.stream().anyMatch(p -> p instanceof CleanPixelData)) {
      String sopClassUID = dcm.getString(Tag.SOPClassUID);
      if (!StringUtil.hasText(sopClassUID)) {
        throw new IllegalStateException("DICOM Object does not contain sopClassUID");
      }
      String scuPattern = sopClassUID + ".";
      MaskArea mask = getMask(dcm.getString(Tag.StationName));
      // A mask must be applied with all the US and Secondary Capture sopClassUID, and with
      // BurnedInAnnotation
      if (scuPattern.startsWith("1.2.840.10008.5.1.4.1.1.6.")
          || scuPattern.startsWith("1.2.840.10008.5.1.4.1.1.7.")
          || scuPattern.startsWith("1.2.840.10008.5.1.4.1.1.3.")
          || scuPattern.equals("1.2.840.10008.5.1.4.1.1.77.1.1")
          || "YES".equalsIgnoreCase(dcm.getString(Tag.BurnedInAnnotation))) {
        context.setMaskArea(mask);
        if (mask == null) {
          throw new IllegalStateException("Cannot clean pixel data to sopClassUID " + sopClassUID);
        }
      } else {
        context.setMaskArea(null);
      }
    }

    applyAction(dcm, dcmCopy, hmac, null, null, context);

    // Set tags by default
    DeidentificationTags.setTagsByDefault(dcm, newPatientID, newPatientName);
    DeidentificationTags.setClinicalTrialAttributes(
        dcm, destinationEntity.getProjectEntity(), pseudonym);
    DeidentificationTags.setDeidentificationMethodCodeSequence(
        dcm, destinationEntity.getProjectEntity());

    final Marker clincalMarker = MarkerFactory.getMarker("CLINICAL");
    LOGGER.info(
        clincalMarker,
        "SOPInstanceUID_OLD={} SOPInstanceUID_NEW={} SeriesInstanceUID_OLD={} "
            + "SeriesInstanceUID_NEW={} ProjectName={} ProfileName={} ProfileCodenames={}",
        SOPInstanceUID,
        dcm.getString(Tag.SOPInstanceUID),
        SeriesInstanceUID,
        dcm.getString(Tag.SeriesInstanceUID),
        destinationEntity.getProjectEntity().getName(),
        profileEntity.getName(),
        profilesCodeName);
    MDC.clear();
  }

  private HMAC generateHMAC(DestinationEntity destinationEntity, String patientID) {
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

    if (patientID == null) {
      throw new IllegalStateException("Cannot build the HMAC no PatientID given");
    }

    HashContext hashContext = new HashContext(secret, patientID);
    return new HMAC(hashContext);
  }

  public BigInteger generatePatientID(String pseudonym, HMAC hmac) {
    byte[] bytes = new byte[16];
    System.arraycopy(hmac.byteHash(pseudonym), 0, bytes, 0, 16);
    return new BigInteger(1, bytes);
  }
}
