/*
* Copyright (c) 2021 Weasis Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.backend.enums;

import org.karnak.backend.model.profiles.ActionDates;
import org.karnak.backend.model.profiles.ActionTags;
import org.karnak.backend.model.profiles.BasicProfile;
import org.karnak.backend.model.profiles.CleanPixelData;
import org.karnak.backend.model.profiles.Expression;
import org.karnak.backend.model.profiles.PrivateTags;
import org.karnak.backend.model.profiles.ProfileItem;
import org.karnak.backend.model.profiles.UpdateUIDsProfile;

public enum ProfileItemType {
  BASIC_DICOM(BasicProfile.class, "basic.dicom.profile"),
  CLEAN_PIXEL_DATA(CleanPixelData.class, "clean.pixel.data"),
  REPLACE_UID(UpdateUIDsProfile.class, "replace.uid"),
  ACTION_TAGS(ActionTags.class, "action.on.specific.tags"),
  ACTION_PRIVATETAGS(PrivateTags.class, "action.on.privatetags"),
  ACTION_DATES(ActionDates.class, "action.on.dates"),
  EXPRESSION_TAGS(Expression.class, "expression.on.tags");

  private final Class<? extends ProfileItem> profileClass;
  private final String classAlias;

  ProfileItemType(Class<? extends ProfileItem> profileClass, String alias) {
    this.profileClass = profileClass;
    this.classAlias = alias;
  }

  public static ProfileItemType getType(String alias) {
    for (ProfileItemType t : ProfileItemType.values()) {
      if (t.classAlias.equals(alias)) {
        return t;
      }
    }
    return null;
  }

  public Class<? extends ProfileItem> getProfileClass() {
    return profileClass;
  }

  public String getClassAlias() {
    return classAlias;
  }
}
