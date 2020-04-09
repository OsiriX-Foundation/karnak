package org.karnak.profile.action;

import java.util.Optional;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;

public class DReplace implements Action{

    private Algorithm algo = new Algorithm();

    public void execute(DicomObject dcm, int tag) {
        Optional<DicomElement> dcmItem = dcm.get(tag);
        if(dcmItem.isPresent()) {
            DicomElement dcmEl = dcmItem.get();
            String valueString = dcm.getString(tag).orElse(null);
            System.out.println(valueString);
            int seed = 3; // ByteBuffer.wrap(value).getInt();
            String vrValue = this.algo.execute(dcmEl.vr(), seed);
            if (vrValue != "-1") {
                dcm.setString(tag, dcmEl.vr(), vrValue);
            }
            
//            var type = switch (dcmEl.vr()) {
//                case AE, AT, CS, LO, SH, UC, UI -> TagType.STRING;
//                case LT, ST, UT -> TagType.TEXT;
//                case IS, SL, SS, US, UL -> TagType.INTEGER;
//                case FL -> TagType.FLOAT;
//                case DS, FD -> TagType.DOUBLE;
//                case DA -> TagType.DICOM_DATE;
//                case TM -> TagType.DICOM_TIME;
//                case SQ -> TagType.DICOM_SEQUENCE;
//                case PN -> TagType.DICOM_PERSON_NAME;
//                case AS -> TagType.DICOM_PERIOD;
//                case UR -> TagType.URI;
//                default -> TagType.BYTE;
//            };
        }
    }
}