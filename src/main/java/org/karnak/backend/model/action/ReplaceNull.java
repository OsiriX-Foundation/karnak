package org.karnak.backend.model.action;

import java.util.Iterator;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.TagUtils;
import org.karnak.backend.model.profilepipe.HMAC;
import org.slf4j.MDC;

public class ReplaceNull extends AbstractAction {

  public ReplaceNull(String symbol) {
    super(symbol);
  }

  @Override
  public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, HMAC hmac) {
    String tagValueIn = dcm.getString(tag).orElse(null);

    dcm.get(tag)
        .ifPresent(
            dcmEl -> {
              dcm.setNull(tag, dcmEl.vr());
            });
    LOGGER.trace(
        CLINICAL_MARKER,
        PATTERN_WITH_INOUT,
        MDC.get("SOPInstanceUID"),
        TagUtils.toString(tag),
        symbol,
        tagValueIn,
        null);
  }
}
