package org.karnak.profile.action;

import java.util.Iterator;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;

public class XRemove implements Action {
    public void execute(DicomObject dcm, int tag) {
        synchronized (dcm) {
            for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext();) {
                DicomElement dcmEl = iterator.next();
                if (dcmEl.tag() == tag) {
                    iterator.remove();
                }
            }
        }
    }
}