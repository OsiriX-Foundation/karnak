package org.karnak.backend.model.expression;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.TagUtils;

public class ExprConditionKheops implements ExpressionItem {

  private final DicomObject dcm;

  public ExprConditionKheops(DicomObject dcm) {
    this.dcm = dcm;
  }

  public static void expressionValidation(String condition) {
    ExprConditionKheops exprConditionKheops = new ExprConditionKheops(DicomObject.newDicomObject());
    ExpressionResult.get(condition, exprConditionKheops, Boolean.class);
  }

  public static int intFromHexString(String tag) {
    String cleanTag = tag.replaceAll("[(),]", "").toUpperCase();
    return TagUtils.intFromHexString(cleanTag);
  }

  public boolean tagValueIsPresent(String tag, String value) {
    int cleanTag = intFromHexString(tag);
    return tagValueIsPresent(cleanTag, value);
  }

  public boolean tagValueIsPresent(int tag, String value) {
    String dcmValue = dcm.getString(tag).orElse(null);
    return dcmValue != null && dcmValue.equals(value);
  }

  public boolean tagValueContains(String tag, String value) {
    int cleanTag = intFromHexString(tag);
    return tagValueContains(cleanTag, value);
  }

  public boolean tagValueContains(int tag, String value) {
    String dcmValue = dcm.getString(tag).orElse(null);
    return dcmValue != null && dcmValue.contains(value);
  }

  public boolean tagValueBeginsWith(String tag, String value) {
    int cleanTag = intFromHexString(tag);
    return tagValueBeginsWith(cleanTag, value);
  }

  public boolean tagValueBeginsWith(int tag, String value) {
    String dcmValue = dcm.getString(tag).orElse(null);
    return dcmValue != null && dcmValue.startsWith(value);
  }

  public boolean tagValueEndsWith(String tag, String value) {
    int cleanTag = intFromHexString(tag);
    return tagValueEndsWith(cleanTag, value);
  }

  public boolean tagValueEndsWith(int tag, String value) {
    String dcmValue = dcm.getString(tag).orElse(null);
    return dcmValue != null && dcmValue.endsWith(value);
  }
}
