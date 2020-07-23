package org.karnak.data;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.karnak.data.gateway.SOPClassUID;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;

import java.util.Set;
import java.util.function.Predicate;
import org.weasis.dicom.param.AttributeEditorContext.Abort;

public class FilterEditor  implements AttributeEditor {
    private Set<SOPClassUID> sopClassUIDSet;
    public FilterEditor(Set<SOPClassUID> sopClassUIDSet) {
        this.sopClassUIDSet = sopClassUIDSet;
    }

    @Override
    public void apply(DicomObject dcm, AttributeEditorContext context) {
        String classUID = dcm.getString(Tag.SOPClassUID).orElse(null);
        Predicate<SOPClassUID> sopClassUIDPredicate = sopClassUID -> sopClassUID.getUid().equals(classUID);
        if (!sopClassUIDSet.stream().anyMatch(sopClassUIDPredicate)) {
            context.setAbort(Abort.FILE_EXCEPTION);
            context.setAbortMessage(classUID + " is not in the SOPClassUID filter");
        }
    }
}
