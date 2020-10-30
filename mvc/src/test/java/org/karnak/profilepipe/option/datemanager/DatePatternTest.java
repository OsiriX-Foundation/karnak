package org.karnak.profilepipe.option.datemanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.karnak.data.profile.Argument;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatePatternTest {

    @Test
    void formatDA() {
        assertEquals("20080801", DateFormat.formatDA("20080822", "day"));
        assertEquals("20080101", DateFormat.formatDA("20080822", "month_day"));
    }

    @Test
    void formatDT() {
        assertEquals("20080801131503.000000", DateFormat.formatDT("20080822131503", "day"));
        assertEquals("20080101131503.000000", DateFormat.formatDT("20080822131503", "month_day"));
    }

    @Test
    void verifyPatternArguments() {
        List<Argument> argsFalse = new ArrayList<>();
        argsFalse.add(new Argument("second", "day", null));

        List<Argument> argsFalse2 = new ArrayList<>();
        argsFalse.add(new Argument("remove", "daytt", null));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DateFormat.verifyPatternArguments(argsFalse);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DateFormat.verifyPatternArguments(argsFalse2);
        });

    }

}
