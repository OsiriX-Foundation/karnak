/*
 * Copyright (c) 2009-2019 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.dicom;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.net.ApplicationEntity;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.Connection;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.PDVInputStream;
import org.dcm4che3.net.Status;
import org.dcm4che3.net.TransferCapability;
import org.dcm4che3.net.pdu.PresentationContext;
import org.dcm4che3.net.service.BasicCEchoSCP;
import org.dcm4che3.net.service.BasicCStoreSCP;
import org.dcm4che3.net.service.DicomServiceException;
import org.dcm4che3.net.service.DicomServiceRegistry;
import org.dcm4che3.tool.common.CLIUtils;
import org.karnak.backend.dicom.ForwardUtil.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.dicom.param.AdvancedParams;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.DicomNode;

public class StoreScpForward {

  private static final Logger LOGGER = LoggerFactory.getLogger(StoreScpForward.class);

  private final Device device = new Device("storescp");
  private final ApplicationEntity ae = new ApplicationEntity("*");
  private final Connection conn = new Connection();
  private volatile int priority;
  private volatile int status = 0;

  private final Map<ForwardDicomNode, List<ForwardDestination>> destinations;

  private final BasicCStoreSCP cstoreSCP =
      new BasicCStoreSCP("*") {

        @Override
        protected void store(
            Association as,
            PresentationContext pc,
            Attributes rq,
            PDVInputStream data,
            Attributes rsp)
            throws IOException {
          Optional<ForwardDicomNode> sourceNode =
              destinations.keySet().stream()
                  .filter(n -> n.getForwardAETitle().equals(as.getCalledAET()))
                  .findFirst();
          if (sourceNode.isEmpty()) {
            throw new IllegalStateException("Cannot find the forward AeTitle " + as.getCalledAET());
          }
          ForwardDicomNode fwdNode = sourceNode.get();
          List<ForwardDestination> destList = destinations.get(fwdNode);
          if (destList == null || destList.isEmpty()) {
            throw new IllegalStateException("No DICOM destinations for " + fwdNode);
          }

          DicomNode callingNode = DicomNode.buildRemoteDicomNode(as);
          Set<DicomNode> srcNodes = fwdNode.getAcceptedSourceNodes();
          boolean valid =
              srcNodes.isEmpty()
                  || srcNodes.stream()
                      .anyMatch(
                          n ->
                              n.getAet().equals(callingNode.getAet())
                                  && (!n.isValidateHostname()
                                      || n.equalsHostname(callingNode.getHostname())));
          if (!valid) {
            rsp.setInt(Tag.Status, VR.US, Status.NotAuthorized);
            LOGGER.error(
                "Refused: not authorized (124H). Source node: {}. SopUID: {}",
                callingNode,
                rq.getString(Tag.AffectedSOPInstanceUID));
            return;
          }

          rsp.setInt(Tag.Status, VR.US, status);

          try {
            Params p =
                new Params(
                    rq.getString(Tag.AffectedSOPInstanceUID),
                    rq.getString(Tag.AffectedSOPClassUID),
                    pc.getTransferSyntax(),
                    priority,
                    data,
                    as);

            ForwardUtil.storeMulitpleDestination(fwdNode, destList, p);

          } catch (Exception e) {
            throw new DicomServiceException(Status.ProcessingFailure, e);
          }
        }
      };

  public StoreScpForward(ForwardDicomNode fwdNode, DicomNode destinationNode) throws IOException {
    this(null, fwdNode, destinationNode, null);
  }

  public StoreScpForward(
      AdvancedParams forwardParams, ForwardDicomNode callingNode, DicomNode destinationNode)
      throws IOException {
    this(forwardParams, callingNode, destinationNode, null);
  }

  /**
   * @param forwardParams the optional advanced parameters (proxy, authentication, connection and
   *     TLS) for the final destination
   * @param fwdNode the calling DICOM node configuration
   * @param destinationNode the final DICOM node configuration
   * @param attributesEditor the editor for modifying attributes on the fly (can be Null)
   */
  public StoreScpForward(
      AdvancedParams forwardParams,
      ForwardDicomNode fwdNode,
      DicomNode destinationNode,
      List<AttributeEditor> attributesEditor)
      throws IOException {
    device.setDimseRQHandler(createServiceRegistry());
    device.addConnection(conn);
    device.addApplicationEntity(ae);
    ae.setAssociationAcceptor(true);
    ae.addConnection(conn);

    DicomForwardDestination uniqueDestination =
        new DicomForwardDestination(forwardParams, fwdNode, destinationNode, attributesEditor);
    this.destinations = new HashMap<>();
    destinations.put(fwdNode, Collections.singletonList(uniqueDestination));
  }

  public StoreScpForward(Map<ForwardDicomNode, List<ForwardDestination>> destinations) {
    device.setDimseRQHandler(createServiceRegistry());
    device.addConnection(conn);
    device.addApplicationEntity(ae);
    ae.setAssociationAcceptor(true);
    ae.addConnection(conn);
    this.destinations = Objects.requireNonNull(destinations);
  }

  public final void setPriority(int priority) {
    this.priority = priority;
  }

  private DicomServiceRegistry createServiceRegistry() {
    DicomServiceRegistry serviceRegistry = new DicomServiceRegistry();
    serviceRegistry.addDicomService(new BasicCEchoSCP());
    serviceRegistry.addDicomService(cstoreSCP);
    return serviceRegistry;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void loadDefaultTransferCapability(URL transferCapabilityFile) {
    Properties p = new Properties();

    try {
      if (transferCapabilityFile != null) {
        try (InputStream in = transferCapabilityFile.openStream()) {
          p.load(in);
        }
      } else {
        p.load(this.getClass().getResourceAsStream("sop-classes.properties"));
      }
    } catch (IOException e) {
      LOGGER.error("Cannot read sop-classes", e);
    }

    for (String cuid : p.stringPropertyNames()) {
      String ts = p.getProperty(cuid);
      TransferCapability tc =
          new TransferCapability(
              null, CLIUtils.toUID(cuid), TransferCapability.Role.SCP, CLIUtils.toUIDs(ts));
      ae.addTransferCapability(tc);
    }
  }

  public ApplicationEntity getApplicationEntity() {
    return ae;
  }

  public Connection getConnection() {
    return conn;
  }

  public Device getDevice() {
    return device;
  }

  public void stop() {
    destinations.values().forEach(l -> l.forEach(ForwardDestination::stop));
  }
}
