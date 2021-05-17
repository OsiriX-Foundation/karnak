package org.karnak.backend.model.editor;

import org.dcm4che3.data.Attributes;
import org.karnak.backend.model.expression.ExprConditionDestination;
import org.karnak.backend.model.expression.ExpressionResult;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;
import org.weasis.dicom.param.AttributeEditorContext.Abort;

public class ConditionEditor implements AttributeEditor {
  private String condition;

  public ConditionEditor(String condition) {
     this.condition = condition;
  }

  private static boolean validateCondition(String condition, Attributes dcm) {
    return (Boolean) ExpressionResult.get(condition, new ExprConditionDestination(dcm), Boolean.class);
  }

  @Override
  public void apply(Attributes dcm, AttributeEditorContext context) {
    if (!validateCondition(this.condition, dcm)) {
      context.setAbort(Abort.FILE_EXCEPTION);
      context.setAbortMessage(
              "The instance is blocked because "
              + " is does not meet the condition");
    }
  }
}
