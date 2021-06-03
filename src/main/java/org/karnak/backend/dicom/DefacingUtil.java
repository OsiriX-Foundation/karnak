/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.dicom;

import java.security.SecureRandom;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.opencv.core.Core.MinMaxLocResult;
import org.weasis.opencv.data.ImageCV;
import org.weasis.opencv.data.PlanarImage;
import org.weasis.opencv.op.ImageProcessor;

public class DefacingUtil {

  private DefacingUtil() {}

  public static int randomY(int minY, int maxY, int bound) {
    SecureRandom secureRandom = new SecureRandom();
    double randomDouble = secureRandom.nextDouble();
    return (int) Math.floor(randomDouble * (maxY - minY + bound) + minY);
  }

  public static double pickRndYPxlColor(int xInit, int minY, int maxY, PlanarImage imgToPick) {
    int yRand = DefacingUtil.randomY(minY, maxY, 1);
    int size = 4;
    double mean = 0;
    int sum = 0;
    // convolution
    for (int x = xInit - (size / 2); x < xInit + (size / 2) + 1; x++) {
      for (int y = yRand; y < yRand + size + 1; y++) {
        int xPickColor = checkBoundsOfImageX(x, imgToPick);
        int yPickColor = checkBoundsOfImageY(y, imgToPick);
        double color = imgToPick.toMat().get(yPickColor, xPickColor)[0];

        mean = mean + color;
        sum++;
      }
    }
    if (sum != 0) {
      return mean / sum;
    }
    return mean;
  }

  public static int checkBoundsOfImageX(int x, PlanarImage image) {
    if (x < 0) {
      return 0;
    }
    if (x >= image.width()) {
      return image.width() - 1;
    }
    return x;
  }

  public static int checkBoundsOfImageY(int y, PlanarImage image) {
    if (y < 0) {
      return 0;
    }
    if (y >= image.height()) {
      return image.height() - 1;
    }
    return y;
  }

  public static PlanarImage transformToByte(PlanarImage srcImg) {
    ImageCV imgTransform = new ImageCV();
    srcImg.toMat().copyTo(imgTransform);

    MinMaxLocResult minMaxLocResult = ImageProcessor.findMinMaxValues(imgTransform.toMat());
    double min = minMaxLocResult.minVal;
    double max = minMaxLocResult.maxVal;
    double slope = 255.0 / (max - min);
    double yint = 255.0 - slope * max;
    imgTransform = ImageProcessor.rescaleToByte(imgTransform.toImageCV(), slope, yint);
    return imgTransform;
  }

  public static double hounsfieldToPxlValue(Attributes attributes, double hounsfield) {
    double intercept = attributes.getDouble(Tag.RescaleIntercept, 0);
    double slope = attributes.getDouble(Tag.RescaleSlope, 1.0);
    return (hounsfield - intercept) / slope;
  }

  public static PlanarImage rescaleForVisualizing(
      PlanarImage srcImg, Double contrast, Double brigtness) {
    ImageCV imageForVisualizing = new ImageCV();
    srcImg.toMat().copyTo(imageForVisualizing);
    PlanarImage transformImg = transformToByte(imageForVisualizing);
    transformImg =
        ImageProcessor.rescaleToByte(transformImg.toImageCV(), contrast / 100.0, brigtness);
    return transformImg;
  }

  public static boolean isCT(Attributes attributes) {
    String sopClassUID = attributes.getString(Tag.SOPClassUID);
    return (sopClassUID + ".").startsWith("1.2.840.10008.5.1.4.1.1.2.");
  }

  public static boolean isAxial(Attributes attributes) {
    double[] vector = attributes.getDoubles(Tag.ImageOrientationPatient);
    ImageOrientation.Label label =
        ImageOrientation.makeImageOrientationLabelFromImageOrientationPatient(vector);
    return label.equals(ImageOrientation.Label.AXIAL);
  }
}
