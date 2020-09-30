package org.karnak.profilepipe;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.karnak.data.profile.Argument;
import org.karnak.data.profile.IncludedTag;
import org.karnak.data.profile.Profile;
import org.karnak.data.profile.ProfileElement;
import org.karnak.profilepipe.utils.DicomObjectTools;
import org.karnak.profilepipe.utils.ExprDCMElem;
import org.karnak.profilepipe.utils.HMAC;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ProfilesTest {
    private static final HMAC hmacTest = new HMAC("HmacKeyToTEST");

    @Test
    void propagationInSequenceDeletePatientIDButNotInSequence(){
        final DicomObject dataset1 = DicomObject.newDicomObject();
        final DicomObject dataset2 = DicomObject.newDicomObject();

        dataset1.setString(Tag.PatientAge, VR.AS, "075Y");
        dataset1.setString(Tag.StudyInstanceUID, VR.UI, "12345");
        dataset1.setString(Tag.PatientID, VR.LO, "10987654321");
        dataset1.setString(Tag.PatientName, VR.PN, "toto");
        dataset1.setString(Tag.PatientBirthDate, VR.DA, "20200101");
        dataset1.setString(Tag.PatientSex, VR.CS, "M");
        dataset1.setString(Tag.IssuerOfPatientID, VR.LO, "12345678910");
        DicomElement dicomElemSeq1 = dataset1.newDicomSequence(Tag.GroupOfPatientsIdentificationSequence);
        final DicomObject datasetSeq1 = DicomObject.newDicomObject();
        datasetSeq1.setString(Tag.PatientID, VR.LO, "12345");
        DicomElement dicomElemSeq12 = datasetSeq1.newDicomSequence(Tag.IssuerOfPatientIDQualifiersSequence);
        final DicomObject datasetSeq12 = DicomObject.newDicomObject();
        datasetSeq12.setString(Tag.UniversalEntityID, VR.UT, "UT");
        dicomElemSeq12.addItem(datasetSeq12);
        dicomElemSeq1.addItem(datasetSeq1);


        dataset2.setString(Tag.PatientAge, VR.AS, "075Y");
        dataset2.setString(Tag.StudyInstanceUID, VR.UI, "12345");
        dataset2.setString(Tag.PatientName, VR.PN, "toto");
        dataset2.setString(Tag.PatientBirthDate, VR.DA, "20200101");
        dataset2.setString(Tag.PatientSex, VR.CS, "M");
        dataset2.setString(Tag.IssuerOfPatientID, VR.LO, "12345678910");
        DicomElement dicomElemSeq2 = dataset2.newDicomSequence(Tag.GroupOfPatientsIdentificationSequence);
        final DicomObject datasetSeq2 = DicomObject.newDicomObject();
        datasetSeq2.setString(Tag.PatientID, VR.LO, "12345");
        DicomElement dicomElemSeq22 = datasetSeq2.newDicomSequence(Tag.IssuerOfPatientIDQualifiersSequence);
        final DicomObject datasetSeq22 = DicomObject.newDicomObject();
        datasetSeq22.setString(Tag.UniversalEntityID, VR.UT, "UT");
        dicomElemSeq22.addItem(datasetSeq22);
        dicomElemSeq2.addItem(datasetSeq2);

        final Profile profile = new Profile("TEST", "0.9.1", "0.9.1", "DPA");
        final ProfileElement profileElement1 = new ProfileElement("Keep tag Source Group..", "action.on.specific.tags", null, "K", null, 0, profile);
        profileElement1.addIncludedTag(new IncludedTag("(0010,0027)", profileElement1));
        final ProfileElement profileElement2 = new ProfileElement("Remove tag PatientID", "action.on.specific.tags", null, "X", null, 0, profile);
        profileElement2.addIncludedTag(new IncludedTag("(0010,0020)", profileElement2));

        profile.addProfilePipe(profileElement1);
        profile.addProfilePipe(profileElement2);
        final Profiles profiles = new Profiles(profile, hmacTest);
        profiles.applyAction(dataset1, dataset1, "pseudonym", null);
        assertTrue(DicomObjectTools.dicomObjectEquals(dataset2, dataset1));
    }

    @Test
    void propagationInSequence1(){
        final DicomObject dataset1 = DicomObject.newDicomObject();
        final DicomObject dataset2 = DicomObject.newDicomObject();

        dataset1.setString(Tag.PatientAge, VR.AS, "075Y");
        dataset1.setString(Tag.StudyInstanceUID, VR.UI, "12345");
        dataset1.setString(Tag.PatientID, VR.LO, "10987654321");
        dataset1.setString(Tag.PatientName, VR.PN, "toto");
        dataset1.setString(Tag.PatientBirthDate, VR.DA, "20200101");
        dataset1.setString(Tag.PatientSex, VR.CS, "M");
        dataset1.setString(Tag.IssuerOfPatientID, VR.LO, "12345678910");
        DicomElement dicomElemSeq1 = dataset1.newDicomSequence(Tag.GroupOfPatientsIdentificationSequence);
        final DicomObject datasetSeq1 = DicomObject.newDicomObject();
        datasetSeq1.setString(Tag.PatientID, VR.LO, "12345");
        DicomElement dicomElemSeq12 = datasetSeq1.newDicomSequence(Tag.IssuerOfPatientIDQualifiersSequence);
        final DicomObject datasetSeq12 = DicomObject.newDicomObject();
        datasetSeq12.setString(Tag.UniversalEntityID, VR.UT, "UT");
        dicomElemSeq12.addItem(datasetSeq12);
        dicomElemSeq1.addItem(datasetSeq1);


        DicomElement dicomElemSeq2 = dataset2.newDicomSequence(Tag.GroupOfPatientsIdentificationSequence);
        final DicomObject datasetSeq2 = DicomObject.newDicomObject();
        datasetSeq2.setString(Tag.PatientID, VR.LO, "12345");
        DicomElement dicomElemSeq22 = datasetSeq2.newDicomSequence(Tag.IssuerOfPatientIDQualifiersSequence);
        final DicomObject datasetSeq22 = DicomObject.newDicomObject();
        datasetSeq22.setString(Tag.UniversalEntityID, VR.UT, "UT");
        dicomElemSeq22.addItem(datasetSeq22);
        dicomElemSeq2.addItem(datasetSeq2);


        final Profile profile = new Profile("TEST", "0.9.1", "0.9.1", "DPA");
        final ProfileElement profileElement1 = new ProfileElement("Remove tag", "action.on.specific.tags", null, "X", null, 0, profile);
        profileElement1.addIncludedTag(new IncludedTag("(0010,1010)", profileElement1));
        final ProfileElement profileElement2 = new ProfileElement("Keep tag", "action.on.specific.tags", null, "K", null, 0, profile);
        profileElement2.addIncludedTag(new IncludedTag("(0010,0027)", profileElement2));
        final ProfileElement profileElement3 = new ProfileElement("Remove tag", "action.on.specific.tags", null, "X", null, 0, profile);
        profileElement3.addIncludedTag(new IncludedTag("(xxxx,xxxx)", profileElement3));

        profile.addProfilePipe(profileElement1);
        profile.addProfilePipe(profileElement2);
        profile.addProfilePipe(profileElement3);
        final Profiles profiles = new Profiles(profile, hmacTest);
        profiles.applyAction(dataset1, dataset1, "pseudonym", null);
        assertTrue(DicomObjectTools.dicomObjectEquals(dataset2, dataset1));
    }

    @Test
    void propagationInSequence2(){
        final DicomObject dataset1 = DicomObject.newDicomObject();
        final DicomObject dataset2 = DicomObject.newDicomObject();

        dataset1.setString(Tag.PatientAge, VR.AS, "075Y");
        dataset1.setString(Tag.StudyInstanceUID, VR.UI, "12345");
        dataset1.setString(Tag.PatientID, VR.LO, "10987654321");
        dataset1.setString(Tag.PatientName, VR.PN, "toto");
        dataset1.setString(Tag.PatientBirthDate, VR.DA, "20200101");
        dataset1.setString(Tag.PatientSex, VR.CS, "M");
        dataset1.setString(Tag.IssuerOfPatientID, VR.LO, "12345678910");
        DicomElement dicomElemSeq1 = dataset1.newDicomSequence(Tag.GroupOfPatientsIdentificationSequence);
        final DicomObject datasetSeq1 = DicomObject.newDicomObject();
        datasetSeq1.setString(Tag.PatientID, VR.LO, "12345");

        DicomElement dicomElemSeq12 = datasetSeq1.newDicomSequence(Tag.IssuerOfPatientIDQualifiersSequence);
        final DicomObject datasetSeq12 = DicomObject.newDicomObject();
        datasetSeq12.setString(Tag.UniversalEntityID, VR.UT, "UT");
        dicomElemSeq12.addItem(datasetSeq12);
        dicomElemSeq1.addItem(datasetSeq1);


        DicomElement dicomElemSeq2 = dataset2.newDicomSequence(Tag.GroupOfPatientsIdentificationSequence);
        final DicomObject datasetSeq2 = DicomObject.newDicomObject();
        datasetSeq2.setString(Tag.PatientID, VR.LO, "12345");
        DicomElement dicomElemSeq22 = datasetSeq2.newDicomSequence(Tag.IssuerOfPatientIDQualifiersSequence);
        final DicomObject datasetSeq22 = DicomObject.newDicomObject();
        dicomElemSeq22.addItem(datasetSeq22);
        dicomElemSeq2.addItem(datasetSeq2);


        final Profile profile = new Profile("TEST", "0.9.1", "0.9.1", "DPA");
        final ProfileElement profileElement1 = new ProfileElement("Remove tag", "action.on.specific.tags", null, "X", null, 0, profile);
        profileElement1.addIncludedTag(new IncludedTag("(0010,1010)", profileElement1));
        profileElement1.addIncludedTag(new IncludedTag("(0040,0032)", profileElement1));
        final ProfileElement profileElement2 = new ProfileElement("Keep tag", "action.on.specific.tags", null, "K", null, 0, profile);
        profileElement2.addIncludedTag(new IncludedTag("(0010,0027)", profileElement2));
        final ProfileElement profileElement3 = new ProfileElement("Remove tag", "action.on.specific.tags", null, "X", null, 0, profile);
        profileElement3.addIncludedTag(new IncludedTag("(xxxx,xxxx)", profileElement3));

        profile.addProfilePipe(profileElement1);
        profile.addProfilePipe(profileElement2);
        profile.addProfilePipe(profileElement3);
        final Profiles profiles = new Profiles(profile, hmacTest);
        profiles.applyAction(dataset1, dataset1, "pseudonym", null);
        assertTrue(DicomObjectTools.dicomObjectEquals(dataset2, dataset1));
    }

    @Test
    void propagationInSequence3(){
        final DicomObject dataset1 = DicomObject.newDicomObject();
        final DicomObject dataset2 = DicomObject.newDicomObject();

        dataset1.setString(Tag.StudyInstanceUID, VR.UI, "12345");
        dataset1.setString(Tag.PatientID, VR.LO, "10987654321");
        dataset1.setString(Tag.PatientName, VR.PN, "toto");
        dataset1.setString(Tag.PatientBirthDate, VR.DA, "20200101");
        dataset1.setString(Tag.PatientSex, VR.CS, "M");
        dataset1.setString(Tag.IssuerOfPatientID, VR.LO, "12345678910");
        dataset1.setString(Tag.PatientAge, VR.AS, "075Y");
        DicomElement dicomElemSeq1 = dataset1.newDicomSequence(Tag.CTExposureSequence);
        final DicomObject datasetSeq1 = DicomObject.newDicomObject();
        datasetSeq1.setDouble(Tag.EstimatedDoseSaving, VR.FD, 0d);
        datasetSeq1.setDouble(Tag.ExposureTimeInms, VR.FD, 2.099d);
        datasetSeq1.setDouble(Tag.XRayTubeCurrentInmA, VR.FD, 381d);
        datasetSeq1.setDouble(Tag.ExposureInmAs, VR.FD, 800d);
        datasetSeq1.setDouble(Tag.CTDIvol, VR.FD, 47d);
        dicomElemSeq1.addItem(datasetSeq1);

        dataset2.setString(Tag.StudyInstanceUID, VR.UI, "12345");
        dataset2.setString(Tag.PatientID, VR.LO, "10987654321");
        dataset2.setString(Tag.PatientName, VR.PN, "toto");
        dataset2.setString(Tag.PatientBirthDate, VR.DA, "20190101");
        dataset2.setString(Tag.PatientSex, VR.CS, "M");
        dataset2.setString(Tag.IssuerOfPatientID, VR.LO, "12345678910");
        dataset2.setString(Tag.PatientAge, VR.AS, "076Y");
        DicomElement dicomElemSeq2 = dataset2.newDicomSequence(Tag.CTExposureSequence);
        final DicomObject datasetSeq2 = DicomObject.newDicomObject();
        datasetSeq2.setDouble(Tag.ExposureTimeInms, VR.FD, 2.099d);
        datasetSeq2.setDouble(Tag.XRayTubeCurrentInmA, VR.FD, 381d);
        datasetSeq2.setDouble(Tag.ExposureInmAs, VR.FD, 800d);
        datasetSeq2.setDouble(Tag.CTDIvol, VR.FD, 47d);
        dicomElemSeq2.addItem(datasetSeq2);

        final Profile profile = new Profile("TEST", "0.9.1", "0.9.1", "DPA");


        final ProfileElement profileElement1 = new ProfileElement("Shift Date with arguments", "action.on.dates", null, null, "shift", 0, profile);
        profileElement1.addIncludedTag(new IncludedTag("(xxxx,xxxx)", profileElement1));
        profileElement1.addArgument(new Argument("seconds", "60", profileElement1));
        profileElement1.addArgument(new Argument("days", "365", profileElement1));

        final ProfileElement profileElement2 = new ProfileElement("Remove tag", "action.on.specific.tags", null, "X", null, 0, profile);
        profileElement2.addIncludedTag(new IncludedTag("(0018,9324)", profileElement2));

        final ProfileElement profileElement3 = new ProfileElement("Keep tag", "action.on.specific.tags", null, "K", null, 0, profile);
        profileElement3.addIncludedTag(new IncludedTag("(0018,9321)", profileElement3));

        final ProfileElement profileElement4 = new ProfileElement("Replace null", "action.on.specific.tags", null, "Z", null, 0, profile);
        profileElement4.addIncludedTag(new IncludedTag("(0018,9330)", profileElement2));

        profile.addProfilePipe(profileElement1);
        profile.addProfilePipe(profileElement2);
        profile.addProfilePipe(profileElement3);
        profile.addProfilePipe(profileElement4);
        final Profiles profiles = new Profiles(profile, hmacTest);
        profiles.applyAction(dataset1, dataset1, "pseudonym", null);
        assertTrue(DicomObjectTools.dicomObjectEquals(dataset2, dataset1));
    }


    @Test
    void XactionTagsProfile(){
        final DicomObject dataset1 = DicomObject.newDicomObject();
        final DicomObject dataset2 = DicomObject.newDicomObject();

        dataset1.setString(Tag.PatientName, VR.PN, "TEST-Expr-AddAction");
        dataset1.setString(Tag.StudyInstanceUID, VR.UI, "12345");
        dataset1.setString(Tag.PatientAge, VR.AS, "075Y");

        dataset2.setString(Tag.PatientName, VR.PN, "TEST-Expr-AddAction");
        dataset2.setString(Tag.StudyInstanceUID, VR.UI, "12345");

        final Profile profile = new Profile("TEST", "0.9.1", "0.9.1", "DPA");
        final ProfileElement profileElement = new ProfileElement("Remove tag", "action.on.specific.tags", null, "X", null, 0, profile);
        profileElement.addIncludedTag(new IncludedTag("(0010,1010)", profileElement));
        profile.addProfilePipe(profileElement);
        final Profiles profiles = new Profiles(profile, hmacTest);
        profiles.applyAction(dataset1, dataset1, "pseudonym", null);
        assertTrue(DicomObjectTools.dicomObjectEquals(dataset2, dataset1));
    }

    @Test
    void ZactionTagsProfile(){
        final DicomObject dataset1 = DicomObject.newDicomObject();
        final DicomObject dataset2 = DicomObject.newDicomObject();

        dataset1.setString(Tag.PatientName, VR.PN, "TEST-Expr-AddAction");
        dataset1.setString(Tag.StudyInstanceUID, VR.UI, "12345");
        dataset1.setString(Tag.PatientAge, VR.AS, "075Y");

        dataset2.setString(Tag.PatientName, VR.PN, "TEST-Expr-AddAction");
        dataset2.setString(Tag.StudyInstanceUID, VR.UI, "12345");
        dataset2.setNull(Tag.PatientAge, VR.AS);

        Profile profile = new Profile("TEST", "0.9.1", "0.9.1", "DPA");
        ProfileElement profileElement = new ProfileElement("Replace tag by null", "action.on.specific.tags", null, "Z", null, 0, profile);
        profileElement.addIncludedTag(new IncludedTag("(0010,1010)", profileElement));
        profile.addProfilePipe(profileElement);
        final Profiles profiles = new Profiles(profile, hmacTest);
        profiles.applyAction(dataset1, dataset1, "pseudonym", null);
        assertTrue(DicomObjectTools.dicomObjectEquals(dataset2, dataset1));
    }


        @Test
        void shiftDateProfileOptionShift(){
            //SHIFT days: 365, seconds:60
            final DicomObject dataset1 = DicomObject.newDicomObject();
            final DicomObject dataset2 = DicomObject.newDicomObject();

            dataset1.setString(Tag.PatientName, VR.PN, "TEST-Expr-AddAction");
            dataset1.setString(Tag.StudyInstanceUID, VR.UI, "12345");
            dataset1.setString(Tag.PatientAge, VR.AS, "069Y");
            dataset1.setString(Tag.PatientBirthDate, VR.DA, "20080822");
            dataset1.setString(Tag.AcquisitionDateTime, VR.DT, "20080729131503");
            dataset1.setString(Tag.InstanceCreationTime, VR.TM, "131735.000000");

            dataset2.setString(Tag.PatientName, VR.PN, "TEST-Expr-AddAction");
            dataset2.setString(Tag.StudyInstanceUID, VR.UI, "12345");
            dataset2.setString(Tag.PatientAge, VR.AS, "070Y");
            dataset2.setString(Tag.PatientBirthDate, VR.DA, "20070823");
            dataset2.setString(Tag.AcquisitionDateTime, VR.DT, "20070730131403.000000");
            dataset2.setString(Tag.InstanceCreationTime, VR.TM, "131635.000000");

            Profile profile = new Profile("TEST", "0.9.1", "0.9.1", "DPA");
            ProfileElement profileElement = new ProfileElement("Shift Date with arguments", "action.on.dates", null, null, "shift", 0, profile);
            profileElement.addIncludedTag(new IncludedTag("(xxxx,xxxx)", profileElement));
            profileElement.addArgument(new Argument("seconds", "60", profileElement));
            profileElement.addArgument(new Argument("days", "365", profileElement));
            profile.addProfilePipe(profileElement);
            final Profiles profiles = new Profiles(profile, hmacTest);
            profiles.applyAction(dataset1, dataset1, "pseudonym", null);
            assertTrue(DicomObjectTools.dicomObjectEquals(dataset2, dataset1));
        }

    @Test
    void shiftDateProfileOptionShiftRange(){
        //SHIFT range with hmackey: HmacKeyToTEST -> days: 80, seconds:36
        final DicomObject dataset1 = DicomObject.newDicomObject();
        final DicomObject dataset2 = DicomObject.newDicomObject();

        dataset1.setString(Tag.PatientName, VR.PN, "TEST-Expr-AddAction");
        dataset1.setString(Tag.StudyInstanceUID, VR.UI, "12345");
        dataset1.setString(Tag.PatientAge, VR.AS, "069Y");
        dataset1.setString(Tag.PatientBirthDate, VR.DA, "20080822");
        dataset1.setString(Tag.AcquisitionDateTime, VR.DT, "20080729131503");
        dataset1.setString(Tag.InstanceCreationTime, VR.TM, "131735.000000");

        dataset2.setString(Tag.PatientName, VR.PN, "TEST-Expr-AddAction");
        dataset2.setString(Tag.StudyInstanceUID, VR.UI, "12345");
        dataset2.setString(Tag.PatientAge, VR.AS, "069Y");
        dataset2.setString(Tag.PatientBirthDate, VR.DA, "20080703");
        dataset2.setString(Tag.AcquisitionDateTime, VR.DT, "20080609131503.000000");
        dataset2.setString(Tag.InstanceCreationTime, VR.TM, "131735.000000");

        Profile profile = new Profile("TEST", "0.9.1", "0.9.1", "DPA");
        ProfileElement profileElement = new ProfileElement("Shift Date with arguments", "action.on.dates", null, null, "shift_range", 0, profile);
        profileElement.addIncludedTag(new IncludedTag("(xxxx,xxxx)", profileElement));
        profileElement.addArgument(new Argument("max_seconds", "60", profileElement));
        profileElement.addArgument(new Argument("min_days", "50", profileElement));
        profileElement.addArgument(new Argument("max_days", "100", profileElement));

        profile.addProfilePipe(profileElement);
        final Profiles profiles = new Profiles(profile, hmacTest);
        profiles.applyAction(dataset1, dataset1, "pseudonym", null);
        assertTrue(DicomObjectTools.dicomObjectEquals(dataset2, dataset1));
    }

@Test
void XZactionTagsProfile(){
    final DicomObject dataset1 = DicomObject.newDicomObject();
    final DicomObject dataset2 = DicomObject.newDicomObject();

    dataset1.setString(Tag.PatientName, VR.PN, "TEST-Expr-AddAction");
    dataset1.setString(Tag.StudyInstanceUID, VR.UI, "12345");
    dataset1.setString(Tag.PatientAge, VR.AS, "075Y");

    dataset2.setNull(Tag.PatientName, VR.PN);
    dataset2.setNull(Tag.StudyInstanceUID, VR.UI);

    final Profile profile = new Profile("TEST", "0.9.1", "0.9.1", "DPA");
    final ProfileElement profileElement = new ProfileElement("Remove tag", "action.on.specific.tags", null, "X", null, 0, profile);
    profileElement.addIncludedTag(new IncludedTag("(0010,1010)", profileElement));
    profile.addProfilePipe(profileElement);
    final ProfileElement profileElement2 = new ProfileElement("Replace by null", "action.on.specific.tags", null, "Z", null, 0, profile);
    profileElement2.addIncludedTag(new IncludedTag("(xxxx,xxxx)", profileElement));
    profile.addProfilePipe(profileElement2);
    final Profiles profiles = new Profiles(profile, hmacTest);
    profiles.applyAction(dataset1, dataset1, "pseudonym", null);
    assertTrue(DicomObjectTools.dicomObjectEquals(dataset2, dataset1));
}

    @Test
    void KprivateTagsAndXRestProfile(){
        final DicomObject dataset1 = DicomObject.newDicomObject();
        final DicomObject dataset2 = DicomObject.newDicomObject();

        dataset1.setString(Tag.PatientName, VR.PN, "TEST-Expr-AddAction");
        dataset1.setString(Tag.StudyInstanceUID, VR.UI, "12345");
        dataset1.setString(Tag.PatientAge, VR.AS, "069Y");
        dataset1.setString(Tag.PatientBirthDate, VR.DA, "20080822");
        dataset1.setString(Tag.AcquisitionDateTime, VR.DT, "20080729131503");
        dataset1.setString(Tag.InstanceCreationTime, VR.TM, "131735.000000");
        dataset1.setString(0x70531200, VR.LO, "Private Tag");
        dataset1.setString(0x70534200, VR.LO, "Private Tag");
        dataset1.setString(0x70531209, VR.LO, "Private Tag");
        dataset1.setString(0x70534209, VR.LO, "Private Tag");
        dataset1.setString(0x70534205, VR.LO, "Private Tag"); //it's a private tag but it's not in scope

        dataset2.setString(0x70531200, VR.LO, "Private Tag");
        dataset2.setString(0x70534200, VR.LO, "Private Tag");
        dataset2.setString(0x70531209, VR.LO, "Private Tag");
        dataset2.setString(0x70534209, VR.LO, "Private Tag");

        final Profile profile = new Profile("TEST", "0.9.1", "0.9.1", "DPA");
        final ProfileElement profileElement = new ProfileElement("Remove tag", "action.on.privatetags", null, "K", null, 0, profile);
        profileElement.addIncludedTag(new IncludedTag("(7053,xx00)", profileElement));
        profileElement.addIncludedTag(new IncludedTag("(7053,xx09)", profileElement));
        profile.addProfilePipe(profileElement);
        final ProfileElement profileElement2 = new ProfileElement("Replace by null", "action.on.specific.tags", null, "X", null, 0, profile);
        profileElement2.addIncludedTag(new IncludedTag("(xxxx,xxxx)", profileElement));
        profile.addProfilePipe(profileElement2);
        final Profiles profiles = new Profiles(profile, hmacTest);
        profiles.applyAction(dataset1, dataset1, "pseudonym", null);
        assertTrue(DicomObjectTools.dicomObjectEquals(dataset2, dataset1));
    }

    @Test
    void expressionProfile() {

        final DicomObject dataset1 = DicomObject.newDicomObject();
        final DicomObject dataset2 = DicomObject.newDicomObject();

        dataset1.setString(Tag.PatientName, VR.PN, "TEST-Expr-AddAction");
        dataset1.setString(Tag.StudyInstanceUID, VR.UI, "12345");
        dataset1.setString(Tag.PatientAge, VR.AS, "075Y");

        dataset2.setString(Tag.PatientName, VR.PN, "TEST-Expr-AddAction");
        dataset2.setString(Tag.StudyInstanceUID, VR.UI, "12345");


        Profile profile = new Profile("TEST", "0.9.1", "0.9.1", "DPA");
        ProfileElement profileElement = new ProfileElement("Expr", "expression.on.tags", null, null, null, 0, profile);
        profileElement.addArgument(new Argument("expr", "stringValue == '075Y'? Remove() : Keep()", profileElement));
        profileElement.addIncludedTag(new IncludedTag("(xxxx,xxxx)", profileElement));

        profile.addProfilePipe(profileElement);
        final Profiles profiles = new Profiles(profile, hmacTest);
        profiles.applyAction(dataset1, dataset1, "pseudonym", null);
        assertTrue(DicomObjectTools.dicomObjectEquals(dataset2, dataset1));
    }


    //#######################   TEST getResultCondition ############################################
    @ParameterizedTest
    @ValueSource(strings = {"tag == (0010,00xx) or", "tag == (0010,0010) and stringValue == 'CARDIX'", "tag == (0010,00xx)",
            "tag == 0010,00x0) and stringValue == 'CARDIX'", "tag == (00x0,0010", "tag == 001x00x0", "tag == (00x0,0010 and vr == #VR.PN"})
    void getResultConditionTrue1(String input){
        final ExprDCMElem exprDCMElem1 = new ExprDCMElem(TagUtils.intFromHexString("00100010"), VR.PN, "CARDIX");
        assertTrue(Profiles.getResultCondition(input, exprDCMElem1)); // generate an exception
    }

    @ParameterizedTest
    @ValueSource(strings = {"tag == (00x0,0020) and tag == #Tag.PatientID", "tag == 001xxx20 or #Tag.PatientName",
            "tag <= 2096928 and tag >= 1048608", "tag <= 001FFF20 and tag >= 00100020", "tag < 1048609",
            "tag == (00x0,0020) and tag == #Tag.PatientID and vr == #VR.AE"})
    void getResultConditionTrue2(String input){
        final ExprDCMElem exprDCMElem2 = new ExprDCMElem(TagUtils.intFromHexString("00100020"), VR.AE, "AE_TITLE"); //tag decimal = 1048608
        assertTrue(Profiles.getResultCondition(input, exprDCMElem2)); // generate an exception
    }

    @ParameterizedTest
    @ValueSource(strings = {"tag == 02100220)", "tag == (02100220 and vr == #VR.DA", "tag == 0210,0220 and stringValue == '1M'",
            "tag == 0210,0220 and stringValue == '1M' and vr == #VR.DA", "tag == 0210,0220)"})
    void getResultConditionTrue3(String input){
        final ExprDCMElem exprDCMElem3 = new ExprDCMElem(TagUtils.intFromHexString("02100220"), VR.DA, "1M");
        assertTrue(Profiles.getResultCondition(input, exprDCMElem3)); // generate an exception
    }

    @ParameterizedTest
    @ValueSource(strings = {"tag == (0010,0010) and stringValue == 'PANORAMIX'", "tag == 2222,00x0) and stringValue == 'CARDIX'",
            "tag == (00x0,0010 and vr == #VR.AE", "tag == 1" })
    void getResultConditionFalse1(String input){
        final ExprDCMElem exprDCMElem1 = new ExprDCMElem(TagUtils.intFromHexString("00100010"), VR.PN, "CARDIX");
        assertFalse(Profiles.getResultCondition(input, exprDCMElem1)); // generate an exception
    }

    @ParameterizedTest
    @ValueSource(strings = {"tag == (00x0,0020) and tag == #Tag.PatientName", "tag == (0010,0010) or tag == #Tag.PatientName", "tag < 1048608" })
    void getResultConditionFalse2(String input){
        final ExprDCMElem exprDCMElem2 = new ExprDCMElem(TagUtils.intFromHexString("00100020"), VR.AE, "AE_TITLE"); //tag decimal = 1048608
        assertFalse(Profiles.getResultCondition(input, exprDCMElem2)); // generate an exception
    }

    @ParameterizedTest
    @ValueSource(strings = {"tag == 12100220)", "tag == 12100220)", "tag == 0210,0220 and stringValue == '1'", "tag == 2210,0220 and stringValue == '1' and vr == #VR.PN"})
    void getResultConditionFalse3(String input){
        final ExprDCMElem exprDCMElem3 = new ExprDCMElem(TagUtils.intFromHexString("02100220"), VR.DA, "1M");
        assertFalse(Profiles.getResultCondition(input, exprDCMElem3)); // generate an exception
    }
}