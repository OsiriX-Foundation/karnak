package org.karnak.profilepipe;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.karnak.data.profile.Argument;
import org.karnak.data.profile.IncludedTag;
import org.karnak.data.profile.Profile;
import org.karnak.data.profile.ProfileElement;
import org.karnak.profilepipe.utils.DicomObjectTools;
import org.karnak.profilepipe.utils.ExprDCMElem;
import org.karnak.profilepipe.utils.HMAC;


import static org.junit.jupiter.api.Assertions.*;

class ProfilesTest {
    private static final HMAC hmacTest = new HMAC("0123456789");
    private static DicomObject dataset1 = DicomObject.newDicomObject();
    private static DicomObject dataset2 = DicomObject.newDicomObject();

    private static Profile profileExpressions = new Profile("TEST-Expr-AddAction", "0.9.1", "0.9.1", "DPA");
    private static ProfileElement profileElementExpr;
    private static Profiles profiles;
    @BeforeAll
    protected static void setUpBeforeClass() throws Exception {
        //Datasets
        dataset1.setString(Tag.PatientName, VR.PN, "TEST-Expr-AddAction");
        dataset1.setString(Tag.StudyInstanceUID, VR.UI, "250");

        dataset2.setString(Tag.PatientName, VR.PN, "TEST-Expr-AddAction");
        dataset2.setString(Tag.StudyInstanceUID, VR.UI, "250");
        dataset2.setString(Tag.PatientAge, VR.AS, "075Y");
    }


    @Test
    void expressionProfile() {
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
    }

    @Test
    void getResultCondition() {
        final ExprDCMElem exprDCMElem1 = new ExprDCMElem(TagUtils.intFromHexString("00100010"), VR.PN, "CARDIX");
        assertEquals(true, Profiles.getResultCondition("tag == (0010,00xx) or", exprDCMElem1)); // generate an exception
        assertEquals(true, Profiles.getResultCondition("tag == (0010,0010) and stringValue == 'CARDIX'", exprDCMElem1));
        assertEquals(true, Profiles.getResultCondition("tag == (0010,00xx)", exprDCMElem1));
        assertEquals(true, Profiles.getResultCondition("tag == 0010,00x0) and stringValue == 'CARDIX'", exprDCMElem1));
        assertEquals(true, Profiles.getResultCondition("tag == (00x0,0010", exprDCMElem1));
        assertEquals(true, Profiles.getResultCondition("tag == 001x00x0", exprDCMElem1));
        assertEquals(true, Profiles.getResultCondition("tag == (00x0,0010 and vr == #VR.PN", exprDCMElem1));

        assertEquals(false, Profiles.getResultCondition("tag == (0010,0010) and stringValue == 'PANORAMIX'", exprDCMElem1));
        assertEquals(false, Profiles.getResultCondition("tag == 2222,00x0) and stringValue == 'CARDIX'", exprDCMElem1));
        assertEquals(false, Profiles.getResultCondition("tag == (00x0,0010 and vr == #VR.AE", exprDCMElem1));
        assertEquals(false, Profiles.getResultCondition("tag == 1", exprDCMElem1));

        final ExprDCMElem exprDCMElem2 = new ExprDCMElem(TagUtils.intFromHexString("00100020"), VR.AE, "AE_TITLE"); //tag decimal = 1048608
        assertEquals(true, Profiles.getResultCondition("tag == (00x0,0020) and tag == #Tag.PatientID", exprDCMElem2));
        assertEquals(true, Profiles.getResultCondition("tag == 001xxx20 or #Tag.PatientName", exprDCMElem2));
        assertEquals(true, Profiles.getResultCondition("tag <= 2096928 and tag >= 1048608", exprDCMElem2));
        assertEquals(true, Profiles.getResultCondition("tag <= 001FFF20 and tag >= 00100020", exprDCMElem2));
        assertEquals(true, Profiles.getResultCondition("tag < 1048609", exprDCMElem2));
        assertEquals(true, Profiles.getResultCondition("tag == (00x0,0020) and tag == #Tag.PatientID and vr == #VR.AE", exprDCMElem2));

        assertEquals(false, Profiles.getResultCondition("tag == (00x0,0020) and tag == #Tag.PatientName", exprDCMElem2));
        assertEquals(false, Profiles.getResultCondition("tag == (0010,0010) or tag == #Tag.PatientName", exprDCMElem2));
        assertEquals(false, Profiles.getResultCondition("tag < 1048608", exprDCMElem2));


        final ExprDCMElem exprDCMElem3 = new ExprDCMElem(TagUtils.intFromHexString("02100220"), VR.DA, "1M");
        assertEquals(true, Profiles.getResultCondition("tag == 02100220)", exprDCMElem3));
        assertEquals(true, Profiles.getResultCondition("tag == (02100220 and vr == #VR.DA", exprDCMElem3));
        assertEquals(true, Profiles.getResultCondition("tag == 0210,0220 and stringValue == '1M'", exprDCMElem3));
        assertEquals(true, Profiles.getResultCondition("tag == 0210,0220 and stringValue == '1M' and vr == #VR.DA", exprDCMElem3));
        assertEquals(true, Profiles.getResultCondition("tag == 0210,0220)", exprDCMElem3));

        assertEquals(false, Profiles.getResultCondition("tag == 12100220)", exprDCMElem3));
        assertEquals(false, Profiles.getResultCondition("tag == (02100220 and vr == #VR.AE", exprDCMElem3));
        assertEquals(false, Profiles.getResultCondition("tag == 0210,0220 and stringValue == '1'", exprDCMElem3));
        assertEquals(false, Profiles.getResultCondition("tag == 2210,0220 and stringValue == '1' and vr == #VR.PN", exprDCMElem3));
    }
}