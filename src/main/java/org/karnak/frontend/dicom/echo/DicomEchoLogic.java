package org.karnak.frontend.dicom.echo;

import org.karnak.backend.model.dicom.DicomEchoQueryData;
import org.karnak.frontend.dicom.Util;
import org.weasis.dicom.param.DicomNode;

public class DicomEchoLogic {

  // PAGE
  private final DicomEchoView view;


    public DicomEchoLogic(DicomEchoView view) {
        this.view = view;
    }


    public void dicomEcho(DicomEchoQueryData data) {
        String aet = data.getCalledAet();
        String hostname = data.getCalledHostname();
        int port = data.getCalledPort();

        StringBuilder result = new StringBuilder();
        result.append("<P><h6>Network status</h6>");
        boolean reachable = Util.getNetworkResponse(result, aet, hostname, port, true);
        result.append("</P>");
        if (reachable) {
            result.append("<br><P>");
            DicomNode dcmNode = new DicomNode(data.getCalledAet(), data.getCalledHostname(), data.getCalledPort());
            result.append("<h6>DICOM Echo: ");
            result.append(dcmNode.getAet());
            result.append("</h6>");
            Util.getEchoResponse(result, data.getCallingAet(), dcmNode, true, "HTML");
            result.append("</P>");
        }

        view.displayStatus(result.toString());
    }

}
