package org.karnak.backend.model.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.model.action.ActionItem;
import org.karnak.backend.model.profilepipe.HMAC;

public class CleanPixelData extends AbstractProfileItem {

  public CleanPixelData(ProfileElementEntity profileElementEntity) throws Exception {
    super(profileElementEntity);
  }

  @Override
  public ActionItem getAction(DicomObject dcm, DicomObject dcmCopy, DicomElement dcmElem,
      HMAC hmac) {
    // Action handles in the DICOM content not in metadata.
    return null;
  }
}
