package org.karnak.data;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.karnak.profileschain.ProfileChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;

import java.net.URL;

import static org.weasis.dicom.op.ValidationResult.Invalid.VR;

public class DeidentifyEditor implements AttributeEditor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeidentifyEditor.class);

    private final ProfileChain profileChain;
    private boolean enable;

    public DeidentifyEditor() {
        this(ProfileChain.class.getResource("profileChain.yml"));
    }

    public DeidentifyEditor(URL url) {
        this.profileChain = new ProfileChain(url);
        this.enable = !profileChain.getSortedSet().isEmpty();
    }

    @Override
    public void apply(DicomObject dcm, AttributeEditorContext context) {
        if (enable) {
            profileChain.apply(dcm);
        }
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }


}
