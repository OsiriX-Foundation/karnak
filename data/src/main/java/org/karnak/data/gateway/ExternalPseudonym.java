package org.karnak.data.gateway;

import javax.persistence.*;

@Entity(name = "ExternalPseudonym")
@Table(name = "external_pseudonym")
public class ExternalPseudonym {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private IdTypes idTypes;

    private String tag;

    private String delimiter;

    private Integer position;

    private Boolean pseudonymAsPatientName;


    public ExternalPseudonym() {
        this.tag = null;
        this.delimiter = null;
        this.position = null;
        this.pseudonymAsPatientName = null;
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

    public IdTypes getIdTypes() {
        return idTypes;
    }

    public void setIdTypes(IdTypes idTypes) {
        this.idTypes = idTypes;
    }
}
