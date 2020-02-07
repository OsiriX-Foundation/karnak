package org.karnak.ui.output;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.karnak.data.output.Destination;
import org.karnak.data.output.ForwardNode;
import org.karnak.data.output.OutputRepository;
import org.karnak.data.output.SourceNode;

@SuppressWarnings("serial")
public class DataServiceImpl extends DataService {
    private OutputRepository outputRepository;

    {
        outputRepository = OutputConfiguration.getInstance().getOutputRepository();
    }

    @Override
    public Collection<ForwardNode> getAllForwardNodes() {
        List<ForwardNode> list = new ArrayList<>();
        outputRepository.findAll() //
                .forEach(list::add);
        return list;
    }

    @Override
    public ForwardNode getForwardNodeById(Long dataId) {
        return outputRepository.findById(dataId).orElse(null);
    }

    @Override
    public ForwardNode updateForwardNode(ForwardNode data) {
        return outputRepository.saveAndFlush(data);
    }

    @Override
    public void deleteForwardNode(Long dataId) {
        outputRepository.deleteById(dataId);
        outputRepository.flush();
    }

    @Override
    public Collection<Destination> getAllDestinations(ForwardNode forwardNode) {
        if (forwardNode != null) {
            return forwardNode.getDestinations();
        }
        return new HashSet<>();
    }

    @Override
    public Destination getDestinationById(ForwardNode forwardNode, Long dataId) {
        Collection<Destination> destinations = getAllDestinations(forwardNode);
        for (Destination destination : destinations) {
            if (Objects.equals(destination.getId(), dataId)) {
                return destination;
            }
        }
        return null;
    }

    @Override
    public Destination updateDestination(ForwardNode forwardNode, Destination data) {
        if (forwardNode == null || data == null) {
            return null;
        }
        Collection<Destination> destinations = getAllDestinations(forwardNode);
        if (!destinations.contains(data)) {
            forwardNode.addDestination(data);
        }
        return data;
    }

    @Override
    public void deleteDestination(ForwardNode forwardNode, Destination data) {
        if (forwardNode == null || data == null) {
            return;
        }
        forwardNode.removeDestination(data);
    }

    @Override
    public Collection<SourceNode> getAllSourceNodes(ForwardNode forwardNode) {
        if (forwardNode != null) {
            return forwardNode.getSourceNodes();
        }
        return new HashSet<>();
    }

    @Override
    public SourceNode getSourceNodeById(ForwardNode forwardNode, Long dataId) {
        Collection<SourceNode> sourceNodes = getAllSourceNodes(forwardNode);
        for (SourceNode sourceNode : sourceNodes) {
            if (Objects.equals(sourceNode.getId(), dataId)) {
                return sourceNode;
            }
        }
        return null;
    }

    @Override
    public SourceNode updateSourceNode(ForwardNode forwardNode, SourceNode data) {
        if (forwardNode == null || data == null) {
            return null;
        }
        Collection<SourceNode> sourceNodes = getAllSourceNodes(forwardNode);
        if (!sourceNodes.contains(data)) {
            forwardNode.addSourceNode(data);
        }
        return data;
    }

    @Override
    public void deleteSourceNode(ForwardNode forwardNode, SourceNode data) {
        if (forwardNode == null || data == null) {
            return;
        }
        forwardNode.removeSourceNode(data);
    }
}
