/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.editor;

import java.util.Set;
import java.util.function.Predicate;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.karnak.backend.data.entity.SOPClassUIDEntity;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;
import org.weasis.dicom.param.AttributeEditorContext.Abort;

public class FilterEditor implements AttributeEditor {

  private Set<SOPClassUIDEntity> sopClassUIDEntitySet;

  public FilterEditor(Set<SOPClassUIDEntity> sopClassUIDEntitySet) {
    this.sopClassUIDEntitySet = sopClassUIDEntitySet;
  }

  @Override
  public void apply(DicomObject dcm, AttributeEditorContext context) {
    String classUID = dcm.getString(Tag.SOPClassUID).orElse(null);
    Predicate<SOPClassUIDEntity> sopClassUIDPredicate =
        sopClassUID -> sopClassUID.getUid().equals(classUID);
    if (!sopClassUIDEntitySet.stream().anyMatch(sopClassUIDPredicate)) {
      context.setAbort(Abort.FILE_EXCEPTION);
      context.setAbortMessage(
          dcm.getString(Tag.SOPInstanceUID).orElse(null)
              + " is blocked because "
              + classUID
              + " is not in the SOPClassUID filter");
    }
  }
}
