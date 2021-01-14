package org.karnak.backend.service.dicom;

import java.util.ArrayList;
import java.util.List;
import org.karnak.backend.model.dicom.DicomNodeList;
import org.karnak.frontend.dicom.Util;

public class DicomNodeManager {

  private DicomNodeManager() {
  }

  public static List<DicomNodeList> getAllDicomNodeTypesDefinedLocally() {
    List<DicomNodeList> dicomNodeTypes = new ArrayList<>();

    dicomNodeTypes.add(
        Util.readnodes(
            DicomNodeManager.class.getResource("/config/workstations-nodes.csv"), "Workstations"));
    dicomNodeTypes.add(
        Util.readnodes(
            DicomNodeManager.class.getResource("/config/pacs-nodes-web.csv"), "PACS Public WEB"));

    return dicomNodeTypes;
  }

  public static DicomNodeList getAllWorkListNodesDefinedLocally() {
    return Util.readnodes(
        DicomNodeManager.class.getResource("/config/worklist-nodes.csv"), "Worklists");
  }
}
