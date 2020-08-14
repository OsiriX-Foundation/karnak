package org.karnak.profilepipe.option.datemanager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShiftDateTest {

    @Test
    void DAbyDays() {
        assertEquals("19930822", ShiftDate.DAbyDays("19930823", 1));
        assertEquals("20391231", ShiftDate.DAbyDays("20400120", 20));
        assertEquals("19920102", ShiftDate.DAbyDays("1993", 365));
    }

    @Test
    void TMbySeconds() {
        assertEquals("070906.070500", ShiftDate.TMbySeconds("070907.0705", 1));
        assertEquals("100959.000000", ShiftDate.TMbySeconds("1010", 1));
    }

    @Test
    void ASbyDays() {
        assertEquals("019M", ShiftDate.ASbyDays("018M", 40));
        assertEquals("009M", ShiftDate.ASbyDays("008M", 40));
        assertEquals("009M", ShiftDate.ASbyDays("009M", 20));
        assertEquals("002Y", ShiftDate.ASbyDays("001Y", 365));
        assertEquals("031Y", ShiftDate.ASbyDays("029Y", 730));
        assertEquals("009D", ShiftDate.ASbyDays("008D", 1));
    }

    @Test
    void DTbyDays() {
        assertEquals("20180301", ShiftDate.DTbyDays("20180302", 1));
        assertEquals("20080728131503.000000", ShiftDate.DTbyDays("20080729131503", 1));
        assertEquals("20201211000030.000000", ShiftDate.DTbyDays("20201212000030", 1));
        assertEquals("20201211000130.000000", ShiftDate.DTbyDays("20201212000130", 1));
    }
}