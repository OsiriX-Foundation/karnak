package org.karnak.frontend.dicom.mwl;

import java.time.LocalDate;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.net.Status;
import org.dcm4che6.util.DateTimeUtils;
import org.karnak.backend.enums.MessageFormat;
import org.karnak.backend.enums.MessageLevel;
import org.karnak.backend.enums.Modality;
import org.karnak.backend.model.dicom.Message;
import org.karnak.backend.model.dicom.WorkListQueryData;
import org.weasis.dicom.op.CFind;
import org.weasis.dicom.param.DicomNode;
import org.weasis.dicom.param.DicomParam;
import org.weasis.dicom.param.DicomState;
import org.weasis.dicom.tool.ModalityWorklist;

public class DicomWorkListLogic {

  // PAGE
  private final DicomWorkListView view;

  public DicomWorkListLogic(DicomWorkListView view) {
    this.view = view;
  }

  public void query(WorkListQueryData queryData) {

    String modalityStr = null;
    Modality modality = queryData.getScheduledModality();
    if (modality != Modality.ALL) {
      modalityStr = modality.name();
    }

    int[] sps = {Tag.ScheduledProcedureStepSequence};

    DicomParam[] RETURN_KEYS = {
        new DicomParam(Tag.AccessionNumber, queryData.getAccessionNumber()),
        CFind.IssuerOfAccessionNumberSequence,
        CFind.ReferringPhysicianName,
        new DicomParam(Tag.PatientName, queryData.getPatientName()),
        new DicomParam(Tag.PatientID, queryData.getPatientId()),
        CFind.IssuerOfPatientID,
        CFind.PatientBirthDate,
        CFind.PatientSex,
        ModalityWorklist.PatientWeight,
        ModalityWorklist.MedicalAlerts,
        ModalityWorklist.Allergies,
        ModalityWorklist.PregnancyStatus,
        CFind.StudyInstanceUID,
        ModalityWorklist.RequestingPhysician,
        ModalityWorklist.RequestingService,
        ModalityWorklist.RequestedProcedureDescription,
        ModalityWorklist.RequestedProcedureCodeSequence,
        new DicomParam(Tag.AdmissionID, queryData.getAdmissionId()),
        ModalityWorklist.IssuerOfAdmissionIDSequence,
        ModalityWorklist.SpecialNeeds,
        ModalityWorklist.CurrentPatientLocation,
        ModalityWorklist.PatientState,
        ModalityWorklist.RequestedProcedureID,
        ModalityWorklist.RequestedProcedurePriority,
        ModalityWorklist.PatientTransportArrangements,
        ModalityWorklist.PlacerOrderNumberImagingServiceRequest,
        ModalityWorklist.FillerOrderNumberImagingServiceRequest,
        ModalityWorklist.ConfidentialityConstraintOnPatientDataDescription,
        // Scheduled Procedure Step Sequence
        new DicomParam(sps, Tag.Modality, modalityStr),
        ModalityWorklist.RequestedContrastAgent,
        new DicomParam(sps, Tag.ScheduledStationAETitle, queryData.getScheduledStationAet()),
        new DicomParam(
            sps, Tag.ScheduledProcedureStepStartDate, getDate(queryData.getScheduledFrom())),
        new DicomParam(sps, Tag.ScheduledProcedureStepEndDate, getDate(queryData.getScheduledTo())),
        ModalityWorklist.ScheduledPerformingPhysicianName,
        ModalityWorklist.ScheduledProcedureStepDescription,
        ModalityWorklist.ScheduledProcedureStepID,
        ModalityWorklist.ScheduledStationName,
        ModalityWorklist.ScheduledProcedureStepLocation,
        ModalityWorklist.PreMedication,
        ModalityWorklist.ScheduledProcedureStepStatus,
        ModalityWorklist.ScheduledProtocolCodeSequence
    };

    DicomNode workListNode =
        new DicomNode(
            queryData.getWorkListAet(),
            queryData.getWorkListHostname(),
            queryData.getWorkListPort());

    DicomState state =
        ModalityWorklist.process(
            null, new DicomNode(queryData.getCallingAet()), workListNode, 0, RETURN_KEYS);

    view.loadAttributes(state.getDicomRSP());

    if (state != null && state.getStatus().orElse(Status.Pending) != Status.Success) {
      String errorMsg =
          "Cannot get a worklist! DICOM error status: "
              + Integer.toHexString(state.getStatus().orElse(Status.Pending));
      Message message = new Message(MessageLevel.ERROR, MessageFormat.TEXT, errorMsg);
      view.displayMessage(message);
    }
  }

  public void itemSelected(DicomObject attributes) {
    view.openDicomPane(attributes);
  }

  private String getDate(LocalDate date) {
    return date == null ? null : DateTimeUtils.formatDA(date);
  }
}