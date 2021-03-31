/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.gateway;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.data.entity.KheopsAlbumsEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.data.repo.ForwardNodeRepo;
import org.karnak.backend.enums.DestinationType;
import org.karnak.backend.enums.NodeEventType;
import org.karnak.backend.model.NodeEvent;
import org.karnak.backend.service.DeIdentifyEditorService;
import org.karnak.backend.service.FilterEditorService;
import org.karnak.backend.service.StreamRegistryService;
import org.karnak.backend.service.kheops.SwitchingAlbumService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GatewaySetUpServiceTest {

  // Repositories
  final ForwardNodeRepo forwardNodeRepoMock = Mockito.mock(ForwardNodeRepo.class);

  // Service
  private GatewaySetUpService gatewaySetUpService;
  final SwitchingAlbumService switchingAlbumServiceMock = Mockito.mock(SwitchingAlbumService.class);
  final DeIdentifyEditorService deIdentifyEditorServiceMock =
      Mockito.mock(DeIdentifyEditorService.class);
  final StreamRegistryService streamRegistryServiceMock = Mockito.mock(StreamRegistryService.class);
  final FilterEditorService filterEditorServiceMock = Mockito.mock(FilterEditorService.class);

  @BeforeEach
  public void setUp() throws Exception {

    // Build mocked service
    gatewaySetUpService =
        new GatewaySetUpService(
            forwardNodeRepoMock,
            switchingAlbumServiceMock,
            deIdentifyEditorServiceMock,
            streamRegistryServiceMock,
            filterEditorServiceMock);
  }

  @Test
  void should_reload_gateway_stow() {
    // Init data
    List<ForwardNodeEntity> forwardNodeEntities = new ArrayList<>();
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();
    forwardNodeEntity.setFwdAeTitle("fwdAeTitle");
    forwardNodeEntity.setId(1L);
    Set<DicomSourceNodeEntity> dicomSourceNodeEntities = new HashSet<>();
    DicomSourceNodeEntity dicomSourceNodeEntity = new DicomSourceNodeEntity();
    dicomSourceNodeEntity.setId(2L);
    dicomSourceNodeEntity.setAeTitle("aeTitle");
    dicomSourceNodeEntity.setHostname("hostName");
    forwardNodeEntity.setSourceNodes(dicomSourceNodeEntities);
    Set<DestinationEntity> destinationEntities = new HashSet<>();
    DestinationEntity destinationEntity = new DestinationEntity();
    destinationEntity.setFilterBySOPClasses(true);
    List<KheopsAlbumsEntity> kheopsAlbumsEntities = new ArrayList<>();
    KheopsAlbumsEntity kheopsAlbumsEntity = new KheopsAlbumsEntity();
    kheopsAlbumsEntities.add(kheopsAlbumsEntity);
    destinationEntity.setKheopsAlbumEntities(kheopsAlbumsEntities);
    destinationEntity.setDesidentification(true);
    ProjectEntity projectEntity = new ProjectEntity();
    ProfileEntity profileEntity = new ProfileEntity();
    projectEntity.setProfileEntity(profileEntity);
    destinationEntity.setProjectEntity(projectEntity);
    destinationEntity.setNotifyInterval(1);
    destinationEntity.setDestinationType(DestinationType.stow);
    destinationEntities.add(destinationEntity);
    forwardNodeEntity.setDestinationEntities(destinationEntities);
    forwardNodeEntities.add(forwardNodeEntity);

    // Mock
    Mockito.when(forwardNodeRepoMock.findAll()).thenReturn(forwardNodeEntities);

    // Call service
    gatewaySetUpService.reloadGatewayPersistence();

    // Test results
    Assert.assertNotNull(gatewaySetUpService.getDestinations());
    Assert.assertEquals(1, gatewaySetUpService.getDestinations().size());
    // TODO: add more asserts
  }

  @Test
  void should_reload_gateway_dicom() {
    // Init data
    List<ForwardNodeEntity> forwardNodeEntities = new ArrayList<>();
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();
    forwardNodeEntity.setFwdAeTitle("fwdAeTitle");
    forwardNodeEntity.setId(1L);
    Set<DicomSourceNodeEntity> dicomSourceNodeEntities = new HashSet<>();
    DicomSourceNodeEntity dicomSourceNodeEntity = new DicomSourceNodeEntity();
    dicomSourceNodeEntity.setId(2L);
    dicomSourceNodeEntity.setAeTitle("aeTitle");
    dicomSourceNodeEntity.setHostname("hostName");
    forwardNodeEntity.setSourceNodes(dicomSourceNodeEntities);
    Set<DestinationEntity> destinationEntities = new HashSet<>();
    DestinationEntity destinationEntity = new DestinationEntity();
    destinationEntity.setFilterBySOPClasses(true);
    destinationEntity.setAeTitle("aeTitle");
    List<KheopsAlbumsEntity> kheopsAlbumsEntities = new ArrayList<>();
    KheopsAlbumsEntity kheopsAlbumsEntity = new KheopsAlbumsEntity();
    kheopsAlbumsEntities.add(kheopsAlbumsEntity);
    destinationEntity.setKheopsAlbumEntities(kheopsAlbumsEntities);
    destinationEntity.setDesidentification(true);
    ProjectEntity projectEntity = new ProjectEntity();
    ProfileEntity profileEntity = new ProfileEntity();
    projectEntity.setProfileEntity(profileEntity);
    destinationEntity.setProjectEntity(projectEntity);
    destinationEntity.setNotifyInterval(1);
    destinationEntity.setPort(11112);
    destinationEntity.setDestinationType(DestinationType.dicom);
    destinationEntities.add(destinationEntity);
    forwardNodeEntity.setDestinationEntities(destinationEntities);
    forwardNodeEntities.add(forwardNodeEntity);

    // Mock
    Mockito.when(forwardNodeRepoMock.findAll()).thenReturn(forwardNodeEntities);

    // Call service
    gatewaySetUpService.reloadGatewayPersistence();

    // Test results
    Assert.assertNotNull(gatewaySetUpService.getDestinations());
    Assert.assertEquals(1, gatewaySetUpService.getDestinations().size());
    // TODO: add more asserts
  }

  @Test
  void should_update_dicom_source_node() {
    // Init data
    DicomSourceNodeEntity dicomSourceNodeEntity = new DicomSourceNodeEntity();
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();
    forwardNodeEntity.setFwdAeTitle("fwdAeTitle");
    forwardNodeEntity.setId(2L);
    dicomSourceNodeEntity.setForwardNodeEntity(forwardNodeEntity);
    dicomSourceNodeEntity.setId(1L);
    dicomSourceNodeEntity.setAeTitle("aeTitle");
    dicomSourceNodeEntity.setHostname("hostName");

    NodeEvent nodeEvent = new NodeEvent(dicomSourceNodeEntity, NodeEventType.ADD);

    // Call service ADD
    gatewaySetUpService.update(nodeEvent);

    // Test results
    Assert.assertTrue(gatewaySetUpService.getDestinationNode("fwdAeTitle").isPresent());

    // Modify aeTitle
    forwardNodeEntity.setFwdAeTitle("aeTitleModified");
    // Set Update
    nodeEvent = new NodeEvent(dicomSourceNodeEntity, NodeEventType.UPDATE);

    // Call service UPDATE
    gatewaySetUpService.update(nodeEvent);

    // Test results
    Assert.assertTrue(gatewaySetUpService.getDestinationNode("fwdAeTitle").isPresent());
    // TODO modify this assert
    Assert.assertEquals("aeTitleModified",gatewaySetUpService.getDestinationNode("fwdAeTitle").get().getAet());



    // TODO: add more asserts



  }
}
