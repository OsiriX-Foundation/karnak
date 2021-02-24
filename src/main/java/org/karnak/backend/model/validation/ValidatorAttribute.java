package org.karnak.backend.model.validation;

import java.util.List;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.karnak.backend.model.standard.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidatorAttribute {
  private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorAttribute.class);

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

    if ((type.equals("2") || type.equals("2C")) && tagValue == null) {
      return true;
    }

    return false;
  }

  public static boolean vrNotValid(String tagValue, int tag, VR vr) {
    DicomObject dcmObj = DicomObject.newDicomObject();
    try {
      String val = "1234567890123456789012345678901234567890";
      dcmObj.setString(Tag.RetrieveAETitle, VR.AE, val);
      vr.type.elementOf(dcmObj, tag, vr, val);
    } catch (UnsupportedOperationException exception) {
      LOGGER.error("test" ,exception);
    }
    return false;
  }
}
