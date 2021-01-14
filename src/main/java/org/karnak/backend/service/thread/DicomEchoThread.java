package org.karnak.backend.service.thread;

import java.util.concurrent.Callable;
import org.karnak.backend.model.dicom.ConfigNode;
import org.karnak.frontend.dicom.Util;
import org.weasis.dicom.param.DicomNode;

public class DicomEchoThread implements Callable<String> {

  private final ConfigNode node;

  public DicomEchoThread(ConfigNode node) {
    this.node = node;
  }

  @Override
  public String call() throws Exception {
    StringBuilder result = new StringBuilder();

    result.append("<P>");
    DicomNode dcmNode = node.getCalledNode();
    result.append("<h6>DICOM Echo: ");
    result.append(node.toString());
    result.append("</h6>");
    result.append(dcmNode.toString());
    result.append("<br>");
    result.append("<small>");
    boolean success = Util.getEchoResponse(result, "PACSMONITOR", dcmNode, true, "HTML", 3000);
    if (!success) {
      Util.getNetworkResponse(
          result, dcmNode.getAet(), dcmNode.getHostname(), dcmNode.getPort(), true);
    }
    result.append("</small>");
    result.append("</P>");
    result.append("<hr>");

    return result.toString();
  }
}
