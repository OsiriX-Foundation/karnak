package org.karnak.profilepipe;

import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.junit.jupiter.api.Test;
import org.karnak.profilepipe.utils.ExprDCMElem;

import static org.junit.jupiter.api.Assertions.*;

class ProfilesTest {

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