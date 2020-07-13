package org.karnak.data;

import org.dcm4che6.data.DicomObject;
import org.karnak.profilepipe.ProfilePipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;

import java.net.URL;

public class DeidentifyEditor implements AttributeEditor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeidentifyEditor.class);

    private final ProfilePipe profilePipe;
    // private static String defaultYamlProfile = "profileChain.yml";
    private static String defaultYamlProfile = "profilePipe.yml";

    public DeidentifyEditor() {
        URL defaultProfilePipe = ProfilePipe.class.getResource(defaultYamlProfile);
        this.profilePipe = new ProfilePipe(defaultProfilePipe);
    }

    public DeidentifyEditor(URL url) {
        this.profilePipe = new ProfilePipe(url);
    }

    @Override
    public void apply(DicomObject dcm, AttributeEditorContext context) {
        profilePipe.apply(dcm);
    }
}
