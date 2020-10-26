package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.data.profile.ProfileElement;
import org.karnak.profilepipe.action.ActionItem;
import org.karnak.profilepipe.utils.HMAC;

public class CleanPixelData extends AbstractProfileItem {

    public CleanPixelData(ProfileElement profileElement) throws Exception{
        super(profileElement);
    }

    @Override
    public ActionItem getAction(DicomObject dcm, DicomObject dcmCopy, DicomElement dcmElem, HMAC hmac) {
        // Action handles in the DICOM content not in metadata.
        return null;
    }
}
