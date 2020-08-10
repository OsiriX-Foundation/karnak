package org.karnak.profilepipe.utils;

import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.junit.jupiter.api.Assertions.*;

class MyDCMElemTest {

    @Test
    void conditionInterpreter() {
    }

    @Test
    void isHexTag() {
        assertEquals(true, MyDCMElem.isHexTag("(0221,0000)"));
        assertEquals(true, MyDCMElem.isHexTag("0xx1,0000)"));
        assertEquals(true, MyDCMElem.isHexTag("0xx1,0000"));
        assertEquals(true, MyDCMElem.isHexTag("(0xx1,0000"));

        assertEquals(true, MyDCMElem.isHexTag("(02210000)"));
        assertEquals(true, MyDCMElem.isHexTag("0xx10000)"));
        assertEquals(true, MyDCMElem.isHexTag("0xx10000"));
        assertEquals(true, MyDCMElem.isHexTag("(0xx10000"));

        assertEquals(true, MyDCMElem.isHexTag("02210000)"));
        assertEquals(true, MyDCMElem.isHexTag("0x010000)"));
        assertEquals(true, MyDCMElem.isHexTag("00010000"));
        assertEquals(true, MyDCMElem.isHexTag("(00010000"));


        assertEquals(true, MyDCMElem.isHexTag("0201,0000"));

        assertEquals(false, MyDCMElem.isHexTag("ef00)"));
        assertEquals(false, MyDCMElem.isHexTag("(e,0000)"));
        assertEquals(false, MyDCMElem.isHexTag("xx1,0000)"));
        assertEquals(false, MyDCMElem.isHexTag("xx1,00 00)"));

    }
}