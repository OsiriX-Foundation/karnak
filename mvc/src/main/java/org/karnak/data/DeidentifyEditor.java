package org.karnak.data;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.util.TagUtils;
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
    // private static String defaultYamlProfile = "profileChain.yml";
    private static String defaultYamlProfile = "profilePipe.yml";

    public DeidentifyEditor() {
        URL defaultProfileChain = ProfileChain.class.getResource(defaultYamlProfile);
        this.profileChain = new ProfileChain(defaultProfileChain);
    }

    public DeidentifyEditor(URL url) {
        this.profileChain = new ProfileChain(url);
    }

    @Override
    public void apply(DicomObject dcm, AttributeEditorContext context) {
        profileChain.apply(dcm, context);
    }
}
