package org.karnak.data.input;

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

@Entity(name = "InputSourceNode")
@Table(name = "input_source_node")
public class SourceNode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;

    // AET source during the forward (see by the destination as the source AET).
    @NotBlank(message = "Source AETitle is mandatory")
    @Size(max = 16, message = "Source AETitle has more than 16 characters")
    private String srcAeTitle;

    // Destination AET.
    @NotBlank(message = "Destination AETitle is mandatory")
    @Size(max = 16, message = "Destination AETitle has more than 16 characters")
    private String dstAeTitle;

    // the host or IP of the destination node.
    @NotBlank(message = "Hostname is mandatory")
    private String hostname;

    // if "true" check the hostname during the DICOM association and if not match
    // the connection is abort
    private Boolean checkHostname;

    // Specification of a final DICOM destination node. Multiple destinations can be
    // defined as a DICOM.
    @OneToMany(mappedBy = "sourceNode", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Destination> destinations = new HashSet<>();

    public static SourceNode ofEmpty() {
        SourceNode instance = new SourceNode();
        return instance;
    }

    protected SourceNode() {
        this.description = "";
        this.srcAeTitle = "";
        this.dstAeTitle = "";
        this.hostname = "";
        this.checkHostname = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public boolean isNewData() {
        return id == null;
    }

    public String getStringReference() {
        return getDstAeTitle();
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSrcAeTitle() {
        return this.srcAeTitle;
    }

    public void setSrcAeTitle(String srcAeTitle) {
        this.srcAeTitle = srcAeTitle;
    }

    public String getDstAeTitle() {
        return this.dstAeTitle;
    }

    public void setDstAeTitle(String dstAeTitle) {
        this.dstAeTitle = dstAeTitle;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Boolean getCheckHostname() {
        return checkHostname;
    }

    public void setCheckHostname(Boolean checkHostname) {
        this.checkHostname = checkHostname;
    }

    public Set<Destination> getDestinations() {
        return destinations;
    }

    public void addDestination(Destination destination) {
        destination.setSourceNode(this);
        this.destinations.add(destination);
    }

    public void removeDestination(Destination destination) {
        if (this.destinations.remove(destination)) {
            destination.setSourceNode(null);
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
        if (contains(srcAeTitle, filterText) //
            || contains(dstAeTitle, filterText) //
            || contains(hostname, filterText) //
            || contains(description, filterText)) {
            return true;
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
        return "SourceNode [id=" + id + ", description=" + description + ", srcAeTitle=" + srcAeTitle + ", dstAeTitle="
            + dstAeTitle + ", hostname=" + hostname + ", checkHostname=" + checkHostname + "]";
    }
}
