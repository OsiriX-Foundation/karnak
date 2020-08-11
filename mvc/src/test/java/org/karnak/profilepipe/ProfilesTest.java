package org.karnak.profilepipe;

import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.junit.jupiter.api.Test;
import org.karnak.profilepipe.utils.MyDCMElem;

import static org.junit.jupiter.api.Assertions.*;

class ProfilesTest {

    @Test
    void getResultCondition() {
        final MyDCMElem myDCMElem1 = new MyDCMElem(TagUtils.intFromHexString("00100010"), VR.PN, "CARDIX");
        assertEquals(true, Profiles.getResultCondition("tag == (0010,00xx) or", myDCMElem1)); // generate an exception
        assertEquals(true, Profiles.getResultCondition("tag == (0010,0010) and stringValue == 'CARDIX'", myDCMElem1));
        assertEquals(true, Profiles.getResultCondition("tag == (0010,00xx)", myDCMElem1));
        assertEquals(true, Profiles.getResultCondition("tag == 0010,00x0) and stringValue == 'CARDIX'", myDCMElem1));
        assertEquals(true, Profiles.getResultCondition("tag == (00x0,0010", myDCMElem1));
        assertEquals(true, Profiles.getResultCondition("tag == 001x00x0", myDCMElem1));
        assertEquals(true, Profiles.getResultCondition("tag == (00x0,0010 and vr == #VR.PN", myDCMElem1));

        assertEquals(false, Profiles.getResultCondition("tag == (0010,0010) and stringValue == 'PANORAMIX'", myDCMElem1));
        assertEquals(false, Profiles.getResultCondition("tag == 2222,00x0) and stringValue == 'CARDIX'", myDCMElem1));
        assertEquals(false, Profiles.getResultCondition("tag == (00x0,0010 and vr == #VR.AE", myDCMElem1));
        assertEquals(false, Profiles.getResultCondition("tag == 1", myDCMElem1));

        final MyDCMElem myDCMElem2 = new MyDCMElem(TagUtils.intFromHexString("00100020"), VR.AE, "AE_TITLE"); //tag decimal = 1048608
        assertEquals(true, Profiles.getResultCondition("tag == (00x0,0020) and tag == #TAG.PatientID", myDCMElem2));
        assertEquals(true, Profiles.getResultCondition("tag == 001xxx20 or #TAG.PatientName", myDCMElem2));
        assertEquals(true, Profiles.getResultCondition("tag <= 2096928 and tag >= 1048608", myDCMElem2));
        assertEquals(true, Profiles.getResultCondition("tag <= 001FFF20 and tag >= 00100020", myDCMElem2));
        assertEquals(true, Profiles.getResultCondition("tag < 1048609", myDCMElem2));
        assertEquals(true, Profiles.getResultCondition("tag == (00x0,0020) and tag == #TAG.PatientID and vr == #VR.AE", myDCMElem2));

        assertEquals(false, Profiles.getResultCondition("tag == (00x0,0020) and tag == #TAG.PatientName", myDCMElem2));
        assertEquals(false, Profiles.getResultCondition("tag == (0010,0010) or tag == #TAG.PatientName", myDCMElem2));
        assertEquals(false, Profiles.getResultCondition("tag < 1048608", myDCMElem2));


        final MyDCMElem myDCMElem3 = new MyDCMElem(TagUtils.intFromHexString("02100220"), VR.DA, "1M");
        assertEquals(true, Profiles.getResultCondition("tag == 02100220)", myDCMElem3));
        assertEquals(true, Profiles.getResultCondition("tag == (02100220 and vr == #VR.DA", myDCMElem3));
        assertEquals(true, Profiles.getResultCondition("tag == 0210,0220 and stringValue == '1M'", myDCMElem3));
        assertEquals(true, Profiles.getResultCondition("tag == 0210,0220 and stringValue == '1M' and vr == #VR.DA", myDCMElem3));
        assertEquals(true, Profiles.getResultCondition("tag == 0210,0220)", myDCMElem3));

        assertEquals(false, Profiles.getResultCondition("tag == 12100220)", myDCMElem3));
        assertEquals(false, Profiles.getResultCondition("tag == (02100220 and vr == #VR.AE", myDCMElem3));
        assertEquals(false, Profiles.getResultCondition("tag == 0210,0220 and stringValue == '1'", myDCMElem3));
        assertEquals(false, Profiles.getResultCondition("tag == 2210,0220 and stringValue == '1' and vr == #VR.PN", myDCMElem3));




    }
}