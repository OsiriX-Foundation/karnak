package org.karnak.backend.model.expression;

public class ExpressionError {

  private boolean isValid;
  private String msg;

  public ExpressionError(boolean isValid, String msg) {
    this.isValid = isValid;
    this.msg = msg;
  }

  public boolean isValid() {
    return isValid;
  }

  public void setValid(boolean valid) {
    isValid = valid;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
