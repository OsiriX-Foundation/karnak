package org.karnak.backend.model.validation;

import java.time.DateTimeException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.DateTimeUtils;

// http://dicom.nema.org/medical/dicom/current/output/html/part05.html#sect_6.2
public class ValidatorVR {
  public static boolean validation(String tagValue, VR vr) {
    switch (vr) {
      case AE: return validAE(tagValue);
      case AS: return validAS(tagValue);
      case AT: return validAT(tagValue);
      case CS: return validCS(tagValue);
      case DA: return validDA(tagValue);
      case DS: return validDS(tagValue);
      case DT: return validDT(tagValue);
      case FD: return validFD(tagValue);
      case FL: return validFL(tagValue);
      case IS: return validIS(tagValue);
      case LO: return validLO(tagValue);
      case LT: return validLT(tagValue);
      case PN: return true;
      case SH: return validSH(tagValue);
      case SL: return validSL(tagValue);
      case SQ: return true;
      case SS: return validSS(tagValue);
      case ST: return validST(tagValue);
      case TM: return validTM(tagValue);
      case UC: return validUC(tagValue);
      case UI: return validUI(tagValue);
      case UL: return validUL(tagValue);
      case UN: return validUN(tagValue);
      case UR: return validUR(tagValue);
      case US: return validUS(tagValue);
      case UT: return validUT(tagValue);
    }
    return true;
  }

  public static boolean validAE(String tagValue) {
    if (tagValue.length() > 16) {
      return false;
    }
    return true;
  }

  public static boolean validAS(String tagValue) {
    Pattern pattern = Pattern.compile("^\\d{3}[DWMY]$");
    Matcher matcher = pattern.matcher(tagValue);
    boolean matchFound = matcher.find();

    if (!matchFound) {
      return false;
    }
    return true;
  }

  public static boolean validAT(String tagValue) {
    if (tagValue.length() != 4) {
      return false;
    }
    return true;
  }

  public static boolean validCS(String tagValue) {
    Pattern pattern = Pattern.compile("^[A-Z0-9_ ]*$");
    Matcher matcher = pattern.matcher(tagValue);
    boolean matchFound = matcher.find();

    if (!matchFound && tagValue.length() > 16) {
      return false;
    }
    return true;
  }

  public static boolean validDA(String tagValue) {
    try {
      DateTimeUtils.parseDA(tagValue);
    } catch (DateTimeException dateTimeException) {
      return false;
    }
    return true;
  }

  public static boolean validDS(String tagValue) {
    Pattern pattern = Pattern.compile("^[0-9Ee+\\-. ]*$");
    Matcher matcher = pattern.matcher(tagValue);
    boolean matchFound = matcher.find();

    if (!matchFound && tagValue.length() > 16) {
      return false;
    }
    return true;
  }

  public static boolean validDT(String tagValue) {
    try {
      DateTimeUtils.parseDT(tagValue);
    } catch (DateTimeException dateTimeException) {
      return false;
    }
    return true;
  }

  public static boolean validTM(String tagValue) {
    try {
      DateTimeUtils.parseTM(tagValue);
    } catch (DateTimeException dateTimeException) {
      return false;
    }
    return true;
  }

  public static boolean validFL(String tagValue) {
    if (tagValue.length() > 4) {
      return false;
    }
    return true;
  }

  public static boolean validFD(String tagValue) {
    if (tagValue.length() > 8) {
      return false;
    }
    return true;
  }

  public static boolean validIS(String tagValue) {
    Pattern pattern = Pattern.compile("^[0-9\\-+ ]*$");
    Matcher matcher = pattern.matcher(tagValue);
    boolean matchFound = matcher.find();

    if (tagValue.length() > 12 && !matchFound) {
      return false;
    }
    return true;
  }

  public static boolean validLO(String tagValue) {
    if (tagValue.length() > 64) {
      return false;
    }
    return true;
  }

  public static boolean validLT(String tagValue) {
    if (tagValue.length() > 10240) {
      return false;
    }
    return true;
  }

  public static boolean validSH(String tagValue) {
    if (tagValue.length() > 16) {
      return false;
    }
    return true;
  }

  public static boolean validSL(String tagValue) {
    if (tagValue.length() != 4) {
      return false;
    }
    return true;
  }

  public static boolean validSS(String tagValue) {
    if (tagValue.length() != 2) {
      return false;
    }
    return true;
  }

  public static boolean validST(String tagValue) {
    if (tagValue.length() > 1024) {
      return false;
    }
    return true;
  }

  public static boolean validSV(String tagValue) {
    if (tagValue.length() != 8) {
      return false;
    }
    return true;
  }

  public static boolean validUC(String tagValue) {
    if (tagValue.length() > Math.pow(2,32) - 2) {
      return false;
    }
    return true;
  }

  public static boolean validUI(String tagValue) {
    Pattern pattern = Pattern.compile("^[0-9.]*$");
    Matcher matcher = pattern.matcher(tagValue);
    boolean matchFound = matcher.find();

    if (tagValue.length() > 64 && !matchFound) {
      return false;
    }
    return true;
  }

  public static boolean validUL(String tagValue) {
    if (tagValue.length() > 4) {
      return false;
    }
    return true;
  }

  public static boolean validUN(String tagValue) {
    return true;
  }

  public static boolean validUR(String tagValue) {
    if (tagValue.length() > Math.pow(2, 32) - 2) {
      return false;
    }
    return true;
  }

  public static boolean validUS(String tagValue) {
    if (tagValue.length() > 2) {
      return false;
    }
    return true;
  }

  public static boolean validUT(String tagValue) {
    if (tagValue.length() > Math.pow(2, 32) - 2) {
      return false;
    }
    return true;
  }

  public static boolean validUV(String tagValue) {
    if (tagValue.length() > 8) {
      return false;
    }
    return true;
  }
}
