/*
* Copyright (c) 2021 Weasis Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.backend.model.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.karnak.backend.data.entity.ExcludedTagEntity;
import org.karnak.backend.data.entity.IncludedTagEntity;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.model.action.ActionItem;
import org.karnak.backend.model.action.Replace;
import org.karnak.backend.model.expression.ExprConditionDestination;
import org.karnak.backend.model.expression.ExpressionError;
import org.karnak.backend.model.expression.ExpressionResult;
import org.karnak.backend.model.profilepipe.HMAC;
import org.karnak.backend.model.profilepipe.TagActionMap;
import org.karnak.backend.util.DateFormat;
import org.karnak.backend.util.ShiftDate;
import org.karnak.backend.util.ShiftRangeDate;

public class ActionDates extends AbstractProfileItem {

  private final TagActionMap tagsAction;
  private final TagActionMap exceptedTagsAction;
  private final ActionItem actionByDefault;
  private final ShiftRangeDate shiftRangeDate;

  public ActionDates(ProfileElementEntity profileElementEntity) throws Exception {
    super(profileElementEntity);
    shiftRangeDate = new ShiftRangeDate();
    tagsAction = new TagActionMap();
    exceptedTagsAction = new TagActionMap();
    actionByDefault = new Replace("D");
    profileValidation();
    setActionHashMap();
  }

  private void setActionHashMap() throws Exception {
    if (tagEntities != null && tagEntities.size() > 0) {
      for (IncludedTagEntity tag : tagEntities) {
        tagsAction.put(tag.getTagValue(), actionByDefault);
      }
    }
    if (excludedTagEntities != null && excludedTagEntities.size() > 0) {
      for (ExcludedTagEntity tag : excludedTagEntities) {
        exceptedTagsAction.put(tag.getTagValue(), actionByDefault);
      }
    }
  }

  @Override
  public void profileValidation() throws Exception {
    try {
      if (option == null) {
        throw new Exception(
            "Cannot build the profile "
                + codeName
                + " : An option must be given. Option available: [shift, shift_range]");
      }
      switch (option) {
        case "shift" -> ShiftDate.verifyShiftArguments(argumentEntities);
        case "shift_range" -> ShiftRangeDate.verifyShiftArguments(argumentEntities);
        case "date_format" -> DateFormat.verifyPatternArguments(argumentEntities);
        default -> throw new Exception(
            "Cannot build the profile "
                + codeName
                + " with the option given "
                + option
                + " : Option available (shift, shift_range)");
      }
    } catch (Exception e) {
      throw e;
    }

    final ExpressionError expressionError =
        ExpressionResult.isValid(
            condition,
            new ExprConditionDestination(
                1, VR.AE, DicomObject.newDicomObject(), DicomObject.newDicomObject()),
            Boolean.class);
    if (condition != null && !expressionError.isValid()) {
      throw new Exception(expressionError.getMsg());
    }
  }

  @Override
  public ActionItem getAction(
      DicomObject dcm, DicomObject dcmCopy, DicomElement dcmElem, HMAC hmac) {
    final int tag = dcmElem.tag();
    final VR vr = dcmElem.vr();

    if (vr == VR.AS || vr == VR.DA || vr == VR.DT || vr == VR.TM) {
      if (exceptedTagsAction.get(tag) != null) {
        return null;
      }

      if (tagsAction.isEmpty() == false && tagsAction.get(tag) == null) {
        return null;
      }
      String dummyValue = applyOption(dcmCopy, dcmElem, hmac);
      if (dummyValue != null) {
        actionByDefault.setDummyValue(dummyValue);
        return actionByDefault;
      }
    }
    return null;
  }

  private String applyOption(DicomObject dcmCopy, DicomElement dcmElem, HMAC hmac) {
    return switch (option) {
      case "shift" -> ShiftDate.shift(dcmCopy, dcmElem, argumentEntities);
      case "shift_range" -> shiftRangeDate.shift(dcmCopy, dcmElem, argumentEntities, hmac);
      case "date_format" -> DateFormat.format(dcmCopy, dcmElem, argumentEntities);
      default -> null;
    };
  }
}
