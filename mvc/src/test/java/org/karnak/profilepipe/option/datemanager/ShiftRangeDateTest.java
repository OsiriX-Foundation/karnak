package org.karnak.profilepipe.option.datemanager;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.karnak.data.profile.Argument;
import org.karnak.profilepipe.utils.HMAC;
import org.karnak.profilepipe.utils.HashContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShiftRangeDateTest {
    private static DicomObject dataset = DicomObject.newDicomObject();
    private static List<Argument> arguments = new ArrayList<>();
    private static Argument max_seconds = new Argument();
    private static Argument max_days = new Argument();
    private static Argument min_seconds = new Argument();
    private static Argument min_days = new Argument();

    @BeforeAll
    protected static void setUpBeforeClass() throws Exception {
        max_seconds.setKey("max_seconds");
        max_seconds.setValue("1000");
        max_days.setKey("max_days");
        max_days.setValue("200");

        arguments.add(max_seconds);
        arguments.add(max_days);
        dataset.setString(Tag.StudyDate, VR.DA, "20180209");
        dataset.setString(Tag.StudyTime, VR.TM, "120843");
        dataset.setString(Tag.PatientAge, VR.AS, "043Y");
        dataset.setString(Tag.AcquisitionDateTime, VR.DT, "20180209120854.354");
        dataset.setString(Tag.AcquisitionTime, VR.TM, "000134");
    }

    @Test
    void shift() {
        byte[] HMAC_KEY = {-116, -11, -20, 53, -37, -94, 64, 103, 63, -89, -108, -70, 84, 43, -74, -8};
        String Patient_ID = "Patient 1";
        HashContext hashContext = new HashContext(HMAC_KEY, Patient_ID);
        HMAC hmac = new HMAC(hashContext);

        String Patient_ID_2 = "Patient 2";
        byte[] HMAC_KEY_2 = {-57, -80, 125, -55, 54, 85, 52, 102, 20, -116, -78, -6, 108, 47, -37, -43};
        HashContext hashContext_2 = new HashContext(HMAC_KEY_2, Patient_ID_2);
        HMAC hmac_2 = new HMAC(hashContext_2);

        ShiftRangeDate shiftRangeDate = new ShiftRangeDate();

        assertEquals("20171001",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyDate).orElse(null), arguments, hmac)
        );
        assertEquals("115745.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyTime).orElse(null), arguments, hmac)
        );
        assertEquals("043Y",
                shiftRangeDate.shift(dataset, dataset.get(Tag.PatientAge).orElse(null), arguments, hmac)
        );
        assertEquals("20171001115756.354000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionDateTime).orElse(null), arguments, hmac)
        );
        assertEquals("235036.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, hmac)
        );

        assertEquals("20171114",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyDate).orElse(null), arguments, hmac_2)
        );
        assertEquals("120126.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyTime).orElse(null), arguments, hmac_2)
        );
        assertEquals("043Y",
                shiftRangeDate.shift(dataset, dataset.get(Tag.PatientAge).orElse(null), arguments, hmac_2)
        );
        assertEquals("20171114120137.354000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionDateTime).orElse(null), arguments, hmac_2)
        );
        assertEquals("235417.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, hmac_2)
        );


        min_seconds.setKey("min_seconds");
        min_seconds.setValue("500");
        min_days.setKey("min_days");
        min_days.setValue("100");

        arguments.add(min_seconds);
        arguments.add(min_days);

        assertEquals("20170828",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyDate).orElse(null), arguments, hmac)
        );
        assertEquals("115454.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyTime).orElse(null), arguments, hmac)
        );
        assertEquals("043Y",
                shiftRangeDate.shift(dataset, dataset.get(Tag.PatientAge).orElse(null), arguments, hmac)
        );
        assertEquals("20170828115505.354000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionDateTime).orElse(null), arguments, hmac)
        );
        assertEquals("234745.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, hmac)
        );

        assertEquals("20170919",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyDate).orElse(null), arguments, hmac_2)
        );
        assertEquals("115645.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyTime).orElse(null), arguments, hmac_2)
        );
        assertEquals("043Y",
                shiftRangeDate.shift(dataset, dataset.get(Tag.PatientAge).orElse(null), arguments, hmac_2)
        );
        assertEquals("20170919115656.354000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionDateTime).orElse(null), arguments, hmac_2)
        );
        assertEquals("234936.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, hmac_2)
        );


        max_seconds.setKey("test_max_seconds");
        max_days.setKey("max_days");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, hmac);
        });

        max_seconds.setKey("test_max_seconds");
        max_days.setKey("max_days");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, hmac);
        });

        max_seconds.setKey("max_seconds");
        max_days.setKey("test_max_days");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, hmac);
        });

        max_seconds.setKey("test_max_seconds");
        max_days.setKey("test_max_days");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, hmac);
        });


        List<Argument> arguments_2 = new ArrayList<>();
        Argument arg_1 = new Argument();
        arg_1.setKey("max_seconds");
        arg_1.setValue("12");
        arguments_2.add(arg_1);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments_2, hmac);
        });

        arg_1.setKey("max_days");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments_2, hmac);
        });

        arg_1.setKey("min_seconds");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments_2, hmac);
        });

        arg_1.setKey("min_days");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments_2, hmac);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), new ArrayList<>(), hmac);
        });
    }
}