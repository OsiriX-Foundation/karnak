package org.karnak.data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.util.DateTimeUtils;
import org.karnak.api.PseudonymApi;
import org.karnak.api.rqbody.Fields;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.profiles.ProfileChain;
import org.karnak.profileschain.utils.CreateProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;
import org.weasis.dicom.param.AttributeEditorContext.Abort;
import org.weasis.dicom.param.DicomProgress;

public class StreamRegistry implements AttributeEditor {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamRegistry.class);

    private boolean enable = false;
    private final Map<String, Study> studyMap = new HashMap<>();

    @Override
    public boolean apply(DicomObject dcm, AttributeEditorContext context) {
        if (enable) {
            deident(dcm);

            String studyUID = dcm.getString(Tag.StudyInstanceUID).orElseThrow();
            Study study = getStudy(studyUID);
            if (study == null) {
                study = new Study(studyUID, dcm.getString(Tag.PatientID).orElse(null));
                study.setOtherPatientIDs(dcm.getStrings(Tag.OtherPatientIDs).orElse(null));
                study.setAccessionNumber(dcm.getString(Tag.AccessionNumber).orElse(null));
                study.setStudyDescription(dcm.getString(Tag.StudyDescription).orElse(""));
                study.setStudyDate(getDateTime(dcm, Tag.StudyDate, Tag.StudyTime));
                addStudy(study);
            }

            String seriesUID = dcm.getString(Tag.SeriesInstanceUID).orElseThrow();
            Series series = study.getSeries(seriesUID);
            if (series == null) {
                series = new Series(seriesUID);
                series.setSeriesDescription(dcm.getString(Tag.SeriesDescription).orElse(study.getStudyDescription()));
                LocalDateTime dateTime = getDateTime(dcm, Tag.SeriesDate,Tag.SeriesTime);
                series.setSeriesDate(dateTime == null ? study.getStudyDate() : dateTime);
                study.addSeries(series);
            }

            String sopUID = dcm.getString(Tag.SOPInstanceUID).orElseThrow();
            SopInstance sopInstance = series.getSopInstance(sopUID);
            if (sopInstance == null) {
                sopInstance = new SopInstance(sopUID);
                sopInstance.setSopClassUID(dcm.getString(Tag.SOPClassUID).orElse(null));
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

    private static LocalDateTime getDateTime(DicomObject dicom, int date, int time) {
        Optional<String> od = dicom.getString(date);
        Optional<String> ot = dicom.getString(time);
        if (dicom == null || od.isEmpty()) {
            return null;
        }
        
        return LocalDateTime.from(DateTimeUtils.parseDT(od.get() + ot.orElse("")));
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
            DicomObject dcm = progress.getAttributes();
            if (dcm != null) {
                String sopUID = dcm.getString(Tag.AffectedSOPInstanceUID).orElse(null);
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

    public String addInfoPatientToPseudonym(DicomObject dcm){
        //Get patient info in Dicom File receive
        String patientID = dcm.getString(Tag.PatientID).orElse(null);
        String patientName = dcm.getString(Tag.PatientName).orElse(null);
        String patientBirthDate = dcm.getString(Tag.PatientBirthDate).orElse(null);
        String patientSex = dcm.getString(Tag.PatientSex).orElse(null);
        String issuerOfPatientID = dcm.getString(Tag.IssuerOfPatientID).orElse(null);

        PseudonymApi pseudonymApi = new PseudonymApi();

        Fields newPatientFields = new Fields(patientID, patientName, patientBirthDate, patientSex, issuerOfPatientID);
      
        String pseudonym = pseudonymApi.createPatient(newPatientFields);
        pseudonymApi.searchPatient(pseudonym);
        return pseudonym;
    }

    public void deident(DicomObject dcm) {
        final String pseudonym = addInfoPatientToPseudonym(dcm);
        final String profileFilename = "profileChain.yml";
        try {
            CreateProfile createProfile = new CreateProfile(profileFilename);
            ProfileChain standardProfile = createProfile.getProfile();
            for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext();) {
                final DicomElement dcmEl = iterator.next();
                Action action = standardProfile.getAction(dcmEl);
                System.out.println(dcmEl.tag()+" "+action.getStrAction());
            }
        } catch (final Exception e) {
            LOGGER.error("Cannot execute actions", e);
        }
        // dcm.setString(Tag.ClinicalTrialSponsorName, VR.LO, profileFilename);
        // dcm.setString(Tag.PatientID, VR.LO, profileFilename + pseudonym);
        // dcm.setString(Tag.ClinicalTrialProtocolID, VR.LO, hash(profileFilename));
        // dcm.setString(Tag.ClinicalTrialSubjectID, VR.LO, pseudonym);
    }

}
