/*
* Copyright (c) 2020-2021 Karnak Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.backend.enums;

public enum IdTypes {
  PID("pid"),
  EXTID("extid"),
  ADD_EXTID("extid");

  private final String value;

  IdTypes(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  /*public String toSentence(){
      switch(this){
          case PID: return "Automated pseudonym generated";
          case EXTID: return "Pseudonym is already store in karnak";
          case ADD_EXTID: return "Pseudonym is in a dicom tag";
          default: return "Automated pseudonym generated";
      }
  }

  public static  IdTypes toIdTypes(String str){
      switch(str){
          case "Automated pseudonym generated": return IdTypes.PID;
          case "Pseudonym is already store in karnak": return IdTypes.EXTID;
          case "Pseudonym is in a dicom tag": return IdTypes.ADD_EXTID;
          default: return IdTypes.PID;
      }
  }*/
}
