package org.karnak.backend.service;

import java.util.Set;
import java.util.function.Predicate;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.karnak.backend.data.entity.SOPClassUIDEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;
import org.weasis.dicom.param.AttributeEditorContext.Abort;

@Service
public class FilterEditorService implements AttributeEditor {

  private Set<SOPClassUIDEntity> sopClassUIDEntitySet;

  @Autowired
  public FilterEditorService() {
  }

  public void init(Set<SOPClassUIDEntity> sopClassUIDEntitySet) {
    this.sopClassUIDEntitySet = sopClassUIDEntitySet;
  }

  @Override
  public void apply(DicomObject dcm, AttributeEditorContext context) {
    String classUID = dcm.getString(Tag.SOPClassUID).orElse(null);
    Predicate<SOPClassUIDEntity> sopClassUIDPredicate = sopClassUID -> sopClassUID.getUid()
        .equals(classUID);
    if (!sopClassUIDEntitySet.stream().anyMatch(sopClassUIDPredicate)) {
      context.setAbort(Abort.FILE_EXCEPTION);
      context.setAbortMessage(
          dcm.getString(Tag.SOPInstanceUID).orElse(null) + " is blocked because " + classUID
              + " is not in the SOPClassUID filter");
    }
  }
}
