package org.karnak.profilepipe.utils;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.TagUtils;

public class MetadataDICOMObject {
    /*
     * Search a tagValue in the current DicomObject and his parent
     * Will loop in the parent of the DicomObject until the last parent or the tagValue
     * */
    public static String getValue(DicomObject dcm, int tag) {
        return getValueRec(dcm, tag);
    }

    private static String getValueRec(DicomObject dcm, int tag) {
        String tagValue = dcm.getString(tag).orElse(null);
        DicomObject dcmParent = dcm.getParent().orElse(null);
        if (dcmParent != null && tagValue == null) {
            return getValueRec(dcmParent, tag);
        }
        return tagValue;
    }

    /*
    * Generate the tag Path as needed in the class StandardDICOM
    * Will loop in the parent of the DicomObject until the last parent
    * */
    public static String getTagPath(DicomObject dcm, int currentTag) {
        return getTagPathRec(dcm, TagUtils.toString(currentTag));
    }

    private static String getTagPathRec(DicomObject dcm, String tagPath) {
        DicomObject dcmParent = dcm.getParent().orElse(null);
        DicomElement dcmElemParent = dcm.containedBy().orElse(null);
        if (dcmElemParent != null) {
            return getTagPathRec(dcmParent, String.format("%s:%s", TagUtils.toHexString(dcmElemParent.tag()), tagPath));
        }
        return tagPath;
    }
}
