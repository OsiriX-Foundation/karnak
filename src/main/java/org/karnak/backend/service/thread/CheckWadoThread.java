package org.karnak.backend.service.thread;

import java.util.concurrent.Callable;
import org.karnak.backend.model.dicom.WadoNode;
import org.karnak.frontend.dicom.Util;

public class CheckWadoThread implements Callable<String> {

  private final WadoNode node;

  public CheckWadoThread(WadoNode node) {
    this.node = node;
  }

  @Override
  public String call() throws Exception {
    StringBuilder result = new StringBuilder();

    result.append("<h6>WADO HTTP-GET: ");
    result.append(node.toString());
    result.append("</h6>");
    result.append("<small>");
    Util.getWadoResponse(result, node, true, "HTML", 3000, 5000);
    result.append("</small>");
    result.append("<hr>");

    return result.toString();
  }
}
