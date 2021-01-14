package org.karnak.backend.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Objects;
import java.util.Optional;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.ElementDictionary;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.DateTimeUtils;
import org.dcm4che6.util.TagUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.core.util.StringUtil;

public class DateUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

  private DateUtil() {
  }

  public static String getPatientAgeInPeriod(
      DicomObject dicom, int tag, boolean computeOnlyIfNull) {
    if (dicom == null) {
      return null;
    }

    if (computeOnlyIfNull) {
      String s = dicom.getString(tag).orElse(null);
      if (StringUtil.hasText(s)) {
        return s;
      }
    }

    LocalDate date =
        getDate(
            dicom,
            Tag.ContentDate,
            Tag.AcquisitionDate,
            Tag.DateOfSecondaryCapture,
            Tag.SeriesDate,
            Tag.StudyDate);

    if (date != null) {
      LocalDate bithdate = getLocalDate(dicom, Tag.PatientBirthDate, null);
      if (bithdate != null) {
        return getPeriod(bithdate, date);
      }
    }
    return null;
  }

  private static LocalDate getDate(DicomObject dicom, int... tagID) {
    LocalDate date = null;
    for (int i : tagID) {
      date = getLocalDate(dicom, i, null);
      if (date != null) {
        return date;
      }
    }
    return date;
  }

  public static LocalTime getLocalTime(DicomObject dicom, int tag, LocalTime defaultValue) {
    if (dicom == null) {
      return defaultValue;
    }
    Optional<String> time = dicom.getString(tag);
    try {
      if (time.isPresent()) {
        return DateTimeUtils.parseTM(time.get());
      }
    } catch (Exception e1) {
      LOGGER.error("Parse DICOM time of tag {}", TagUtils.toString(tag), e1); // $NON-NLS-1$
    }
    return defaultValue;
  }

  public static LocalDate getLocalDate(DicomObject dicom, int tag, LocalDate defaultValue) {
    if (dicom == null) {
      return defaultValue;
    }
    Optional<String> date = dicom.getString(tag);
    try {
      if (date.isPresent()) {
        return DateTimeUtils.parseDA(date.get());
      }
    } catch (Exception e1) {
      LOGGER.error("Parse DICOM date of tag {}", TagUtils.toString(tag), e1); // $NON-NLS-1$
    }
    return defaultValue;
  }

  public static Optional<Temporal> getLocalDateTime(DicomObject dicom, int tag) {
    if (dicom == null) {
      return Optional.empty();
    }
    Optional<String> date = dicom.getString(tag);
    try {
      if (date.isPresent()) {
        return Optional.of(DateTimeUtils.parseDT(date.get()));
      }
    } catch (Exception e1) {
      LOGGER.error("Parse DICOM DateTime of tag {}", TagUtils.toString(tag), e1); // $NON-NLS-1$
    }
    return Optional.empty();
  }

  public static void setLocalDateTime(DicomObject dicom, int tag, Temporal t) {
    if (dicom != null) {
      VR vr = ElementDictionary.vrOf(tag, dicom.getPrivateCreator(tag));
      try {
        String value;
        if (t == null) {
          value = null;
        } else if (vr == VR.DA) {
          value = DateTimeUtils.formatDA(t);
        } else if (vr == VR.TM) {
          value = DateTimeUtils.formatTM(t);
        } else if (vr == VR.DT) {
          value = DateTimeUtils.formatDT(t);
        } else {
          value = null;
        }
        dicom.setString(tag, vr, value);
      } catch (Exception e) {
        LOGGER.error("Cannot format Temporal from tag {}", TagUtils.toString(tag), e);
      }
    }
  }

  public static String getPeriod(LocalDate first, LocalDate last) {
    Objects.requireNonNull(first);
    Objects.requireNonNull(last);

    long years = ChronoUnit.YEARS.between(first, last);
    if (years < 2) {
      long months = ChronoUnit.MONTHS.between(first, last);
      if (months < 2) {
        return String.format("%03dD", ChronoUnit.DAYS.between(first, last)); // $NON-NLS-1$
      }
      return String.format("%03dM", months); // $NON-NLS-1$
    }
    return String.format("%03dY", years); // $NON-NLS-1$
  }
}
