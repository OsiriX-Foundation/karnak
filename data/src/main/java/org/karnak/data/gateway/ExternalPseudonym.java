package org.karnak.data.gateway;

import javax.persistence.*;

@Entity(name = "ExternalPseudonym")
@Table(name = "external_pseudonym")
public class ExternalPseudonym {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String tag;

    private String delimiter;

    private Integer position;

    private Boolean pseudonymAsPatientName;

    public ExternalPseudonym() {
        this.tag = "";
        this.delimiter = "";
        this.position = 0;
        this.pseudonymAsPatientName = false;
    }

    public ExternalPseudonym(String tag, String delimiter, Integer position, Boolean pseudonymAsPatientName) {
        this.tag = tag;
        this.delimiter = delimiter;
        this.position = position;
        this.pseudonymAsPatientName = pseudonymAsPatientName;
    }

    public Long getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Boolean getPseudonymAsPatientName() {
        return pseudonymAsPatientName;
    }

    public void setPseudonymAsPatientName(Boolean useExternalPseudonym) {
        this.pseudonymAsPatientName = useExternalPseudonym;
    }
}
