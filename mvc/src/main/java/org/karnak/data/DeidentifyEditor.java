package org.karnak.data;

import org.dcm4che6.data.DicomObject;
import org.karnak.profilepipe.Profiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;

import java.net.URL;

public class DeidentifyEditor implements AttributeEditor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeidentifyEditor.class);

    private Profiles profiles;

    // private static String defaultYamlProfile = "profileChain.yml";
    private static String defaultYamlProfile = "profilePipe.yml";

    public DeidentifyEditor(org.karnak.data.profile.ProfilePipe profilePipe) {
        profiles = new Profiles(profilePipe);
    }

    @Override
    public void apply(DicomObject dcm, AttributeEditorContext context) {
        profiles.apply(dcm);
    }
}
