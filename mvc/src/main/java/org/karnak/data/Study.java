package org.karnak.data;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

public class Study {

    private final String studyInstanceUID;
    private final Map<String, Series> seriesMap;
    private String patientID;
    private String[] otherPatientIDs;
    private String studyDescription;
    private String accessionNumber;
    private Date studyDate;
    private long timeStamp;

    public Study(String studyInstanceUID, String patientID) {
        this.studyInstanceUID = Objects.requireNonNull(studyInstanceUID, "studyInstanceUID cannot be null!");
        this.patientID = patientID == null ? "" : patientID;
        this.studyDescription = "";
        this.seriesMap = new HashMap<>();
    }

    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getStudyDescription() {
        return studyDescription;
    }

    public Date getStudyDate() {
        return studyDate;
    }

    public void setStudyDate(Date studyDate) {
        this.studyDate = studyDate;
    }

    public void setStudyDescription(String studyDesc) {
        this.studyDescription = studyDesc;
    }

    public void addSeries(Series s) {
        if (s != null) {
            seriesMap.put(s.getSeriesInstanceUID(), s);
        }
    }

    public Series removeSeries(String seriesUID) {
        return seriesMap.remove(seriesUID);
    }

    public boolean isEmpty() {
        for (Series s : seriesMap.values()) {
            if (!s.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Series getSeries(String seriesUID) {
        return seriesMap.get(seriesUID);
    }

    public Collection<Series> getSeries() {
        return seriesMap.values();
    }

    public Set<Entry<String, Series>> getEntrySet() {
        return seriesMap.entrySet();
    }

    @Override
    public int hashCode() {
        return 31 + studyInstanceUID.hashCode();
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
        Study other = (Study) obj;
        return studyInstanceUID.equals(other.studyInstanceUID);
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String[] getOtherPatientIDs() {
        return otherPatientIDs;
    }

    public void setOtherPatientIDs(String[] otherPatientIDs) {
        this.otherPatientIDs = otherPatientIDs;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

}
