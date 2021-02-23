package org.karnak.backend.service;

import org.dcm4che6.data.DicomObject;
import org.karnak.backend.service.report.Reporting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;

public class ReportingEditor implements AttributeEditor {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReportingEditor.class);
  private final Reporting reporting;

  public ReportingEditor() {
    this.reporting = new Reporting();
  }

  @Override
  public void apply(DicomObject dcm, AttributeEditorContext context) {
    reporting.apply(dcm);
  }
}
