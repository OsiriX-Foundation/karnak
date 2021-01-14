package org.karnak.frontend.dicom;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.filechooser.FileFilter;

/**
 * The Class FileFormatFilter.
 *
 * @author Nicolas Roduit
 */
public class FileFormatFilter extends FileFilter {

  private final Map<String, FileFormatFilter> fExtensions;
  private String fDescription;
  private String fFullDescription;
  private String fDefaultExtension;
  private boolean fUseExtensionsInDescription;

  public FileFormatFilter(String extension, String description) {
    fExtensions = new TreeMap<String, FileFormatFilter>();
    fDescription = null;
    fFullDescription = null;
    fDefaultExtension = null;
    fUseExtensionsInDescription = true;
    if (extension != null) {
      addExtension(extension);
    }
    if (description != null) {
      setDescription(description);
    }
  }

  public FileFormatFilter(String[] filters) {
    this(filters, null);
  }

  public FileFormatFilter(String[] filters, String description) {
    fExtensions = new TreeMap<String, FileFormatFilter>();
    fDescription = null;
    fFullDescription = null;
    fDefaultExtension = null;
    fUseExtensionsInDescription = true;
    for (int i = 0; i < filters.length; i++) {
      addExtension(filters[i]);
    }
    if (description != null) {
      setDescription(description);
    }
  }

  public String getDefaultExtension() {
    return fDefaultExtension;
  }

  @Override
  public boolean accept(File f) {
    if (f != null) {
      if (f.isDirectory()) {
        return true;
      }
      String extension = getExtension(f);
      return extension != null && fExtensions.get(extension) != null;
    }
    return false;
  }

  public String getExtension(File f) {
    if (f != null) {
      String filename = f.getName();
      int i = filename.lastIndexOf(46);
      if (i > 0 && i < filename.length() - 1) {
        return filename.substring(i + 1).toLowerCase();
      }
    }
    return null;
  }

  public void addExtension(String extension) {
    extension = extension.replace('*', ' ');
    extension = extension.replace('.', ' ');
    fExtensions.put(extension.trim().toLowerCase(), this);
    if (fDefaultExtension == null) {
      fDefaultExtension = extension;
    }
    fFullDescription = null;
  }

  @Override
  public String getDescription() {
    if (fFullDescription == null) {
      if (fDescription == null || isExtensionListInDescription()) {
        fFullDescription =
            fDescription != null ? fDescription + " (" : "("; // $NON-NLS-1$ //$NON-NLS-2$
        Set<String> extensions = fExtensions.keySet();
        Iterator<String> it = extensions.iterator();
        if (it.hasNext()) {
          fFullDescription += "*." + it.next(); // $NON-NLS-1$
        }
        while (it.hasNext()) {
          fFullDescription += ", *." + it.next(); // $NON-NLS-1$
        }
        fFullDescription += ")"; // $NON-NLS-1$
      } else {
        fFullDescription = fDescription;
      }
    }
    return fFullDescription;
  }

  public void setDescription(String description) {
    fDescription = description;
    fFullDescription = null;
  }

  public boolean isExtensionListInDescription() {
    return fUseExtensionsInDescription;
  }

  public void setExtensionListInDescription(boolean b) {
    fUseExtensionsInDescription = b;
    fFullDescription = null;
  }

  public void setFFullDescription(String fFullDescription) {
    this.fFullDescription = fFullDescription;
  }
}
