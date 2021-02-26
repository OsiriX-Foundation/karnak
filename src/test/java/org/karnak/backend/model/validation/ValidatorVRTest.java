package org.karnak.backend.model.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.karnak.backend.model.profilepipe.PatientMetadata;
import org.karnak.backend.util.PatientClientUtil;

class ValidatorVRTest {

  private static Stream<Arguments> providerValidAS() {
    return Stream.of(
        Arguments.of("000D"),
        Arguments.of("999D"),
        Arguments.of("123W"),
        Arguments.of("321W"),
        Arguments.of("456M"),
        Arguments.of("654M"),
        Arguments.of("789Y"),
        Arguments.of("987Y")
    );
  }

  @ParameterizedTest
  @MethodSource("providerValidAS")
  void validAS(String tagValue) {
    assertTrue(ValidatorVR.validAS(tagValue));
  }

  private static Stream<Arguments> providerNotValidAS() {
    return Stream.of(
        Arguments.of("000DD"),
        Arguments.of("9999D"),
        Arguments.of("123WW"),
        Arguments.of("3321W"),
        Arguments.of("456MM"),
        Arguments.of("6654M"),
        Arguments.of("789YY"),
        Arguments.of("9987Y"),
        Arguments.of("00D"),
        Arguments.of("99D"),
        Arguments.of("23W"),
        Arguments.of("21W"),
        Arguments.of("56M"),
        Arguments.of("54M"),
        Arguments.of("89Y"),
        Arguments.of("87Y"),
        Arguments.of("000A"),
        Arguments.of("999B")
    );
  }

  @ParameterizedTest
  @MethodSource("providerNotValidAS")
  void notValidAS(String tagValue) {
    assertFalse(ValidatorVR.validAS(tagValue));
  }

  @Test
  void validation() {}

  @Test
  void validAE() {}
}