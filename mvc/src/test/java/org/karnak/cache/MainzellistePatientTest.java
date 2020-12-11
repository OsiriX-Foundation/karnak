package org.karnak.cache;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainzellistePatientTest {
    static MainzellistePatient mainzellistePatient;
    static MainzellistePatient mainzellistePatientWithFirstName;
    static MainzellistePatient mainzellistePatientWithLastName;
    static MainzellistePatient mainzellistePatientWithLastNameNull;
    static MainzellistePatient mainzellistePatientWithFirstNameNull;

    @BeforeAll
    protected static void setUpBeforeClass() throws Exception {
        mainzellistePatient = new MainzellistePatient("pseudonym", "mykasa", "Kenny", "Ackermann",
                LocalDate.of(1982, 2, 4), "M", "DPA");
        mainzellistePatientWithFirstName = new MainzellistePatient("pseudonym", "mykasa", "Kenny", "",
                LocalDate.of(1982, 2, 4), "M", "DPA");
        mainzellistePatientWithLastName = new MainzellistePatient("pseudonym", "mykasa", "", "Ackermann",
                LocalDate.of(1982, 2, 4), "M", "DPA");
        mainzellistePatientWithLastNameNull = new MainzellistePatient("pseudonym", "mykasa", "Kenny", null,
                LocalDate.of(1982, 2, 4), "M", "DPA");
        mainzellistePatientWithFirstNameNull = new MainzellistePatient("pseudonym", "mykasa", null, "Ackermann",
                LocalDate.of(1982, 2, 4), "M", "DPA");
    }

    @ParameterizedTest
    @MethodSource("providerGetPatientName")
    void getPatientName(MainzellistePatient mainzellistePatient, String output){
        assertEquals(mainzellistePatient.getPatientName(), output);
    }

    private static Stream<Arguments> providerGetPatientName() {
        return Stream.of(
                Arguments.of(mainzellistePatient, "Ackermann^Kenny"),
                Arguments.of(mainzellistePatientWithFirstName, "^Kenny"),
                Arguments.of(mainzellistePatientWithLastName, "Ackermann"),
                Arguments.of(mainzellistePatientWithLastNameNull, "^Kenny"),
                Arguments.of(mainzellistePatientWithFirstNameNull, "Ackermann")
        );
    }
}