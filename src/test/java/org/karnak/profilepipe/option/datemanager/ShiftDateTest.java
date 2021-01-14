package org.karnak.profilepipe.option.datemanager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.ArgumentEntity;
import org.karnak.backend.util.ShiftDate;

class ShiftDateTest {

  private static final DicomObject dataset = DicomObject.newDicomObject();
  private static final List<ArgumentEntity> argumentEntities = new ArrayList<>();
  private static final ArgumentEntity seconds = new ArgumentEntity();
  private static final ArgumentEntity days = new ArgumentEntity();

  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
    seconds.setKey("seconds");
    seconds.setValue("500");
    days.setKey("days");
    days.setValue("40");

    argumentEntities.add(seconds);
    argumentEntities.add(days);

    dataset.setString(Tag.StudyDate, VR.DA, "20180209");
    dataset.setString(Tag.StudyTime, VR.TM, "120843");
    dataset.setString(Tag.PatientAge, VR.AS, "043Y");
    dataset.setString(Tag.AcquisitionDateTime, VR.DT, "20180209120854.354");
    dataset.setString(Tag.AcquisitionTime, VR.TM, "000134");
  }

  @Test
  void DAbyDays() {
    assertEquals("19930822", ShiftDate.DAbyDays("19930823", 1));
    assertEquals("20391231", ShiftDate.DAbyDays("20400120", 20));
    assertEquals("19920102", ShiftDate.DAbyDays("19930101", 365));
    assertEquals("19940101", ShiftDate.DAbyDays("19930101", -365));

    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.DAbyDays("199", 365);
        });

    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.DAbyDays("19932", 365);
        });

    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.DAbyDays("199320", 365);
        });

    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.DAbyDays("1993021", 365);
        });

    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.DAbyDays("19930254", 365);
        });
  }

  @Test
  void TMbySeconds() {
    assertEquals("070906.070500", ShiftDate.TMbySeconds("070907.0705", 1));
    assertEquals("100959.000000", ShiftDate.TMbySeconds("1010", 1));
    assertEquals("100900.000000", ShiftDate.TMbySeconds("1010", 60));
    assertEquals("091000.000000", ShiftDate.TMbySeconds("1010", 60 * 60));
    assertEquals("101000.000000", ShiftDate.TMbySeconds("1010", 24 * 60 * 60));

    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.TMbySeconds("1", 15);
        });
    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.TMbySeconds("35", 15);
        });
    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.TMbySeconds("125", 15);
        });
    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.TMbySeconds("1270", 15);
        });
    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.TMbySeconds("12598", 15);
        });
    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.TMbySeconds("125980", 15);
        });
  }

  @Test
  void ASbyDays() {
    assertEquals("019M", ShiftDate.ASbyDays("018M", 40));
    assertEquals("009M", ShiftDate.ASbyDays("008M", 40));
    assertEquals("009M", ShiftDate.ASbyDays("009M", 20));
    assertEquals("002Y", ShiftDate.ASbyDays("001Y", 365));
    assertEquals("031Y", ShiftDate.ASbyDays("029Y", 730));
    assertEquals("009D", ShiftDate.ASbyDays("008D", 1));
  }

  @Test
  void DTbyDays() {
    assertEquals("20180228235900.000000", ShiftDate.DTbyDays("20180302", 1, 60));
    assertEquals("20080728131403.000000", ShiftDate.DTbyDays("20080729131503", 1, 60));
    assertEquals("20201210235930.000000", ShiftDate.DTbyDays("20201212000030", 1, 60));
    assertEquals("20201211000030.000000", ShiftDate.DTbyDays("20201212000130", 1, 60));
    assertEquals("20080721235900.000000", ShiftDate.DTbyDays("2008072824", 7, 60));

    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.DTbyDays("200807281", 365, 60);
        });
    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.DTbyDays("2008072825", 365, 60);
        });
    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.DTbyDays("20080728121", 365, 60);
        });
    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.DTbyDays("200807281261", 365, 60);
        });
    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.DTbyDays("2008072812592", 365, 60);
        });
    Assertions.assertThrows(
        DateTimeParseException.class,
        () -> {
          ShiftDate.DTbyDays("20080728125989", 365, 60);
        });
  }

  @Test
  void shift() {

    assertEquals(
        "20171231",
        ShiftDate.shift(dataset, dataset.get(Tag.StudyDate).orElse(null), argumentEntities));
    assertEquals(
        "120023.000000",
        ShiftDate.shift(dataset, dataset.get(Tag.StudyTime).orElse(null), argumentEntities));
    assertEquals(
        "043Y",
        ShiftDate.shift(dataset, dataset.get(Tag.PatientAge).orElse(null), argumentEntities));
    assertEquals(
        "20171231120034.354000",
        ShiftDate.shift(
            dataset, dataset.get(Tag.AcquisitionDateTime).orElse(null), argumentEntities));
    assertEquals(
        "235314.000000",
        ShiftDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), argumentEntities));

    seconds.setKey("notseconds");
    days.setKey("days");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> {
          ShiftDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), argumentEntities);
        });

    seconds.setKey("seconds");
    days.setKey("notdays");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> {
          ShiftDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), argumentEntities);
        });

    seconds.setKey("notseconds");
    days.setKey("notdays");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> {
          ShiftDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), argumentEntities);
        });

    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> {
          ShiftDate.shift(
              dataset, dataset.get(Tag.AcquisitionTime).orElse(null), new ArrayList<>());
        });
  }
}
