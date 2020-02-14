package org.karnak.data.input;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;

import javax.validation.ConstraintViolationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@RunWith(SpringRunner.class)
@DataJpaTest
public class InputRepositoryTest {
    private Consumer<SourceNode> sourceNodeConsumer = //
        x -> assertThat(x) //
            .hasFieldOrPropertyWithValue("description", "description") //
            .hasFieldOrPropertyWithValue("srcAeTitle", "srcAeTitle") //
            .hasFieldOrPropertyWithValue("dstAeTitle", "dstAeTitle") //
            .hasFieldOrPropertyWithValue("hostname", "hostname") //
            .hasFieldOrPropertyWithValue("checkHostname", true) //
            .extracting(Object::toString).asString().matches("^SourceNode \\[.*");
    private Consumer<Destination> destinationConsumer = //
        x -> assertThat(x) //
            .hasFieldOrPropertyWithValue("description", "description") //
            .hasFieldOrPropertyWithValue("aeTitle", "aeTitle") //
            .hasFieldOrPropertyWithValue("hostname", "hostname") //
            .hasFieldOrPropertyWithValue("port", 1024) //
            .extracting(Object::toString).asString().matches("^Destination \\[.*");

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InputRepository repository;

    private SourceNode createDefaultSourceNode() {
        SourceNode instance = SourceNode.ofEmpty();
        instance.setDescription("description");
        instance.setSrcAeTitle("srcAeTitle");
        instance.setDstAeTitle("dstAeTitle");
        instance.setHostname("hostname");
        instance.setCheckHostname(Boolean.TRUE);
        return instance;
    }

    private Destination createDefaultDestination() {
        Destination instance = Destination.ofEmpty();
        instance.setDescription("description");
        instance.setAeTitle("aeTitle");
        instance.setHostname("hostname");
        instance.setPort(1024);
        instance.setUseaetdest(Boolean.TRUE);
        instance.setSecure(Boolean.TRUE);
        return instance;
    }

    @Test
    public void testInvalidSourceNode_SourceAETitle_mandatory() {
        expectedEx.expect(ConstraintViolationException.class);
        expectedEx.expectMessage("Source AETitle is mandatory");

        SourceNode sourceNode = createDefaultSourceNode();

        sourceNode.setSrcAeTitle(null);

        entityManager.persistAndFlush(sourceNode);
    }

    @Test
    public void testInvalidSourceNode_SourceAETitle_size() {
        expectedEx.expect(ConstraintViolationException.class);
        expectedEx.expectMessage("Source AETitle has more than 16 characters");

        SourceNode sourceNode = createDefaultSourceNode();

        sourceNode.setSrcAeTitle("0123456789-0123456789");

        entityManager.persistAndFlush(sourceNode);
    }

    @Test
    public void testInvalidSourceNode_DestinationAETitle_mandatory() {
        expectedEx.expect(ConstraintViolationException.class);
        expectedEx.expectMessage("Destination AETitle is mandatory");

        SourceNode sourceNode = createDefaultSourceNode();

        sourceNode.setDstAeTitle(null);

        entityManager.persistAndFlush(sourceNode);
    }

    @Test
    public void testInvalidSourceNode_DestinationAETitle_size() {
        expectedEx.expect(ConstraintViolationException.class);
        expectedEx.expectMessage("Destination AETitle has more than 16 characters");

        SourceNode sourceNode = createDefaultSourceNode();

        sourceNode.setDstAeTitle("0123456789-0123456789");

        entityManager.persistAndFlush(sourceNode);
    }

    @Test
    public void testInvalidDestination_AETitle_mandatory() {
        expectedEx.expect(ConstraintViolationException.class);
        expectedEx.expectMessage("AETitle is mandatory");

        SourceNode sourceNode = createDefaultSourceNode();
        Destination destination = createDefaultDestination();
        sourceNode.addDestination(destination);

        destination.setAeTitle(null);

        entityManager.persistAndFlush(sourceNode);
    }

    @Test
    public void testSourceNode() {
        SourceNode sourceNode = createDefaultSourceNode();

        entityManager.persistAndFlush(sourceNode);

        Iterable<SourceNode> all = repository.findAll();
        assertThat(all) //
            .hasSize(1) //
            .first() //
            .satisfies(sourceNodeConsumer);
    }

    @Test
    public void testWithDestination() {
        SourceNode sourceNode = createDefaultSourceNode();
        Destination destination = createDefaultDestination();
        sourceNode.addDestination(destination);

        entityManager.persistAndFlush(sourceNode);

        Iterable<SourceNode> all = repository.findAll();
        assertThat(all) //
            .hasSize(1) //
            .first() //
            .satisfies(sourceNodeConsumer);

        assertThat(all) //
            .hasSize(1) //
            .flatExtracting(SourceNode::getDestinations) //
            .hasSize(1) //
            .first() //
            .satisfies(destinationConsumer);
    }
}
