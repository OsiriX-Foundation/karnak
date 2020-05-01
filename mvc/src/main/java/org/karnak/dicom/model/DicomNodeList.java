package org.karnak.dicom.model;

import java.util.ArrayList;
import java.util.Collection;

public class DicomNodeList extends ArrayList<ConfigNode> {
    private String name;

    public DicomNodeList(String name) {
        super();
        this.name = name;
    }

    public DicomNodeList(String name, Collection<? extends ConfigNode> c) {
        super(c);
        this.name = name;
    }

    public DicomNodeList(String name, int initialCapacity) {
        super(initialCapacity);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
