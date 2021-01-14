package org.karnak.backend.model.action;

import java.util.Iterator;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.karnak.backend.model.profilepipe.HMAC;
import org.slf4j.MDC;

public class UID extends AbstractAction {

  public UID(String symbol) {
    super(symbol);
  }

  @Override
  public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, HMAC hmac) {
    String uidValue = dcm.getString(tag).orElse(null);
    String uidHashed = null;
    if (uidValue != null) {
      uidHashed = hmac.uidHash(uidValue);
      dcm.setString(tag, VR.UI, uidHashed);
    }
    LOGGER.trace(
        CLINICAL_MARKER,
        PATTERN_WITH_INOUT,
        MDC.get("SOPInstanceUID"),
        TagUtils.toString(tag),
        symbol,
        uidValue,
        uidHashed);
  }
}