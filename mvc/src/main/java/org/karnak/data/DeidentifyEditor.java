package org.karnak.data;

import org.dcm4che6.data.DicomObject;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.Project;
import org.karnak.profilepipe.Profiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;

public class DeidentifyEditor implements AttributeEditor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeidentifyEditor.class);

    private Profiles profiles;
    private Destination destination;

    public DeidentifyEditor(Destination destination) {
        this.destination = destination;
        Project project = destination.getProject();
        profiles = new Profiles(project.getProfile());
    }

    @Override
    public void apply(DicomObject dcm, AttributeEditorContext context) {
        profiles.apply(dcm, destination, context);
    }
}
