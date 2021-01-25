/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.util.DateTimeUtils;
import org.karnak.backend.model.Series;
import org.karnak.backend.model.SopInstance;
import org.karnak.backend.model.Study;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;
import org.weasis.dicom.param.AttributeEditorContext.Abort;
import org.weasis.dicom.param.DicomProgress;

@Service
public class StreamRegistryService implements AttributeEditor {

  private static final Logger LOGGER = LoggerFactory.getLogger(StreamRegistryService.class);
  private final Map<String, Study> studyMap = new HashMap<>();
  private boolean enable = false;

  @Autowired
  public StreamRegistryService() {}

  private static LocalDateTime getDateTime(DicomObject dicom, int date, int time) {
    Optional<String> od = dicom.getString(date);
    Optional<String> ot = dicom.getString(time);
    if (dicom == null || od.isEmpty()) {
      return null;
    }

    return LocalDateTime.from(DateTimeUtils.parseDT(od.get() + ot.orElse("")));
  }

  @Override
  public void apply(DicomObject dcm, AttributeEditorContext context) {
    if (enable) {
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
        series.setSeriesDescription(
            dcm.getString(Tag.SeriesDescription).orElse(study.getStudyDescription()));
        LocalDateTime dateTime = getDateTime(dcm, Tag.SeriesDate, Tag.SeriesTime);
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
}
