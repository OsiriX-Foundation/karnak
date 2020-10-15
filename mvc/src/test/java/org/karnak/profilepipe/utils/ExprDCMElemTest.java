package org.karnak.profilepipe.utils;

import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.junit.jupiter.api.Test;
import org.karnak.expression.ExprDCMElem;

import static org.junit.jupiter.api.Assertions.*;

class ExprDCMElemTest {

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