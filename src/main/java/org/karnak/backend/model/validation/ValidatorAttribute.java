package org.karnak.backend.model.validation;

import java.util.List;
import org.karnak.backend.model.standard.Attribute;

public class ValidatorAttribute {
  public static boolean attributeNotExist(List<Attribute> attributes) {
    if (attributes.size() == 0) {
      return true;
    }
    return false;
  }

  public static boolean typeNotValid(String type, String tagValue) {
    if ((type.equals("1") || type.equals("1C")) && (tagValue == null || tagValue.length() == 0)) {
      return true;
    }
    // can't be checked now ...
    /*
    if (type.equals("2") || type.equals("2C")) {
      return false;
    }
    */
    return false;
  }

  public static boolean vrNotValid(String tagValue) {
    return false;
  }
}
