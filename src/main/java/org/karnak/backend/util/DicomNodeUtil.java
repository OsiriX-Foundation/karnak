package org.karnak.backend.util;

import java.util.ArrayList;
import java.util.List;
import org.karnak.backend.model.dicom.DicomNodeList;
import org.karnak.frontend.dicom.Util;

public class DicomNodeUtil {

  public static List<DicomNodeList> getAllDicomNodeTypesDefinedLocally() {
    List<DicomNodeList> dicomNodeTypes = new ArrayList<>();

    dicomNodeTypes
        .add(Util.readnodes(DicomNodeUtil.class.getResource("/config/workstations-nodes.csv"),
            "Workstations"));
    dicomNodeTypes
        .add(Util.readnodes(DicomNodeUtil.class.getResource("/config/pacs-nodes-web.csv"),
            "PACS Public WEB"));

    return dicomNodeTypes;
    }

    public static DicomNodeList getAllWorkListNodesDefinedLocally() {
      return Util
          .readnodes(DicomNodeUtil.class.getResource("/config/worklist-nodes.csv"), "Worklists");
    }

}
