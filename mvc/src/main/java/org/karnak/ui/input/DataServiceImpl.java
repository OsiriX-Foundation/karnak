package org.karnak.ui.input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.karnak.data.input.Destination;
import org.karnak.data.input.InputRepository;
import org.karnak.data.input.SourceNode;

@SuppressWarnings("serial")
public class DataServiceImpl extends DataService {
    private InputRepository inputRepository;

    {
        inputRepository = InputConfiguration.getInstance().getInputRepository();
    }

    @Override
    public Collection<SourceNode> getAllSourceNodes() {
        List<SourceNode> list = new ArrayList<>();
        inputRepository.findAll() //
                .forEach(list::add);
        return list;
    }

    @Override
    public SourceNode getSourceNodeById(Long dataId) {
        return inputRepository.findById(dataId).orElse(null);
    }

    @Override
    public SourceNode updateSourceNode(SourceNode data) {
        return inputRepository.saveAndFlush(data);
    }

    @Override
    public void deleteSourceNode(Long dataId) {
        inputRepository.deleteById(dataId);
        inputRepository.flush();
    }

    @Override
    public Collection<Destination> getAllDestinations(SourceNode sourceNode) {
        if (sourceNode != null) {
            return sourceNode.getDestinations();
        }
        return new HashSet<>();
    }

    @Override
    public Destination getDestinationById(SourceNode sourceNode, Long dataId) {
        Collection<Destination> destinations = getAllDestinations(sourceNode);
        for (Destination destination : destinations) {
            if (Objects.equals(destination.getId(), dataId)) {
                return destination;
            }
        }
        return null;
    }

    @Override
    public Destination updateDestination(SourceNode sourceNode, Destination data) {
        if (sourceNode == null || data == null) {
            return null;
        }
        Collection<Destination> destinations = getAllDestinations(sourceNode);
        if (!destinations.contains(data)) {
            sourceNode.addDestination(data);
        }
        return data;
    }

    @Override
    public void deleteDestination(SourceNode sourceNode, Destination data) {
        if (sourceNode == null || data == null) {
            return;
        }
        sourceNode.removeDestination(data);
    }
}
