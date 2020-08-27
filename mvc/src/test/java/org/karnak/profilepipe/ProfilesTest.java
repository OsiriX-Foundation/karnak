package org.karnak.profilepipe;

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


import static org.junit.jupiter.api.Assertions.*;

class ProfilesTest {
    private static final HMAC hmacTest = new HMAC("HMACTEST");


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
        profiles.apply(dataset1, true);
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
        profiles.apply(dataset1, true);
        assertTrue(DicomObjectTools.dicomObjectEquals(dataset2, dataset1));
    }


    @Test
    void shiftDateProfile(){
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

        //TEST replace null profile.
        Profile profile = new Profile("TEST", "0.9.1", "0.9.1", "DPA");
        ProfileElement profileElement = new ProfileElement("Shift Date with arguments", "action.on.dates", null, null, "shift", 0, profile);
        profileElement.addIncludedTag(new IncludedTag("(xxxx,xxxx)", profileElement));
        profileElement.addArgument(new Argument("seconds", "60", profileElement));
        profileElement.addArgument(new Argument("days", "365", profileElement));
        profile.addProfilePipe(profileElement);
        final Profiles profiles = new Profiles(profile, hmacTest);
        profiles.apply(dataset1, true);
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
        profiles.apply(dataset1, true);
        assertTrue(DicomObjectTools.dicomObjectEquals(dataset2, dataset1));
    }


    @Test
    void expressionProfile() {
        /*
        //TEST expression profile with tagIsPresent() method, Add() method and Keep() method.
        profileElementExpr = new ProfileElement("expr Add tag", "expression.on.tags", null, null, null, 0, profileExpressions);
        profileElementExpr.addArgument(new Argument("expr", "tagIsPresent(#Tag.PatientAge) == false? Add(#Tag.PatientAge, #VR.AS, '075Y') : Keep()", profileElementExpr));
        profileElementExpr.addIncludedTag(new IncludedTag("(xxxx,xxxx)", profileElementExpr));
        profileExpressions.addProfilePipe(profileElementExpr);
        profiles = new Profiles(profileExpressions, hmacTest);
        profiles.apply(dataset1, true);
        assertTrue(DicomObjectTools.dicomObjectEquals(dataset2, dataset1));


        profileElementExpr = new ProfileElement("expr Remove tag", "expression.on.tags", null, null, null, 0, profileExpressions);
        profileElementExpr.addArgument(new Argument("expr", "stringValue == '075Y'? Remove() : Keep()", profileElementExpr));
        profileElementExpr.addIncludedTag(new IncludedTag("(xxxx,xxxx)", profileElementExpr));
        profileExpressions.addProfilePipe(profileElementExpr);
        profiles = new Profiles(profileExpressions, hmacTest);
        profiles.apply(dataset2, true);
        assertTrue(DicomObjectTools.dicomObjectEquals(dataset1, dataset2));
        */
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