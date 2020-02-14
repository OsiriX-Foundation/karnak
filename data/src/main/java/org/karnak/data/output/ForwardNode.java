package org.karnak.data.output;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity(name = "ForwardNode")
@Table(name = "forward_node")
public class ForwardNode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;

    // AETitle which defined a mapping of the gateway. This AETitle is configured as
    // a destination in the DICOM component that sends images to the gateway.
    @NotBlank(message = "Forward AETitle is mandatory")
    @Size(max = 16, message = "Forward AETitle has more than 16 characters")
    private String fwdAeTitle;

    // Specification of a DICOM source node (the one which sends images to the
    // gateway). When no source node is defined all the DICOM nodes are accepted by
    // the gateway.
    @OneToMany(mappedBy = "forwardNode", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<SourceNode> sourceNodes = new HashSet<>();

    // Specification of a final DICOM destination node. Multiple destinations can be
    // defined either as a DICOM or DICOMWeb type.
    @OneToMany(mappedBy = "forwardNode", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Destination> destinations = new HashSet<>();

    public static ForwardNode ofEmpty() {
        ForwardNode instance = new ForwardNode();
        return instance;
    }

    protected ForwardNode() {
        this.description = "";
        this.fwdAeTitle = "";
    }

    public Long getId() {
        return id;
    }

    public boolean isNewData() {
        return id == null;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFwdAeTitle() {
        return this.fwdAeTitle;
    }

    public void setFwdAeTitle(String fwdAeTitle) {
        this.fwdAeTitle = fwdAeTitle;
    }

    public Set<SourceNode> getSourceNodes() {
        return this.sourceNodes;
    }

    public void addSourceNode(SourceNode sourceNode) {
        sourceNode.setForwardNode(this);
        this.sourceNodes.add(sourceNode);
    }

    public void removeSourceNode(SourceNode sourceNode) {
        if (this.sourceNodes.remove(sourceNode)) {
            sourceNode.setForwardNode(null);
        }
    }

    public Set<Destination> getDestinations() {
        return destinations;
    }

    public void addDestination(Destination destination) {
        destination.setForwardNode(this);
        this.destinations.add(destination);
    }

    public void removeDestination(Destination destination) {
        if (this.destinations.remove(destination)) {
            destination.setForwardNode(null);
        }
    }

    /**
     * Informs if this object matches with the filter as text.
     * 
     * @param filterText
     *            the filter as text.
     * @return true if this object matches with the filter as text; false otherwise.
     */
    public boolean matchesFilter(String filterText) {
        if (contains(fwdAeTitle, filterText) //
            || contains(description, filterText)) {
            return true;
        }

        for (SourceNode sourceNode : sourceNodes) {
            if (sourceNode.matchesFilter(filterText)) {
                return true;
            }
        }

        for (Destination destination : destinations) {
            if (destination.matchesFilter(filterText)) {
                return true;
            }
        }

        return false;
    }

    private boolean contains(String value, String filterText) {
        return value != null && value.contains(filterText);
    }

    @Override
    public String toString() {
        return "ForwardNode [id=" + id + ", description=" + description + ", fwdAeTitle=" + fwdAeTitle
            + ", sourceNodes=" + sourceNodes + ", destinations=" + destinations + "]";
    }
}
