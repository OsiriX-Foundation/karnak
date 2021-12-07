package org.karnak.backend.exception;

import org.weasis.dicom.param.AttributeEditorContext.Abort;

public final class AbortException extends IllegalStateException {

  private static final long serialVersionUID = 3993065212756372490L;
  private final Abort abort;

  public AbortException(Abort abort, String s) {
    super(s);
    this.abort = abort;
  }

  public AbortException(Abort abort, String string, Exception e) {
    super(string, e);
    this.abort = abort;
  }

  @Override
  public String toString() {
    return getMessage();
  }

  public Abort getAbort() {
    return abort;
  }
}
