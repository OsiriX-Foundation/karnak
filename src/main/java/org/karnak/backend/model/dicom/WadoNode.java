package org.karnak.backend.model.dicom;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WadoNode {
	private final String name;
    private final URL url;
  private final List<String> tagEntities = new ArrayList<String>(2);

    public WadoNode(String name, URL url) {
        this.name = Objects.requireNonNull(name);
        this.url = Objects.requireNonNull(url);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public URL getUrl() {
        return url;
    }

    public List<String> getTags() {
      return tagEntities;
    }

}
