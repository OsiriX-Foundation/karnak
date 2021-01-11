package org.karnak.cache;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.karnak.profilepipe.utils.PatientMetadata;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PatientClientUtilTest {
    static DicomObject dataset;
    static DicomObject datasetWithIssuer;

    @BeforeAll
    protected static void setUpBeforeClass() throws Exception {
        dataset = DicomObject.newDicomObject();
        dataset.setString(Tag.PatientID, VR.LO, "EREN");
        dataset.setString(Tag.PatientName, VR.PN, "Patient^Name");
        dataset.setString(Tag.PatientBirthDate, VR.DA, "19930216");
        dataset.setString(Tag.PatientSex, VR.CS, "M");

        datasetWithIssuer = DicomObject.newDicomObject();
        datasetWithIssuer.setString(Tag.PatientID, VR.LO, "EREN");
        datasetWithIssuer.setString(Tag.PatientName, VR.PN, "Patient^Name");
        datasetWithIssuer.setString(Tag.PatientBirthDate, VR.DA, "19930216");
        datasetWithIssuer.setString(Tag.PatientSex, VR.CS, "M");
        datasetWithIssuer.setString(Tag.IssuerOfPatientID, VR.LO, "PDA");
    }

    @ParameterizedTest
    @MethodSource("providerGenerateKey")
    void generateKey(String PatientID, String PatientName, String IssuerOfPatientID, String output){
        assertEquals(PatientClientUtil.generateKey(PatientID, PatientName, IssuerOfPatientID), output);
    }

    private static Stream<Arguments> providerGenerateKey() {
        return Stream.of(
            Arguments.of("123", "456", "789", "123456789"),
            Arguments.of("123", "456", "", "123456"),
            Arguments.of("EREN", "Patient^Name", "PDA", "ERENPatient^NamePDA"),
            Arguments.of("EREN", "Patient^Name", "", "ERENPatient^Name")
        );
    }

    @ParameterizedTest
    @MethodSource("providerGenerateKeyPseudonymPatient")
    void generateKeyPseudonymPatient(PseudonymPatient patient, String output){
        assertEquals(PatientClientUtil.generateKey(patient), output);
    }

    private static Stream<Arguments> providerGenerateKeyPseudonymPatient() {
        return Stream.of(
            /*Arguments.of(new CachedPatient("pseudo",  "123", "456", "789"), "123456789"),
            Arguments.of(new CachedPatient("pseudo",  "123", "456", ""), "123456"),
            Arguments.of(new CachedPatient("pseudo",  "EREN", "Patient^Name", "PDA"), "ERENPatient^NamePDA"),
            Arguments.of(new CachedPatient("pseudo",  "EREN", "Patient^Name", ""), "ERENPatient^Name"),*/
            Arguments.of(new MainzellistePatient("pseudo",  "123", "", "456"
                , LocalDate.of(1993, 02, 16), "M", ""), "123456"),
            Arguments.of(new MainzellistePatient("pseudo",  "123", "", "456"
                , LocalDate.of(1993, 02, 16), "M", "789"), "123456789"),
            Arguments.of(new MainzellistePatient("pseudo",  "EREN", "Name", "Patient"
                , LocalDate.of(1993, 02, 16), "M", "PDA"), "ERENPatient^NamePDA"),
            Arguments.of(new MainzellistePatient("pseudo",  "EREN", "Name", "Patient"
                , LocalDate.of(1993, 02, 16), "M", ""), "ERENPatient^Name")
        );
    }

    @ParameterizedTest
    @MethodSource("providerGenerateKeyPatientMetadata")
    void generateKeyPatientMetadata(PatientMetadata patientMetadata, String output){
        assertEquals(PatientClientUtil.generateKey(patientMetadata), output);
    }

    private static Stream<Arguments> providerGenerateKeyPatientMetadata() {
        return Stream.of(
            Arguments.of(new PatientMetadata(dataset, "PDA"), "ERENPatient^NamePDA"),
            Arguments.of(new PatientMetadata(dataset, ""), "ERENPatient^Name"),
            Arguments.of(new PatientMetadata(datasetWithIssuer, "TEST"), "ERENPatient^NamePDA"),
            Arguments.of(new PatientMetadata(datasetWithIssuer, ""), "ERENPatient^NamePDA")
        );
    }
}