package org.karnak.data;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.karnak.data.gateway.SOPClassUID;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;

import java.util.List;
import java.util.function.Predicate;

public class FilterEditor  implements AttributeEditor {
    private List<SOPClassUID> sopClassUIDSet;
    public FilterEditor(List<SOPClassUID> sopClassUIDSet) {
        this.sopClassUIDSet = sopClassUIDSet;
    }

    @Override
    public void apply(DicomObject dcm, AttributeEditorContext context) {
        final String classUID = dcm.getString(Tag.SOPClassUID).orElse(null);
        final Predicate<SOPClassUID> sopClassUIDPredicate = sopClassUID -> classUID.equals(sopClassUID.getUid());
        boolean sopClassUIDisPresent = this.sopClassUIDSet.stream().anyMatch(sopClassUIDPredicate);
        if(sopClassUIDisPresent==false){
            throw new IllegalStateException("SOPClassUID is not in a filter");
        }
    }
}
