/*
* Copyright (c) 2020-2021 Karnak Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.backend.model.expression;

import java.util.Objects;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.karnak.backend.model.action.ActionItem;
import org.karnak.backend.model.action.Keep;
import org.karnak.backend.model.action.Remove;
import org.karnak.backend.model.action.Replace;
import org.karnak.backend.model.action.ReplaceNull;
import org.karnak.backend.model.action.UID;
import org.karnak.backend.util.DicomObjectTools;
import org.weasis.core.util.StringUtil;

public class ExprAction implements ExpressionItem {

  private int tag;
  private VR vr;
  private String stringValue;
  private DicomObject dcm;
  private DicomObject dcmCopy;

  public ExprAction(int tag, VR vr, DicomObject dcm, DicomObject dcmCopy) {
    this.tag = Objects.requireNonNull(tag);
    this.vr = Objects.requireNonNull(vr);
    this.stringValue = dcmCopy.getString(this.tag).orElse(null);
    this.dcmCopy = dcmCopy;
    this.dcm = dcm;
  }

  public ExprAction(int tag, VR vr, String stringValue) {
    this.tag = Objects.requireNonNull(tag);
    this.vr = Objects.requireNonNull(vr);
    this.stringValue = stringValue;
  }

  public static boolean isHexTag(String elem) {
    String cleanElem = elem.replaceAll("[(),]", "").toUpperCase();

    if (!StringUtil.hasText(cleanElem) || cleanElem.length() != 8) {
      return false;
    }
    return cleanElem.matches("[0-9A-FX]+");
  }

  public int getTag() {
    return tag;
  }

  public void setTag(int tag) {
    this.tag = tag;
  }

  public VR getVr() {
    return vr;
  }

  public void setVr(VR vr) {
    this.vr = vr;
  }

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }

  public ActionItem Keep() {
    return new Keep("K");
  }

  public ActionItem Remove() {
    return new Remove("X");
  }

  public ActionItem Replace(String dummyValue) {
    ActionItem replace = new Replace("D");
    replace.setDummyValue(dummyValue);
    return replace;
  }

  public ActionItem UID() {
    return new UID("U");
  }

  public ActionItem ReplaceNull() {
    return new ReplaceNull("Z");
  }

  public String getString(int tag) {
    return dcmCopy.getString(tag).orElse(null);
  }

  public boolean tagIsPresent(int tag) {
    return DicomObjectTools.tagIsInDicomObject(tag, dcmCopy);
  }

  /*public ActionItem Add(int newTag, VR newVr, String newValue){
      Add add = new Add("A", newTag, newVr, newValue);
      add.execute(dcm, newTag, null, null);
      return null;
  }*/
}
