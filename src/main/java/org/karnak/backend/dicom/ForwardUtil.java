/*
 * Copyright (c) 2009-2019 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.dicom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.img.DicomOutputData;
import org.dcm4che3.img.op.MaskArea;
import org.dcm4che3.img.stream.BytesWithImageDescriptor;
import org.dcm4che3.img.stream.ImageAdapter;
import org.dcm4che3.img.stream.ImageAdapter.AdaptTransferSyntax;
import org.dcm4che3.img.util.Editable;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomInputStream.IncludeBulkData;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.DataWriter;
import org.dcm4che3.net.DataWriterAdapter;
import org.dcm4che3.net.InputStreamDataWriter;
import org.dcm4che3.net.PDVInputStream;
import org.dcm4che3.net.Status;
import org.karnak.backend.model.Quarantine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.core.util.FileUtil;
import org.weasis.core.util.LangUtil;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;
import org.weasis.dicom.param.AttributeEditorContext.Abort;
import org.weasis.dicom.util.ServiceUtil;
import org.weasis.dicom.util.ServiceUtil.ProgressStatus;
import org.weasis.dicom.util.StoreFromStreamSCU;
import org.weasis.dicom.web.DicomStowRS;
import org.weasis.dicom.web.HttpException;
import org.weasis.opencv.data.PlanarImage;

public class ForwardUtil {
  private static final String ERROR_WHEN_FORWARDING =
      "Error when forwarding to the final destination";
  private static final Logger LOGGER = LoggerFactory.getLogger(ForwardUtil.class);

  public static final class Params {
    private final String iuid;
    private final String cuid;
    private final String tsuid;
    private final InputStream data;
    private final Association as;
    private final int priority;

    public Params(
        String iuid, String cuid, String tsuid, int priority, InputStream data, Association as) {
      super();
      this.iuid = iuid;
      this.cuid = cuid;
      this.tsuid = tsuid;
      this.priority = priority;
      this.as = as;
      this.data = data;
    }

    public String getIuid() {
      return iuid;
    }

    public String getCuid() {
      return cuid;
    }

    public String getTsuid() {
      return tsuid;
    }

    public int getPriority() {
      return priority;
    }

    public Association getAs() {
      return as;
    }

    public InputStream getData() {
      return data;
    }
  }

  private static final class AbortException extends IllegalStateException {
    private static final long serialVersionUID = 3993065212756372490L;
    private final Abort abort;

    public AbortException(Abort abort, String s) {
      super(s);
      this.abort = abort;
    }

    public AbortException(Abort abort, String string, Exception e) {
      super(string, e);
      this.abort = abort;
    }

    @Override
    public String toString() {
      return getMessage();
    }

    public Abort getAbort() {
      return abort;
    }
  }

  private ForwardUtil() {}

  public static void storeMultipleDestination(
      ForwardDicomNode fwdNode, List<ForwardDestination> destList, Params p) throws IOException {
    if (destList == null || destList.isEmpty()) {
      throw new IllegalStateException(
          "Cannot find the DICOM destination from " + fwdNode.toString());
    }
    // Exclude DICOMDIR
    if ("1.2.840.10008.1.3.10".equals(p.cuid)) {
      LOGGER.warn("Cannot send DICOMDIR {}", p.iuid);
      return;
    }

    if (destList.size() == 1) {
      storeOneDestination(fwdNode, destList.get(0), p);
    } else {
      List<ForwardDestination> destConList = new ArrayList<>();
      for (ForwardDestination fwDest : destList) {
        try {
          if (fwDest instanceof DicomForwardDestination) {
            prepareTransfer((DicomForwardDestination) fwDest, p);
          }
          destConList.add(fwDest);
        } catch (Exception e) {
          LOGGER.error("Cannot connect to the final destination", e);
        }
      }

      if (destConList.isEmpty()) {
        return;
      } else if (destConList.size() == 1) {
        storeOneDestination(fwdNode, destConList.get(0), p);
      } else {
        List<File> files = null;
        try {
          Attributes attributes = new Attributes();
          ForwardDestination fistDest = destConList.get(0);
          if (fistDest instanceof DicomForwardDestination) {
            files = transfer(fwdNode, (DicomForwardDestination) fistDest, attributes, p);
          } else if (fistDest instanceof WebForwardDestination) {
            files = transfer(fwdNode, (WebForwardDestination) fistDest, attributes, p);
          }
          if (!attributes.isEmpty()) {
            for (int i = 1; i < destConList.size(); i++) {
              ForwardDestination dest = destConList.get(i);
              if (dest instanceof DicomForwardDestination) {
                transferOther(fwdNode, (DicomForwardDestination) dest, attributes, p);
              } else if (dest instanceof WebForwardDestination) {
                transferOther(fwdNode, (WebForwardDestination) dest, attributes, p);
              }
            }
          }
        } finally {
          if (files != null) {
            // Force to clean if tmp bulk files
            for (File file : files) {
              FileUtil.delete(file);
            }
          }
        }
      }
    }
  }

  public static void storeOneDestination(
      ForwardDicomNode fwdNode, ForwardDestination destination, Params p) throws IOException {
    if (destination instanceof DicomForwardDestination) {
      DicomForwardDestination dest = (DicomForwardDestination) destination;
      prepareTransfer(dest, p);
      transfer(fwdNode, dest, null, p);
    } else if (destination instanceof WebForwardDestination) {
      transfer(fwdNode, (WebForwardDestination) destination, null, p);
    }
  }

  public static synchronized StoreFromStreamSCU prepareTransfer(
      DicomForwardDestination destination, Params p) throws IOException {
    String cuid = p.getCuid();
    String tsuid = p.getTsuid();
    String dstTsuid = destination.getOutputTransferSyntax(tsuid);
    StoreFromStreamSCU streamSCU = destination.getStreamSCU();
    if (streamSCU.hasAssociation()) {
      // Handle dynamically new SOPClassUID
      Set<String> tss = streamSCU.getTransferSyntaxesFor(cuid);
      if (!tss.contains(dstTsuid)) {
        streamSCU.close(true);
      }

      // Add Presentation Context for the association
      streamSCU.addData(cuid, dstTsuid);
      if (DicomOutputData.isAdaptableSyntax(dstTsuid)) {
        streamSCU.addData(cuid, UID.JPEGLosslessSV1);
      }

      if (!streamSCU.isReadyForDataTransfer()) {
        // If connection has been closed just reopen
        streamSCU.open();
      }
    } else {
      destination.getStreamSCUService().start();
      // Add Presentation Context for the association
      streamSCU.addData(cuid, dstTsuid);
      if (!dstTsuid.equals(UID.ExplicitVRLittleEndian)) {
        streamSCU.addData(cuid, UID.ExplicitVRLittleEndian);
      }
      if (DicomOutputData.isAdaptableSyntax(dstTsuid)) {
        streamSCU.addData(cuid, UID.JPEGLosslessSV1);
      }
      streamSCU.open();
    }
    return streamSCU;
  }

  public static List<File> transfer(
      ForwardDicomNode sourceNode, DicomForwardDestination destination, Attributes copy, Params p)
      throws IOException {
    StoreFromStreamSCU streamSCU = destination.getStreamSCU();
    DicomInputStream in = null;
    List<File> files;
    try {
      if (!streamSCU.isReadyForDataTransfer()) {
        throw new IllegalStateException("Association not ready for transfer.");
      }
      DataWriter dataWriter;
      String cuid = p.getCuid();
      String iuid = p.getIuid();
      String tsuid = p.getTsuid();
      var syntax =
          new AdaptTransferSyntax(
              tsuid,
              streamSCU.selectTransferSyntax(cuid, destination.getOutputTransferSyntax(tsuid)));
      List<AttributeEditor> editors = destination.getDicomEditors();

      if (copy == null && editors.isEmpty() && syntax.getRequested().equals(tsuid)) {
        dataWriter = new InputStreamDataWriter(p.getData());
      } else {
        AttributeEditorContext context =
            new AttributeEditorContext(tsuid, sourceNode, streamSCU.getRemoteDicomNode());
        in = new DicomInputStream(p.getData(), tsuid);
        in.setIncludeBulkData(IncludeBulkData.URI);
        Attributes attributes = in.readDataset();
        if (copy != null) {
          copy.addAll(attributes);
        }

        if (!editors.isEmpty()) {
          editors.forEach(e -> e.apply(attributes, context));
          iuid = attributes.getString(Tag.SOPInstanceUID);
          cuid = attributes.getString(Tag.SOPClassUID);
        }

        if (context.getAbort() == Abort.FILE_EXCEPTION) {
          sourceNode.setQuarantine(new Quarantine(copy, p));
          if (p.getData() instanceof PDVInputStream) {
            ((PDVInputStream) p.getData()).skipAll();
          }
          throw new AbortException(context.getAbort(), context.getAbortMessage());
        } else if (context.getAbort() == Abort.CONNECTION_EXCEPTION) {
          sourceNode.setQuarantine(new Quarantine(copy, p));
          if (p.getAs() != null) {
            p.getAs().abort();
          }
          throw new AbortException(
              context.getAbort(), "DICOM association abort: " + context.getAbortMessage());
        }

        BytesWithImageDescriptor desc = ImageAdapter.imageTranscode(attributes, syntax, context);
        Editable<PlanarImage> editable = transformImage(attributes, context);
        dataWriter = ImageAdapter.buildDataWriter(attributes, syntax, editable, desc);
      }

      streamSCU.cstore(cuid, iuid, p.getPriority(), dataWriter, syntax.getSuitable());
      progressNotify(
          destination, p.getIuid(), p.getCuid(), false, streamSCU.getNumberOfSuboperations());
    } catch (AbortException e) {
      progressNotify(
          destination, p.getIuid(), p.getCuid(), true, streamSCU.getNumberOfSuboperations());
      if (e.getAbort() == Abort.CONNECTION_EXCEPTION) {
        throw e;
      }
    } catch (Exception e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      progressNotify(
          destination, p.getIuid(), p.getCuid(), true, streamSCU.getNumberOfSuboperations());
      sourceNode.setQuarantine(new Quarantine(copy, p));
      LOGGER.error(ERROR_WHEN_FORWARDING, e);
    } finally {
      streamSCU.triggerCloseExecutor();
      files = cleanOrGetBulkDataFiles(in, copy == null);
    }
    return files;
  }

  private static Editable<PlanarImage> transformImage(
      Attributes attributes, AttributeEditorContext context) {
    MaskArea m = context.getMaskArea();
    boolean defacing =
        LangUtil.getEmptytoFalse(context.getProperties().getProperty(Defacer.APPLY_DEFACING));
    if (m != null || defacing) {
      return img -> {
        PlanarImage image = img;
        if (defacing) {
          image = Defacer.apply(attributes, image);
        }
        if (m != null) {
          image = MaskArea.drawShape(image.toMat(), m);
        }
        return image;
      };
    }
    return null;
  }

  private static List<File> cleanOrGetBulkDataFiles(DicomInputStream in, boolean clean) {
    FileUtil.safeClose(in);
    if (clean) {
      // Force to clean if tmp bulk files
      ServiceUtil.safeClose(in);
    } else if (in != null) {
      // Return tmp bulk files
      return in.getBulkDataFiles();
    }
    return null;
  }

  public static void transferOther(
      ForwardDicomNode fwdNode, DicomForwardDestination destination, Attributes copy, Params p) {
    StoreFromStreamSCU streamSCU = destination.getStreamSCU();

    try {
      if (!streamSCU.isReadyForDataTransfer()) {
        throw new IllegalStateException("Association not ready for transfer.");
      }

      DataWriter dataWriter;
      String tsuid = p.getTsuid();
      String iuid = p.getIuid();
      String cuid = p.getCuid();
      var syntax =
          new AdaptTransferSyntax(
              tsuid,
              streamSCU.selectTransferSyntax(cuid, destination.getOutputTransferSyntax(tsuid)));
      List<AttributeEditor> editors = destination.getDicomEditors();
      if (editors.isEmpty() && syntax.getRequested().equals(tsuid)) {
        dataWriter = new DataWriterAdapter(copy);
      } else {
        AttributeEditorContext context =
            new AttributeEditorContext(tsuid, fwdNode, streamSCU.getRemoteDicomNode());
        Attributes attributes = new Attributes(copy);
        if (!editors.isEmpty()) {
          editors.forEach(e -> e.apply(attributes, context));
          iuid = attributes.getString(Tag.SOPInstanceUID);
          cuid = attributes.getString(Tag.SOPClassUID);
        }

        if (context.getAbort() == Abort.FILE_EXCEPTION) {
          fwdNode.setQuarantine(new Quarantine(copy, p));
          throw new AbortException(context.getAbort(), context.getAbortMessage());
        } else if (context.getAbort() == Abort.CONNECTION_EXCEPTION) {
          fwdNode.setQuarantine(new Quarantine(copy, p));
          throw new AbortException(
              context.getAbort(), "DICOM association abort. " + context.getAbortMessage());
        }

        BytesWithImageDescriptor desc = ImageAdapter.imageTranscode(attributes, syntax, context);
        Editable<PlanarImage> editable = transformImage(attributes, context);
        dataWriter = ImageAdapter.buildDataWriter(attributes, syntax, editable, desc);
      }

      streamSCU.cstore(cuid, iuid, p.getPriority(), dataWriter, syntax.getSuitable());
      progressNotify(
          destination, p.getIuid(), p.getCuid(), false, streamSCU.getNumberOfSuboperations());
    } catch (AbortException e) {
      progressNotify(
          destination, p.getIuid(), p.getCuid(), true, streamSCU.getNumberOfSuboperations());
      if (e.getAbort() == Abort.CONNECTION_EXCEPTION) {
        throw e;
      }
    } catch (Exception e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      progressNotify(
          destination, p.getIuid(), p.getCuid(), true, streamSCU.getNumberOfSuboperations());
      fwdNode.setQuarantine(new Quarantine(copy, p));
      LOGGER.error(ERROR_WHEN_FORWARDING, e);
    } finally {
      streamSCU.triggerCloseExecutor();
    }
  }

  public static List<File> transfer(
      ForwardDicomNode fwdNode, WebForwardDestination destination, Attributes copy, Params p) {
    DicomInputStream in = null;
    List<File> files;
    try {
      List<AttributeEditor> editors = destination.getDicomEditors();
      DicomStowRS stow = destination.getStowrsSingleFile();
      var syntax =
          new AdaptTransferSyntax(p.getTsuid(), destination.getOutputTransferSyntax(p.getTsuid()));

      if (syntax.getRequested().equals(p.getTsuid()) && copy == null && editors.isEmpty()) {
        Attributes fmi =
            Attributes.createFileMetaInformation(p.getIuid(), p.getCuid(), syntax.getRequested());
        try (InputStream stream = p.getData()) {
          stow.uploadDicom(stream, fmi);
        } catch (HttpException httpException) {
          throw new AbortException(Abort.FILE_EXCEPTION, httpException.getMessage());
        }
      } else {
        AttributeEditorContext context = new AttributeEditorContext(p.getTsuid(), fwdNode, null);
        in = new DicomInputStream(p.getData(), p.getTsuid());
        in.setIncludeBulkData(IncludeBulkData.URI);
        Attributes attributes = in.readDataset();
        if (copy != null) {
          copy.addAll(attributes);
        }
        if (!editors.isEmpty()) {
          editors.forEach(e -> e.apply(attributes, context));
        }

        if (context.getAbort() == Abort.FILE_EXCEPTION) {
          fwdNode.setQuarantine(new Quarantine(copy, p));
          if (p.getData() instanceof PDVInputStream) {
            ((PDVInputStream) p.getData()).skipAll();
          }
          throw new AbortException(context.getAbort(), context.getAbortMessage());
        } else if (context.getAbort() == Abort.CONNECTION_EXCEPTION) {
          fwdNode.setQuarantine(new Quarantine(copy, p));
          if (p.getAs() != null) {
            p.getAs().abort();
          }
          throw new AbortException(
              context.getAbort(), "STOW-RS abort: " + context.getAbortMessage());
        }

        BytesWithImageDescriptor desc = ImageAdapter.imageTranscode(attributes, syntax, context);
        if (desc == null) {
          stow.uploadDicom(attributes, syntax.getOriginal());
        } else {
          Editable<PlanarImage> editable = transformImage(attributes, context);
          stow.uploadPayload(ImageAdapter.preparePlayload(attributes, syntax, desc, editable));
        }
      }
      progressNotify(destination, p.getIuid(), p.getCuid(), false, 0);
    } catch (AbortException e) {
      progressNotify(destination, p.getIuid(), p.getCuid(), true, 0);
      if (e.getAbort() == Abort.CONNECTION_EXCEPTION) {
        throw e;
      }
    } catch (Exception e) {
      progressNotify(destination, p.getIuid(), p.getCuid(), true, 0);
      fwdNode.setQuarantine(new Quarantine(copy, p));
      LOGGER.error(ERROR_WHEN_FORWARDING, e);
    } finally {
      files = cleanOrGetBulkDataFiles(in, copy == null);
    }
    return files;
  }

  public static void transferOther(
      ForwardDicomNode fwdNode, WebForwardDestination destination, Attributes copy, Params p) {
    try {
      List<AttributeEditor> editors = destination.getDicomEditors();
      DicomStowRS stow = destination.getStowrsSingleFile();
      var syntax =
          new AdaptTransferSyntax(p.getTsuid(), destination.getOutputTransferSyntax(p.getTsuid()));
      if (syntax.getRequested().equals(p.getTsuid()) && editors.isEmpty()) {
        stow.uploadDicom(copy, syntax.getRequested());
      } else {
        AttributeEditorContext context = new AttributeEditorContext(p.getTsuid(), fwdNode, null);
        Attributes attributes = new Attributes(copy);
        editors.forEach(e -> e.apply(attributes, context));

        if (context.getAbort() == Abort.FILE_EXCEPTION) {
          fwdNode.setQuarantine(new Quarantine(copy, p));
          throw new AbortException(context.getAbort(), context.getAbortMessage());
        } else if (context.getAbort() == Abort.CONNECTION_EXCEPTION) {
          fwdNode.setQuarantine(new Quarantine(copy, p));
          throw new AbortException(
              context.getAbort(), "DICOM associtation abort. " + context.getAbortMessage());
        }

        BytesWithImageDescriptor desc = ImageAdapter.imageTranscode(attributes, syntax, context);
        if (desc == null) {
          stow.uploadDicom(attributes, syntax.getOriginal());
        } else {
          Editable<PlanarImage> editable = transformImage(attributes, context);
          stow.uploadPayload(ImageAdapter.preparePlayload(attributes, syntax, desc, editable));
        }
        progressNotify(destination, p.getIuid(), p.getCuid(), false, 0);
      }
    } catch (HttpException httpException) {
      progressNotify(destination, p.getIuid(), p.getCuid(), true, 0);
      throw new AbortException(Abort.FILE_EXCEPTION, "DICOMWeb forward", httpException);
    } catch (AbortException e) {
      progressNotify(destination, p.getIuid(), p.getCuid(), true, 0);
      if (e.getAbort() == Abort.CONNECTION_EXCEPTION) {
        throw e;
      }
    } catch (Exception e) {
      progressNotify(destination, p.getIuid(), p.getCuid(), true, 0);
      fwdNode.setQuarantine(new Quarantine(copy, p));
      LOGGER.error(ERROR_WHEN_FORWARDING, e);
    }
  }

  private static void progressNotify(
      ForwardDestination destination, String iuid, String cuid, boolean failed, int subOperations) {
    ServiceUtil.notifyProgession(
        destination.getState(),
        iuid,
        cuid,
        failed ? Status.ProcessingFailure : Status.Success,
        failed ? ProgressStatus.FAILED : ProgressStatus.COMPLETED,
        subOperations);
  }

  public static String selectTransferSyntax(Association as, String cuid, String filets) {
    Set<String> tss = as.getTransferSyntaxesFor(cuid);
    if (tss.contains(filets)) {
      return filets;
    }

    if (tss.contains(UID.ExplicitVRLittleEndian)) {
      return UID.ExplicitVRLittleEndian;
    }

    return UID.ImplicitVRLittleEndian;
  }

  public static void transferQuarantine(
      ForwardDicomNode fwdNode, Map<ForwardDicomNode, ForwardDestination> fwdQuarantineDest) {
    List<File> files = null;
    try {
      ForwardDestination quarantineFwdDest = fwdQuarantineDest.get(fwdNode);
      DicomForwardDestination quarantineDest = (DicomForwardDestination) quarantineFwdDest;
      var quarantineAttributes = fwdNode.getQuarantine().getAttributes();
      var params = fwdNode.getQuarantine().getParams();
      prepareTransfer(quarantineDest, params);
      files = transfer(fwdNode, quarantineDest, quarantineAttributes, params);
      if (files != null) {
        // Force to clean if tmp bulk files
        for (File file : files) {
          FileUtil.delete(file);
        }
      }
      fwdNode.setQuarantine(null);
    } catch (Exception e) {
      LOGGER.error("Error when forwarding in quarantine", e);
    } finally {
      if (files != null) {
        // Force to clean if tmp bulk files
        for (File file : files) {
          FileUtil.delete(file);
        }
      }
    }
  }
}
