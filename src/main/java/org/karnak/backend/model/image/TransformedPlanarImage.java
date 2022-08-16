package org.karnak.backend.model.image;

import org.dcm4che3.img.util.Editable;
import org.weasis.opencv.data.PlanarImage;

public class TransformedPlanarImage {

  Editable<PlanarImage> editablePlanarImage;
  PlanarImage planarImage;

  public Editable<PlanarImage> getEditablePlanarImage() {
    return editablePlanarImage;
  }

  public void setEditablePlanarImage(
      Editable<PlanarImage> editablePlanarImage) {
    this.editablePlanarImage = editablePlanarImage;
  }

  public PlanarImage getPlanarImage() {
    return planarImage;
  }

  public void setPlanarImage(PlanarImage planarImage) {
    this.planarImage = planarImage;
  }
}
