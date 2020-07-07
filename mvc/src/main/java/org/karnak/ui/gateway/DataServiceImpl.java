package org.karnak.ui.gateway;

import java.util.*;

import org.karnak.data.gateway.*;

@SuppressWarnings("serial")
public class DataServiceImpl extends DataService {
    private GatewayPersistence gatewayPersistence;
    {
        gatewayPersistence = GatewayConfiguration.getInstance().getGatewayPersistence();
    }

    private SOPClassUIDPersistence sopClassUIDPersistence;
    {
        sopClassUIDPersistence = GatewayConfiguration.getInstance().getSopClassUIDPersistence();
    }


    @Override
    public Collection<ForwardNode> getAllForwardNodes() {
        List<ForwardNode> list = new ArrayList<>();
        gatewayPersistence.findAll() //
                .forEach(list::add);
        return list;
    }

    @Override
    public ForwardNode getForwardNodeById(Long dataId) {
        return gatewayPersistence.findById(dataId).orElse(null);
    }

    @Override
    public ForwardNode updateForwardNode(ForwardNode data) {
        return gatewayPersistence.saveAndFlush(data);
    }

    @Override
    public void deleteForwardNode(Long dataId) {
        gatewayPersistence.deleteById(dataId);
        gatewayPersistence.flush();
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
    public Collection<DicomSourceNode> getAllSourceNodes(ForwardNode forwardNode) {
        if (forwardNode != null) {
            return forwardNode.getSourceNodes();
        }
        return new HashSet<>();
    }

    @Override
    public DicomSourceNode getSourceNodeById(ForwardNode forwardNode, Long dataId) {
        Collection<DicomSourceNode> sourceNodes = getAllSourceNodes(forwardNode);
        for (DicomSourceNode sourceNode : sourceNodes) {
            if (Objects.equals(sourceNode.getId(), dataId)) {
                return sourceNode;
            }
        }
        return null;
    }

    @Override
    public DicomSourceNode updateSourceNode(ForwardNode forwardNode, DicomSourceNode data) {
        if (forwardNode == null || data == null) {
            return null;
        }
        Collection<DicomSourceNode> sourceNodes = getAllSourceNodes(forwardNode);
        if (!sourceNodes.contains(data)) {
            forwardNode.addSourceNode(data);
        }
        return data;
    }

    @Override
    public void deleteSourceNode(ForwardNode forwardNode, DicomSourceNode data) {
        if (forwardNode == null || data == null) {
            return;
        }
        forwardNode.removeSourceNode(data);
    }

    @Override
    public List<SOPClassUID> getAllSOPClassUIDs() {
        List<SOPClassUID> list = new ArrayList<>();
        sopClassUIDPersistence.findAll() //
                .forEach(list::add);
        return list;
    }

    @Override
    public SOPClassUID getSOPClassUIDByName(String name) {
        return sopClassUIDPersistence.getSOPClassUIDByName(name);
    }


}
