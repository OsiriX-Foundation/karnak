package org.karnak.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.data.repo.DicomSourceNodeRepo;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

@SpringBootTest
class SourceNodeServiceTest {

  // Application Event Publisher
  private final ApplicationEventPublisher applicationEventPublisherMock =
      Mockito.mock(ApplicationEventPublisher.class);

  // Repositories
  private final DicomSourceNodeRepo dicomSourceNodeRepoMock =
      Mockito.mock(DicomSourceNodeRepo.class);

  // Services
  private final ForwardNodeService forwardNodeServiceMock = Mockito.mock(ForwardNodeService.class);
  private SourceNodeService sourceNodeService;

  @BeforeEach
  public void setUp() {
    // Build mocked service
    sourceNodeService =
        new SourceNodeService(
            dicomSourceNodeRepoMock, forwardNodeServiceMock, applicationEventPublisherMock);
  }

  @Test
  void should_retrieve_source_nodes_from_forward_node() {
    // Init data
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();

    // Call service
    sourceNodeService.retrieveSourceNodes(forwardNodeEntity);

    // Test results
    Mockito.verify(forwardNodeServiceMock, Mockito.times(1))
        .getAllSourceNodes(Mockito.any(ForwardNodeEntity.class));
  }

  @Test
  void should_save_dicom_source_node() {
    // Init data
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();
    DicomSourceNodeEntity dicomSourceNodeEntity = new DicomSourceNodeEntity();

    // Mock service
    Mockito.when(
            forwardNodeServiceMock.updateSourceNode(
                Mockito.any(ForwardNodeEntity.class), Mockito.any(DicomSourceNodeEntity.class)))
        .thenReturn(dicomSourceNodeEntity);

    // Call service
    sourceNodeService.save(forwardNodeEntity, dicomSourceNodeEntity);

    // Test results
    Mockito.verify(forwardNodeServiceMock, Mockito.times(1))
        .updateSourceNode(
            Mockito.any(ForwardNodeEntity.class), Mockito.any(DicomSourceNodeEntity.class));
    Mockito.verify(dicomSourceNodeRepoMock, Mockito.times(1))
        .saveAndFlush(Mockito.any(DicomSourceNodeEntity.class));
  }

  @Test
  void should_delete_source_node() {
    // Init data
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();
    DicomSourceNodeEntity dicomSourceNodeEntity = new DicomSourceNodeEntity();
    dicomSourceNodeEntity.setId(1L);

    // Call service
    sourceNodeService.delete(forwardNodeEntity, dicomSourceNodeEntity);

    // Test results
    Mockito.verify(forwardNodeServiceMock, Mockito.times(1))
        .deleteSourceNode(
            Mockito.any(ForwardNodeEntity.class), Mockito.any(DicomSourceNodeEntity.class));
    Mockito.verify(dicomSourceNodeRepoMock, Mockito.times(1)).deleteById(Mockito.anyLong());
  }
}
