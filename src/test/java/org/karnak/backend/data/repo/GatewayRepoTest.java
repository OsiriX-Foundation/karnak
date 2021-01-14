package org.karnak.backend.data.repo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Consumer;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.enums.DestinationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class GatewayRepoTest {

  private final Consumer<ForwardNodeEntity> forwardNodeConsumer = //
      x ->
          assertThat(x) //
              .hasFieldOrPropertyWithValue("description", "description") //
              .hasFieldOrPropertyWithValue("fwdAeTitle", "fwdAeTitle") //
              .extracting(Object::toString)
              .asString()
              .matches("^ForwardNode \\[.*");
  private final Consumer<DicomSourceNodeEntity> sourceNodeConsumer = //
      x ->
          assertThat(x) //
              .hasFieldOrPropertyWithValue("description", "description") //
              .hasFieldOrPropertyWithValue("aeTitle", "aeTitle") //
              .hasFieldOrPropertyWithValue("hostname", "hostname") //
              .hasFieldOrPropertyWithValue("checkHostname", true) //
              .extracting(Object::toString)
              .asString()
              .matches("^DicomSourceNode \\[.*");
  private final Consumer<DestinationEntity> destinationDicomConsumer = //
      x ->
          assertThat(x) //
              .hasFieldOrPropertyWithValue("description", "description") //
              .hasFieldOrPropertyWithValue("type", DestinationType.dicom) //
              .hasFieldOrPropertyWithValue("aeTitle", "aeTitle") //
              .hasFieldOrPropertyWithValue("hostname", "hostname") //
              .hasFieldOrPropertyWithValue("port", 123) //
              .extracting(Object::toString)
              .asString()
              .matches("^Destination \\[.*");
  private final Consumer<DestinationEntity> destinationStowConsumer = //
      x ->
          assertThat(x) //
              .hasFieldOrPropertyWithValue("description", "description") //
              .hasFieldOrPropertyWithValue("type", DestinationType.stow) //
              .hasFieldOrPropertyWithValue("url", "url") //
              .hasFieldOrPropertyWithValue("urlCredentials", "urlCredentials") //
              .hasFieldOrPropertyWithValue("headers", "headers") //
              .extracting(Object::toString)
              .asString()
              .matches("^Destination \\[.*");

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private GatewayRepo repository;

  @Test
  public void testInvalidForwardNode_Mandatory() {
    ForwardNodeEntity forwardNodeEntity = ForwardNodeEntity.ofEmpty();
    forwardNodeEntity.setFwdAeTitle(null);
    String expectedMessage = "Forward AETitle is mandatory";
    Exception exception =
        assertThrows(
            ConstraintViolationException.class,
            () -> {
              entityManager.persistAndFlush(forwardNodeEntity);
            });
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void testInvalidForwardNode_Size() {
    ForwardNodeEntity forwardNodeEntity = ForwardNodeEntity.ofEmpty();
    forwardNodeEntity.setFwdAeTitle("ABCDEFGHIJ-ABCDEFGHIJ");
    String expectedMessage = "Forward AETitle has more than 16 characters";
    Exception exception =
        assertThrows(
            ConstraintViolationException.class,
            () -> {
              entityManager.persistAndFlush(forwardNodeEntity);
            });
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void testInvalidSourceNode_AETitle_mandatory() {
    ForwardNodeEntity forwardNodeEntity = ForwardNodeEntity.ofEmpty();
    forwardNodeEntity.setDescription("description");
    forwardNodeEntity.setFwdAeTitle("fwdAeTitle");
    DicomSourceNodeEntity sourceNode = DicomSourceNodeEntity.ofEmpty();
    sourceNode.setDescription("description");
    sourceNode.setAeTitle(null);
    sourceNode.setHostname("hostname");
    forwardNodeEntity.addSourceNode(sourceNode);

    String expectedMessage = "AETitle is mandatory";
    Exception exception =
        assertThrows(
            ConstraintViolationException.class,
            () -> {
              entityManager.persistAndFlush(forwardNodeEntity);
            });
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void testInvalidDestinationDicom_AETitle_mandatory() {
    ForwardNodeEntity forwardNodeEntity = ForwardNodeEntity.ofEmpty();
    forwardNodeEntity.setDescription("description");
    forwardNodeEntity.setFwdAeTitle("fwdAeTitle");
    DestinationEntity destinationEntity =
        DestinationEntity.ofDicom("description", null, "hostname", 123, null);
    forwardNodeEntity.addDestination(destinationEntity);

    String expectedMessage = "AETitle is mandatory";
    Exception exception =
        assertThrows(
            ConstraintViolationException.class,
            () -> {
              entityManager.persistAndFlush(forwardNodeEntity);
            });
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void testInvalidDestinationStow_URL_mandatory() {
    ForwardNodeEntity forwardNodeEntity = ForwardNodeEntity.ofEmpty();
    forwardNodeEntity.setDescription("description");
    forwardNodeEntity.setFwdAeTitle("fwdAeTitle");
    DestinationEntity destinationEntity =
        DestinationEntity.ofStow("description", null, "urlCredentials", "headers");
    forwardNodeEntity.addDestination(destinationEntity);

    String expectedMessage = "URL is mandatory";
    Exception exception =
        assertThrows(
            ConstraintViolationException.class,
            () -> {
              entityManager.persistAndFlush(forwardNodeEntity);
            });
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void testForwardNode() {
    ForwardNodeEntity forwardNodeEntity = ForwardNodeEntity.ofEmpty();
    forwardNodeEntity.setDescription("description");
    forwardNodeEntity.setFwdAeTitle("fwdAeTitle");
    entityManager.persistAndFlush(forwardNodeEntity);

    Iterable<ForwardNodeEntity> all = repository.findAll();
    assertThat(all) //
        .hasSize(1) //
        .first() //
        .satisfies(forwardNodeConsumer);
  }

  @Test
  public void testWithSourceNode() {
    ForwardNodeEntity forwardNodeEntity = ForwardNodeEntity.ofEmpty();
    forwardNodeEntity.setDescription("description");
    forwardNodeEntity.setFwdAeTitle("fwdAeTitle");
    DicomSourceNodeEntity sourceNode = DicomSourceNodeEntity.ofEmpty();
    sourceNode.setDescription("description");
    sourceNode.setAeTitle("aeTitle");
    sourceNode.setHostname("hostname");
    sourceNode.setCheckHostname(Boolean.TRUE);
    forwardNodeEntity.addSourceNode(sourceNode);
    entityManager.persistAndFlush(forwardNodeEntity);

    Iterable<ForwardNodeEntity> all = repository.findAll();
    assertThat(all) //
        .hasSize(1) //
        .first() //
        .satisfies(forwardNodeConsumer);

    assertThat(all) //
        .hasSize(1) //
        .flatExtracting(ForwardNodeEntity::getSourceNodes) //
        .hasSize(1) //
        .first() //
        .satisfies(sourceNodeConsumer);

    assertThat(all) //
        .hasSize(1) //
        .flatExtracting(ForwardNodeEntity::getDestinationEntities) //
        .hasSize(0);
  }

  @Test
  public void testWithDestinationDicom() {
    ForwardNodeEntity forwardNodeEntity = ForwardNodeEntity.ofEmpty();
    forwardNodeEntity.setDescription("description");
    forwardNodeEntity.setFwdAeTitle("fwdAeTitle");
    DestinationEntity destinationEntity =
        DestinationEntity.ofDicom("description", "aeTitle", "hostname", 123, null);
    forwardNodeEntity.addDestination(destinationEntity);
    entityManager.persistAndFlush(forwardNodeEntity);

    Iterable<ForwardNodeEntity> all = repository.findAll();
    assertThat(all) //
        .hasSize(1) //
        .first() //
        .satisfies(forwardNodeConsumer);

    assertThat(all) //
        .hasSize(1) //
        .flatExtracting(ForwardNodeEntity::getSourceNodes) //
        .hasSize(0);

    assertThat(all) //
        .hasSize(1) //
        .flatExtracting(ForwardNodeEntity::getDestinationEntities) //
        .hasSize(1) //
        .first() //
        .satisfies(destinationDicomConsumer);
  }

  @Test
  public void testWithDestinationStow() {
    ForwardNodeEntity forwardNodeEntity = ForwardNodeEntity.ofEmpty();
    forwardNodeEntity.setDescription("description");
    forwardNodeEntity.setFwdAeTitle("fwdAeTitle");
    DestinationEntity destinationEntity =
        DestinationEntity.ofStow("description", "url", "urlCredentials", "headers");
    forwardNodeEntity.addDestination(destinationEntity);
    entityManager.persistAndFlush(forwardNodeEntity);

    Iterable<ForwardNodeEntity> all = repository.findAll();
    assertThat(all) //
        .hasSize(1) //
        .first() //
        .satisfies(forwardNodeConsumer);

    assertThat(all) //
        .hasSize(1) //
        .flatExtracting(ForwardNodeEntity::getSourceNodes) //
        .hasSize(0);

    assertThat(all) //
        .hasSize(1) //
        .flatExtracting(ForwardNodeEntity::getDestinationEntities) //
        .hasSize(1) //
        .first() //
        .satisfies(destinationStowConsumer);
  }

  @Test
  public void testWithSourceNodeAndDestinationDicom() {
    ForwardNodeEntity forwardNodeEntity = ForwardNodeEntity.ofEmpty();
    forwardNodeEntity.setDescription("description");
    forwardNodeEntity.setFwdAeTitle("fwdAeTitle");
    DicomSourceNodeEntity sourceNode = DicomSourceNodeEntity.ofEmpty();
    sourceNode.setDescription("description");
    sourceNode.setAeTitle("aeTitle");
    sourceNode.setHostname("hostname");
    sourceNode.setCheckHostname(Boolean.TRUE);
    forwardNodeEntity.addSourceNode(sourceNode);
    DestinationEntity destinationEntity =
        DestinationEntity.ofDicom("description", "aeTitle", "hostname", 123, null);
    forwardNodeEntity.addDestination(destinationEntity);
    entityManager.persistAndFlush(forwardNodeEntity);

    Iterable<ForwardNodeEntity> all = repository.findAll();
    assertThat(all) //
        .hasSize(1) //
        .first() //
        .satisfies(forwardNodeConsumer);

    assertThat(all) //
        .hasSize(1) //
        .flatExtracting(ForwardNodeEntity::getSourceNodes) //
        .hasSize(1) //
        .first() //
        .satisfies(sourceNodeConsumer);

    assertThat(all) //
        .hasSize(1) //
        .flatExtracting(ForwardNodeEntity::getDestinationEntities) //
        .hasSize(1) //
        .first() //
        .satisfies(destinationDicomConsumer);
  }
}
