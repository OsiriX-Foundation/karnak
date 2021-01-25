/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.action;

import java.util.Iterator;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.TagUtils;
import org.karnak.backend.model.profilepipe.HMAC;
import org.slf4j.MDC;

public class Remove extends AbstractAction {

  public Remove(String symbol) {
    super(symbol);
  }

  @Override
  public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, HMAC hmac) {
    String tagValueIn = dcm.getString(tag).orElse(null);
    iterator.remove();
    LOGGER.trace(
        CLINICAL_MARKER,
        PATTERN_WITH_IN,
        MDC.get("SOPInstanceUID"),
        TagUtils.toString(tag),
        symbol,
        tagValueIn);
  }
}
