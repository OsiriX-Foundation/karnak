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
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.model.action.ActionItem;
import org.karnak.backend.model.action.Remove;
import org.karnak.backend.model.action.ReplaceNull;
import org.karnak.backend.model.action.UID;
import org.karnak.backend.model.profilepipe.HMAC;

public class UpdateUIDsProfile extends AbstractProfileItem {

  public UpdateUIDsProfile(ProfileElementEntity profileElementEntity) {
    super(profileElementEntity);
    /*
    if (not BlackList) {
        throw new IllegalStateException(String.format("The policy %s is not consistent with the profile %s!", policy, codeName));
    }
    */
  }

  @Override
  public ActionItem getAction(
      DicomObject dcm, DicomObject dcmCopy, DicomElement dcmElem, HMAC hmac) {
    ActionItem val = tagMap.get(dcmElem.tag());
    return val;
  }

  @Override
  public ActionItem put(int tag, ActionItem action) {
    if (!(action instanceof UID)
        && !(action instanceof Remove)
        && !(action instanceof ReplaceNull)) {
      throw new IllegalStateException(String.format("The action %s is not consistent !", action));
    }
    return tagMap.put(tag, action);
  }
}
