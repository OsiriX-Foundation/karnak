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

import java.awt.Color;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.VR;
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

public class ActionTags extends AbstractProfileItem {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionTags.class);

  private final TagActionMap tagsAction;
  private final TagActionMap exceptedTagsAction;
  private final ActionItem actionByDefault;

  public ActionTags(ProfileElementEntity profileElementEntity) throws Exception {
    super(profileElementEntity);
    tagsAction = new TagActionMap();
    exceptedTagsAction = new TagActionMap();
    actionByDefault = AbstractAction.convertAction(this.action);
    profileValidation();
    setActionHashMap();
  }

  public static String color2Hexadecimal(Color c, boolean alpha) {
    int val = c == null ? 0 : alpha ? c.getRGB() : c.getRGB() & 0x00ffffff;
    return Integer.toHexString(val);
  }

  public static Color hexadecimal2Color(String hexColor) {
    int intValue = 0xff000000;

    try {
      if (hexColor != null && hexColor.length() > 6) {
        intValue = (int) (Long.parseLong(hexColor, 16) & 0xffffffff);
      } else {
        intValue |= Integer.parseInt(hexColor, 16);
      }
    } catch (NumberFormatException e) {
      LOGGER.error("Cannot parse color {} into int", hexColor); // $NON-NLS-1$
    }
    return new Color(intValue, true);
  }

  private void setActionHashMap() throws Exception {
    for (IncludedTagEntity tag : tagEntities) {
      tagsAction.put(tag.getTagValue(), actionByDefault);
    }
    if (excludedTagEntities != null) {
      for (ExcludedTagEntity tag : excludedTagEntities) {
        exceptedTagsAction.put(tag.getTagValue(), actionByDefault);
      }
    }
  }

  @Override
  public ActionItem getAction(Attributes dcm, Attributes dcmCopy, int tag, HMAC hmac) {
    if (exceptedTagsAction.get(tag) == null) {
      return tagsAction.get(tag);
    }
    return null;
  }

  @Override
  public void profileValidation() throws Exception {
    if (action == null && (tagEntities == null || tagEntities.size() <= 0)) {
      throw new Exception(
          "Cannot build the profile " + codeName + ": Unknown Action and no tags defined");
    }

    if (action == null) {
      throw new Exception("Cannot build the profile " + codeName + ": Unknown Action");
    }

    if (tagEntities == null || tagEntities.size() <= 0) {
      throw new Exception("Cannot build the profile " + codeName + ": No tags defined");
    }

    final ExpressionError expressionError =
        ExpressionResult.isValid(condition, new ExprConditionDestination(1, VR.AE), Boolean.class);
    if (condition != null && !expressionError.isValid()) {
      throw new Exception(expressionError.getMsg());
    }
  }
}
