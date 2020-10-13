package org.karnak.profilepipe.option.datemanager;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.karnak.data.profile.Argument;
import org.karnak.profilepipe.utils.HMAC;

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

    /*
    @Test
    void shift() {
        HMAC hmac = new HMAC("HmacKeyToTEST");
        ShiftRangeDate shiftRangeDate = new ShiftRangeDate(hmac);
        String patientID = "179123795467495361251232164722472023966";

        assertEquals("20171009",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyDate).orElse(null), arguments, patientID)
        );
        assertEquals("115825.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyTime).orElse(null), arguments, patientID)
        );
        assertEquals("043Y",
                shiftRangeDate.shift(dataset, dataset.get(Tag.PatientAge).orElse(null), arguments, patientID)
        );
        assertEquals("20171009115836.354000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionDateTime).orElse(null), arguments, patientID)
        );
        assertEquals("235116.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, patientID)
        );

        String patientID_2 = "MustBeAnotherValues";

        assertEquals("20180204",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyDate).orElse(null), arguments, patientID_2)
        );
        assertEquals("120818.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyTime).orElse(null), arguments, patientID_2)
        );
        assertEquals("043Y",
                shiftRangeDate.shift(dataset, dataset.get(Tag.PatientAge).orElse(null), arguments, patientID_2)
        );
        assertEquals("20180204120829.354000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionDateTime).orElse(null), arguments, patientID_2)
        );
        assertEquals("000109.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, patientID_2)
        );


        min_seconds.setKey("min_seconds");
        min_seconds.setValue("500");
        min_days.setKey("min_days");
        min_days.setValue("100");

        arguments.add(min_seconds);
        arguments.add(min_days);

        assertEquals("20170901",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyDate).orElse(null), arguments, patientID)
        );
        assertEquals("115514.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyTime).orElse(null), arguments, patientID)
        );
        assertEquals("043Y",
                shiftRangeDate.shift(dataset, dataset.get(Tag.PatientAge).orElse(null), arguments, patientID)
        );
        assertEquals("20170901115525.354000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionDateTime).orElse(null), arguments, patientID)
        );
        assertEquals("234805.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, patientID)
        );

        assertEquals("20171030",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyDate).orElse(null), arguments, patientID_2)
        );
        assertEquals("120011.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.StudyTime).orElse(null), arguments, patientID_2)
        );
        assertEquals("043Y",
                shiftRangeDate.shift(dataset, dataset.get(Tag.PatientAge).orElse(null), arguments, patientID_2)
        );
        assertEquals("20171030120022.354000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionDateTime).orElse(null), arguments, patientID_2)
        );
        assertEquals("235302.000000",
                shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, patientID_2)
        );


        max_seconds.setKey("test_max_seconds");
        max_days.setKey("max_days");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, patientID);
        });

        max_seconds.setKey("test_max_seconds");
        max_days.setKey("max_days");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, patientID);
        });

        max_seconds.setKey("max_seconds");
        max_days.setKey("test_max_days");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, patientID);
        });

        max_seconds.setKey("test_max_seconds");
        max_days.setKey("test_max_days");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments, patientID);
        });


        List<Argument> arguments_2 = new ArrayList<>();
        Argument arg_1 = new Argument();
        arg_1.setKey("max_seconds");
        arg_1.setValue("12");
        arguments_2.add(arg_1);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments_2, patientID);
        });

        arg_1.setKey("max_days");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments_2, patientID);
        });

        arg_1.setKey("min_seconds");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments_2, patientID);
        });

        arg_1.setKey("min_days");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), arguments_2, patientID);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            shiftRangeDate.shift(dataset, dataset.get(Tag.AcquisitionTime).orElse(null), new ArrayList<>(), patientID);
        });
    }
    */
}