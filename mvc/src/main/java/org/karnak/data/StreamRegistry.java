package org.karnak.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.json.JSONArray;
import org.karnak.api.PseudonymApi;
import org.karnak.api.rqbody.Fields;
import org.karnak.api.rqbody.SearchIds;
import org.karnak.profile.Profile;
import org.karnak.profile.action.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.core.api.media.data.TagUtil;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;
import org.weasis.dicom.param.AttributeEditorContext.Abort;
import org.weasis.dicom.param.DicomProgress;

public class StreamRegistry implements AttributeEditor {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamRegistry.class);

    private boolean enable = false;
    private final Map<String, Study> studyMap = new HashMap<>();

    @Override
    public boolean apply(Attributes attributes, AttributeEditorContext context) {
        if (enable) {
            String pseudonym = addInfoPatientToPseudonym(attributes);
            deident(attributes);
            attributes.setString(Tag.PatientID, VR.LO, pseudonym);

            String studyUID = attributes.getString(Tag.StudyInstanceUID);
            Study study = getStudy(studyUID);
            if (study == null) {
                study = new Study(studyUID, attributes.getString(Tag.PatientID));
                study.setOtherPatientIDs(attributes.getStrings(Tag.OtherPatientIDs));
                study.setAccessionNumber(attributes.getString(Tag.AccessionNumber));
                study.setStudyDescription(attributes.getString(Tag.StudyDescription, ""));
                Date date = TagUtil.dateTime(getDateFromDicomElement(attributes, Tag.StudyDate, null),
                    getDateFromDicomElement(attributes, Tag.StudyTime, null));
                study.setStudyDate(date);
                addStudy(study);
            }

            String seriesUID = attributes.getString(Tag.SeriesInstanceUID);
            Series series = study.getSeries(seriesUID);
            if (series == null) {
                series = new Series(seriesUID);
                series.setSeriesDescription(attributes.getString(Tag.SeriesDescription, study.getStudyDescription()));
                Date date = TagUtil.dateTime(getDateFromDicomElement(attributes, Tag.SeriesDate, null),
                    getDateFromDicomElement(attributes, Tag.SeriesTime, null));
                series.setSeriesDate(date == null ? study.getStudyDate() : date);
                study.addSeries(series);
            }

            String sopUID = attributes.getString(Tag.SOPInstanceUID);
            SopInstance sopInstance = series.getSopInstance(sopUID);
            if (sopInstance == null) {
                sopInstance = new SopInstance(sopUID);
                sopInstance.setSopClassUID(attributes.getString(Tag.SOPInstanceUID));
                series.addSopInstance(sopInstance);
            } else {
                context.setAbort(Abort.FILE_EXCEPTION);
                context.setAbortMessage("Duplicate transfer of " + sopUID);
            }
            // When it is a duplicate, avoid to send again a partial exam.
            study.setTimeStamp(System.currentTimeMillis());
        }
        return false;
    }

    private static Date getDateFromDicomElement(Attributes dicom, int tag, Date defaultValue) {
        if (dicom == null || !dicom.containsValue(tag)) {
            return defaultValue;
        }
        return dicom.getDate(tag, defaultValue);
    }

    public void addStudy(Study study) {
        if (study != null) {
            studyMap.put(study.getStudyInstanceUID(), study);
        }
    }

    public Study removeStudy(String studyUID) {
        return studyMap.remove(studyUID);
    }

    public Study getStudy(String studyUID) {
        return studyMap.get(studyUID);
    }

    public Set<Entry<String, Study>> getEntrySet() {
        return studyMap.entrySet();
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void update(DicomProgress progress) {
        if (enable) {
            Attributes attributes = progress.getAttributes();
            if (attributes != null) {
                String sopUID = attributes.getString(Tag.AffectedSOPInstanceUID);
                Iterator<Entry<String, Study>> studyIt = studyMap.entrySet().iterator();
                while (studyIt.hasNext()) {
                    Study study = studyIt.next().getValue();
                    Iterator<Entry<String, Series>> seriesIt = study.getEntrySet().iterator();
                    while (seriesIt.hasNext()) {
                        Series series = seriesIt.next().getValue();
                        SopInstance sopInstance = series.getSopInstance(sopUID);
                        if (sopInstance != null) {
                            sopInstance.setSent(!progress.isLastFailed());
                            return;
                        }
                    }
                }
                LOGGER.error("sopUID [{}] doesn't exist for notify the state", sopUID);
            }
        }
    }

    public String addInfoPatientToPseudonym(Attributes attributes){
        //Get patient info in Dicom File receive
        String patientID = attributes.getString(Tag.PatientID);
        String patientName = attributes.getString(Tag.PatientName);
        String patientBirthDate = attributes.getString(Tag.PatientBirthDate);
        String patientSex = attributes.getString(Tag.PatientSex);
        String issuerOfPatientID = attributes.getString(Tag.IssuerOfPatientID);

        PseudonymApi pseudonymApi = new PseudonymApi();

        Fields newPatientFields = new Fields(patientID, patientName, patientBirthDate, patientSex, issuerOfPatientID);
      
        String pseudonym = pseudonymApi.createPatient(newPatientFields);
        searchPatient(pseudonym);
        return pseudonym;
    }

    public JSONArray searchPatient(String pseudonym){
        PseudonymApi pseudonymApi = new PseudonymApi();
        SearchIds [] searchIds = {new SearchIds("elasticid", pseudonym)}; //search example
        JSONArray patientsReturns = pseudonymApi.getPatients(searchIds);
        return patientsReturns;
    }

    public void deident(Attributes attributes) {
        //store (init app)
        Profile profile = new Profile("/mvc/profiles/profile.json");
        // Profile profile = new Profile();
        
        //execute (stream registry)
        profile.execute(attributes);
    }

}
