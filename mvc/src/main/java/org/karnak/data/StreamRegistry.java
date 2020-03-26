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
            attributes = editInstance(attributes, pseudonym);

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

    public Attributes editInstance(Attributes attributes, String pseudonym){
        attributes.remove(Tag.StudyInstanceUID);
        attributes.remove(Tag.SeriesInstanceUID);
        attributes.remove(Tag.SOPInstanceUID);
        attributes.remove(Tag.PatientID);
        attributes.remove(Tag.PatientName);
        attributes.remove(Tag.PatientBirthDate);
        attributes.remove(Tag.PatientBirthTime);
        attributes.remove(Tag.PatientAge);
        attributes.remove(Tag.PatientAddress);
        attributes.remove(Tag.PatientSex);
        
        attributes.setString(Tag.StudyInstanceUID, VR.LO, "2.16.840.1.113669.632.20.1211."+new Random().nextInt(536871066));
        attributes.setString(Tag.SeriesInstanceUID, VR.LO, "1.2.276.0.7238010.5.1.3.0.1254.1347882964."+new Random().nextInt(536871066));
        attributes.setString(Tag.SOPInstanceUID, VR.LO, "1.2.276.0.7238010.5.1.4.0.1254.1347882964."+new Random().nextInt(536871066));
        attributes.setString(Tag.PatientID, VR.LO, pseudonym);
        attributes.setString(Tag.PatientName, VR.LO, "Jessica");
        attributes.setString(Tag.PatientBirthDate, VR.DA, "19930222");
        attributes.setString(Tag.PatientBirthTime, VR.TM, "070907.0705");
        attributes.setString(Tag.PatientAge, VR.LO, "026Y");
        attributes.setString(Tag.PatientAddress, VR.LO, "Rue des Pâquis 1200, Genève");
        attributes.setString(Tag.PatientSex, VR.LO, "M");
        return attributes;
    }

    public String addInfoPatientToPseudonym(Attributes attributes){
        //Get patient info in Dicom File receive
        String patientID = attributes.getString(Tag.PatientID);
        String patientName = attributes.getString(Tag.PatientName);
        String patientBirthDate = attributes.getString(Tag.PatientBirthDate);
        String patientBirthTime = attributes.getString(Tag.PatientBirthTime);
        String patientAge = attributes.getString(Tag.PatientAge);
        String patientSex = attributes.getString(Tag.PatientSex);
        String patientAddress = attributes.getString(Tag.PatientAddress);

        PseudonymApi pseudonymApi = new PseudonymApi();

        Fields newPatientFields = new Fields(patientID, patientName, patientBirthDate, patientBirthTime, patientAge, patientSex, patientAddress);
      
        String pseudonym = pseudonymApi.createPatient(newPatientFields);
        searchPatient(pseudonym);
        return pseudonym;
    }

    public JSONArray searchPatient(String pseudonym){
        PseudonymApi pseudonymApi = new PseudonymApi();
        SearchIds [] searchIds = {new SearchIds("pid", pseudonym)}; //search example
        JSONArray patientsReturns = pseudonymApi.getPatients(searchIds);
        return patientsReturns;
    }

}
