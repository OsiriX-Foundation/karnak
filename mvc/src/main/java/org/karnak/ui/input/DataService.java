package org.karnak.ui.input;

import java.io.Serializable;
import java.util.Collection;

import org.karnak.data.input.Destination;
import org.karnak.data.input.SourceNode;

/**
 * Back-end service interface for retrieving and updating data.
 */
public abstract class DataService implements Serializable {
    private static final long serialVersionUID = -1402338736361739563L;

    public abstract Collection<SourceNode> getAllSourceNodes();

    public abstract SourceNode getSourceNodeById(Long dataId);

    public abstract SourceNode updateSourceNode(SourceNode data);

    public abstract void deleteSourceNode(Long dataId);

    public abstract Collection<Destination> getAllDestinations(SourceNode sourceNode);

    public abstract Destination getDestinationById(SourceNode sourceNode, Long dataId);

    public abstract Destination updateDestination(SourceNode sourceNode, Destination data);

    public abstract void deleteDestination(SourceNode sourceNode, Destination data);
}
