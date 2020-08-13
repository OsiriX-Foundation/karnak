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
        assertEquals("071907.0705", ShiftDate.ASbyDays("070907.0705", 1)); //???????
        assertEquals("1020", ShiftDate.ASbyDays("1010", 1));
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
        assertEquals("20180729131503", ShiftDate.ASbyDays("20080729131503", 1)); // ?????

    }
}