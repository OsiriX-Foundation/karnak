package org.karnak.backend.model.dicom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class WadoNodeList extends ArrayList<WadoNode> {

  private final String name;

  public WadoNodeList(String name) {
    this.name = Objects.requireNonNull(name);
  }

  public WadoNodeList(String name, Collection<? extends WadoNode> c) {
    super(c);
    this.name = name;
  }

  public WadoNodeList(String name, int initialCapacity) {
    super(initialCapacity);
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
