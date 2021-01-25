/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
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
import org.dcm4che6.util.TagUtils;
import org.karnak.backend.data.entity.ExcludedTagEntity;
import org.karnak.backend.data.entity.IncludedTagEntity;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.model.action.AbstractAction;
import org.karnak.backend.model.action.ActionItem;
import org.karnak.backend.model.expression.ExprConditionDestination;
import org.karnak.backend.model.expression.ExpressionError;
import org.karnak.backend.model.expression.ExpressionResult;
import org.karnak.backend.model.profilepipe.HMAC;
import org.karnak.backend.model.profilepipe.TagActionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivateTags extends AbstractProfileItem {

  private static final Logger LOGGER = LoggerFactory.getLogger(PrivateTags.class);

  private final TagActionMap tagsAction;
  private final TagActionMap exceptedTagsAction;
  private final ActionItem actionByDefault;

  public PrivateTags(ProfileElementEntity profileElementEntity) throws Exception {
    super(profileElementEntity);
    tagsAction = new TagActionMap();
    exceptedTagsAction = new TagActionMap();
    actionByDefault = AbstractAction.convertAction(this.action);
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
  public ActionItem getAction(
      DicomObject dcm, DicomObject dcmCopy, DicomElement dcmElem, HMAC hmac) {
    final int tag = dcmElem.tag();
    if (TagUtils.isPrivateGroup(tag)) {
      if (tagsAction.isEmpty() == false && exceptedTagsAction.isEmpty()) {
        return tagsAction.get(tag);
      }

      if (tagsAction.isEmpty() && exceptedTagsAction.isEmpty() == false) {
        if (exceptedTagsAction.get(tag) != null) {
          return null;
        }
      }

      if (tagsAction.isEmpty() == false && exceptedTagsAction.isEmpty() == false) {
        if (exceptedTagsAction.get(dcmElem.tag()) == null) {
          return tagsAction.get(dcmElem.tag());
        }
        return null;
      }
      return actionByDefault;
    }
    return null;
  }

  public void profileValidation() throws Exception {
    if (action == null) {
      throw new Exception("Cannot build the profile " + codeName + ": Unknown Action");
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
}
