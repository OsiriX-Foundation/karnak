package org.karnak.data;

import org.weasis.dicom.param.DicomNode;

public class SourceNode {
    private final String forwardAETitle;
    private final DicomNode sourceNode;

    public SourceNode(String forwardAETitle, DicomNode sourceNode) {
        this.forwardAETitle = forwardAETitle;
        this.sourceNode = sourceNode;
    }

    public String getForwardAETitle() {
        return forwardAETitle;
    }

    public DicomNode getSourceNode() {
        return sourceNode;
    }

    @Override
    public int hashCode() {
        return 31 + forwardAETitle.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SourceNode other = (SourceNode) obj;
        return forwardAETitle.equals(other.forwardAETitle);
    }
}
