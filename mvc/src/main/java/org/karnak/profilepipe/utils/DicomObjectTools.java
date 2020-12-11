package org.karnak.profilepipe.utils;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;

import java.util.Iterator;

public class DicomObjectTools {

    public static boolean dicomObjectEquals(Object o1, Object o2){
        if (o1 == o2)
            return true;
        else if (o1 == null || o2 == null)
            return false;

        if (!(o1 instanceof DicomObject) && !(o2 instanceof DicomObject)) return false;

        Iterator<DicomElement> dicomElementIterator1 = ((DicomObject) o1).iterator();
        Iterator<DicomElement> dicomElementIterator2 = ((DicomObject) o2).iterator();

        if(dicomElementIterator1.hasNext() != dicomElementIterator2.hasNext() ){
            return false;
        }

        while (dicomElementIterator1.hasNext() || dicomElementIterator2.hasNext()) {
            if(!dicomElementIterator1.hasNext() || !dicomElementIterator2.hasNext()){
                return false;
            }

            final DicomElement dcmDicomElement1 = dicomElementIterator1.next();
            final DicomElement dcmDicomElement2 = dicomElementIterator2.next();
            final String stringValue1 = dcmDicomElement1.stringValue(0).orElse(null);
            final String stringValue2 = dcmDicomElement2.stringValue(0).orElse(null);
            final int tag1 = dcmDicomElement1.tag();
            final int tag2 = dcmDicomElement2.tag();
            final VR vr1 = dcmDicomElement1.vr();
            final VR vr2 = dcmDicomElement2.vr();

            if (stringValue1 != null && stringValue2 != null) {
                if (!stringValue1.equals(stringValue2) || tag1 != tag2 || vr1 != vr2) {
                    return false;
                }
            } else{
                if ((stringValue1 == null && stringValue2 != null) || (stringValue1 != null && stringValue2 == null)) {
                    return false;
                }
            }

            //SEQUENCES
            if (vr1 == VR.SQ && vr2 == VR.SQ ) {
                int i = 0;
                while (dcmDicomElement1.getItem(i) != null && dcmDicomElement2.getItem(i) != null){
                    DicomObject dcmItem1 = dcmDicomElement1.getItem(i);
                    DicomObject dcmItem2 = dcmDicomElement2.getItem(i);
                    boolean resultSequence = dicomObjectEquals(dcmItem1, dcmItem2);
                    if(!resultSequence){
                        return false;
                    }
                    i = i +1;
                }
            }
        }
        return true;
    }


    public static boolean tagIsInDicomObject(int tag, DicomObject dcm){
        for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext(); ) {
            final DicomElement dcmEl = iterator.next();

            if(dcmEl.tag() == tag){
                return true;
            }

            if (dcmEl.vr() == VR.SQ) {
                int i = 0;
                while (dcmEl.getItem(i) != null){
                    DicomObject dcmItem1 = dcmEl.getItem(i);
                    boolean resultSequence = tagIsInDicomObject(tag, dcmItem1);
                    if(resultSequence == true){
                        return true;
                    }
                    i = i +1;
                }
            }
        }
        return false;
    }
}
