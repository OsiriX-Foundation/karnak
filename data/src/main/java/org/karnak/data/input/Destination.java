package org.karnak.data.input;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity (name = "InputDestination")
@Table(name = "input_destination")
public class Destination {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;

    // the AETitle of the destination node.
    @NotBlank(message = "AETitle is mandatory")
    @Size(max = 16, message = "AETitle has more than 16 characters")
    @Pattern(regexp = "^[^\\s]+$", message = "AETitle contains white spaces")
    private String aeTitle;

    // the host or IP of the destination node.
    @NotBlank(message = "Hostname is mandatory")
    private String hostname;

    // the port of the destination node.
    @NotNull(message = "Port is mandatory")
    @Min(value = 1, message = "Port should be between 1 and 65535")
    @Max(value = 65535, message = "Port should be between 1 and 65535")
    private Integer port;

    // false by default; if "true" then use the destination AETitle as the calling
    // AETitle at the gateway side. Otherwise with "false" the calling AETitle is
    // the AETitle defined in the property "listener.aet" of the file
    // gateway.properties.
    private Boolean useaetdest;

    // valeur "true" ou "false" => DICOM-S ou DICOM (par défaut "false"), si "true"
    // utilise le keystore.jks et trust.jks se trouvant dans le même répertoire.
    private Boolean secure;

    // list of emails (comma separated) used when the images have been sent (or
    // partially sent) to the final destination. Note: if an issue appears before
    // sending to the final destination then no email is delivered.
    private String notify;

    @ManyToOne
    @JoinColumn
    private SourceNode sourceNode;

    public static Destination ofEmpty() {
        Destination instance = new Destination();
        return instance;
    }

    protected Destination() {
        this.description = "";
        this.aeTitle = "";
        this.hostname = "";
        this.port = Integer.valueOf(0);
        this.useaetdest = Boolean.FALSE;
        this.secure = Boolean.FALSE;
        this.notify = "";
    }

    public Long getId() {
        return id;
    }

    public boolean isNewData() {
        return id == null;
    }

    public String getStringReference() {
        return getAeTitle();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAeTitle() {
        return aeTitle;
    }

    public void setAeTitle(String aeTitle) {
        this.aeTitle = aeTitle;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getUseaetdest() {
        return useaetdest;
    }

    public void setUseaetdest(Boolean useaetdest) {
        this.useaetdest = useaetdest;
    }

    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public SourceNode getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(SourceNode sourceNode) {
        this.sourceNode = sourceNode;
    }

    /**
     * Informs if this object matches with the filter as text.
     * 
     * @param filterText the filter as text.
     * @return true if this object matches with the filter as text; false otherwise.
     */
    public boolean matchesFilter(String filterText) {
        if (contains(description, filterText) //
                || contains(aeTitle, filterText) //
                || contains(hostname, filterText) //
                || contains(aeTitle, filterText) //
                || equals(port, filterText) //
                || contains(notify, filterText)) {
            return true;
        }
        return false;
    }

    private boolean contains(String value, String filterText) {
        return value != null && value.contains(filterText);
    }

    private boolean equals(Integer value, String filterText) {
        return value != null && value.toString().equals(filterText);
    }

    @Override
    public String toString() {
        return "Destination [id=" + id + ", description=" + description + ", aeTitle=" + aeTitle + ", hostname="
                + hostname + ", port=" + port + ", useaetdest=" + useaetdest + ", secure=" + secure + ", notify="
                + notify + "]";
    }
}
