package org.karnak.profilepipe.utils;

import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExprDCMElemTest {

    @Test
    void conditionInterpreter() {
        final ExprDCMElem exprDCMElem = new ExprDCMElem(TagUtils.intFromHexString("00100010"), VR.PN, "CARDIX");
        assertEquals("tag == 1048592 and stringValue == 'CARDIX'", exprDCMElem.conditionInterpreter("tag == (0010,0010) and stringValue == 'CARDIX'"));
        assertEquals("tag == 1048592", exprDCMElem.conditionInterpreter("tag == (0010,00xx)"));
        assertEquals("tag == 1048592 and stringValue == 'CARDIX'", exprDCMElem.conditionInterpreter("tag == 0010,00x0) and stringValue == 'CARDIX'"));
        assertEquals("tag == 1048592", exprDCMElem.conditionInterpreter("tag == (00x0,0010"));
        assertEquals("tag == 1048592", exprDCMElem.conditionInterpreter("tag == 001x00x0"));

        final ExprDCMElem exprDCMElem2 = new ExprDCMElem(TagUtils.intFromHexString("00100020"), VR.PN, "CARDIX");
        assertEquals("tag == 1048608 and tag == #Tag.PatientName", exprDCMElem2.conditionInterpreter("tag == (00x0,0020) and tag == #Tag.PatientName"));
        assertEquals("tag == 1048608", exprDCMElem2.conditionInterpreter("tag == 001xxx20"));
        assertEquals("tag == 1048608", exprDCMElem2.conditionInterpreter("tag == 0010002x"));
        assertEquals("tag == 1048608", exprDCMElem2.conditionInterpreter("tag == x010002x"));

        final ExprDCMElem exprDCMElem3 = new ExprDCMElem(TagUtils.intFromHexString("02100220"), VR.PN, "CADRIX");
        assertEquals("tag == 34603552", exprDCMElem3.conditionInterpreter("tag == 02100220)"));
        assertEquals("tag == 34603552", exprDCMElem3.conditionInterpreter("tag == (02100220"));
        assertEquals("tag == 34603552", exprDCMElem3.conditionInterpreter("tag == 0210,0220"));
        assertEquals("tag == 34603552", exprDCMElem3.conditionInterpreter("tag == 0210,0220)"));
    }

    @Test
    void isHexTag() {
        assertEquals(true, ExprDCMElem.isHexTag("(0221,0000)"));
        assertEquals(true, ExprDCMElem.isHexTag("0xx1,0000)"));
        assertEquals(true, ExprDCMElem.isHexTag("0xx1,0000"));
        assertEquals(true, ExprDCMElem.isHexTag("(0xx1,0000"));

        assertEquals(true, ExprDCMElem.isHexTag("(02210000)"));
        assertEquals(true, ExprDCMElem.isHexTag("0xx10000)"));
        assertEquals(true, ExprDCMElem.isHexTag("0xx10000"));
        assertEquals(true, ExprDCMElem.isHexTag("(0xx10000"));

        assertEquals(true, ExprDCMElem.isHexTag("02210000)"));
        assertEquals(true, ExprDCMElem.isHexTag("0x010000)"));
        assertEquals(true, ExprDCMElem.isHexTag("00010000"));
        assertEquals(true, ExprDCMElem.isHexTag("(00010000"));


        assertEquals(true, ExprDCMElem.isHexTag("0201,0000"));

        assertEquals(false, ExprDCMElem.isHexTag("ef00)"));
        assertEquals(false, ExprDCMElem.isHexTag("(e,0000)"));
        assertEquals(false, ExprDCMElem.isHexTag("xx1,0000)"));
        assertEquals(false, ExprDCMElem.isHexTag("xx1,00 00)"));

    }
}