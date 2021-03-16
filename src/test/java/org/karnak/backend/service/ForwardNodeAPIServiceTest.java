package org.karnak.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.model.NodeEvent;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

@SpringBootTest
class ForwardNodeAPIServiceTest {

  // Application Event Publisher
  private final ApplicationEventPublisher applicationEventPublisherMock =
      Mockito.mock(ApplicationEventPublisher.class);

  // Service
  private final ForwardNodeService forwardNodeServiceMock = Mockito.mock(ForwardNodeService.class);
  private ForwardNodeAPIService forwardNodeAPIService;

  @BeforeEach
  public void setUp() {

    // Build mocked service
    forwardNodeAPIService = new ForwardNodeAPIService(forwardNodeServiceMock, applicationEventPublisherMock);
  }

  @Test
  void should_add_new_forward_node() {
    // Init data
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();

    // Call service
    forwardNodeAPIService.addForwardNode(forwardNodeEntity);

    // Test results
    Mockito.verify(forwardNodeServiceMock, Mockito.times(1))
        .getAllForwardNodes();
    Mockito.verify(forwardNodeServiceMock, Mockito.times(1))
        .save(Mockito.any(ForwardNodeEntity.class));
    Mockito.verify(applicationEventPublisherMock, Mockito.times(1))
        .publishEvent(Mockito.any(NodeEvent.class));
  }

  @Test
  void should_update_forward_node() {
    // Init data
    ForwardNodeEntity forwardNodeEntity = new ForwardNodeEntity();
    forwardNodeEntity.setId(1L);

    // Call service
    forwardNodeAPIService.addForwardNode(forwardNodeEntity);

    // Test results
    Mockito.verify(forwardNodeServiceMock, Mockito.times(0))
        .getAllForwardNodes();
    Mockito.verify(forwardNodeServiceMock, Mockito.times(1))
        .save(Mockito.any(ForwardNodeEntity.class));
    Mockito.verify(applicationEventPublisherMock, Mockito.times(1))
        .publishEvent(Mockito.any(NodeEvent.class));
  }

}
