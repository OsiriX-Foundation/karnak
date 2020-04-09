package org.karnak.data.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;

import javax.validation.ConstraintViolationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.DestinationType;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.data.gateway.GatewayPersistence;
import org.karnak.data.gateway.DicomSourceNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@RunWith(SpringRunner.class)
@DataJpaTest
public class GatewayPersistenceTest {
    private Consumer<ForwardNode> forwardNodeConsumer = //
            x -> assertThat(x) //
                    .hasFieldOrPropertyWithValue("description", "description") //
                    .hasFieldOrPropertyWithValue("fwdAeTitle", "fwdAeTitle") //
                    .extracting(Object::toString).asString().matches("^ForwardNode \\[.*");
    private Consumer<DicomSourceNode> sourceNodeConsumer = //
            x -> assertThat(x) //
                    .hasFieldOrPropertyWithValue("description", "description") //
                    .hasFieldOrPropertyWithValue("aeTitle", "aeTitle") //
                    .hasFieldOrPropertyWithValue("hostname", "hostname") //
                    .hasFieldOrPropertyWithValue("checkHostname", true) //
                    .extracting(Object::toString).asString().matches("^DicomSourceNode \\[.*");
    private Consumer<Destination> destinationDicomConsumer = //
            x -> assertThat(x) //
                    .hasFieldOrPropertyWithValue("description", "description") //
                    .hasFieldOrPropertyWithValue("type", DestinationType.dicom) //
                    .hasFieldOrPropertyWithValue("aeTitle", "aeTitle") //
                    .hasFieldOrPropertyWithValue("hostname", "hostname") //
                    .hasFieldOrPropertyWithValue("port", 123) //
                    .extracting(Object::toString).asString().matches("^Destination \\[.*");
    private Consumer<Destination> destinationStowConsumer = //
            x -> assertThat(x) //
                    .hasFieldOrPropertyWithValue("description", "description") //
                    .hasFieldOrPropertyWithValue("type", DestinationType.stow) //
                    .hasFieldOrPropertyWithValue("url", "url") //
                    .hasFieldOrPropertyWithValue("urlCredentials", "urlCredentials") //
                    .hasFieldOrPropertyWithValue("headers", "headers") //
                    .extracting(Object::toString).asString().matches("^Destination \\[.*");

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GatewayPersistence repository;

    @Test
    public void testInvalidForwardNode_Mandatory() {
        expectedEx.expect(ConstraintViolationException.class);
        expectedEx.expectMessage("Forward AETitle is mandatory");

        ForwardNode forwardNode = ForwardNode.ofEmpty();
        forwardNode.setFwdAeTitle(null);
        entityManager.persistAndFlush(forwardNode);
    }

    @Test
    public void testInvalidForwardNode_Size() {
        expectedEx.expect(ConstraintViolationException.class);
        expectedEx.expectMessage("Forward AETitle has more than 16 characters");

        ForwardNode forwardNode = ForwardNode.ofEmpty();
        forwardNode.setFwdAeTitle("ABCDEFGHIJ-ABCDEFGHIJ");
        entityManager.persistAndFlush(forwardNode);
    }

    @Test
    public void testInvalidSourceNode_AETitle_mandatory() {
        expectedEx.expect(ConstraintViolationException.class);
        expectedEx.expectMessage("AETitle is mandatory");

        ForwardNode forwardNode = ForwardNode.ofEmpty();
        forwardNode.setDescription("description");
        forwardNode.setFwdAeTitle("fwdAeTitle");
        DicomSourceNode sourceNode = DicomSourceNode.ofEmpty();
        sourceNode.setDescription("description");
        sourceNode.setAeTitle(null);
        sourceNode.setHostname("hostname");
        forwardNode.addSourceNode(sourceNode);
        entityManager.persistAndFlush(forwardNode);
    }

    @Test
    public void testInvalidDestinationDicom_AETitle_mandatory() {
        expectedEx.expect(ConstraintViolationException.class);
        expectedEx.expectMessage("AETitle is mandatory");

        ForwardNode forwardNode = ForwardNode.ofEmpty();
        forwardNode.setDescription("description");
        forwardNode.setFwdAeTitle("fwdAeTitle");
        Destination destination = Destination.ofDicom("description", null, "hostname", 123, null);
        forwardNode.addDestination(destination);
        entityManager.persistAndFlush(forwardNode);
    }

    @Test
    public void testInvalidDestinationStow_URL_mandatory() {
        expectedEx.expect(ConstraintViolationException.class);
        expectedEx.expectMessage("URL is mandatory");

        ForwardNode forwardNode = ForwardNode.ofEmpty();
        forwardNode.setDescription("description");
        forwardNode.setFwdAeTitle("fwdAeTitle");
        Destination destination = Destination.ofStow("description", null, "urlCredentials", "headers");
        forwardNode.addDestination(destination);
        entityManager.persistAndFlush(forwardNode);
    }

    @Test
    public void testForwardNode() {
        ForwardNode forwardNode = ForwardNode.ofEmpty();
        forwardNode.setDescription("description");
        forwardNode.setFwdAeTitle("fwdAeTitle");
        entityManager.persistAndFlush(forwardNode);

        Iterable<ForwardNode> all = repository.findAll();
        assertThat(all) //
                .hasSize(1) //
                .first() //
                .satisfies(forwardNodeConsumer);
    }

    @Test
    public void testWithSourceNode() {
        ForwardNode forwardNode = ForwardNode.ofEmpty();
        forwardNode.setDescription("description");
        forwardNode.setFwdAeTitle("fwdAeTitle");
        DicomSourceNode sourceNode = DicomSourceNode.ofEmpty();
        sourceNode.setDescription("description");
        sourceNode.setAeTitle("aeTitle");
        sourceNode.setHostname("hostname");
        sourceNode.setCheckHostname(Boolean.TRUE);
        forwardNode.addSourceNode(sourceNode);
        entityManager.persistAndFlush(forwardNode);

        Iterable<ForwardNode> all = repository.findAll();
        assertThat(all) //
                .hasSize(1) //
                .first() //
                .satisfies(forwardNodeConsumer);

        assertThat(all) //
                .hasSize(1) //
                .flatExtracting(ForwardNode::getSourceNodes) //
                .hasSize(1) //
                .first() //
                .satisfies(sourceNodeConsumer);

        assertThat(all) //
                .hasSize(1) //
                .flatExtracting(ForwardNode::getDestinations) //
                .hasSize(0);
    }

    @Test
    public void testWithDestinationDicom() {
        ForwardNode forwardNode = ForwardNode.ofEmpty();
        forwardNode.setDescription("description");
        forwardNode.setFwdAeTitle("fwdAeTitle");
        Destination destination = Destination.ofDicom("description", "aeTitle", "hostname", 123, null);
        forwardNode.addDestination(destination);
        entityManager.persistAndFlush(forwardNode);

        Iterable<ForwardNode> all = repository.findAll();
        assertThat(all) //
                .hasSize(1) //
                .first() //
                .satisfies(forwardNodeConsumer);

        assertThat(all) //
                .hasSize(1) //
                .flatExtracting(ForwardNode::getSourceNodes) //
                .hasSize(0);

        assertThat(all) //
                .hasSize(1) //
                .flatExtracting(ForwardNode::getDestinations) //
                .hasSize(1) //
                .first() //
                .satisfies(destinationDicomConsumer);
    }

    @Test
    public void testWithDestinationStow() {
        ForwardNode forwardNode = ForwardNode.ofEmpty();
        forwardNode.setDescription("description");
        forwardNode.setFwdAeTitle("fwdAeTitle");
        Destination destination = Destination.ofStow("description", "url", "urlCredentials", "headers");
        forwardNode.addDestination(destination);
        entityManager.persistAndFlush(forwardNode);

        Iterable<ForwardNode> all = repository.findAll();
        assertThat(all) //
                .hasSize(1) //
                .first() //
                .satisfies(forwardNodeConsumer);

        assertThat(all) //
                .hasSize(1) //
                .flatExtracting(ForwardNode::getSourceNodes) //
                .hasSize(0);

        assertThat(all) //
                .hasSize(1) //
                .flatExtracting(ForwardNode::getDestinations) //
                .hasSize(1) //
                .first() //
                .satisfies(destinationStowConsumer);
    }

    @Test
    public void testWithSourceNodeAndDestinationDicom() {
        ForwardNode forwardNode = ForwardNode.ofEmpty();
        forwardNode.setDescription("description");
        forwardNode.setFwdAeTitle("fwdAeTitle");
        DicomSourceNode sourceNode = DicomSourceNode.ofEmpty();
        sourceNode.setDescription("description");
        sourceNode.setAeTitle("aeTitle");
        sourceNode.setHostname("hostname");
        sourceNode.setCheckHostname(Boolean.TRUE);
        forwardNode.addSourceNode(sourceNode);
        Destination destination = Destination.ofDicom("description", "aeTitle", "hostname", 123, null);
        forwardNode.addDestination(destination);
        entityManager.persistAndFlush(forwardNode);

        Iterable<ForwardNode> all = repository.findAll();
        assertThat(all) //
                .hasSize(1) //
                .first() //
                .satisfies(forwardNodeConsumer);

        assertThat(all) //
                .hasSize(1) //
                .flatExtracting(ForwardNode::getSourceNodes) //
                .hasSize(1) //
                .first() //
                .satisfies(sourceNodeConsumer);

        assertThat(all) //
                .hasSize(1) //
                .flatExtracting(ForwardNode::getDestinations) //
                .hasSize(1) //
                .first() //
                .satisfies(destinationDicomConsumer);
    }
}
