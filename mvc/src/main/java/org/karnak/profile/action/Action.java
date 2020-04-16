package org.karnak.profile.action;

import org.dcm4che6.data.DicomObject;
import java.util.Iterator;
import org.dcm4che6.data.DicomElement;

@FunctionalInterface
public interface Action {
    void execute(DicomObject attributes, int tag, Iterator<DicomElement> iterator);
}
