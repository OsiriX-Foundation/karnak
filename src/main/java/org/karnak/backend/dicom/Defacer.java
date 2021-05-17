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
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.weasis.opencv.data.ImageCV;
import org.weasis.opencv.data.PlanarImage;
import org.weasis.opencv.op.ImageProcessor;

public class Defacer {

  public static final String APPLY_DEFACING = "defacing";

  private Defacer() {}

  public static PlanarImage apply(Attributes attributes, PlanarImage image) {
    PlanarImage faceDetectionImg = faceDetection(image);
    PlanarImage randPxlLineImg = addRandPxlLine(image, faceDetectionImg);
    PlanarImage mergedImg = mergeImg(image, randPxlLineImg, faceDetectionImg);
    PlanarImage imgBlured = blurImg(mergedImg, randPxlLineImg, faceDetectionImg);
    return imgBlured;
  }

  public static PlanarImage faceDetection(PlanarImage srcImg) {
    ImageCV faceDetectionImg = new ImageCV();
    srcImg.toMat().copyTo(faceDetectionImg);

    MinMaxLocResult minMaxLocResult = ImageProcessor.findMinMaxValues(faceDetectionImg.toMat());

    // THRESHOLD
    Imgproc.threshold(
        faceDetectionImg.toImageCV(),
        faceDetectionImg.toMat(),
        300,
        minMaxLocResult.maxVal,
        Imgproc.THRESH_BINARY);

    // ERODE
    Mat kernel = new Mat();
    int kernel_size = 1;
    Mat ones = Mat.ones(kernel_size, kernel_size, CvType.CV_32F);
    Core.multiply(ones, new Scalar(1 / (double) (kernel_size * kernel_size)), kernel);
    Imgproc.erode(faceDetectionImg.toImageCV(), faceDetectionImg.toMat(), kernel);

    // FILL BLACK HOLE
    Mat kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(30, 30));
    Imgproc.morphologyEx(
        faceDetectionImg.toImageCV(), faceDetectionImg.toMat(), Imgproc.MORPH_CLOSE, kernel2);

    // RESCALE 8BIT
    faceDetectionImg = DefacingUtil.transformToByte(faceDetectionImg).toImageCV();

    // CANNY DETECT CONTOUR
    Imgproc.Canny(faceDetectionImg.toImageCV(), faceDetectionImg.toMat(), 240, 260);

    // DRAW BLACK RECT 1/3
    int rectProportion = (int) (faceDetectionImg.height() / 2.9);
    Rect rect = new Rect(0, rectProportion, faceDetectionImg.width(), faceDetectionImg.height());
    Imgproc.rectangle(faceDetectionImg.toImageCV(), rect, new Scalar(0, 0, 0), Imgproc.FILLED);

    return faceDetectionImg;
  }

  public static PlanarImage addRandPxlLine(PlanarImage srcImg, PlanarImage faceDetectImg) {
    ImageCV randPxlLineImg = new ImageCV();
    srcImg.toMat().copyTo(randPxlLineImg);

    // DRAW A LINE WITH RANDOM VALUE WHEN FACE DETECTED
    int marge = 10;
    int yOffsetRand = 4;
    // scan the image from left to right and bottom to top until the face is detected in Y
    for (int x = 0; x < faceDetectImg.width(); x++) {
      boolean faceDetected = false;
      int yFaceDetected = 0;

      for (int y = faceDetectImg.height() - 1; y > 0; y--) {
        double faceDetectPixelValue = faceDetectImg.toMat().get(y, x)[0];
        if (faceDetectPixelValue != 0.0) {
          faceDetected = true;
          yFaceDetected = y;

          // Put random color before the first 10 lines of the face detection
          int yMaxMarge = yFaceDetected + marge;
          for (int yMarge = yFaceDetected; yMarge <= yMaxMarge; yMarge++) {
            int yRandInMarge =
                DefacingUtil.randomY(
                    yFaceDetected + yOffsetRand,
                    yFaceDetected + yOffsetRand + marge,
                    1);
            double randPxlColorInMarge = srcImg.toMat().get(yRandInMarge, x)[0];
            randPxlLineImg.toMat().put(yMarge, x, randPxlColorInMarge);
          }
        }

        if (faceDetected) {
          // Put random color after the face detection
          int yRand =
              DefacingUtil.randomY(
                  yFaceDetected + yOffsetRand,
                  yFaceDetected + yOffsetRand + marge,
                  1);
          double randPxlColor = srcImg.toMat().get(yRand, x)[0];
          randPxlLineImg.toMat().put(y, x, randPxlColor);
        } else {
          randPxlLineImg.toMat().put(y, x, 0.0);
        }
      }
    }
    return randPxlLineImg;
  }

  public static PlanarImage blurImg(
      PlanarImage srcImg, PlanarImage randPxlLineImg, PlanarImage faceDetectImg) {
    ImageCV bluredImgRandPxlLine = new ImageCV();
    srcImg.toMat().copyTo(bluredImgRandPxlLine);

    Imgproc.blur(bluredImgRandPxlLine.toImageCV(), bluredImgRandPxlLine.toMat(), new Size(6, 6));

    for (int x = 0; x < faceDetectImg.width(); x++) {
      for (int y = faceDetectImg.height() - 1; y > 0; y--) {
        if (randPxlLineImg.toMat().get(y, x)[0] == 0.0) {
          bluredImgRandPxlLine.toMat().put(y, x, srcImg.toMat().get(y, x)[0]);
        }
      }
    }

    return bluredImgRandPxlLine;
  }

  public static PlanarImage mergeImg(
      PlanarImage srcImg, PlanarImage randPxlLineImg, PlanarImage faceDetectImg) {
    // MERGE IMAGE SRC AND RAND PIXEL LINE
    ImageCV newImg = new ImageCV();
    srcImg.toMat().copyTo(newImg);

    for (int x = 0; x < faceDetectImg.width(); x++) {
      for (int y = faceDetectImg.height() - 1; y > 0; y--) {
        if (randPxlLineImg.toMat().get(y, x)[0] != 0.0) {
          newImg.toMat().put(y, x, randPxlLineImg.toMat().get(y, x)[0]);
        } else {
          newImg.toMat().put(y, x, srcImg.toMat().get(y, x)[0]);
        }
      }
    }
    return newImg;
  }
}
