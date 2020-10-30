package org.karnak.kheops;

public class MetadataSwitching {
    private final String studyInstanceUID;
    private final String seriesInstanceUID;
    private final String SOPinstanceUID;
    private boolean applied;

    public MetadataSwitching(String studyInstanceUID, String seriesInstanceUID, String SOPinstanceUID) {
        this.studyInstanceUID = studyInstanceUID;
        this.seriesInstanceUID = seriesInstanceUID;
        this.SOPinstanceUID = SOPinstanceUID;
        this.applied = false;
    }

    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }

    public String getSeriesInstanceUID() {
        return seriesInstanceUID;
    }

    public String getSOPinstanceUID() {
        return SOPinstanceUID;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }
}
