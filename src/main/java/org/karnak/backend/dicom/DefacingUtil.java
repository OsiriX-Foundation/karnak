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

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.opencv.core.Core.MinMaxLocResult;
import org.weasis.opencv.data.ImageCV;
import org.weasis.opencv.data.PlanarImage;
import org.weasis.opencv.op.ImageProcessor;

public class DefacingUtil {

  public static int randomY(int minY, int maxY, int bound) {
    return (int) Math.floor(Math.random() * (maxY - minY + bound) + minY);
  }

  public static double pickRndYPxlColor(int xInit, int minY, int maxY, PlanarImage imgToPick) {
    int yRand = DefacingUtil.randomY(minY, maxY, 1);
    int size = 4;
    double mean = 0;
    int sum = 0;
    // convolution
    for (int x = xInit - (size / 2); x < xInit + (size / 2) + 1; x++) {
      for (int y = yRand; y < yRand + size + 1; y++) {
        double color = 0;
        try {
          color = imgToPick.toMat().get(y, x)[0];
        } catch (Exception e) {
        }

        mean = mean + color;
        sum++;
      }
    }
    return mean / sum;
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
    String interceptS = attributes.getString(Tag.RescaleIntercept);
    String slopeS = attributes.getString(Tag.RescaleSlope);
    double intercept = Double.parseDouble(interceptS);
    double slope = Double.parseDouble(slopeS);

    // int hounsfield = pixel * slope + intercept;
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
    String scuPattern = sopClassUID + ".";
    if (scuPattern.equals("1.2.840.10008.5.1.4.1.1.2.")) {
      return true;
    }
    return false;
  }

  public static boolean isAxial(Attributes attributes) {
    double[] vector = attributes.getDoubles(Tag.ImageOrientationPatient);
    ImageOrientation.Label label =
        ImageOrientation.makeImageOrientationLabelFromImageOrientationPatient(vector);
    if (label.equals(ImageOrientation.Label.AXIAL)) {
      return true;
    }
    return false;
  }
}
