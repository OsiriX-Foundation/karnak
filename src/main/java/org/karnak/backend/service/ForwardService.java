/*
 * Copyright (c) 2009-2019 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.karnak.backend.data.entity.TransferStatusEntity;
import org.karnak.backend.dicom.Defacer;
import org.karnak.backend.dicom.DicomForwardDestination;
import org.karnak.backend.dicom.ForwardDestination;
import org.karnak.backend.dicom.ForwardDicomNode;
import org.karnak.backend.dicom.Params;
import org.karnak.backend.dicom.WebForwardDestination;
import org.karnak.backend.exception.AbortException;
import org.karnak.backend.model.event.TransferMonitoringEvent;
import org.karnak.backend.model.image.TransformedPlanarImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
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

@Service
public class ForwardService {

  private static final String ERROR_WHEN_FORWARDING =
      "Error when forwarding to the final destination";

  private static final Logger LOGGER = LoggerFactory.getLogger(ForwardService.class);

  private final ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  public ForwardService(final ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void storeMultipleDestination(
      ForwardDicomNode fwdNode, List<ForwardDestination> destList, Params p) throws IOException {
    if (destList == null || destList.isEmpty()) {
      throw new IllegalStateException(
          "Cannot find the DICOM destination from " + fwdNode.toString());
    }
    // Exclude DICOMDIR
    if ("1.2.840.10008.1.3.10".equals(p.getCuid())) {
      LOGGER.warn("Cannot send DICOMDIR {}", p.getIuid());
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

  public void storeOneDestination(
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
        LOGGER.info("New output transfer syntax "+dstTsuid+": closing streamSCU");
        LOGGER.info("Current list of transfer syntaxes for "+cuid+":"+tss.stream().collect(
            Collectors.joining(",")));
        streamSCU.close(true);
      }

      // Add Presentation Context for the association
      streamSCU.addData(cuid, dstTsuid);
      if (DicomOutputData.isAdaptableSyntax(dstTsuid)) {
        streamSCU.addData(cuid, UID.JPEGLosslessSV1);
      }

      if (!streamSCU.isReadyForDataTransfer()) {
        // If connection has been closed just reopen
        LOGGER.info("streamSCU not ready for data transfer, reopen streamSCU");
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
      LOGGER.info("open streamSCU");
      streamSCU.open();
    }
    return streamSCU;
  }

  public List<File> transfer(
      ForwardDicomNode sourceNode, DicomForwardDestination destination, Attributes copy, Params p)
      throws IOException {
    StoreFromStreamSCU streamSCU = destination.getStreamSCU();
    DicomInputStream in = null;
    List<File> files;
    Attributes attributesOriginal = new Attributes();
    Attributes attributesToSend = new Attributes();
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
      TransformedPlanarImage transformedPlanarImage = new TransformedPlanarImage();
      if (copy == null && editors.isEmpty() && syntax.getRequested().equals(tsuid)) {
        dataWriter = new InputStreamDataWriter(p.getData());
        attributesToSend = new DicomInputStream(p.getData()).readDataset();
        attributesOriginal.addAll(attributesToSend);
      } else {
        AttributeEditorContext context =
            new AttributeEditorContext(tsuid, sourceNode, streamSCU.getRemoteDicomNode());
        in = new DicomInputStream(p.getData(), tsuid);
        in.setIncludeBulkData(IncludeBulkData.URI);
        Attributes attributes = in.readDataset();
        attributesOriginal.addAll(attributes);
        attributesToSend = attributes;
        if (copy != null) {
          copy.addAll(attributes);
        }

        if (!editors.isEmpty()) {
          editors.forEach(e -> e.apply(attributes, context));
          iuid = attributes.getString(Tag.SOPInstanceUID);
          cuid = attributes.getString(Tag.SOPClassUID);
        }

        if (context.getAbort() == Abort.FILE_EXCEPTION) {
          if (p.getData() instanceof PDVInputStream) {
            ((PDVInputStream) p.getData()).skipAll();
          }
          throw new AbortException(context.getAbort(), context.getAbortMessage());
        } else if (context.getAbort() == Abort.CONNECTION_EXCEPTION) {
          if (p.getAs() != null) {
            p.getAs().abort();
          }
          throw new AbortException(
              context.getAbort(), "DICOM association abort: " + context.getAbortMessage());
        }
        dataWriter = buildDataWriterFromTransformedImage(syntax, context, attributes,
            transformedPlanarImage);
      }

      launchCStore(p, streamSCU, dataWriter, cuid, iuid, syntax, transformedPlanarImage);

      progressNotify(
          destination, p.getIuid(), p.getCuid(), false, streamSCU.getNumberOfSuboperations());
      monitor(
          sourceNode.getId(),
          destination.getId(),
          attributesOriginal,
          attributesToSend,
          true,
          null);
    } catch (AbortException e) {
      progressNotify(
          destination, p.getIuid(), p.getCuid(), true, streamSCU.getNumberOfSuboperations());
      monitor(
          sourceNode.getId(),
          destination.getId(),
          attributesOriginal,
          attributesToSend,
          false,
          e.getMessage());
      if (e.getAbort() == Abort.CONNECTION_EXCEPTION) {
        throw e;
      }
    } catch (IOException e) {
      progressNotify(
          destination, p.getIuid(), p.getCuid(), true, streamSCU.getNumberOfSuboperations());
      monitor(
          sourceNode.getId(),
          destination.getId(),
          attributesOriginal,
          attributesToSend,
          false,
          e.getMessage());
      throw e;
    } catch (Exception e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      progressNotify(
          destination, p.getIuid(), p.getCuid(), true, streamSCU.getNumberOfSuboperations());
      monitor(
          sourceNode.getId(),
          destination.getId(),
          attributesOriginal,
          attributesToSend,
          false,
          e.getMessage());
      LOGGER.error(ERROR_WHEN_FORWARDING, e);
    } finally {
      streamSCU.triggerCloseExecutor();
      files = cleanOrGetBulkDataFiles(in, copy == null);
    }
    return files;
  }

  private void launchCStore(Params p, StoreFromStreamSCU streamSCU, DataWriter dataWriter,
      String cuid,
      String iuid, AdaptTransferSyntax syntax, TransformedPlanarImage transformedPlanarImage)
      throws IOException, InterruptedException {
    try {
      streamSCU.cstore(cuid, iuid, p.getPriority(), dataWriter, syntax.getSuitable());
    } finally {
      if (transformedPlanarImage != null && transformedPlanarImage.getPlanarImage() != null) {
        transformedPlanarImage.getPlanarImage().release();
      }
    }
  }

  private DataWriter buildDataWriterFromTransformedImage(AdaptTransferSyntax syntax,
      AttributeEditorContext context,
      Attributes attributes, TransformedPlanarImage transformedPlanarImage) throws IOException {
    DataWriter dataWriter;
    BytesWithImageDescriptor desc = ImageAdapter.imageTranscode(attributes, syntax, context);
    transformedPlanarImage = transformImage(attributes, context, transformedPlanarImage);
    dataWriter = ImageAdapter.buildDataWriter(attributes,
        syntax,
        transformedPlanarImage != null ? transformedPlanarImage.getEditablePlanarImage() : null,
        desc);
    return dataWriter;
  }

  private static TransformedPlanarImage transformImage(
      Attributes attributes, AttributeEditorContext context,
      TransformedPlanarImage transformedPlanarImage) {
    MaskArea m = context.getMaskArea();
    boolean defacing =
        LangUtil.getEmptytoFalse(context.getProperties().getProperty(Defacer.APPLY_DEFACING));
    if (m != null || defacing) {
      Editable<PlanarImage> editablePlanarImage = buildEditablePlanarImage(attributes, m, defacing,
          transformedPlanarImage);
      transformedPlanarImage.setEditablePlanarImage(editablePlanarImage);
      return transformedPlanarImage;
    }
    return null;
  }

  private static Editable<PlanarImage> buildEditablePlanarImage(Attributes attributes, MaskArea m,
      boolean defacing, TransformedPlanarImage transformedPlanarImage) {
    return img -> {
      PlanarImage planarImage = buildPlanarImage(attributes, m, defacing, img);
      transformedPlanarImage.setPlanarImage(planarImage);
      return planarImage;
    };
  }

  private static PlanarImage buildPlanarImage(Attributes attributes, MaskArea m, boolean defacing,
      PlanarImage img) {
    PlanarImage image = img;
    if (defacing) {
      image = Defacer.apply(attributes, image);
    }
    if (m != null) {
      image = MaskArea.drawShape(image.toMat(), m);
    }
    return image;
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

  public void transferOther(
      ForwardDicomNode fwdNode, DicomForwardDestination destination, Attributes copy, Params p)
      throws IOException {
    StoreFromStreamSCU streamSCU = destination.getStreamSCU();
    Attributes attributesToSend = new Attributes();
    Attributes attributesOriginal = new Attributes();
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
      TransformedPlanarImage transformedPlanarImage = new TransformedPlanarImage();
      if (editors.isEmpty() && syntax.getRequested().equals(tsuid)) {
        dataWriter = new DataWriterAdapter(copy);
        attributesOriginal.addAll(copy);
        attributesToSend = copy;
      } else {
        AttributeEditorContext context =
            new AttributeEditorContext(tsuid, fwdNode, streamSCU.getRemoteDicomNode());
        Attributes attributes = new Attributes(copy);
        attributesOriginal.addAll(attributes);
        attributesToSend = attributes;
        if (!editors.isEmpty()) {
          editors.forEach(e -> e.apply(attributes, context));
          iuid = attributes.getString(Tag.SOPInstanceUID);
          cuid = attributes.getString(Tag.SOPClassUID);
        }

        if (context.getAbort() == Abort.FILE_EXCEPTION) {
          throw new AbortException(context.getAbort(), context.getAbortMessage());
        } else if (context.getAbort() == Abort.CONNECTION_EXCEPTION) {
          throw new AbortException(
              context.getAbort(), "DICOM association abort. " + context.getAbortMessage());
        }
        dataWriter = buildDataWriterFromTransformedImage(syntax, context, attributes,
            transformedPlanarImage);
      }

      launchCStore(p, streamSCU, dataWriter, cuid, iuid, syntax, transformedPlanarImage);

      progressNotify(
          destination, p.getIuid(), p.getCuid(), false, streamSCU.getNumberOfSuboperations());
      monitor(
          fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, true, null);
    } catch (AbortException e) {
      progressNotify(
          destination, p.getIuid(), p.getCuid(), true, streamSCU.getNumberOfSuboperations());
      monitor(
          fwdNode.getId(),
          destination.getId(),
          attributesOriginal,
          attributesToSend,
          false,
          e.getMessage());
      if (e.getAbort() == Abort.CONNECTION_EXCEPTION) {
        throw e;
      }
    } catch (IOException e) {
      progressNotify(
          destination, p.getIuid(), p.getCuid(), true, streamSCU.getNumberOfSuboperations());
      monitor(
          fwdNode.getId(),
          destination.getId(),
          attributesOriginal,
          attributesToSend,
          false,
          e.getMessage());
      throw e;
    } catch (Exception e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      progressNotify(
          destination, p.getIuid(), p.getCuid(), true, streamSCU.getNumberOfSuboperations());
      monitor(
          fwdNode.getId(),
          destination.getId(),
          attributesOriginal,
          attributesToSend,
          false,
          e.getMessage());
      LOGGER.error(ERROR_WHEN_FORWARDING, e);
    } finally {
      streamSCU.triggerCloseExecutor();
    }
  }

  public List<File> transfer(
      ForwardDicomNode fwdNode, WebForwardDestination destination, Attributes copy, Params p)
      throws IOException {
    DicomInputStream in = null;
    List<File> files;
    Attributes attributesToSend = new Attributes();
    Attributes attributesOriginal = new Attributes();
    try {
      List<AttributeEditor> editors = destination.getDicomEditors();
      DicomStowRS stow = destination.getStowrsSingleFile();
      var syntax =
          new AdaptTransferSyntax(p.getTsuid(), destination.getOutputTransferSyntax(p.getTsuid()));

      if (syntax.getRequested().equals(p.getTsuid()) && copy == null && editors.isEmpty()) {
        Attributes fmi =
            Attributes.createFileMetaInformation(p.getIuid(), p.getCuid(), syntax.getRequested());
        try (InputStream stream = p.getData()) {
          attributesToSend = new DicomInputStream(p.getData()).readDataset();
          attributesOriginal.addAll(attributesToSend);
          stow.uploadDicom(stream, fmi);
        } catch (HttpException httpException) {
          if (httpException.getStatusCode() != 409) {
            throw new AbortException(Abort.FILE_EXCEPTION, httpException.getMessage());
          } else {
            LOGGER.debug("File already present in destination");
          }
        }
      } else {
        AttributeEditorContext context = new AttributeEditorContext(p.getTsuid(), fwdNode, null);
        in = new DicomInputStream(p.getData(), p.getTsuid());
        in.setIncludeBulkData(IncludeBulkData.URI);
        Attributes attributes = in.readDataset();
        attributesToSend = attributes;
        attributesOriginal.addAll(attributes);
        if (copy != null) {
          copy.addAll(attributes);
        }
        if (!editors.isEmpty()) {
          editors.forEach(e -> e.apply(attributes, context));
        }

        if (context.getAbort() == Abort.FILE_EXCEPTION) {
          if (p.getData() instanceof PDVInputStream) {
            ((PDVInputStream) p.getData()).skipAll();
          }
          throw new AbortException(context.getAbort(), context.getAbortMessage());
        } else if (context.getAbort() == Abort.CONNECTION_EXCEPTION) {
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
          uploadPayLoadFromTransformedImage(stow, syntax, context, attributes, desc);
        }
      }
      progressNotify(destination, p.getIuid(), p.getCuid(), false, 0);
      monitor(
          fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, true, null);
    } catch (AbortException e) {
      progressNotify(destination, p.getIuid(), p.getCuid(), true, 0);
      monitor(
          fwdNode.getId(),
          destination.getId(),
          attributesOriginal,
          attributesToSend,
          false,
          e.getMessage());
      if (e.getAbort() == Abort.CONNECTION_EXCEPTION) {
        throw e;
      }
    } catch (IOException e) {
      progressNotify(destination, p.getIuid(), p.getCuid(), true, 0);
      monitor(
          fwdNode.getId(),
          destination.getId(),
          attributesOriginal,
          attributesToSend,
          false,
          e.getMessage());
      throw e;
    } catch (Exception e) {
      progressNotify(destination, p.getIuid(), p.getCuid(), true, 0);
      monitor(
          fwdNode.getId(),
          destination.getId(),
          attributesOriginal,
          attributesToSend,
          false,
          e.getMessage());
      LOGGER.error(ERROR_WHEN_FORWARDING, e);
    } finally {
      files = cleanOrGetBulkDataFiles(in, copy == null);
    }
    return files;
  }

  private void uploadPayLoadFromTransformedImage(DicomStowRS stow, AdaptTransferSyntax syntax,
      AttributeEditorContext context, Attributes attributes, BytesWithImageDescriptor desc)
      throws Exception {
    TransformedPlanarImage transformedPlanarImage = new TransformedPlanarImage();
    try {
      transformedPlanarImage = transformImage(attributes, context, transformedPlanarImage);
      stow.uploadPayload(ImageAdapter.preparePlayload(attributes, syntax, desc,
          transformedPlanarImage != null ? transformedPlanarImage.getEditablePlanarImage() : null));
    } finally {
      if (transformedPlanarImage != null && transformedPlanarImage.getPlanarImage() != null) {
        transformedPlanarImage.getPlanarImage().release();
      }
    }
  }

  public void transferOther(
      ForwardDicomNode fwdNode, WebForwardDestination destination, Attributes copy, Params p)
      throws IOException {
    Attributes attributesToSend = new Attributes();
    Attributes attributesOriginal = new Attributes();
    try {
      List<AttributeEditor> editors = destination.getDicomEditors();
      DicomStowRS stow = destination.getStowrsSingleFile();
      var syntax =
          new AdaptTransferSyntax(p.getTsuid(), destination.getOutputTransferSyntax(p.getTsuid()));
      if (syntax.getRequested().equals(p.getTsuid()) && editors.isEmpty()) {
        attributesToSend = copy;
        attributesOriginal.addAll(copy);
        stow.uploadDicom(copy, syntax.getRequested());
      } else {
        AttributeEditorContext context = new AttributeEditorContext(p.getTsuid(), fwdNode, null);
        Attributes attributes = new Attributes(copy);
        attributesToSend = attributes;
        attributesOriginal.addAll(attributes);
        editors.forEach(e -> e.apply(attributes, context));

        if (context.getAbort() == Abort.FILE_EXCEPTION) {
          throw new AbortException(context.getAbort(), context.getAbortMessage());
        } else if (context.getAbort() == Abort.CONNECTION_EXCEPTION) {
          throw new AbortException(
              context.getAbort(), "DICOM associtation abort. " + context.getAbortMessage());
        }

        BytesWithImageDescriptor desc = ImageAdapter.imageTranscode(attributes, syntax, context);
        if (desc == null) {
          stow.uploadDicom(attributes, syntax.getOriginal());
        } else {
          uploadPayLoadFromTransformedImage(stow, syntax, context, attributes, desc);
        }
        progressNotify(destination, p.getIuid(), p.getCuid(), false, 0);
        monitor(
            fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, true, null);
      }
    } catch (HttpException httpException) {
      if (httpException.getStatusCode() != 409) {
        progressNotify(destination, p.getIuid(), p.getCuid(), true, 0);
        monitor(
            fwdNode.getId(),
            destination.getId(),
            attributesOriginal,
            attributesToSend,
            false,
            httpException.getMessage());
        throw new AbortException(Abort.FILE_EXCEPTION, "DICOMWeb forward", httpException);
      } else {
        progressNotify(destination, p.getIuid(), p.getCuid(), false, 0);
        monitor(
            fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, true, null);
        LOGGER.debug("File already present in destination");
      }
    } catch (AbortException e) {
      progressNotify(destination, p.getIuid(), p.getCuid(), true, 0);
      monitor(
          fwdNode.getId(),
          destination.getId(),
          attributesOriginal,
          attributesToSend,
          false,
          e.getMessage());
      if (e.getAbort() == Abort.CONNECTION_EXCEPTION) {
        throw e;
      }
    } catch (IOException e) {
      progressNotify(destination, p.getIuid(), p.getCuid(), true, 0);
      monitor(
          fwdNode.getId(),
          destination.getId(),
          attributesOriginal,
          attributesToSend,
          false,
          e.getMessage());
      throw e;
    } catch (Exception e) {
      progressNotify(destination, p.getIuid(), p.getCuid(), true, 0);
      monitor(
          fwdNode.getId(),
          destination.getId(),
          attributesOriginal,
          attributesToSend,
          false,
          e.getMessage());
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

  /**
   * Publish an event for monitoring purpose
   *
   * @param forwardNodeId      ForwardNode Id
   * @param destinationId      Destination Id
   * @param attributesOriginal Original value
   * @param attributesToSend   De-identify value
   * @param sent               Flag to know if the transfer occurred
   * @param reason             Reason of not transferring the file
   */
  private void monitor(
      Long forwardNodeId,
      Long destinationId,
      Attributes attributesOriginal,
      Attributes attributesToSend,
      boolean sent,
      String reason) {
    applicationEventPublisher.publishEvent(
        new TransferMonitoringEvent(
            TransferStatusEntity.buildTransferStatusEntity(
                forwardNodeId, destinationId, attributesOriginal, attributesToSend, sent, reason)));
  }
}
