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
