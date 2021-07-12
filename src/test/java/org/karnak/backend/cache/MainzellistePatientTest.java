/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MainzellistePatientTest {

  static MainzellistePatient mainzellistePatient;
  static MainzellistePatient mainzellistePatientWithFirstName;
  static MainzellistePatient mainzellistePatientWithLastName;
  static MainzellistePatient mainzellistePatientWithLastNameNull;
  static MainzellistePatient mainzellistePatientWithFirstNameNull;

  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
    mainzellistePatient =
        new MainzellistePatient(
            "pseudonym", "mykasa", "Kenny", "Ackermann", LocalDate.of(1982, 2, 4), "M", "DPA");
    mainzellistePatientWithFirstName =
        new MainzellistePatient(
            "pseudonym", "mykasa", "Kenny", "", LocalDate.of(1982, 2, 4), "M", "DPA");
    mainzellistePatientWithLastName =
        new MainzellistePatient(
            "pseudonym", "mykasa", "", "Ackermann", LocalDate.of(1982, 2, 4), "M", "DPA");
    mainzellistePatientWithLastNameNull =
        new MainzellistePatient(
            "pseudonym", "mykasa", "Kenny", null, LocalDate.of(1982, 2, 4), "M", "DPA");
    mainzellistePatientWithFirstNameNull =
        new MainzellistePatient(
            "pseudonym", "mykasa", null, "Ackermann", LocalDate.of(1982, 2, 4), "M", "DPA");
  }

  private static Stream<Arguments> providerGetPatientName() {
    return Stream.of(
        Arguments.of(mainzellistePatient, "Ackermann^Kenny"),
        Arguments.of(mainzellistePatientWithFirstName, "^Kenny"),
        Arguments.of(mainzellistePatientWithLastName, "Ackermann"),
        Arguments.of(mainzellistePatientWithLastNameNull, "^Kenny"),
        Arguments.of(mainzellistePatientWithFirstNameNull, "Ackermann"));
  }

  private static Stream<Arguments> providerGetPatientFirstName() {
    return Stream.of(
        Arguments.of(mainzellistePatient, "Kenny"),
        Arguments.of(mainzellistePatientWithFirstName, "Kenny"),
        Arguments.of(mainzellistePatientWithLastName, ""),
        Arguments.of(mainzellistePatientWithLastNameNull, "Kenny"),
        Arguments.of(mainzellistePatientWithFirstNameNull, ""));
  }

  private static Stream<Arguments> providerGetPatientLastName() {
    return Stream.of(
        Arguments.of(mainzellistePatient, "Ackermann"),
        Arguments.of(mainzellistePatientWithFirstName, ""),
        Arguments.of(mainzellistePatientWithLastName, "Ackermann"),
        Arguments.of(mainzellistePatientWithLastNameNull, ""),
        Arguments.of(mainzellistePatientWithFirstNameNull, "Ackermann"));
  }

  private static Stream<Arguments> providerUpdatePatientLastName() {
    mainzellistePatient.setPatientLastName("Katshan");
    mainzellistePatient.setPatientFirstName("Kenny");
    mainzellistePatientWithFirstName.setPatientLastName(null);
    mainzellistePatientWithFirstName.setPatientFirstName("Kenny");
    mainzellistePatientWithLastName.setPatientLastName("Katshan");
    mainzellistePatientWithLastName.setPatientFirstName("");
    mainzellistePatientWithLastNameNull.setPatientLastName("");
    mainzellistePatientWithLastNameNull.setPatientFirstName("Kenny");
    mainzellistePatientWithFirstNameNull.setPatientLastName("Katshan");
    mainzellistePatientWithFirstNameNull.setPatientFirstName(null);
    return Stream.of(
        Arguments.of(mainzellistePatient, "Katshan"),
        Arguments.of(mainzellistePatientWithFirstName, ""),
        Arguments.of(mainzellistePatientWithLastName, "Katshan"),
        Arguments.of(mainzellistePatientWithLastNameNull, ""),
        Arguments.of(mainzellistePatientWithFirstNameNull, "Katshan"));
  }

  private static Stream<Arguments> providerUpdatePatientFirstName() {
    mainzellistePatient.setPatientLastName("Ackermann");
    mainzellistePatient.setPatientFirstName("Bakugo");
    mainzellistePatientWithFirstName.setPatientLastName("");
    mainzellistePatientWithFirstName.setPatientFirstName("Bakugo");
    mainzellistePatientWithLastName.setPatientLastName("Ackermann");
    mainzellistePatientWithLastName.setPatientFirstName(null);
    mainzellistePatientWithLastNameNull.setPatientLastName(null);
    mainzellistePatientWithLastNameNull.setPatientFirstName("Bakugo");
    mainzellistePatientWithFirstNameNull.setPatientLastName("Ackermann");
    mainzellistePatientWithFirstNameNull.setPatientFirstName("");
    return Stream.of(
        Arguments.of(mainzellistePatient, "Bakugo"),
        Arguments.of(mainzellistePatientWithFirstName, "Bakugo"),
        Arguments.of(mainzellistePatientWithLastName, ""),
        Arguments.of(mainzellistePatientWithLastNameNull, "Bakugo"),
        Arguments.of(mainzellistePatientWithFirstNameNull, ""));
  }

  private static Stream<Arguments> providerUpdatePatientName() {
    mainzellistePatient.setPatientLastName("Katshan");
    mainzellistePatient.setPatientFirstName("Bakugo");
    mainzellistePatientWithFirstName.setPatientLastName(null);
    mainzellistePatientWithFirstName.setPatientFirstName("Bakugo");
    mainzellistePatientWithLastName.setPatientLastName("Katshan");
    mainzellistePatientWithLastName.setPatientFirstName("");
    mainzellistePatientWithLastNameNull.setPatientLastName("");
    mainzellistePatientWithLastNameNull.setPatientFirstName("Bakugo");
    mainzellistePatientWithFirstNameNull.setPatientLastName("Katshan");
    mainzellistePatientWithFirstNameNull.setPatientFirstName(null);
    return Stream.of(
        Arguments.of(mainzellistePatient, "Katshan^Bakugo"),
        Arguments.of(mainzellistePatientWithFirstName, "^Bakugo"),
        Arguments.of(mainzellistePatientWithLastName, "Katshan"),
        Arguments.of(mainzellistePatientWithLastNameNull, "^Bakugo"),
        Arguments.of(mainzellistePatientWithFirstNameNull, "Katshan"));
  }

  @ParameterizedTest
  @MethodSource("providerGetPatientName")
  void getPatientName(MainzellistePatient mainzellistePatient, String output) {
    assertEquals(mainzellistePatient.getPatientName(), output);
  }

  @ParameterizedTest
  @MethodSource("providerGetPatientFirstName")
  void getPatientFirstName(MainzellistePatient mainzellistePatient, String output) {
    assertEquals(mainzellistePatient.getPatientFirstName(), output);
  }

  @ParameterizedTest
  @MethodSource("providerGetPatientLastName")
  void getPatientLastName(MainzellistePatient mainzellistePatient, String output) {
    assertEquals(mainzellistePatient.getPatientLastName(), output);
  }

  @ParameterizedTest
  @MethodSource("providerUpdatePatientLastName")
  void getUpdatedPatientLastName(MainzellistePatient mainzellistePatient, String output) {
    assertEquals(mainzellistePatient.getPatientLastName(), output);
  }

  @ParameterizedTest
  @MethodSource("providerUpdatePatientFirstName")
  void getUpdatedPatientFirstName(MainzellistePatient mainzellistePatient, String output) {
    assertEquals(mainzellistePatient.getPatientFirstName(), output);
  }

  @ParameterizedTest
  @MethodSource("providerUpdatePatientName")
  void getUpdatedPatientName(MainzellistePatient mainzellistePatient, String output) {
    assertEquals(mainzellistePatient.getPatientName(), output);
  }
}
