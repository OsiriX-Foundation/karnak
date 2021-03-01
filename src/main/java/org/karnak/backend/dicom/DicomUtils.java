/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.dicom;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.util.TagUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.core.util.StringUtil;

public class DicomUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(DicomUtils.class);

  public static String getFormattedText(Object value, String format) {
    if (value == null) {
      return StringUtil.EMPTY_STRING;
    }

    String str;

    if (value instanceof String) {
      str = (String) value;
    } else if (value instanceof String[]) {
      str = Arrays.asList((String[]) value).stream().collect(Collectors.joining("\\"));
    } else if (value instanceof TemporalAccessor) {
      str = DateTimeUtils.formatDateTime((TemporalAccessor) value);
    } else if (value instanceof TemporalAccessor[]) {
      str =
          Stream.of((TemporalAccessor[]) value)
              .map(DateTimeUtils::formatDateTime)
              .collect(Collectors.joining(", "));
    } else if (value instanceof float[]) {
      float[] array = (float[]) value;
      str =
          IntStream.range(0, array.length)
              .mapToObj(i -> String.valueOf(array[i]))
              .collect(Collectors.joining(", "));
    } else if (value instanceof double[]) {
      str =
          DoubleStream.of((double[]) value)
              .mapToObj(String::valueOf)
              .collect(Collectors.joining(", "));
    } else if (value instanceof int[]) {
      str = IntStream.of((int[]) value).mapToObj(String::valueOf).collect(Collectors.joining(", "));
    } else {
      str = value.toString();
    }

    if (StringUtil.hasText(format) && !"$V".equals(format.trim())) {
      return formatValue(str, value instanceof Float || value instanceof Double, format);
    }

    return str == null ? StringUtil.EMPTY_STRING : str;
  }

  protected static String formatValue(String value, boolean decimal, String format) {
    String str = value;
    int index = format.indexOf("$V");
    int fmLength = 2;
    if (index != -1) {
      boolean suffix = format.length() > index + fmLength;
      // If the value ($V) is followed by ':' that means a number formatter is used
      if (suffix && format.charAt(index + fmLength) == ':') {
        fmLength++;
        if (format.charAt(index + fmLength) == 'f' && decimal) {
          fmLength++;
          String pattern = getPattern(index + fmLength, format);
          if (pattern != null) {
            fmLength += pattern.length() + 2;
            try {
              str =
                  new DecimalFormat(pattern, DecimalFormatSymbols.getInstance())
                      .format(Double.parseDouble(str));
            } catch (NumberFormatException e) {
              LOGGER.warn("Cannot apply pattern to decimal value", e);
            }
          }
        } else if (format.charAt(index + fmLength) == 'l') {
          fmLength++;
          String pattern = getPattern(index + fmLength, format);
          if (pattern != null) {
            fmLength += pattern.length() + 2;
            try {
              int limit = Integer.parseInt(pattern);
              int size = str.length();
              if (size > limit) {
                str = str.substring(0, limit) + "...";
              }
            } catch (NumberFormatException e) {
              LOGGER.warn("Cannot apply pattern to decimal value", e);
            }
          }
        }
      }
      str = format.substring(0, index) + str;
      if (format.length() > index + fmLength) {
        str += format.substring(index + fmLength);
      }
    }
    return str;
  }

  private static String getPattern(int startIndex, String format) {
    int beginIndex = format.indexOf('$', startIndex);
    int endIndex = format.indexOf('$', startIndex + 2);
    if (beginIndex == -1 || endIndex == -1) {
      return null;
    }
    return format.substring(beginIndex + 1, endIndex);
  }

  public static String getStringFromDicomElement(Attributes dicom, int tag) {
    if (dicom == null || !dicom.containsValue(tag)) {
      return null;
    }

    String[] s = dicom.getStrings(tag);
    if (s == null || s.length == 0) {
      return null;
    }
    if (s.length == 1) {
      return s[0];
    }
    StringBuilder sb = new StringBuilder(s[0]);
    for (int i = 1; i < s.length; i++) {
      sb.append("\\" + s[i]);
    }
    return sb.toString();
  }

  public static String[] getStringArrayFromDicomElement(Attributes dicom, int tag) {
    return getStringArrayFromDicomElement(dicom, tag, (String) null);
  }

  public static String[] getStringArrayFromDicomElement(
      Attributes dicom, int tag, String privateCreatorID) {
    if (dicom == null || !dicom.containsValue(tag)) {
      return null;
    }
    return dicom.getStrings(privateCreatorID, tag);
  }

  public static String[] getStringArrayFromDicomElement(
      Attributes dicom, int tag, String[] defaultValue) {
    return getStringArrayFromDicomElement(dicom, tag, null, defaultValue);
  }

  public static String[] getStringArrayFromDicomElement(
      Attributes dicom, int tag, String privateCreatorID, String[] defaultValue) {
    if (dicom == null || !dicom.containsValue(tag)) {
      return defaultValue;
    }
    String[] val = dicom.getStrings(privateCreatorID, tag);
    if (val == null || val.length == 0) {
      return defaultValue;
    }
    return val;
  }

  public static Date getDateFromDicomElement(Attributes dicom, int tag, Date defaultValue) {
    if (dicom == null || !dicom.containsValue(tag)) {
      return defaultValue;
    }
    return dicom.getDate(tag, defaultValue);
  }

  public static Date[] getDatesFromDicomElement(
      Attributes dicom, int tag, String privateCreatorID, Date[] defaultValue) {
    if (dicom == null || !dicom.containsValue(tag)) {
      return defaultValue;
    }
    Date[] val = dicom.getDates(privateCreatorID, tag);
    if (val == null || val.length == 0) {
      return defaultValue;
    }
    return val;
  }

  public static String getPatientAgeInPeriod(Attributes dicom, int tag, boolean computeOnlyIfNull) {
    return getPatientAgeInPeriod(dicom, tag, null, null, computeOnlyIfNull);
  }

  public static String getPatientAgeInPeriod(
      Attributes dicom,
      int tag,
      String privateCreatorID,
      String defaultValue,
      boolean computeOnlyIfNull) {
    if (dicom == null) {
      return defaultValue;
    }

    if (computeOnlyIfNull) {
      String s = dicom.getString(privateCreatorID, tag, defaultValue);
      if (StringUtil.hasText(s)) {
        return s;
      }
    }

    Date date =
        getDate(
            dicom,
            Tag.ContentDate,
            Tag.AcquisitionDate,
            Tag.DateOfSecondaryCapture,
            Tag.SeriesDate,
            Tag.StudyDate);

    if (date != null) {
      Date bithdate = dicom.getDate(Tag.PatientBirthDate);
      if (bithdate != null) {
        return getPeriod(DateTimeUtils.toLocalDate(bithdate), DateTimeUtils.toLocalDate(date));
      }
    }
    return null;
  }

  private static Date getDate(Attributes dicom, int... tagID) {
    Date date = null;
    for (int i : tagID) {
      date = dicom.getDate(i);
      if (date != null) {
        return date;
      }
    }
    return date;
  }

  public static String getPeriod(LocalDate first, LocalDate last) {
    Objects.requireNonNull(first);
    Objects.requireNonNull(last);

    long years = ChronoUnit.YEARS.between(first, last);
    if (years < 2) {
      long months = ChronoUnit.MONTHS.between(first, last);
      if (months < 2) {
        return String.format("%03dD", ChronoUnit.DAYS.between(first, last)); // NON-NLS
      }
      return String.format("%03dM", months); // NON-NLS
    }
    return String.format("%03dY", years); // NON-NLS
  }

  public static Float getFloatFromDicomElement(Attributes dicom, int tag, Float defaultValue) {
    return getFloatFromDicomElement(dicom, tag, null, defaultValue);
  }

  public static Float getFloatFromDicomElement(
      Attributes dicom, int tag, String privateCreatorID, Float defaultValue) {
    if (dicom == null || !dicom.containsValue(tag)) {
      return defaultValue;
    }
    try {
      return dicom.getFloat(privateCreatorID, tag, defaultValue == null ? 0.0F : defaultValue);
    } catch (NumberFormatException e) {
      LOGGER.error("Cannot parse Float of {}: {} ", TagUtils.toString(tag), e.getMessage());
    }
    return defaultValue;
  }

  public static Integer getIntegerFromDicomElement(
      Attributes dicom, int tag, Integer defaultValue) {
    return getIntegerFromDicomElement(dicom, tag, null, defaultValue);
  }

  public static Integer getIntegerFromDicomElement(
      Attributes dicom, int tag, String privateCreatorID, Integer defaultValue) {
    if (dicom == null || !dicom.containsValue(tag)) {
      return defaultValue;
    }
    try {
      return dicom.getInt(privateCreatorID, tag, defaultValue == null ? 0 : defaultValue);
    } catch (NumberFormatException e) {
      LOGGER.error("Cannot parse Integer of {}: {} ", TagUtils.toString(tag), e.getMessage());
    }
    return defaultValue;
  }

  public static Long getLongFromDicomElement(Attributes dicom, int tag, Long defaultValue) {
    return getLongFromDicomElement(dicom, tag, null, defaultValue);
  }

  public static Long getLongFromDicomElement(
      Attributes dicom, int tag, String privateCreatorID, Long defaultValue) {
    if (dicom == null || !dicom.containsValue(tag)) {
      return defaultValue;
    }
    try {
      return dicom.getLong(privateCreatorID, tag, defaultValue == null ? 0L : defaultValue);
    } catch (NumberFormatException e) {
      LOGGER.error("Cannot parse Long of {}: {} ", TagUtils.toString(tag), e.getMessage());
    }
    return defaultValue;
  }

  public static Double getDoubleFromDicomElement(Attributes dicom, int tag, Double defaultValue) {
    return getDoubleFromDicomElement(dicom, tag, null, defaultValue);
  }

  public static Double getDoubleFromDicomElement(
      Attributes dicom, int tag, String privateCreatorID, Double defaultValue) {
    if (dicom == null || !dicom.containsValue(tag)) {
      return defaultValue;
    }
    try {
      return dicom.getDouble(privateCreatorID, tag, defaultValue == null ? 0.0 : defaultValue);
    } catch (NumberFormatException e) {
      LOGGER.error("Cannot parse Double of {}: {} ", TagUtils.toString(tag), e.getMessage());
    }
    return defaultValue;
  }

  public static int[] getIntAyrrayFromDicomElement(Attributes dicom, int tag, int[] defaultValue) {
    return getIntArrayFromDicomElement(dicom, tag, null, defaultValue);
  }

  public static int[] getIntArrayFromDicomElement(
      Attributes dicom, int tag, String privateCreatorID, int[] defaultValue) {
    if (dicom == null || !dicom.containsValue(tag)) {
      return defaultValue;
    }
    try {
      return dicom.getInts(privateCreatorID, tag);
    } catch (NumberFormatException e) {
      LOGGER.error("Cannot parse int[] of {}: {} ", TagUtils.toString(tag), e.getMessage());
    }
    return defaultValue;
  }

  public static float[] getFloatArrayFromDicomElement(
      Attributes dicom, int tag, float[] defaultValue) {
    return getFloatArrayFromDicomElement(dicom, tag, null, defaultValue);
  }

  public static float[] getFloatArrayFromDicomElement(
      Attributes dicom, int tag, String privateCreatorID, float[] defaultValue) {
    if (dicom == null || !dicom.containsValue(tag)) {
      return defaultValue;
    }
    try {
      return dicom.getFloats(privateCreatorID, tag);
    } catch (NumberFormatException e) {
      LOGGER.error("Cannot parse float[] of {}: {} ", TagUtils.toString(tag), e.getMessage());
    }
    return defaultValue;
  }

  public static double[] getDoubleArrayFromDicomElement(
      Attributes dicom, int tag, double[] defaultValue) {
    return getDoubleArrayFromDicomElement(dicom, tag, null, defaultValue);
  }

  public static double[] getDoubleArrayFromDicomElement(
      Attributes dicom, int tag, String privateCreatorID, double[] defaultValue) {
    if (dicom == null || !dicom.containsValue(tag)) {
      return defaultValue;
    }
    try {
      return dicom.getDoubles(privateCreatorID, tag);
    } catch (NumberFormatException e) {
      LOGGER.error("Cannot parse double[] of {}: {} ", TagUtils.toString(tag), e.getMessage());
    }
    return defaultValue;
  }
}
